package net.xolt.freecam;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.KeyboardInput;
import net.minecraft.client.CameraType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.xolt.freecam.config.FreecamConfig;
import net.xolt.freecam.util.FreeCamera;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.function.BiFunction;

@Mod(Freecam.MOD_ID)
public class Freecam {
  public static final String MOD_ID = "freecam";
  public static final Minecraft MC = Minecraft.getInstance();

  public static final KeyMapping KEY_TOGGLE = new KeyMapping("key.freecam.toggle", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F4, "category.freecam.freecam");
  public static final KeyMapping KEY_PLAYER_CONTROL = new KeyMapping("key.freecam.playerControl", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam");
  public static final KeyMapping KEY_TRIPOD_RESET = new KeyMapping("key.freecam.tripodReset", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam");

  private static boolean enabled = false;
  private static boolean tripodEnabled = false;
  private static boolean playerControlEnabled = false;
  private static Integer activeTripod = null;

  private static FreeCamera freeCamera;
  private static HashMap<Integer, FreeCamera> tripods = new HashMap<>();

  public Freecam() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FreecamConfig.SPEC, "freecam.toml");

    ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
        () -> new ConfigScreenHandler.ConfigScreenFactory(new BiFunction<Minecraft, Screen, Screen>() {
          @Override public Screen apply(Minecraft minecraft, Screen screen) {
            return new ConfigScreen(screen);
          }
        }));
  }

  public static void toggle() {
    if (tripodEnabled) {
      toggleTripod(activeTripod);
    } else {
      if (enabled) {
        onDisableFreecam();
      } else {
        onEnableFreecam();
      }
      enabled = !enabled;
    }
  }

  public static void toggleTripod() {
    toggleTripod(activeTripod);
  }

  public static void toggleTripod(int keyCode) {
    if (tripodEnabled) {
      if (activeTripod.equals(keyCode)) {
        onDisableTripod(keyCode);
        tripodEnabled = false;
      } else {
        onDisable();
        tripods.get(activeTripod).input = new Input();
        onEnableTripod(keyCode);
      }
    } else {
      if (enabled) {
        toggle();
      }
      onEnableTripod(keyCode);
      tripodEnabled = true;
    }
  }

  public static void resetCamera(int keyCode) {
    FreeCamera camera = tripods.get(keyCode);
    if (camera != null) {
      camera.copyPosition(MC.player);
      if (FreecamConfig.NOTIFY_TRIPOD.get()) {
        MC.player.displayClientMessage(Component.translatable("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
      }
    }
  }

  public static void switchControls() {
    if (isEnabled()) {
      if (playerControlEnabled) {
        getFreeCamera().input = new KeyboardInput(MC.options);
      } else {
        MC.player.input = new KeyboardInput(MC.options);
        getFreeCamera().input = new Input();
      }
      playerControlEnabled = !playerControlEnabled;
    }
  }

  private static void onEnableTripod(int keyCode) {
    onEnable();
    FreeCamera tripod = tripods.get(keyCode);

    boolean chunkLoaded = false;
    if (tripod != null) {
      ChunkPos chunkPos = tripod.chunkPosition();
      chunkLoaded = MC.level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z);
    }

    if (tripod == null || !chunkLoaded) {
      tripod = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0));
      tripods.put(keyCode, tripod);
      tripod.spawn();
    }

    tripod.input = new KeyboardInput(MC.options);
    MC.setCameraEntity(tripod);
    activeTripod = keyCode;

    if (FreecamConfig.NOTIFY_TRIPOD.get()) {
      MC.player.displayClientMessage(Component.translatable("msg.freecam.openTripod").append("" + activeTripod
          % GLFW.GLFW_KEY_0), true);
    }
  }

  private static void onDisableTripod(int keyCode) {
    onDisable();
    tripods.get(keyCode).input = new Input();

    if (MC.player != null) {
      if (FreecamConfig.NOTIFY_TRIPOD.get()) {
        MC.player.displayClientMessage(Component.translatable("msg.freecam.closeTripod").append("" + activeTripod
            % GLFW.GLFW_KEY_0), true);
      }
    }
    activeTripod = null;
  }

  private static void onEnableFreecam() {
    onEnable();
    freeCamera = new FreeCamera(-420);
    freeCamera.spawn();
    MC.setCameraEntity(freeCamera);

    if (FreecamConfig.NOTIFY_FREECAM.get()) {
      MC.player.displayClientMessage(Component.translatable("msg.freecam.enable"), true);
    }
  }

  private static void onDisableFreecam() {
    onDisable();
    freeCamera.despawn();
    freeCamera = null;

    if (MC.player != null) {
      if (FreecamConfig.NOTIFY_FREECAM.get()) {
        MC.player.displayClientMessage(Component.translatable("msg.freecam.disable"), true);
      }
    }
  }

  private static void onEnable() {
    MC.smartCull = false;
    MC.gameRenderer.setRenderHand(FreecamConfig.SHOW_HAND.get());

    if (MC.gameRenderer.getMainCamera().isDetached()) {
      MC.options.setCameraType(CameraType.FIRST_PERSON);
    }
  }

  private static void onDisable() {
    MC.smartCull = true;
    MC.gameRenderer.setRenderHand(true);
    MC.setCameraEntity(MC.player);
    playerControlEnabled = false;

    if (MC.player != null) {
      MC.player.input = new KeyboardInput(MC.options);
    }
  }

  public static void clearTripods() {
    tripods = new HashMap<>();
  }

  public static FreeCamera getFreeCamera() {
    FreeCamera result = null;
    if (enabled) {
      result = freeCamera;
    } else if (tripodEnabled) {
      result = tripods.get(activeTripod);
    }
    return result;
  }

  public static boolean isEnabled() {
    return enabled || tripodEnabled;
  }

  public static boolean isFreecamEnabled() {
    return enabled;
  }

  public static boolean isTripodEnabled() {
    return tripodEnabled;
  }

  public static boolean isPlayerControlEnabled() {
    return playerControlEnabled;
  }
}
