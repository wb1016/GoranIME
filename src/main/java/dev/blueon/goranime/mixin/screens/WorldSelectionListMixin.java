package dev.blueon.goranime.mixin.screens;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import dev.blueon.goranime.client.search.KoreanSearchMatcher;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(WorldSelectionList.class)
abstract class WorldSelectionListMixin {

    @WrapMethod(method = "filterAccepts")
    private boolean goranime$wrapFilterAccepts(
        final String filter,
        final LevelSummary level,
        Operation<Boolean> original
    ) {
        boolean originalResult = original.call(filter, level);
        if (originalResult) {
            return true;
        }
        return KoreanSearchMatcher.matches(level.getLevelName(), filter) || KoreanSearchMatcher.matches(level.getLevelId(), filter);
    }
}
