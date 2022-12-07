package net.xolt.freecam;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.OptionSlider;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.client.settings.BooleanOption;
import net.minecraft.client.settings.IteratableOption;
import net.minecraft.client.settings.SliderPercentageOption;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.xolt.freecam.config.FreecamConfig;

import java.util.Optional;

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

  private OptionsRowList optionsRowList;

  public ConfigScreen(Screen previous) {
    super(new TranslationTextComponent("text.freecam.configScreen.title"));
    this.previous = previous;
  }

  @Override
  protected void init() {
    this.optionsRowList = new OptionsRowList(
        this.minecraft, this.width, this.height,
        OPTIONS_LIST_TOP_HEIGHT,
        this.height - OPTIONS_LIST_BOTTOM_OFFSET,
        OPTIONS_LIST_ITEM_HEIGHT
    );

    IteratableOption flightMode = new IteratableOption(
        "Flight Mode",
        (unused, newValue) -> FreecamConfig.FLIGHT_MODE.set(FreecamConfig.FlightMode.values()[(((FreecamConfig.FlightMode)FreecamConfig.FLIGHT_MODE.get()).ordinal() + newValue) % FreecamConfig.FlightMode.values().length]),
        (unused, option) -> new StringTextComponent("Flight Mode: " + ((FreecamConfig.FlightMode)FreecamConfig.FLIGHT_MODE.get()).getKey())
    );
    flightMode.setTooltip(MC.font.split(new StringTextComponent("The type of flight used by freecam."), 200));
    this.optionsRowList.addBig(flightMode);

    IteratableOption interactionMode = new IteratableOption(
        "Interaction Mode",
        (unused, newValue) -> FreecamConfig.INTERACTION_MODE.set(FreecamConfig.InteractionMode.values()[(((FreecamConfig.InteractionMode)FreecamConfig.INTERACTION_MODE.get()).ordinal() + newValue) % FreecamConfig.InteractionMode.values().length]),
        (unused, option) -> new StringTextComponent("Interaction Mode: " + ((FreecamConfig.InteractionMode)FreecamConfig.INTERACTION_MODE.get()).getKey())
    );
    interactionMode.setTooltip(MC.font.split(new StringTextComponent("The source of block/entity interactions."), 200));
    this.optionsRowList.addBig(interactionMode);

    SliderPercentageOption horizontalSpeed = new SliderPercentageOption(
        "Horizontal Speed",
        0.0, 10.0, 0.1F,
        unused -> FreecamConfig.HORIZONTAL_SPEED.get(),
        (unused, newValue) -> {
          if (Math.abs((Math.round(newValue * 1000.0) / 1000.0) - FreecamConfig.HORIZONTAL_SPEED.get()) >= 0.095) {
            FreecamConfig.HORIZONTAL_SPEED.set(Math.round(newValue * 10.0) / 10.0);
          }
        },
        (gs, option) -> new StringTextComponent("Horizontal Speed: " + option.get(gs))
    );
    horizontalSpeed.setTooltip(MC.font.split(new StringTextComponent("The horizontal speed of freecam."), 200));
    this.optionsRowList.addBig(horizontalSpeed);

    SliderPercentageOption verticalSpeed = new SliderPercentageOption(
        "Vertical Speed",
        0.0, 10.0, 0.1F,
        unused -> FreecamConfig.VERTICAL_SPEED.get(),
        (unused, newValue) -> {
          if (Math.abs((Math.round(newValue * 1000.0) / 1000.0) - FreecamConfig.VERTICAL_SPEED.get()) >= 0.095) {
            FreecamConfig.VERTICAL_SPEED.set(Math.round(newValue * 10.0) / 10.0);
          }
        },
        (gs, option) -> new StringTextComponent("Vertical Speed: " + option.get(gs))
    );
    verticalSpeed.setTooltip(MC.font.split(new StringTextComponent("The vertical speed of freecam."), 200));
    this.optionsRowList.addBig(verticalSpeed);

    BooleanOption noClip = new BooleanOption(
        "No Clip",
        unused -> FreecamConfig.NO_CLIP.get(),
        (unused, newValue) -> FreecamConfig.NO_CLIP.set(newValue)
    );
    noClip.setTooltip(MC.font.split(new StringTextComponent("Whether you can travel through blocks in freecam."), 200));
    this.optionsRowList.addBig(noClip);

    BooleanOption disableOnDamage = new BooleanOption(
        "Disable on Damage",
        unused -> FreecamConfig.DISABLE_ON_DAMAGE.get(),
        (unused, newValue) -> FreecamConfig.DISABLE_ON_DAMAGE.set(newValue)
    );
    disableOnDamage.setTooltip(MC.font.split(new StringTextComponent("Disables freecam when damage is received."), 200));
    this.optionsRowList.addBig(disableOnDamage);

    BooleanOption freezePlayer = new BooleanOption(
        "Freeze Player",
        unused -> FreecamConfig.FREEZE_PLAYER.get(),
        (unused, newValue) -> FreecamConfig.FREEZE_PLAYER.set(newValue)
    );
    freezePlayer.setTooltip(MC.font.split(new StringTextComponent("Prevents player movement while freecam is active.\n\u00A7cWARNING: Multiplayer usage not advised."), 200));
    this.optionsRowList.addBig(freezePlayer);

    BooleanOption allowInteract = new BooleanOption(
        "Allow Interaction",
        unused -> FreecamConfig.ALLOW_INTERACT.get(),
        (unused, newValue) -> FreecamConfig.ALLOW_INTERACT.set(newValue)
    );
    allowInteract.setTooltip(MC.font.split(new StringTextComponent("Whether you can interact with blocks/entities in freecam.\n\u00A7cWARNING: Multiplayer usage not advised."), 200));
    this.optionsRowList.addBig(allowInteract);

    BooleanOption showPlayer = new BooleanOption(
        "Show Player",
        unused -> FreecamConfig.SHOW_PLAYER.get(),
        (unused, newValue) -> FreecamConfig.SHOW_PLAYER.set(newValue)
    );
    showPlayer.setTooltip(MC.font.split(new StringTextComponent("Shows your player in its original position."), 200));
    this.optionsRowList.addBig(showPlayer);

    BooleanOption showHand = new BooleanOption(
        "Show Hand",
        unused -> FreecamConfig.SHOW_HAND.get(),
        (unused, newValue) -> FreecamConfig.SHOW_HAND.set(newValue)
    );
    showHand.setTooltip(MC.font.split(new StringTextComponent("Whether you can see your hand in freecam."), 200));
    this.optionsRowList.addBig(showHand);

    BooleanOption notifyFreecam = new BooleanOption(
        "Freecam Notifications",
        unused -> FreecamConfig.NOTIFY_FREECAM.get(),
        (unused, newValue) -> FreecamConfig.NOTIFY_FREECAM.set(newValue)
    );
    notifyFreecam.setTooltip(MC.font.split(new StringTextComponent("Notifies you when entering/exiting freecam."), 200));
    this.optionsRowList.addBig(notifyFreecam);

    BooleanOption notifyPersistent = new BooleanOption(
        "Tripod Notifications",
        unused -> FreecamConfig.NOTIFY_PERSISTENT.get(),
        (unused, newValue) -> FreecamConfig.NOTIFY_PERSISTENT.set(newValue)
    );
    notifyPersistent.setTooltip(MC.font.split(new StringTextComponent("Notifies you when entering/exiting tripod cameras."), 200));
    this.optionsRowList.addBig(notifyPersistent);

    this.addButton(new Button(
        (this.width - BUTTON_WIDTH) / 2,
        this.height - DONE_BUTTON_TOP_OFFSET,
        BUTTON_WIDTH, BUTTON_HEIGHT,
        new StringTextComponent("Done"),
        button -> this.onClose()
    ));

    this.children.add(this.optionsRowList);
  }

  @Override
  public void onClose() {
    this.minecraft.setScreen(previous);
  }

  @Override
  public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(matrixStack);
    this.optionsRowList.render(matrixStack, mouseX, mouseY, partialTicks);
    Optional<Widget> hoveredButton = optionsRowList.getMouseOver(mouseX, mouseY);
    if (hoveredButton.isPresent()) {
      if (hoveredButton.get() instanceof OptionButton) {
        renderToolTip(matrixStack, ((OptionButton)hoveredButton.get()).getTooltip().get(), mouseX, mouseY, null);
      } else if (hoveredButton.get() instanceof OptionSlider) {
        renderToolTip(matrixStack, ((OptionSlider)hoveredButton.get()).getTooltip().get(), mouseX, mouseY, null);
      }
    }
    drawCenteredString(matrixStack, this.font, this.title.getString(),
        this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
    super.render(matrixStack, mouseX, mouseY, partialTicks);
  }
}
