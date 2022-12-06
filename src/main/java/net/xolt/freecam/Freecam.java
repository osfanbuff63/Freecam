package net.xolt.freecam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.xolt.freecam.config.FreecamConfig;
import net.xolt.freecam.mixin.GameRendererAccessor;
import net.xolt.freecam.util.FreeCamera;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Mod(Freecam.MOD_ID)
public class Freecam {
  public static final String MOD_ID = "freecam";
  public static final Minecraft MC = Minecraft.getInstance();

  public static final KeyBinding KEY_TOGGLE = new KeyBinding("key.freecam.toggle", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_F4, "category.freecam.freecam");
  public static final KeyBinding KEY_PLAYER_CONTROL = new KeyBinding("key.freecam.playerControl", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam");

  private static boolean enabled = false;
  private static boolean persistentCameraEnabled = false;
  private static boolean playerControlEnabled = false;
  private static Integer activePersistentCamera = null;

  private static FreeCamera freeCamera;
  private static HashMap<Integer, FreeCamera> persistentCameras = new HashMap<>();

  public Freecam() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FreecamConfig.SPEC, "freecam.toml");

    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new ConfigScreen(screen));
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
        persistentCameras.get(activePersistentCamera).input = new MovementInput();
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

  public static void switchControls() {
    if (isEnabled()) {
      if (playerControlEnabled) {
        getFreeCamera().input = new MovementInputFromOptions(MC.options);
      } else {
        MC.player.input = new MovementInputFromOptions(MC.options);
        getFreeCamera().input = new MovementInput();
      }
      playerControlEnabled = !playerControlEnabled;
    }
  }

  private static void onEnablePersistentCamera(int keyCode) {
    onEnable();
    FreeCamera persistentCamera = persistentCameras.get(keyCode);

    boolean chunkLoaded = false;
    if (persistentCamera != null) {
      chunkLoaded = MC.level.getChunkSource().hasChunk(persistentCamera.xChunk, persistentCamera.yChunk);
    }

    if (persistentCamera == null || !chunkLoaded) {
      persistentCamera = new FreeCamera();
      persistentCameras.put(keyCode, persistentCamera);
      persistentCamera.spawn();
    }

    persistentCamera.input = new MovementInputFromOptions(MC.options);
    MC.setCameraEntity(persistentCamera);
    activePersistentCamera = keyCode;

    if (FreecamConfig.NOTIFY_PERSISTENT.get()) {
      MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.enablePersistent").append("" + activePersistentCamera % GLFW.GLFW_KEY_0), true);
    }
  }

  private static void onDisablePersistentCamera(int keyCode) {
    onDisable();
    persistentCameras.get(keyCode).input = new MovementInput();

    if (MC.player != null) {
      if (FreecamConfig.NOTIFY_PERSISTENT.get()) {
        MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.disablePersistent").append("" + activePersistentCamera % GLFW.GLFW_KEY_0), true);
      }
    }
    activePersistentCamera = null;
  }

  private static void onEnableFreecam() {
    onEnable();
    freeCamera = new FreeCamera();
    freeCamera.spawn();
    MC.setCameraEntity(freeCamera);

    if (FreecamConfig.NOTIFY_FREECAM.get()) {
      MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.enable"), true);
    }
  }

  private static void onDisableFreecam() {
    onDisable();
    freeCamera.despawn();
    freeCamera = null;

    if (MC.player != null) {
      if (FreecamConfig.NOTIFY_FREECAM.get()) {
        MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.disable"), true);
      }
    }
  }

  private static void onEnable() {
    MC.smartCull = false;
    ((GameRendererAccessor)MC.gameRenderer).setRenderHand(FreecamConfig.SHOW_HAND.get());

    if (MC.gameRenderer.getMainCamera().isDetached()) {
      MC.options.setCameraType(PointOfView.FIRST_PERSON);
    }
  }

  private static void onDisable() {
    MC.smartCull = true;
    ((GameRendererAccessor)MC.gameRenderer).setRenderHand(true);
    MC.setCameraEntity(MC.player);
    playerControlEnabled = false;

    if (MC.player != null) {
      MC.player.input = new MovementInputFromOptions(MC.options);
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
