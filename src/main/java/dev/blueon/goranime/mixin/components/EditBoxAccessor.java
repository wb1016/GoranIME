package dev.blueon.goranime.mixin.components;

import net.minecraft.client.gui.components.EditBox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EditBox.class)
public interface EditBoxAccessor {
    @Accessor("value")
    String getValue();

    @Accessor("value")
    void setValue(String value);

    @Accessor("cursorPos")
    int getCursorPos();

    @Accessor("cursorPos")
    void setCursorPos(int pos);

    @Accessor("highlightPos")
    int getHighlightPos();

    @Accessor("highlightPos")
    void setHighlightPos(int pos);

    @Accessor("maxLength")
    int getMaxLength();

    @Accessor("displayPos")
    int getDisplayPos();

    @Accessor("displayPos")
    void setDisplayPos(int pos);

    @Accessor("isEditable")
    boolean isEditable();

    @Accessor("textX")
    int getTextX();

    @Accessor("textY")
    int getTextY();
}
