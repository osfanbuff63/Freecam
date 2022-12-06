package net.xolt.freecam.mixin;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.xolt.freecam.Freecam.MC;

@Mixin(FirstPersonRenderer.class)
public class FirstPersonRendererMixin {

    private float tickDelta;

    // Makes arm movement depend upon FreeCamera movement rather than player movement.
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private ClientPlayerEntity onRenderItem(ClientPlayerEntity player) {
        if (Freecam.isEnabled()) {
            return Freecam.getFreeCamera();
        }
        return player;
    }

    @Inject(method = "renderHandsWithItems", at = @At("HEAD"))
    private void storeTickDelta(float tickDelta, MatrixStack matrices, IRenderTypeBuffer.Impl vertexConsumers, ClientPlayerEntity player, int light, CallbackInfo ci) {
        this.tickDelta = tickDelta;
    }

    // Makes arm shading depend upon FreeCamera position rather than player position.
    @ModifyVariable(method = "renderHandsWithItems", at = @At("HEAD"), argsOnly = true)
    private int onRenderItem2(int light) {
        if (Freecam.isEnabled()) {
            return MC.getEntityRenderDispatcher().getPackedLightCoords(Freecam.getFreeCamera(), tickDelta);
        }
        return light;
    }
}
