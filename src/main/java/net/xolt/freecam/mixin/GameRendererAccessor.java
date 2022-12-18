package net.xolt.freecam.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameRenderer.class)
public interface GameRendererAccessor {

    @Accessor("renderHand")
    void setRenderHand(boolean renderHand);

    @Accessor("minecraft") Minecraft getClient();
}
