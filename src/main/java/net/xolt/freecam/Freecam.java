package net.xolt.freecam;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.client.util.InputMappings;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.xolt.freecam.config.ConfigScreen;
import net.xolt.freecam.config.FreecamConfig;
import net.xolt.freecam.mixin.GameRendererAccessor;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;

@Mod(Freecam.MOD_ID)
public class Freecam {
    public static final String MOD_ID = "freecam";
    public static final Minecraft MC = Minecraft.getInstance();
    public static final KeyBinding KEY_TOGGLE = new KeyBinding("key.freecam.toggle", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_F4, "category.freecam.freecam");
    public static final KeyBinding KEY_PLAYER_CONTROL = new KeyBinding("key.freecam.playerControl", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam");
    public static final KeyBinding KEY_TRIPOD_RESET = new KeyBinding("key.freecam.tripodReset", InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_UNKNOWN, "category.freecam.freecam");

    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static boolean disableNextTick = false;
    private static Integer activeTripod = null;
    private static FreeCamera freeCamera;
    private static HashMap<Integer, FreecamPosition> overworld_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> nether_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> end_tripods = new HashMap<>();

    public Freecam() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, FreecamConfig.SPEC, "freecam.toml");

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (mc, screen) -> new ConfigScreen(screen));
    }

    public static void toggle() {
        if (tripodEnabled) {
            toggleTripod(activeTripod);
            return;
        }

        if (freecamEnabled) {
            onDisableFreecam();
        } else {
            onEnableFreecam();
        }
        freecamEnabled = !freecamEnabled;
    }

    public static void toggleTripod(Integer keyCode) {
        if (keyCode == null) {
            return;
        }

        if (tripodEnabled) {
            if (activeTripod.equals(keyCode)) {
                onDisableTripod();
                tripodEnabled = false;
            } else {
                onDisableTripod();
                onEnableTripod(keyCode);
            }
        } else {
            if (freecamEnabled) {
                toggle();
            }
            onEnableTripod(keyCode);
            tripodEnabled = true;
        }
    }

    public static void switchControls() {
        if (!isEnabled()) {
            return;
        }

        if (playerControlEnabled) {
            freeCamera.input = new MovementInputFromOptions(MC.options);
        } else {
            MC.player.input = new MovementInputFromOptions(MC.options);
            freeCamera.input = new MovementInput();
        }
        playerControlEnabled = !playerControlEnabled;
    }

    private static void onEnableTripod(int keyCode) {
        onEnable();

        FreecamPosition position = getTripodsForDimension().get(keyCode);
        boolean chunkLoaded = false;
        if (position != null) {
            ChunkPos chunkPos = position.getChunkPos();
            chunkLoaded = MC.level.getChunkSource().hasChunk(chunkPos.x, chunkPos.z);
        }

        if (!chunkLoaded) {
            resetCamera(keyCode);
            position = null;
        }

        if (position == null) {
            freeCamera = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0));
        } else {
            freeCamera = new FreeCamera(-420 - (keyCode % GLFW.GLFW_KEY_0), position);
        }

        freeCamera.spawn();
        MC.setCameraEntity(freeCamera);
        activeTripod = keyCode;

        if (FreecamConfig.NOTIFY_TRIPOD.get()) {
            MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.enablePersistent").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
        }
    }

    private static void onDisableTripod() {
        getTripodsForDimension().put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

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

        if (MC.player != null) {
            if (FreecamConfig.NOTIFY_FREECAM.get()) {
                MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.disable"), true);
            }
        }
    }

    private static void onEnable() {
        MC.smartCull = false;
        ((GameRendererAccessor) MC.gameRenderer).setRenderHand(FreecamConfig.SHOW_HAND.get());

        if (MC.gameRenderer.getMainCamera().isDetached()) {
            MC.options.setCameraType(PointOfView.FIRST_PERSON);
        }
    }

    private static void onDisable() {
        MC.smartCull = true;
        ((GameRendererAccessor) MC.gameRenderer).setRenderHand(true);
        MC.setCameraEntity(MC.player);
        playerControlEnabled = false;
        freeCamera.despawn();
        freeCamera.input = new MovementInput();
        freeCamera = null;

        if (MC.player != null) {
            MC.player.input = new MovementInputFromOptions(MC.options);
        }
    }

    public static void resetCamera(int keyCode) {
        if (tripodEnabled && activeTripod == keyCode && freeCamera != null) {
            freeCamera.copyPosition(MC.player);
        } else {
            getTripodsForDimension().put(keyCode, null);
        }

        if (FreecamConfig.NOTIFY_TRIPOD.get()) {
            MC.player.displayClientMessage(new TranslationTextComponent("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
        }
    }

    public static void clearTripods() {
        overworld_tripods = new HashMap<>();
        nether_tripods = new HashMap<>();
        end_tripods = new HashMap<>();
    }

    public static FreeCamera getFreeCamera() {
        return freeCamera;
    }

    public static HashMap<Integer, FreecamPosition> getTripodsForDimension() {
        HashMap<Integer, FreecamPosition> result;
        switch (MC.level.effects().skyType()) {
            case NONE:
                result = nether_tripods;
                break;
            case END:
                result = end_tripods;
                break;
            default:
                result = overworld_tripods;
                break;
        }
        return result;
    }

    public static boolean disableNextTick() {
        return disableNextTick;
    }

    public static void setDisableNextTick(boolean damage) {
        disableNextTick = damage;
    }

    public static boolean isEnabled() {
        return freecamEnabled || tripodEnabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }

    public static boolean canUseCheats() {
        return MC.player.hasPermissions(2) || MC.player.isCreative() || MC.hasSingleplayerServer();
    }
}
