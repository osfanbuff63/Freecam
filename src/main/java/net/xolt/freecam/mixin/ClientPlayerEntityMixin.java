package net.xolt.freecam.mixin;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {

    // Needed for Baritone compatibility.
    @Inject(method = "isControlledCamera", at = @At("HEAD"), cancellable = true)
    private void onIsCamera(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && this.equals(MC.player)) {
            cir.setReturnValue(true);
        }
    }

    // Disables freecam upon receiving damage if disableOnDamage is enabled.
    @Inject(method = "hurt", at = @At("HEAD"))
    private void onDamage(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.isEnabled() && FreecamConfig.DISABLE_ON_DAMAGE.get() && this.equals(MC.player) && !MC.player.isCreative()) {
            Freecam.setDisableNextTick(true);
        }
    }
}
