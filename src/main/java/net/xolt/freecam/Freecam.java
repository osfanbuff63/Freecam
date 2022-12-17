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
  public static final KeyBinding KEY_TRIPOD_RESET = new KeyBinding("key.freecam.tripodReset", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam");

  private static boolean enabled = false;
  private static boolean tripodEnabled = false;
  private static boolean playerControlEnabled = false;
  private static Integer activeTripod = null;

  private static FreeCamera freeCamera;
  private static HashMap<Integer, FreeCamera> tripods = new HashMap<>();

  public Freecam() {
    ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FreecamConfig.SPEC, "freecam.toml");

    ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new ConfigScreen(screen));
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
        tripods.get(activeTripod).input = new MovementInput();
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
        MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
      }
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

  private static void onEnableTripod(int keyCode) {
    onEnable();
    FreeCamera tripod = tripods.get(keyCode);

    boolean chunkLoaded = false;
    if (tripod != null) {
      chunkLoaded = MC.level.getChunkSource().hasChunk(tripod.xChunk, tripod.yChunk);
    }

    if (tripod == null || !chunkLoaded) {
      tripod = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0));
      tripods.put(keyCode, tripod);
      tripod.spawn();
    }

    tripod.input = new MovementInputFromOptions(MC.options);
    MC.setCameraEntity(tripod);
    activeTripod = keyCode;

    if (FreecamConfig.NOTIFY_TRIPOD.get()) {
      MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.enablePersistent").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
    }
  }

  private static void onDisableTripod(int keyCode) {
    onDisable();
    tripods.get(keyCode).input = new MovementInput();

    if (MC.player != null) {
      if (FreecamConfig.NOTIFY_TRIPOD.get()) {
        MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.disablePersistent").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
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
