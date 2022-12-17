package net.xolt.freecam.mixin;

import net.minecraft.network.NetworkManager;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class NetworkManagerMixin {

    // Disables freecam if the player disconnects.
    @Inject(method = "handleDisconnection", at = @At("HEAD"))
    private void onHandleDisconnection(CallbackInfo ci) {
        if (Freecam.isEnabled()) {
            Freecam.toggle();
        }
        Freecam.clearTripods();
    }
}
