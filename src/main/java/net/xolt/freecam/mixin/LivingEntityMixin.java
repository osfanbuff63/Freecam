package net.xolt.freecam.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow public abstract float getHealth();

    // Allows for the horizontal speed of creative flight to be configured separately from vertical speed.
    @Inject(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetMovementSpeed(CallbackInfoReturnable<Float> cir) {
        if (Freecam.isEnabled() && FreecamConfig.FLIGHT_MODE.get().equals(FreecamConfig.FlightMode.CREATIVE) && this.equals(Freecam.getFreeCamera())) {
            cir.setReturnValue((float) (FreecamConfig.HORIZONTAL_SPEED.get() / 10) * (Freecam.getFreeCamera().isSprinting() ? 2 : 1));
        }
    }

    // Disables freecam upon receiving damage if disableOnDamage is enabled.
    @Inject(method = "setHealth", at = @At("HEAD"))
    private void onSetHealth(float health, CallbackInfo ci) {
        if (Freecam.isEnabled() && FreecamConfig.DISABLE_ON_DAMAGE.get() && this.equals(MC.player)) {
            if (!MC.player.isCreative() && getHealth() > health) {
                Freecam.setDisableNextTick(true);
            }
        }
    }
}
