package net.xolt.freecam.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {


    // Allows for the horizontal speed of creative flight to be configured separately from vertical speed.
    @Inject(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && FreecamConfig.FLIGHT_MODE.get().equals(FreecamConfig.FlightMode.CREATIVE) && this.equals(Freecam.getFreeCamera())) {
            cir.setReturnValue((float) (FreecamConfig.HORIZONTAL_SPEED.get() / 10) * (Freecam.getFreeCamera().isSprinting() ? 2 : 1));
        }
    }
}
