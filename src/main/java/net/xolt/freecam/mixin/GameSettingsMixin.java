package net.xolt.freecam.mixin;

import net.minecraft.client.GameSettings;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameSettings.class)
public class GameSettingsMixin {

    // Prevents switching to third person in freecam.
    @Inject(method = "setCameraType", at = @At("HEAD"), cancellable = true)
    private void onSetPerspective(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            ci.cancel();
        }
    }
}
