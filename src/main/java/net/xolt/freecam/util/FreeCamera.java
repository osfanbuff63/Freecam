package net.xolt.freecam.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.config.FreecamConfig;

import java.util.UUID;

import static net.xolt.freecam.Freecam.MC;

public class FreeCamera extends LocalPlayer {

    private static final ClientPacketListener CONNECTION = new ClientPacketListener(MC, MC.screen, MC.getConnection().getConnection(), MC.getCurrentServer(), new GameProfile(UUID.randomUUID(), "FreeCamera"), MC.getTelemetryManager().createWorldSessionManager(false, null)) {
        @Override
        public void send(Packet<?> packet) {
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
        xBob = getXRot();
        yBob = getYRot();
        xBobO = getXRot(); // Prevents camera from rotating upon entering freecam.
        yBobO = getYRot();
        getAbilities().flying = true;
        input = new KeyboardInput(MC.options);
    }

    public void spawn() {
        if (clientLevel != null) {
            clientLevel.addPlayer(getId(), this);
        }
    }

    public void despawn() {
        if (clientLevel != null && clientLevel.getEntity(getId()) != null) {
            clientLevel.removeEntity(getId(), RemovalReason.DISCARDED);
        }
    }

    // Prevents fall damage sound when FreeCamera touches ground with noClip disabled.
    @Override
    protected void checkFallDamage(double p_20990_, boolean p_20991_, BlockState p_20992_, BlockPos p_20993_) {
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

    @Override
    public MobEffectInstance getEffect(MobEffect effect) {
        return MC.player.getEffect(effect);
    }

    // Prevents pistons from moving FreeCamera when noClip is enabled.
    @Override public PushReaction getPistonPushReaction() {
        return FreecamConfig.NO_CLIP.get() ? PushReaction.IGNORE : PushReaction.NORMAL;
    }

    // Prevents pose from changing when clipping through blocks.
    @Override
    public void setPose(Pose pose) {
        if (pose.equals(Pose.STANDING) || (pose.equals((Pose.CROUCHING)) && !getPose().equals(Pose.STANDING))) {
            super.setPose(pose);
        }
    }

    @Override
    public void aiStep() {
        noPhysics = FreecamConfig.NO_CLIP.get() && Freecam.canUseCheats();
        if (FreecamConfig.FLIGHT_MODE.get().equals(FreecamConfig.FlightMode.DEFAULT)) {
            getAbilities().setFlyingSpeed(0);
            Motion.doMotion(this, FreecamConfig.HORIZONTAL_SPEED.get(), FreecamConfig.VERTICAL_SPEED.get());
        } else {
            getAbilities().setFlyingSpeed((float) (FreecamConfig.VERTICAL_SPEED.get() / 10));
        }
        super.aiStep();
        getAbilities().flying = true;
        onGround = false;
    }
}
