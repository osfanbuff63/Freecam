package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.entity.Pose;
import net.minecraft.network.IPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.BlockPos;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;

import java.util.UUID;

import static net.xolt.freecam.Freecam.MC;

public class FreeCamera extends ClientPlayerEntity {

    private static final ClientPlayNetHandler CONNECTION = new ClientPlayNetHandler(MC, MC.screen, MC.getConnection().getConnection(), new GameProfile(UUID.randomUUID(), "FreeCamera")) {
        @Override
        public void send(IPacket<?> packet) {
        }
    };

    public FreeCamera(int id) {
        this(id, new FreecamPosition(MC.player));
    }

    public FreeCamera(int id, FreecamPosition position) {
        super(MC, MC.level, CONNECTION, MC.player.getStats(), MC.player.getRecipeBook(), false, false);

        setId(id);
        moveTo(position.x, position.y, position.z, position.yaw, position.pitch);
        super.setPose(position.pose);
        xBob = xRot;
        yBob = yRot;
        xBobO = xRot; // Prevents camera from rotating upon entering freecam.
        yBobO = yRot;
        abilities.flying = true;
        input = new MovementInputFromOptions(MC.options);
    }

    public void spawn() {
        if (clientLevel != null) {
            clientLevel.addPlayer(getId(), this);
        }
    }

    public void despawn() {
        if (clientLevel != null && clientLevel.getEntity(getId()) != null) {
            clientLevel.removeEntity(getId());
        }
    }

    // Prevents fall damage sound when FreeCamera touches ground with noClip disabled.
    @Override
    protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
    }

    // Needed for hand swings to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
    @Override
    public float getAttackAnim(float tickDelta) {
        return MC.player.getAttackAnim(tickDelta);
    }

    // Needed for item use animations to be shown in freecam since the player is replaced by FreeCamera in HeldItemRenderer.renderItem()
    @Override
    public int getUseItemRemainingTicks() {
        return MC.player.getUseItemRemainingTicks();
    }

    // Also needed for item use animations to be shown in freecam.
    @Override
    public boolean isUsingItem() {
        return MC.player.isUsingItem();
    }

    // Prevents slow down from ladders/vines.
    @Override
    public boolean onClimbable() {
        return false;
    }

    // Prevents slow down from water.
    @Override
    public boolean isInWater() {
        return false;
    }

    @Override public EffectInstance getEffect(Effect effect) {
        return MC.player.getEffect(effect);
    }

    // Prevents pistons from moving FreeCamera when noClip is enabled.
    @Override public PushReaction getPistonPushReaction() {
        return FreecamConfig.NO_CLIP.get() ? PushReaction.IGNORE : PushReaction.NORMAL;
    }

    // Prevents pose from changing when clipping through blocks.
    @Override public void setPose(Pose pose) {
        if (pose.equals(Pose.STANDING) || (pose.equals((Pose.CROUCHING)) && !getPose().equals(Pose.STANDING))) {
            super.setPose(pose);
        }
    }

    @Override
    public void aiStep() {
        noPhysics = FreecamConfig.NO_CLIP.get() && Freecam.canUseCheats();
        if (FreecamConfig.FLIGHT_MODE.get().equals(FreecamConfig.FlightMode.DEFAULT)) {
            abilities.setFlyingSpeed(0);
            Motion.doMotion(this, FreecamConfig.HORIZONTAL_SPEED.get(), FreecamConfig.VERTICAL_SPEED.get());
        } else {
            abilities.setFlyingSpeed((float) (FreecamConfig.VERTICAL_SPEED.get() / 10));
        }
        super.aiStep();
        abilities.flying = true;
        onGround = false;
    }
}
