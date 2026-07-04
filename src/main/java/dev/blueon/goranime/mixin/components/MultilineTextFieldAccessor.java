package dev.blueon.goranime.mixin.components;

import net.minecraft.client.gui.components.MultilineTextField;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.function.Consumer;

@Mixin(MultilineTextField.class)
interface MultilineTextFieldAccessor {

    @Accessor("cursor")
    void goranime$cursor(int cursor);

    @Accessor("selectCursor")
    int goranime$selectCursor();

    @Accessor("selectCursor")
    void goranime$selectCursor(int selectCursor);

    @Accessor("valueListener")
    Consumer<String> goranime$valueListener();

    @Invoker("overflowsLineLimit")
    boolean goranime$overflowsLineLimit(String newValue);
}
