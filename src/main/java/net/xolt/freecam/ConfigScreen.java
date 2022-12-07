package net.xolt.freecam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.xolt.freecam.config.FreecamConfig;

import java.util.Arrays;
import java.util.List;

import static net.minecraft.client.gui.screens.OptionsSubScreen.tooltipAt;
import static net.xolt.freecam.Freecam.MC;

public class ConfigScreen extends Screen {
  private static final int TITLE_HEIGHT = 8;
  private static final int OPTIONS_LIST_TOP_HEIGHT = 24;
  private static final int OPTIONS_LIST_BOTTOM_OFFSET = 32;
  private static final int OPTIONS_LIST_ITEM_HEIGHT = 25;
  private static final int BUTTON_WIDTH = 150;
  private static final int BUTTON_HEIGHT = 20;
  private static final int DONE_BUTTON_TOP_OFFSET = 26;

  private final Screen previous;

  private OptionsList optionsList;

  public ConfigScreen(Screen previous) {
    super(Component.translatable("text.freecam.configScreen.title"));
    this.previous = previous;
  }

  @Override
  protected void init() {
    this.optionsList = new OptionsList(
        this.minecraft, this.width, this.height,
        OPTIONS_LIST_TOP_HEIGHT,
        this.height - OPTIONS_LIST_BOTTOM_OFFSET,
        OPTIONS_LIST_ITEM_HEIGHT
    );

    OptionInstance<FreecamConfig.FlightMode> flightMode = new OptionInstance<FreecamConfig.FlightMode>(
        "Flight Mode",
        (value) -> (value2) -> MC.font.split(Component.literal("The type of flight used by freecam."), 200),
        (unused, option) -> Component.literal(option.getKey()),
        new OptionInstance.Enum<>(Arrays.asList(FreecamConfig.FlightMode.values()), Codec.INT.xmap(FreecamConfig.FlightMode::byId, FreecamConfig.FlightMode::getId)),
        (FreecamConfig.FlightMode)FreecamConfig.FLIGHT_MODE.get(),
        (newValue) -> FreecamConfig.FLIGHT_MODE.set(newValue)
    );
    this.optionsList.addBig(flightMode);

    OptionInstance<FreecamConfig.InteractionMode> interactionMode = new OptionInstance<FreecamConfig.InteractionMode>(
        "Interaction Mode",
        (value) -> (value2) -> MC.font.split(Component.literal("The source of block/entity interactions."), 200),
        (unused, option) -> Component.literal(option.getKey()),
        new OptionInstance.Enum<>(Arrays.asList(FreecamConfig.InteractionMode.values()), Codec.INT.xmap(FreecamConfig.InteractionMode::byId, FreecamConfig.InteractionMode::getId)),
        (FreecamConfig.InteractionMode)FreecamConfig.INTERACTION_MODE.get(),
        (newValue) -> FreecamConfig.INTERACTION_MODE.set(newValue)
    );
    this.optionsList.addBig(interactionMode);

    OptionInstance<Double> horizontalSpeed = new OptionInstance<>(
        "Horizontal Speed",
        (value) -> (value2) -> MC.font.split(Component.literal("The horizontal speed of freecam."), 200),
        (unused, option) -> Component.literal("Horizontal Speed: " + FreecamConfig.HORIZONTAL_SPEED.get()),
        OptionInstance.UnitDouble.INSTANCE,
        FreecamConfig.HORIZONTAL_SPEED.get() / 10,
        (value) -> {
          if (Math.abs(value - (FreecamConfig.HORIZONTAL_SPEED.get() / 10)) >= 0.01)
            FreecamConfig.HORIZONTAL_SPEED.set(Math.round(value * 100.0) / 10.0);
        }
    );
    this.optionsList.addBig(horizontalSpeed);

    OptionInstance<Double> verticalSpeed = new OptionInstance<>(
        "Vertical Speed",
        (value) -> (value2) -> MC.font.split(Component.literal("The vertical speed of freecam."), 200),
        (unused, option) -> Component.literal("Vertical Speed: " + FreecamConfig.VERTICAL_SPEED.get()),
        OptionInstance.UnitDouble.INSTANCE,
        FreecamConfig.VERTICAL_SPEED.get() / 10,
        (value) -> {
          if (Math.abs(value - (FreecamConfig.VERTICAL_SPEED.get() / 10)) >= 0.01)
            FreecamConfig.VERTICAL_SPEED.set(Math.round(value * 100.0) / 10.0);
        }
    );
    this.optionsList.addBig(verticalSpeed);

    OptionInstance<Boolean> noClip = OptionInstance.createBoolean(
        "No Clip",
        (value) -> (value2) -> MC.font.split(Component.literal("Whether you can travel through blocks in freecam."), 200),
        FreecamConfig.NO_CLIP.get(),
        (value) -> FreecamConfig.NO_CLIP.set(value)
    );
    this.optionsList.addBig(noClip);

    OptionInstance<Boolean> disableOnDamage = OptionInstance.createBoolean(
        "Disable on Damage",
        (value) -> (value2) -> MC.font.split(Component.literal("Disables freecam when damage is received."), 200),
        FreecamConfig.DISABLE_ON_DAMAGE.get(),
        (value) -> FreecamConfig.DISABLE_ON_DAMAGE.set(value)
    );
    this.optionsList.addBig(disableOnDamage);

    OptionInstance<Boolean> freezePlayer = OptionInstance.createBoolean(
        "Freeze Player",
        (value) -> (value2) -> MC.font.split(Component.literal("Prevents player movement while freecam is active.\n\u00A7cWARNING: Multiplayer usage not advised."), 200),
        FreecamConfig.FREEZE_PLAYER.get(),
        (value) -> FreecamConfig.FREEZE_PLAYER.set(value)
    );
    this.optionsList.addBig(freezePlayer);

    OptionInstance<Boolean> allowInteract = OptionInstance.createBoolean(
        "Allow Interaction",
        (value) -> (value2) -> MC.font.split(Component.literal("Whether you can interact with blocks/entities in freecam.\n\u00A7cWARNING: Multiplayer usage not advised."), 200),
        FreecamConfig.ALLOW_INTERACT.get(),
        (value) -> FreecamConfig.ALLOW_INTERACT.set(value)
    );
    this.optionsList.addBig(allowInteract);

    OptionInstance<Boolean> showPlayer = OptionInstance.createBoolean(
        "Show Player",
        (value) -> (value2) -> MC.font.split(Component.literal("Shows your player in its original position."), 200),
        FreecamConfig.SHOW_PLAYER.get(),
        (value) -> FreecamConfig.SHOW_PLAYER.set(value)
    );
    this.optionsList.addBig(showPlayer);

    OptionInstance<Boolean> showHand = OptionInstance.createBoolean(
        "Show Hand",
        (value) -> (value2) -> MC.font.split(Component.literal("Whether you can see your hand in freecam."), 200),
        FreecamConfig.SHOW_HAND.get(),
        (value) -> FreecamConfig.SHOW_HAND.set(value)
    );
    this.optionsList.addBig(showHand);

    OptionInstance<Boolean> notifyFreecam = OptionInstance.createBoolean(
        "Freecam Notifications",
        (value) -> (value2) -> MC.font.split(Component.literal("Notifies you when entering/exiting freecam."), 200),
        FreecamConfig.NOTIFY_FREECAM.get(),
        (value) -> FreecamConfig.NOTIFY_FREECAM.set(value)
    );
    this.optionsList.addBig(notifyFreecam);

    OptionInstance<Boolean> notifyPersistent = OptionInstance.createBoolean(
        "Tripod Notifications",
        (value) -> (value2) -> MC.font.split(Component.literal("Notifies you when entering/exiting tripod cameras."), 200),
        FreecamConfig.NOTIFY_PERSISTENT.get(),
        (value) -> FreecamConfig.NOTIFY_PERSISTENT.set(value)
    );
    this.optionsList.addBig(notifyPersistent);

    this.addWidget(optionsList);

    this.addRenderableWidget(new Button(
        (this.width - BUTTON_WIDTH) / 2,
        this.height - DONE_BUTTON_TOP_OFFSET,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        CommonComponents.GUI_DONE,
        button -> this.onClose()
    ));
  }

  @Override
  public void onClose() {
    this.minecraft.setScreen(previous);
  }

  @Override
  public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
    this.renderBackground(pPoseStack);
    this.optionsList.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    drawCenteredString(pPoseStack, this.font, this.title, this.width / 2, TITLE_HEIGHT, 16777215);
    super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
    List<FormattedCharSequence> list = tooltipAt(this.optionsList, pMouseX, pMouseY);
    this.renderTooltip(pPoseStack, list, pMouseX, pMouseY);
  }
}
