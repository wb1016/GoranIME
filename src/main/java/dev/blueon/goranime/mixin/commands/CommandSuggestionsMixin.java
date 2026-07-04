package dev.blueon.goranime.mixin.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.blueon.goranime.client.ime.PreeditComposer;
import dev.blueon.goranime.client.ime.PreeditState;
import dev.blueon.goranime.client.search.KoreanSearchMatcher;
import dev.blueon.goranime.mixin.components.EditBoxAccessor;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CommandSuggestions.class)
abstract class CommandSuggestionsMixin {

    @Shadow
    @Final
    private EditBox input;

    @Shadow
    private boolean keepSuggestions;

    @WrapMethod(method = "updateCommandInfo")
    private void goranime$wrapUpdateCommandInfo(Operation<Void> original) {
        if (
            this.keepSuggestions || !(this.input instanceof PreeditState state)
        ) {
            original.call();
            return;
        }
        String composition = state.goranime$composition();
        if (composition.isEmpty()) {
            original.call();
            return;
        }
        EditBoxAccessor box = (EditBoxAccessor) this.input;
        String value = this.input.getValue();
        int cursor = box.getCursorPos();
        int highlight = box.getHighlightPos();
        PreeditComposer.MergeResult result = PreeditComposer.merge(
            value,
            cursor,
            composition
        );
        box.setValue(result.text());
        box.setCursorPos(result.cursor());
        box.setHighlightPos(result.cursor());
        try {
            original.call();
        } finally {
            box.setValue(value);
            box.setCursorPos(cursor);
            box.setHighlightPos(highlight);
        }
    }

    @WrapMethod(method = "calculateSuggestionSuffix")
    private static @Nullable String goranime$wrapCalculateSuggestionSuffix(
        String contents,
        String suggestion,
        Operation<String> original
    ) {
        String result = original.call(contents, suggestion);
        if (result != null || suggestion.length() <= contents.length()) {
            return result;
        }
        String prefix = suggestion.substring(0, contents.length());
        return KoreanSearchMatcher.matches(prefix, contents)
            ? suggestion.substring(contents.length())
            : null;
    }
}
