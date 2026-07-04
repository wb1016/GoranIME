package dev.blueon.goranime.mixin.input;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.Window;
import dev.blueon.goranime.client.GoranimeKeyBinds;
import dev.blueon.goranime.client.config.GoranimeConfig;
import dev.blueon.goranime.client.ime.LangTypeManager;
import dev.blueon.goranime.client.ime.WindowsImeShapeEnforcer;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFWCharCallbackI;
import org.lwjgl.glfw.GLFWIMEStatusCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWPreeditCallbackI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
abstract class KeyboardHandlerMixin {

    @Inject(method = "keyPress", at = @At("HEAD"))
    private void goranime$onKeyPress(
        long window,
        int action,
        KeyEvent keyEvent,
        CallbackInfo ci
    ) {
        if (action != 1) return;
        if (GoranimeKeyBinds.langToggle().matches(keyEvent)) {
            LangTypeManager.get().toggle();
        }
    }

    @WrapOperation(
        method = "setup",
        at = @At(
            value = "INVOKE",
            target = "Lcom/mojang/blaze3d/platform/InputConstants;setupKeyboardCallbacks(Lcom/mojang/blaze3d/platform/Window;Lorg/lwjgl/glfw/GLFWKeyCallbackI;Lorg/lwjgl/glfw/GLFWCharCallbackI;Lorg/lwjgl/glfw/GLFWPreeditCallbackI;Lorg/lwjgl/glfw/GLFWIMEStatusCallbackI;)V"
        )
    )
    private void goranime$wrapKeyboardCallbacks(
        Window window,
        GLFWKeyCallbackI keyPressCallback,
        GLFWCharCallbackI charTypedCallback,
        GLFWPreeditCallbackI preeditCallback,
        GLFWIMEStatusCallbackI imeStatusCallback,
        Operation<Void> original
    ) {
        original.call(
            window,
            keyPressCallback,
            (GLFWCharCallbackI) (handle, codepoint) -> {
                charTypedCallback.invoke(handle, codepoint);
                if (
                    GoranimeConfig.get().preventFullwidth &&
                    (codepoint == 0x3000 ||
                        (codepoint >= 0xFF01 && codepoint <= 0xFF5E))
                ) {
                    WindowsImeShapeEnforcer.forceHalfwidthIfEnabled(window);
                }
            },
            preeditCallback,
            (GLFWIMEStatusCallbackI) handle -> {
                imeStatusCallback.invoke(handle);
                if (GoranimeConfig.get().preventFullwidth) {
                    WindowsImeShapeEnforcer.forceHalfwidthIfEnabled(window);
                }
            }
        );
    }
}
