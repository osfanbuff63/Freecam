package net.xolt.freecam.event;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.xolt.freecam.Freecam;

import static net.xolt.freecam.Freecam.*;

public class ClientEvents {

  @Mod.EventBusSubscriber(modid = Freecam.MOD_ID, value = Dist.CLIENT)
  public static class ClientForgeEvents {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
      ClientRegistry.registerKeyBinding(KEY_TOGGLE);
      ClientRegistry.registerKeyBinding(KEY_PLAYER_CONTROL);
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
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
}
