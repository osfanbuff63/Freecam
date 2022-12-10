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
  private static boolean persistentCameraEnabled = false;
  private static boolean playerControlEnabled = false;
  private static Integer activePersistentCamera = null;

  private static FreeCamera freeCamera;
  private static HashMap<Integer, FreeCamera> persistentCameras = new HashMap<>();

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
    if (persistentCameraEnabled) {
      togglePersistentCamera(activePersistentCamera);
    } else {
      if (enabled) {
        onDisableFreecam();
      } else {
        onEnableFreecam();
      }
      enabled = !enabled;
    }
  }

  public static void togglePersistentCamera() {
    togglePersistentCamera(activePersistentCamera);
  }

  public static void togglePersistentCamera(int keyCode) {
    if (persistentCameraEnabled) {
      if (activePersistentCamera.equals(keyCode)) {
        onDisablePersistentCamera(keyCode);
        persistentCameraEnabled = false;
      } else {
        onDisable();
        persistentCameras.get(activePersistentCamera).input = new Input();
        onEnablePersistentCamera(keyCode);
      }
    } else {
      if (enabled) {
        toggle();
      }
      onEnablePersistentCamera(keyCode);
      persistentCameraEnabled = true;
    }
  }

  public static void resetCamera(int keyCode) {
    FreeCamera camera = persistentCameras.get(keyCode);
    if (camera != null) {
      camera.copyPosition(MC.player);
      if (FreecamConfig.NOTIFY_PERSISTENT.get()) {
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

  private static void onEnablePersistentCamera(int keyCode) {
    onEnable();
    FreeCamera persistentCamera = persistentCameras.get(keyCode);

    boolean chunkLoaded = false;
    if (persistentCamera != null) {
      ChunkPos chunkPos = persistentCamera.chunkPosition();
      chunkLoaded = MC.level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z);
    }

    if (persistentCamera == null || !chunkLoaded) {
      persistentCamera = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0));
      persistentCameras.put(keyCode, persistentCamera);
      persistentCamera.spawn();
    }

    persistentCamera.input = new KeyboardInput(MC.options);
    MC.setCameraEntity(persistentCamera);
    activePersistentCamera = keyCode;

    if (FreecamConfig.NOTIFY_PERSISTENT.get()) {
      MC.player.displayClientMessage(Component.translatable("msg.freecam.enablePersistent").append("" + activePersistentCamera % GLFW.GLFW_KEY_0), true);
    }
  }

  private static void onDisablePersistentCamera(int keyCode) {
    onDisable();
    persistentCameras.get(keyCode).input = new Input();

    if (MC.player != null) {
      if (FreecamConfig.NOTIFY_PERSISTENT.get()) {
        MC.player.displayClientMessage(Component.translatable("msg.freecam.disablePersistent").append("" + activePersistentCamera % GLFW.GLFW_KEY_0), true);
      }
    }
    activePersistentCamera = null;
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

  public static void clearPersistentCameras() {
    persistentCameras = new HashMap<>();
  }

  public static FreeCamera getFreeCamera() {
    FreeCamera result = null;
    if (enabled) {
      result = freeCamera;
    } else if (persistentCameraEnabled) {
      result = persistentCameras.get(activePersistentCamera);
    }
    return result;
  }

  public static boolean isEnabled() {
    return enabled || persistentCameraEnabled;
  }

  public static boolean isFreecamEnabled() {
    return enabled;
  }

  public static boolean isPersistentCameraEnabled() {
    return persistentCameraEnabled;
  }

  public static boolean isPlayerControlEnabled() {
    return playerControlEnabled;
  }
}
