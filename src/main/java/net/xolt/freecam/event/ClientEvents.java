package net.xolt.freecam.event;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fmlclient.registry.ClientRegistry;
import net.xolt.freecam.Freecam;

import static net.xolt.freecam.Freecam.*;

public class ClientEvents {

  @Mod.EventBusSubscriber(modid = Freecam.MOD_ID, value = Dist.CLIENT)
  public static class ClientForgeEvents {

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
      if (Freecam.KEY_TRIPOD_RESET.isDown()) {
        for (KeyMapping hotbarKey : MC.options.keyHotbarSlots) {
          while (hotbarKey.consumeClick()) {
            Freecam.resetCamera(hotbarKey.getDefaultKey().getValue());
            while (Freecam.KEY_TRIPOD_RESET.consumeClick()) {}
          }
        }
      }

      if (Freecam.KEY_TOGGLE.isDown()) {
        for (KeyMapping hotbarKey : MC.options.keyHotbarSlots) {
          while (hotbarKey.consumeClick()) {
            Freecam.togglePersistentCamera(hotbarKey.getDefaultKey().getValue());
            while (Freecam.KEY_TOGGLE.consumeClick()) {}
          }
        }
      } else if (Freecam.KEY_TOGGLE.consumeClick()){
        Freecam.toggle();
        while (Freecam.KEY_TOGGLE.consumeClick()) {}
      }

      while (Freecam.KEY_PLAYER_CONTROL.consumeClick()) {
        Freecam.switchControls();
      }
    }
  }

  @Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
  public static class ClientModBusEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
      ClientRegistry.registerKeyBinding(KEY_TOGGLE);
      ClientRegistry.registerKeyBinding(KEY_PLAYER_CONTROL);
      ClientRegistry.registerKeyBinding(KEY_TRIPOD_RESET);
    }
  }
}
