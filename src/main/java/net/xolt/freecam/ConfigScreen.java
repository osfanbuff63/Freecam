package net.xolt.freecam;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.xolt.freecam.config.FreecamConfig;

import java.util.Arrays;

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
        "text.freecam.configScreen.option.flightMode",
        (value2) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.flightMode.tooltip")),
        (unused, option) -> Component.translatable(option.getKey()),
        new OptionInstance.Enum<>(Arrays.asList(FreecamConfig.FlightMode.values()), Codec.INT.xmap(FreecamConfig.FlightMode::byId, FreecamConfig.FlightMode::getId)),
        (FreecamConfig.FlightMode)FreecamConfig.FLIGHT_MODE.get(),
        (newValue) -> FreecamConfig.FLIGHT_MODE.set(newValue)
    );
    this.optionsList.addBig(flightMode);

    OptionInstance<FreecamConfig.InteractionMode> interactionMode = new OptionInstance<FreecamConfig.InteractionMode>(
        "text.freecam.configScreen.option.interactionMode",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.interactionMode.tooltip")),
        (unused, option) -> Component.translatable(option.getKey()),
        new OptionInstance.Enum<>(Arrays.asList(FreecamConfig.InteractionMode.values()), Codec.INT.xmap(FreecamConfig.InteractionMode::byId, FreecamConfig.InteractionMode::getId)),
        (FreecamConfig.InteractionMode)FreecamConfig.INTERACTION_MODE.get(),
        (newValue) -> FreecamConfig.INTERACTION_MODE.set(newValue)
    );
    this.optionsList.addBig(interactionMode);

    OptionInstance<Double> horizontalSpeed = new OptionInstance<>(
        "text.freecam.configScreen.option.horizontalSpeed",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.horizontalSpeed.tooltip")),
        (unused, option) -> Component.translatable("text.freecam.configScreen.option.horizontalSpeed").append(": " + FreecamConfig.HORIZONTAL_SPEED.get()),
        OptionInstance.UnitDouble.INSTANCE,
        FreecamConfig.HORIZONTAL_SPEED.get() / 10,
        (value) -> {
          if (Math.abs(value - (FreecamConfig.HORIZONTAL_SPEED.get() / 10)) >= 0.01)
            FreecamConfig.HORIZONTAL_SPEED.set(Math.round(value * 100.0) / 10.0);
        }
    );
    this.optionsList.addBig(horizontalSpeed);

    OptionInstance<Double> verticalSpeed = new OptionInstance<>(
        "text.freecam.configScreen.option.verticalSpeed",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.verticalSpeed.tooltip")),
        (unused, option) -> Component.translatable("text.freecam.configScreen.option.verticalSpeed").append(": " + FreecamConfig.VERTICAL_SPEED.get()),
        OptionInstance.UnitDouble.INSTANCE,
        FreecamConfig.VERTICAL_SPEED.get() / 10,
        (value) -> {
          if (Math.abs(value - (FreecamConfig.VERTICAL_SPEED.get() / 10)) >= 0.01)
            FreecamConfig.VERTICAL_SPEED.set(Math.round(value * 100.0) / 10.0);
        }
    );
    this.optionsList.addBig(verticalSpeed);

    OptionInstance<Boolean> noClip = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.noClip",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.noClip.tooltip")),
        FreecamConfig.NO_CLIP.get(),
        (value) -> FreecamConfig.NO_CLIP.set(value)
    );
    this.optionsList.addBig(noClip);

    OptionInstance<Boolean> disableOnDamage = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.disableOnDamage",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.disableOnDamage.tooltip")),
        FreecamConfig.DISABLE_ON_DAMAGE.get(),
        (value) -> FreecamConfig.DISABLE_ON_DAMAGE.set(value)
    );
    this.optionsList.addBig(disableOnDamage);

    OptionInstance<Boolean> allowInteract = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.allowInteract",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.allowInteract.tooltip")),
        FreecamConfig.ALLOW_INTERACT.get(),
        (value) -> FreecamConfig.ALLOW_INTERACT.set(value)
    );
    this.optionsList.addBig(allowInteract);

    OptionInstance<Boolean> freezePlayer = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.freezePlayer",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.freezePlayer.tooltip")),
        FreecamConfig.FREEZE_PLAYER.get(),
        (value) -> FreecamConfig.FREEZE_PLAYER.set(value)
    );
    this.optionsList.addBig(freezePlayer);

    OptionInstance<Boolean> showPlayer = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.showPlayer",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.showPlayer.tooltip")),
        FreecamConfig.SHOW_PLAYER.get(),
        (value) -> FreecamConfig.SHOW_PLAYER.set(value)
    );
    this.optionsList.addBig(showPlayer);

    OptionInstance<Boolean> showHand = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.showHand",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.showHand.tooltip")),
        FreecamConfig.SHOW_HAND.get(),
        (value) -> FreecamConfig.SHOW_HAND.set(value)
    );
    this.optionsList.addBig(showHand);

    OptionInstance<Boolean> notifyFreecam = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.notifyFreecam",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.notifyFreecam.tooltip")),
        FreecamConfig.NOTIFY_FREECAM.get(),
        (value) -> FreecamConfig.NOTIFY_FREECAM.set(value)
    );
    this.optionsList.addBig(notifyFreecam);

    OptionInstance<Boolean> notifyTripod = OptionInstance.createBoolean(
        "text.freecam.configScreen.option.notifyTripod",
        (value) -> Tooltip.create(Component.translatable("text.freecam.configScreen.option.notifyTripod.tooltip")),
        FreecamConfig.NOTIFY_TRIPOD.get(),
        (value) -> FreecamConfig.NOTIFY_TRIPOD.set(value)
    );
    this.optionsList.addBig(notifyTripod);

    this.addWidget(optionsList);

    this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
      this.onClose();
    }).bounds((this.width - BUTTON_WIDTH) / 2,
        this.height - DONE_BUTTON_TOP_OFFSET,
        BUTTON_WIDTH, BUTTON_HEIGHT).build());
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
  }
}
