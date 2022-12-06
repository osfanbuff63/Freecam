package net.xolt.freecam;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.TooltipAccessor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import net.minecraftforge.server.command.TextComponentHelper;
import net.xolt.freecam.config.FreecamConfig;

import java.util.ArrayList;
import java.util.List;

import static net.xolt.freecam.Freecam.MC;

public class ConfigScreen extends Screen {
  private static final int buttonWidth = 150;
  private static final int buttonHeight = 20;
  private static final int buttonSpacing = 4;

  private final ArrayList<AbstractWidget> buttons = new ArrayList<>();
  private final Screen previous;

  public ConfigScreen(Screen previous) {
    super(Component.literal("Freecam"));
    this.previous = previous;
  }

  @Override
  protected void init() {


    buttons.add(CycleButton.builder((FreecamConfig.FlightMode value) -> Component.literal(
        switch (value) {
          case DEFAULT -> "Default";
          case CREATIVE -> "Creative";
        }))
        .withTooltip((value) -> MC.font.split(Component.literal("The type of flight used by freecam."), 200))
        .withValues(FreecamConfig.FlightMode.DEFAULT, FreecamConfig.FlightMode.CREATIVE)
        .withInitialValue((FreecamConfig.FlightMode) FreecamConfig.FLIGHT_MODE.get())
        .create(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height / 6, buttonWidth, buttonHeight,
            Component.literal("Flight Mode"), (button, value) -> FreecamConfig.FLIGHT_MODE.set(value)));

    this.addRenderableWidget(buttons.get(0));

    buttons.add(CycleButton.builder((FreecamConfig.InteractionMode value) -> Component.literal(
        switch (value) {
          case CAMERA -> "Camera";
          case PLAYER -> "Player";
        }))
        .withTooltip((value) -> MC.font.split(Component.literal("The source of block/entity interactions."), 200))
        .withValues(FreecamConfig.InteractionMode.CAMERA, FreecamConfig.InteractionMode.PLAYER)
        .withInitialValue((FreecamConfig.InteractionMode) FreecamConfig.INTERACTION_MODE.get())
        .create(this.width / 2 + buttonSpacing / 2, this.height / 6, buttonWidth, buttonHeight,
            Component.literal("Interaction Mode"), (button, value) -> FreecamConfig.INTERACTION_MODE.set(value)));

    this.addRenderableWidget(buttons.get(1));

    List<FormattedCharSequence> horizontalTooltip = MC.font.split(Component.literal("The horizontal speed of freecam."), 200);
    buttons.add(new TooltipSlider(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height / 6 + buttonHeight + buttonSpacing, buttonWidth, buttonHeight, Component.literal("Horizontal Speed: "), Component.literal(""), horizontalTooltip, 0, 10, FreecamConfig.HORIZONTAL_SPEED.get(), 0.1, 1, true) {
      @Override protected void applyValue() {
        FreecamConfig.HORIZONTAL_SPEED.set(getValue());
      }
    });

    this.addRenderableWidget(buttons.get(2));

    List<FormattedCharSequence> verticalTooltip = MC.font.split(Component.literal("The vertical speed of freecam."), 200);
    buttons.add(new TooltipSlider(this.width / 2 + buttonSpacing / 2, this.height / 6 + buttonHeight + buttonSpacing, buttonWidth, buttonHeight, Component.literal("Vertical Speed: "), Component.literal(""), verticalTooltip, 0, 10, FreecamConfig.VERTICAL_SPEED.get(), 0.1, 1, true) {
      @Override protected void applyValue() {
        FreecamConfig.VERTICAL_SPEED.set(getValue());
      }
    });

    this.addRenderableWidget(buttons.get(3));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.NO_CLIP.get())
        .withTooltip((value) -> MC.font.split(Component.literal("Whether you can travel through blocks in freecam."), 200))
        .create(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 2, buttonWidth, buttonHeight,
            Component.literal("No Clip"), (button, value) -> FreecamConfig.NO_CLIP.set(value)));

    this.addRenderableWidget(buttons.get(4));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.DISABLE_ON_DAMAGE.get())
        .withTooltip((value) -> MC.font.split(Component.literal("Disables freecam when damage is received."), 200))
        .create(this.width / 2 + buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 2, buttonWidth, buttonHeight,
            Component.literal("Disable on Damage"), (button, value) -> FreecamConfig.DISABLE_ON_DAMAGE.set(value)));

    this.addRenderableWidget(buttons.get(5));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.FREEZE_PLAYER.get())
        .withTooltip((value) -> MC.font.split(Component.literal("""
            Prevents player movement while freecam is active.
            \u00A7cWARNING: Multiplayer usage not advised."""), 200))
        .create(this.width / 2 + buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 3, buttonWidth, buttonHeight,
            Component.literal("Freeze Player"), (button, value) -> FreecamConfig.FREEZE_PLAYER.set(value)));

    this.addRenderableWidget(buttons.get(6));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.ALLOW_INTERACT.get())
        .withTooltip((value) -> MC.font.split(Component.literal("""
            Whether you can interact with blocks/entities in freecam.
            \u00A7cWARNING: Multiplayer usage not advised."""), 200))
        .create(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 3, buttonWidth, buttonHeight,
            Component.literal("Allow Interaction"), (button, value) -> FreecamConfig.ALLOW_INTERACT.set(value)));

    this.addRenderableWidget(buttons.get(7));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.SHOW_PLAYER.get())
        .withTooltip((value) -> MC.font.split(Component.literal("Shows your player in its original position."), 200))
        .create(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 4, buttonWidth, buttonHeight,
            Component.literal("Show Player"), (button, value) -> FreecamConfig.SHOW_PLAYER.set(value)));

    this.addRenderableWidget(buttons.get(8));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.SHOW_HAND.get())
        .withTooltip((value) -> MC.font.split(Component.literal("Whether you can see your hand in freecam."), 200))
        .create(this.width / 2 + buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 4, buttonWidth, buttonHeight,
            Component.literal("Show Hand"), (button, value) -> FreecamConfig.SHOW_HAND.set(value)));

    this.addRenderableWidget(buttons.get(9));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.NOTIFY_FREECAM.get())
        .withTooltip((value) -> MC.font.split(Component.literal("Notifies you when entering/exiting freecam."), 200))
        .create(this.width / 2 - buttonWidth - buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 5, buttonWidth, buttonHeight,
            Component.literal("Freecam Notifications"), (button, value) -> FreecamConfig.NOTIFY_FREECAM.set(value)));

    this.addRenderableWidget(buttons.get(10));

    buttons.add(CycleButton.onOffBuilder(FreecamConfig.NOTIFY_PERSISTENT.get())
        .withTooltip((value) -> MC.font.split(Component.literal("Notifies you when entering/exiting tripod cameras."), 200))
        .create(this.width / 2 + buttonSpacing / 2, this.height / 6 + (buttonHeight + buttonSpacing) * 5, buttonWidth, buttonHeight,
            Component.literal("Tripod Notifications"), (button, value) -> FreecamConfig.NOTIFY_PERSISTENT.set(value)));

    this.addRenderableWidget(buttons.get(11));

    this.addRenderableWidget(new Button(
        this.width / 2 - 100, this.height - 27, 200, 20,
        CommonComponents.GUI_DONE, button -> this.onClose()));
  }

  @Override
  public void onClose() {
    this.minecraft.setScreen(previous);
  }

  @Override
  public void render(PoseStack poseStack, int i, int j, float f) {
    this.renderDirtBackground(0);
    drawCenteredString(poseStack, this.font, this.title, this.width / 2, 15, 0xFFFFFF);
    super.render(poseStack, i, j, f);
    for (AbstractWidget button : buttons) {
      if (button.isMouseOver(i, j)) {
        renderTooltip(poseStack, ((TooltipAccessor)button).getTooltip(), i, j);
      }
    }
  }

  @Override
  public void removed() {
  }

  private static class TooltipSlider extends ForgeSlider implements TooltipAccessor {
    private List<FormattedCharSequence> tooltip;

    public TooltipSlider(int x, int y, int width, int height, Component prefix, Component suffix, List<FormattedCharSequence> tooltip, double minValue, double maxValue, double currentValue, double stepSize, int precision, boolean drawString) {
      super(x, y, width, height, prefix, suffix, minValue, maxValue, currentValue, stepSize, precision, drawString);
      this.tooltip = tooltip;
    }

    @Override public List<FormattedCharSequence> getTooltip() {
      return tooltip;
    }
  }
}
