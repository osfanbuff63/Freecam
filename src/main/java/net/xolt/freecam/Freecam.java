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
import net.xolt.freecam.config.ConfigScreen;
import net.xolt.freecam.config.FreecamConfig;
import net.xolt.freecam.util.FreeCamera;
import net.xolt.freecam.util.FreecamPosition;
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

    private static boolean freecamEnabled = false;
    private static boolean tripodEnabled = false;
    private static boolean playerControlEnabled = false;
    private static Integer activeTripod = null;
    private static FreeCamera freeCamera;
    private static HashMap<Integer, FreecamPosition> overworld_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> nether_tripods = new HashMap<>();
    private static HashMap<Integer, FreecamPosition> end_tripods = new HashMap<>();

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
            freeCamera.input = new KeyboardInput(MC.options);
        } else {
            MC.player.input = new KeyboardInput(MC.options);
            freeCamera.input = new Input();
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
            MC.player.displayClientMessage(Component.translatable("msg.freecam.openTripod").append("" + activeTripod
                    % GLFW.GLFW_KEY_0), true);
        }
    }

    private static void onDisableTripod() {
        getTripodsForDimension().put(activeTripod, new FreecamPosition(freeCamera));
        onDisable();

        if (MC.player != null) {
            if (FreecamConfig.NOTIFY_TRIPOD.get()) {
                MC.player.displayClientMessage(Component.translatable("msg.freecam.closeTripod").append("" + activeTripod % GLFW.GLFW_KEY_0), true);
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
        freeCamera.despawn();
        freeCamera.input = new Input();
        freeCamera = null;

        if (MC.player != null) {
            MC.player.input = new KeyboardInput(MC.options);
        }
    }

    public static void resetCamera(int keyCode) {
        if (tripodEnabled && activeTripod == keyCode && freeCamera != null) {
            freeCamera.copyPosition(MC.player);
        } else {
            getTripodsForDimension().put(keyCode, null);
        }

        if (FreecamConfig.NOTIFY_TRIPOD.get()) {
            MC.player.displayClientMessage(Component.translatable("msg.freecam.tripodReset").append("" + keyCode % GLFW.GLFW_KEY_0), true);
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
        switch (MC.level.dimensionTypeId().location().getPath()) {
            case "the_nether":
                result = nether_tripods;
                break;
            case "the_end":
                result = end_tripods;
                break;
            default:
                result = overworld_tripods;
                break;
        }
        return result;
    }

    public static boolean isEnabled() {
        return freecamEnabled || tripodEnabled;
    }

    public static boolean isPlayerControlEnabled() {
        return playerControlEnabled;
    }

    public static boolean canUseCheats() {
        return MC.player.hasPermissions(2) || MC.player.isCreative() || MC.isSingleplayer();
    }
}
