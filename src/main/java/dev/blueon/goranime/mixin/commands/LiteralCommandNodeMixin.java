package dev.blueon.goranime.mixin.commands;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.blueon.goranime.client.search.KoreanSearchMatcher;
import java.util.concurrent.CompletableFuture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LiteralCommandNode.class)
abstract class LiteralCommandNodeMixin {

    @Shadow
    @Final
    private String literal;

    @WrapMethod(method = "listSuggestions")
    private CompletableFuture<Suggestions> goranime$wrapListSuggestions(
        CommandContext<?> context,
        SuggestionsBuilder builder,
        Operation<CompletableFuture<Suggestions>> original
    ) {
        String remaining = builder.getRemaining();
        if (
            !remaining.isEmpty() &&
            remaining.indexOf(' ') < 0 &&
            KoreanSearchMatcher.matches(this.literal, remaining)
        ) {
            return builder.suggest(this.literal).buildFuture();
        }
        return original.call(context, builder);
    }
}
