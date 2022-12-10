package net.xolt.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(Minecraft.class)
public class MinecraftMixin {

    // Prevents player from being controlled when freecam is enabled.
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            if (MC.player != null && MC.player.input instanceof KeyboardInput && !Freecam.isPlayerControlEnabled()) {
                Input input = new Input();
                input.shiftKeyDown = MC.player.input.shiftKeyDown; // Makes player continue to sneak after freecam is enabled.
                MC.player.input = input;
            }
            MC.gameRenderer.setRenderHand(FreecamConfig.SHOW_HAND.get());
        }
    }

    // Prevents attacks when allowInteract is disabled.
    @Inject(method = "startAttack", at = @At("HEAD"), cancellable = true)
    private void onDoAttack(CallbackInfo ci) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !FreecamConfig.ALLOW_INTERACT.get()) {
            ci.cancel();
        }
    }

    // Prevents item pick when allowInteract is disabled.
    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void onDoItemPick(CallbackInfo ci) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !FreecamConfig.ALLOW_INTERACT.get()) {
            ci.cancel();
        }
    }

    // Prevents block breaking when allowInteract is disabled.
    @Inject(method = "continueAttack", at = @At("HEAD"), cancellable = true)
    private void onHandleBlockBreaking(CallbackInfo ci) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && !FreecamConfig.ALLOW_INTERACT.get()) {
            ci.cancel();
        }
    }

    // Prevents hotbar keys from changing selected slot when freecam key is held
    @Inject(method = "handleKeybinds", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;consumeClick()Z", ordinal = 2), cancellable = true)
    private void onHandleInputEvents(CallbackInfo ci) {
        if (Freecam.KEY_TOGGLE.isDown() || Freecam.KEY_TRIPOD_RESET.isDown()) {
            ci.cancel();
        }
    }
}
