package net.xolt.freecam.config;

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
                "text.freecam.configScreen.option.flightMode",
                (unused, newValue) -> FreecamConfig.FLIGHT_MODE.set(FreecamConfig.FlightMode.values()[(((FreecamConfig.FlightMode) FreecamConfig.FLIGHT_MODE.get()).ordinal() + newValue) % FreecamConfig.FlightMode.values().length]),
                (unused, option) -> new TranslationTextComponent("text.freecam.configScreen.option.flightMode").append(": ").append(new TranslationTextComponent(((FreecamConfig.FlightMode) FreecamConfig.FLIGHT_MODE.get()).getKey()))
        );
        flightMode.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.flightMode.tooltip"), 200));
        this.optionsRowList.addBig(flightMode);

        IteratableOption perspective = new IteratableOption(
                "text.freecam.configScreen.option.perspective",
                (unused, newValue) -> FreecamConfig.PERSPECTIVE.set(FreecamConfig.Perspective.values()[(((FreecamConfig.Perspective) FreecamConfig.PERSPECTIVE.get()).ordinal() + newValue) % FreecamConfig.Perspective.values().length]),
                (unused, option) -> new TranslationTextComponent("text.freecam.configScreen.option.perspective").append(": ").append(new TranslationTextComponent(((FreecamConfig.Perspective) FreecamConfig.PERSPECTIVE.get()).getKey()))
        );
        perspective.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.perspective.tooltip"), 200));
        this.optionsRowList.addBig(perspective);

        IteratableOption interactionMode = new IteratableOption(
                "text.freecam.configScreen.option.interactionMode",
                (unused, newValue) -> FreecamConfig.INTERACTION_MODE.set(FreecamConfig.InteractionMode.values()[(((FreecamConfig.InteractionMode) FreecamConfig.INTERACTION_MODE.get()).ordinal() + newValue) % FreecamConfig.InteractionMode.values().length]),
                (unused, option) -> new TranslationTextComponent("text.freecam.configScreen.option.interactionMode").append(": ").append(new TranslationTextComponent(((FreecamConfig.InteractionMode) FreecamConfig.INTERACTION_MODE.get()).getKey()))
        );
        interactionMode.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.interactionMode.tooltip"), 200));
        this.optionsRowList.addBig(interactionMode);

        SliderPercentageOption horizontalSpeed = new SliderPercentageOption(
                "text.freecam.configScreen.option.horizontalSpeed",
                0.0, 10.0, 0.1F,
                unused -> FreecamConfig.HORIZONTAL_SPEED.get(),
                (unused, newValue) -> {
                    if (Math.abs((Math.round(newValue * 1000.0) / 1000.0) - FreecamConfig.HORIZONTAL_SPEED.get()) >= 0.095) {
                        FreecamConfig.HORIZONTAL_SPEED.set(Math.round(newValue * 10.0) / 10.0);
                    }
                },
                (gs, option) -> new TranslationTextComponent("text.freecam.configScreen.option.horizontalSpeed").append(": " + option.get(gs))
        );
        horizontalSpeed.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.horizontalSpeed.tooltip"), 200));
        this.optionsRowList.addBig(horizontalSpeed);

        SliderPercentageOption verticalSpeed = new SliderPercentageOption(
                "text.freecam.configScreen.option.verticalSpeed",
                0.0, 10.0, 0.1F,
                unused -> FreecamConfig.VERTICAL_SPEED.get(),
                (unused, newValue) -> {
                    if (Math.abs((Math.round(newValue * 1000.0) / 1000.0) - FreecamConfig.VERTICAL_SPEED.get()) >= 0.095) {
                        FreecamConfig.VERTICAL_SPEED.set(Math.round(newValue * 10.0) / 10.0);
                    }
                },
                (gs, option) -> new TranslationTextComponent("text.freecam.configScreen.option.verticalSpeed").append(": " + option.get(gs))
        );
        verticalSpeed.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.verticalSpeed.tooltip"), 200));
        this.optionsRowList.addBig(verticalSpeed);

        BooleanOption noClip = new BooleanOption(
                "text.freecam.configScreen.option.noClip",
                unused -> FreecamConfig.NO_CLIP.get(),
                (unused, newValue) -> FreecamConfig.NO_CLIP.set(newValue)
        );
        noClip.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.noClip.tooltip"), 200));
        this.optionsRowList.addBig(noClip);

        BooleanOption checkCollision = new BooleanOption(
                "text.freecam.configScreen.option.checkCollision",
                unused -> FreecamConfig.CHECK_COLLISION.get(),
                (unused, newValue) -> FreecamConfig.CHECK_COLLISION.set(newValue)
        );
        checkCollision.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.checkCollision.tooltip"), 200));
        this.optionsRowList.addBig(checkCollision);

        BooleanOption disableOnDamage = new BooleanOption(
                "text.freecam.configScreen.option.disableOnDamage",
                unused -> FreecamConfig.DISABLE_ON_DAMAGE.get(),
                (unused, newValue) -> FreecamConfig.DISABLE_ON_DAMAGE.set(newValue)
        );
        disableOnDamage.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.disableOnDamage.tooltip"), 200));
        this.optionsRowList.addBig(disableOnDamage);

        BooleanOption allowInteract = new BooleanOption(
                "text.freecam.configScreen.option.allowInteract",
                unused -> FreecamConfig.ALLOW_INTERACT.get(),
                (unused, newValue) -> FreecamConfig.ALLOW_INTERACT.set(newValue)
        );
        allowInteract.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.allowInteract.tooltip"), 200));
        this.optionsRowList.addBig(allowInteract);

        BooleanOption freezePlayer = new BooleanOption(
                "text.freecam.configScreen.option.freezePlayer",
                unused -> FreecamConfig.FREEZE_PLAYER.get(),
                (unused, newValue) -> FreecamConfig.FREEZE_PLAYER.set(newValue)
        );
        freezePlayer.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.freezePlayer.tooltip"), 200));
        this.optionsRowList.addBig(freezePlayer);

        BooleanOption showPlayer = new BooleanOption(
                "text.freecam.configScreen.option.showPlayer",
                unused -> FreecamConfig.SHOW_PLAYER.get(),
                (unused, newValue) -> FreecamConfig.SHOW_PLAYER.set(newValue)
        );
        showPlayer.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.showPlayer.tooltip"), 200));
        this.optionsRowList.addBig(showPlayer);

        BooleanOption showHand = new BooleanOption(
                "text.freecam.configScreen.option.showHand",
                unused -> FreecamConfig.SHOW_HAND.get(),
                (unused, newValue) -> FreecamConfig.SHOW_HAND.set(newValue)
        );
        showHand.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.showHand.tooltip"), 200));
        this.optionsRowList.addBig(showHand);

        BooleanOption notifyFreecam = new BooleanOption(
                "text.freecam.configScreen.option.notifyFreecam",
                unused -> FreecamConfig.NOTIFY_FREECAM.get(),
                (unused, newValue) -> FreecamConfig.NOTIFY_FREECAM.set(newValue)
        );
        notifyFreecam.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.notifyFreecam.tooltip"), 200));
        this.optionsRowList.addBig(notifyFreecam);

        BooleanOption notifyTripod = new BooleanOption(
                "text.freecam.configScreen.option.notifyTripod",
                unused -> FreecamConfig.NOTIFY_TRIPOD.get(),
                (unused, newValue) -> FreecamConfig.NOTIFY_TRIPOD.set(newValue)
        );
        notifyTripod.setTooltip(MC.font.split(new TranslationTextComponent("text.freecam.configScreen.option.notifyTripod.tooltip"), 200));
        this.optionsRowList.addBig(notifyTripod);

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
                renderToolTip(matrixStack, ((OptionButton) hoveredButton.get()).getTooltip().get(), mouseX, mouseY, null);
            } else if (hoveredButton.get() instanceof OptionSlider) {
                renderToolTip(matrixStack, ((OptionSlider) hoveredButton.get()).getTooltip().get(), mouseX, mouseY, null);
            }
        }
        drawCenteredString(matrixStack, this.font, this.title.getString(),
                this.width / 2, TITLE_HEIGHT, 0xFFFFFF);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}
