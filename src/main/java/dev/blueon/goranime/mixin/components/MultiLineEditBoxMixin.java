package dev.blueon.goranime.mixin.components;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.blueon.goranime.client.ime.PreeditComposer;
import dev.blueon.goranime.client.ime.PreeditDispatcher;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.MultilineTextField;
import net.minecraft.client.input.PreeditEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiLineEditBox.class)
abstract class MultiLineEditBoxMixin {

    @Shadow @Final
    private MultilineTextField textField;

    @Unique
    private final PreeditDispatcher preeditDispatcher = new PreeditDispatcher();

    @Inject(method = "preeditUpdated", at = @At("HEAD"), cancellable = true)
    private void goranime$preeditUpdated(final @Nullable PreeditEvent event, CallbackInfoReturnable<Boolean> cir) {
        MultilineTextFieldAccessor field = (MultilineTextFieldAccessor) this.textField;
        this.preeditDispatcher.apply(
            event,
            this.textField.value(),
            this.textField.cursor(),
            field.goranime$selectCursor(),
            mergedText -> mergedText.length() <= this.textField.characterLimit() && !field.goranime$overflowsLineLimit(mergedText),
            this.textField::insertText,
            field.goranime$valueListener()
        );
        cir.setReturnValue(true);
    }

    @WrapMethod(method = "extractContents")
    private void goranime$wrapExtractContents(
        final GuiGraphicsExtractor graphics,
        final int mouseX,
        final int mouseY,
        final float delta,
        Operation<Void> original
    ) {
        if (this.preeditDispatcher.composition().isEmpty()) {
            original.call(graphics, mouseX, mouseY, delta);
            return;
        }
        String previousValue = this.textField.value();
        int previousCursor = this.textField.cursor();
        PreeditComposer.MergeResult result = PreeditComposer.merge(
            previousValue,
            previousCursor,
            this.preeditDispatcher.composition()
        );
        this.textField.setValue(result.text());
        MultilineTextFieldAccessor field = (MultilineTextFieldAccessor) this.textField;
        field.goranime$cursor(result.cursor());
        field.goranime$selectCursor(result.cursor());
        try {
            original.call(graphics, mouseX, mouseY, delta);
        } finally {
            this.textField.setValue(previousValue);
            field.goranime$cursor(previousCursor);
            field.goranime$selectCursor(previousCursor);
        }
    }
}
