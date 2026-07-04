package dev.blueon.goranime.mixin.components;

import dev.blueon.goranime.client.config.GoranimeConfig;
import dev.blueon.goranime.client.ime.HangulProcessor;
import dev.blueon.goranime.client.ime.HangulUtil;
import dev.blueon.goranime.client.ime.KeyboardLayout;
import dev.blueon.goranime.client.ime.KoreanInputHandler;
import dev.blueon.goranime.client.ime.LangTypeManager;
import dev.blueon.goranime.client.ime.PreeditDispatcher;
import dev.blueon.goranime.client.ime.PreeditState;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.PreeditEvent;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
abstract class EditBoxMixin implements PreeditState {

    @Shadow
    private String value;

    @Shadow
    private int maxLength;

    @Shadow
    private int cursorPos;

    @Shadow
    private int highlightPos;

    @Shadow
    private boolean isEditable;

    @Shadow
    private @Nullable Consumer<String> responder;

    @Unique
    private final PreeditDispatcher preeditDispatcher = new PreeditDispatcher();

    @Unique
    private int goranime$lastModifiers;

    @Override
    public String goranime$composition() {
        return this.preeditDispatcher.composition();
    }

    @Inject(method = "preeditUpdated", at = @At("HEAD"), cancellable = true)
    private void goranime$preeditUpdated(
        @Nullable PreeditEvent event,
        CallbackInfoReturnable<Boolean> cir
    ) {
        this.preeditDispatcher.apply(
            event,
            this.value,
            this.cursorPos,
            this.highlightPos,
            this.maxLength,
            ((EditBox) (Object) this)::insertText,
            this.responder
        );
        cir.setReturnValue(true);
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    private void goranime$keyPressed(
        KeyEvent keyEvent,
        CallbackInfoReturnable<Boolean> cir
    ) {
        this.goranime$lastModifiers = keyEvent.modifiers();

        if (!LangTypeManager.get().isKorean()) return;
        if (keyEvent.key() != GLFW.GLFW_KEY_BACKSPACE) return;
        if (this.value.isEmpty()) return;
        if (!this.isEditable) return;
        if (!((EditBox) (Object) this).getHighlighted().isEmpty()) return;

        if (
            KoreanInputHandler.onBackspace(
                this.cursorPos,
                this.value,
                TextInserter.of((EditBox) (Object) this)
            )
        ) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "charTyped", at = @At("HEAD"), cancellable = true)
    private void goranime$charTyped(
        CharacterEvent event,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (!LangTypeManager.get().isKorean()) return;

        char ch = (char) event.codepoint();
        int modifiers = this.goranime$lastModifiers;

        if (ch < 32 || ch == 127) {
            KeyboardLayout.INSTANCE.assemblePosition = -1;
            return;
        }
        if (!this.isEditable) return;
        cir.setReturnValue(true);

        int qwertyIdx = KeyboardLayout.INSTANCE.qwertyIndex(ch);
        if (qwertyIdx == -1) {
            KeyboardLayout.INSTANCE.assemblePosition = -1;
            ((EditBox) (Object) this).insertText(String.valueOf(ch));
            return;
        }

        char koreanChar = KeyboardLayout.INSTANCE.layout.charAt(qwertyIdx);
        if (!HangulProcessor.isHangulChar(koreanChar)) {
            ((EditBox) (Object) this).insertText(String.valueOf(ch));
            KeyboardLayout.INSTANCE.assemblePosition = -1;
            return;
        }
        if (!((EditBox) (Object) this).canConsumeInput()) return;

        if (this.cursorPos > 0 && this.highlightPos == this.cursorPos) {
            if (
                KoreanInputHandler.onCharTyped(
                    ch,
                    modifiers,
                    this.value,
                    this.cursorPos,
                    true,
                    TextInserter.of((EditBox) (Object) this)
                )
            ) return;
        }
        char fixed = HangulUtil.fix(modifiers, ch, koreanChar);
        ((EditBox) (Object) this).insertText(String.valueOf(fixed));
        KeyboardLayout.INSTANCE.assemblePosition = HangulProcessor.isHangulChar(
            fixed
        )
            ? this.cursorPos
            : -1;
    }

    /** Draw KO/EN indicator near the text cursor. */
    @Inject(method = "extractWidgetRenderState", at = @At("TAIL"))
    private void goranime$drawIndicator(
        GuiGraphicsExtractor graphics,
        int mouseX,
        int mouseY,
        float delta,
        CallbackInfo ci
    ) {
        if (!GoranimeConfig.get().showIndicator) return;
        if (!((EditBox) (Object) this).isFocused()) return;

        EditBox self = (EditBox) (Object) this;
        EditBoxAccessor acc = (EditBoxAccessor) this;
        Minecraft client = Minecraft.getInstance();

        int displayed = this.cursorPos - acc.getDisplayPos();
        if (displayed < 0) displayed = 0;
        String visible = this.value.substring(
            acc.getDisplayPos(),
            Math.min(this.cursorPos, this.value.length())
        );
        int cursorX = acc.getTextX() + client.font.width(visible);
        int cursorY = acc.getTextY();

        boolean korean = LangTypeManager.get().isKorean();
        String text = korean ? "KO" : "EN";
        int color = korean ? 0xFFFF4444 : 0xFF44AAFF;
        int tw = client.font.width(text);
        int x = cursorX + 6;
        int y = cursorY;

        graphics.fill(x - 1, y - 1, x + tw + 1, y + 10, 0xC0000000);
        graphics.text(client.font, text, x, y, color);
    }

    @Unique
    private static final class TextInserter
        implements KoreanInputHandler.TextInserter
    {

        private final EditBox box;
        private final EditBoxAccessor acc;

        TextInserter(EditBox box) {
            this.box = box;
            this.acc = (EditBoxAccessor) box;
        }

        static TextInserter of(EditBox box) {
            return new TextInserter(box);
        }

        @Override
        public void write(String text) {
            box.insertText(text);
        }

        @Override
        public void replace(String text) {
            String val = acc.getValue();
            int cursor = acc.getCursorPos();
            if (cursor > 0 && cursor <= val.length()) {
                String before = val.substring(0, cursor - 1);
                String after = val.substring(cursor);
                box.setValue(before + text + after);
                acc.setCursorPos(cursor - 1 + text.length());
                acc.setHighlightPos(cursor - 1 + text.length());
            }
        }
    }
}
