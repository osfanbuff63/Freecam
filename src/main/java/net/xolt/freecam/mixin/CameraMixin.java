package net.xolt.freecam.mixin;

import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.IBlockReader;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ActiveRenderInfo.class)
public class CameraMixin {
    @Shadow Entity entity;
    @Shadow float eyeHeightOld;
    @Shadow float eyeHeight;

    // When toggling freecam, update the camera's eye height instantly without any transition.
    @Inject(method = "setup", at = @At("HEAD"))
    public void onUpdate(IBlockReader area, Entity newFocusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
        if (newFocusedEntity == null || this.entity == null || newFocusedEntity.equals(this.entity)) {
            return;
        }

        if (newFocusedEntity instanceof FreeCamera || this.entity instanceof FreeCamera) {
            this.eyeHeightOld = this.eyeHeight = newFocusedEntity.getEyeHeight();
        }
    }

    // Removes the submersion overlay when underwater, in lava, or powdered snow.
    @Inject(method = "getFluidInCamera", at = @At("HEAD"), cancellable = true)
    public void onGetSubmersionType(CallbackInfoReturnable<FluidState> cir) {
        if (Freecam.isEnabled() && !FreecamConfig.SHOW_SUBMERSION.get()) {
            cir.setReturnValue(Fluids.EMPTY.defaultFluidState());
        }
    }
}
