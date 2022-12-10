package net.xolt.freecam.mixin;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.multiplayer.PlayerController;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.xolt.freecam.Freecam.MC;

@Mixin(PlayerController.class)
public class PlayerControllerMixin {

    // Prevents interacting with blocks when allowInteract is disabled.
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onInteractBlock(ClientPlayerEntity player, ClientWorld pLevel, Hand hand, BlockRayTraceResult hitResult, CallbackInfoReturnable<ActionResultType> cir) {
        if (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && (!FreecamConfig.ALLOW_INTERACT.get() || (!Freecam.canUseCheats() && !FreecamConfig.INTERACTION_MODE.get().equals(FreecamConfig.InteractionMode.PLAYER)))) {
            cir.setReturnValue(ActionResultType.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        if (entity.equals(MC.player) || (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && (!FreecamConfig.ALLOW_INTERACT.get() || (!Freecam.canUseCheats() && !FreecamConfig.INTERACTION_MODE.get().equals(FreecamConfig.InteractionMode.PLAYER))))) {
            cir.setReturnValue(ActionResultType.PASS);
        }
    }

    // Prevents interacting with entities when allowInteract is disabled, and prevents interacting with self.
    @Inject(method = "interactAt", at = @At("HEAD"), cancellable = true)
    private void onInteractEntityAtLocation(PlayerEntity player, Entity entity, EntityRayTraceResult hitResult, Hand hand, CallbackInfoReturnable<ActionResultType> cir) {
        if (entity.equals(MC.player) || (Freecam.isEnabled() && !Freecam.isPlayerControlEnabled() && (!FreecamConfig.ALLOW_INTERACT.get() || (!Freecam.canUseCheats() && !FreecamConfig.INTERACTION_MODE.get().equals(FreecamConfig.InteractionMode.PLAYER))))) {
            cir.setReturnValue(ActionResultType.PASS);
        }
    }

    // Prevents attacking self.
    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (target.equals(MC.player)) {
            ci.cancel();
        }
    }
}
