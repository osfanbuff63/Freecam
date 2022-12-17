package net.xolt.freecam.config;

import net.minecraft.util.Mth;
import net.minecraft.util.OptionEnum;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.Comparator;

public class FreecamConfig {
  public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
  public static final ForgeConfigSpec SPEC;

  public static final ForgeConfigSpec.EnumValue FLIGHT_MODE;
  public static final ForgeConfigSpec.EnumValue INTERACTION_MODE;
  public static final ForgeConfigSpec.DoubleValue HORIZONTAL_SPEED;
  public static final ForgeConfigSpec.DoubleValue VERTICAL_SPEED;
  public static final ForgeConfigSpec.BooleanValue NO_CLIP;
  public static final ForgeConfigSpec.BooleanValue FREEZE_PLAYER;
  public static final ForgeConfigSpec.BooleanValue ALLOW_INTERACT;
  public static final ForgeConfigSpec.BooleanValue DISABLE_ON_DAMAGE;
  public static final ForgeConfigSpec.BooleanValue SHOW_PLAYER;
  public static final ForgeConfigSpec.BooleanValue SHOW_HAND;
  public static final ForgeConfigSpec.BooleanValue NOTIFY_FREECAM;
  public static final ForgeConfigSpec.BooleanValue NOTIFY_TRIPOD;

  static {
    BUILDER.push("Freecam");

    FLIGHT_MODE = BUILDER.comment("The type of flight used by freecam.")
        .defineEnum("Flight Mode", FlightMode.DEFAULT);

    INTERACTION_MODE = BUILDER.comment("The source of block/entity interactions.")
        .defineEnum("Interaction Mode", InteractionMode.CAMERA);

    HORIZONTAL_SPEED = BUILDER.comment("The horizontal speed of freecam.")
        .defineInRange("Horizontal Speed", 1.0, 0.0, 10.0);

    VERTICAL_SPEED = BUILDER.comment("The vertical speed of freecam.")
        .defineInRange("Vertical Speed", 1.0, 0.0, 10.0);

    NO_CLIP = BUILDER.comment("Whether you can travel through blocks in freecam.")
        .define("No Clip", true);

    DISABLE_ON_DAMAGE = BUILDER.comment("Disables freecam when damage is received.")
        .define("Disable on Damage", true);

    ALLOW_INTERACT = BUILDER.comment("Whether you can interact with blocks/entities in freecam.\nWARNING: Multiplayer usage not advised.")
        .define("Allow Interaction", false);

    FREEZE_PLAYER = BUILDER.comment("Prevents player movement while freecam is active.\nWARNING: Multiplayer usage not advised.")
        .define("Freeze Player", false);

    SHOW_PLAYER = BUILDER.comment("Shows your player in its original position.")
        .define("Show Player", true);

    SHOW_HAND = BUILDER.comment("Whether you can see your hand in freecam.")
        .define("Show Hand", false);

    NOTIFY_FREECAM = BUILDER.comment("Notifies you when entering/exiting freecam.")
        .define("Freecam Notifications", true);

    NOTIFY_TRIPOD = BUILDER.comment("Notifies you when entering/exiting tripod cameras.")
        .define("Tripod Notifications", true);

    BUILDER.pop();
    SPEC = BUILDER.build();
  }

  public enum FlightMode implements OptionEnum {
    CREATIVE(0,"text.freecam.configScreen.option.flightMode.creative"),
    DEFAULT(1, "text.freecam.configScreen.option.flightMode.default");

    private static final FlightMode[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(FlightMode::getId)).toArray((size) -> {
      return new FlightMode[size];
    });
    private final int id;
    private final String key;

    FlightMode(int id, String key) {
      this.id = id;
      this.key = key;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public String getKey() {
      return key;
    }

    public static FlightMode byId(int pId) {
      return BY_ID[Mth.positiveModulo(pId, BY_ID.length)];
    }
  }

  public enum InteractionMode implements OptionEnum {
    CAMERA(0,"text.freecam.configScreen.option.interactionMode.camera"),
    PLAYER(1,"text.freecam.configScreen.option.interactionMode.player");

    private static final InteractionMode[] BY_ID = Arrays.stream(values()).sorted(Comparator.comparingInt(InteractionMode::getId)).toArray((size) -> {
      return new InteractionMode[size];
    });
    private final int id;
    private final String key;

    InteractionMode(int id, String name) {
      this.id = id;
      this.key = name;
    }

    @Override
    public int getId() {
      return id;
    }

    @Override
    public String getKey() {
      return key;
    }

    public static InteractionMode byId(int pId) {
      return BY_ID[Mth.positiveModulo(pId, BY_ID.length)];
    }
  }
}
