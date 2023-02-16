package net.xolt.freecam.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.ChunkPos;

public class FreecamPosition {
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public Pose pose;

    public FreecamPosition(Entity entity) {
        x = entity.getX();
        y = entity.getY();
        z = entity.getZ();
        pitch = entity.getXRot();
        yaw = entity.getYRot();
        pose = entity.getPose();
    }

    public static FreecamPosition getSwimmingPosition(Entity entity) {
        FreecamPosition position = new FreecamPosition(entity);

        // Set pose to swimming, adjusting y position so eye-height doesn't change
        if (position.pose != Pose.SWIMMING) {
            position.y += entity.getEyeHeight(position.pose) - entity.getEyeHeight(Pose.SWIMMING);
            position.pose = Pose.SWIMMING;
        }

        return position;
    }

    public ChunkPos getChunkPos() {
        return new ChunkPos((int) (x / 16), (int) (z / 16));
    }
}
