package net.xolt.freecam.config;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.CycleOption;
import net.minecraft.client.ProgressOption;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.FormattedCharSequence;
import net.xolt.freecam.config.FreecamConfig;

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
        super(new TranslatableComponent("text.freecam.configScreen.title"));
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

        CycleOption<FreecamConfig.FlightMode> flightMode = CycleOption.create(
                "text.freecam.configScreen.option.flightMode",
                FreecamConfig.FlightMode.values(),
                (option) -> new TranslatableComponent(option.getKey()),
                (option) -> (FreecamConfig.FlightMode) FreecamConfig.FLIGHT_MODE.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.FLIGHT_MODE.set(pValue)
        );
        flightMode.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.flightMode.tooltip"), 200));
        this.optionsList.addBig(flightMode);

        CycleOption<FreecamConfig.InteractionMode> interactionMode = CycleOption.create(
                "text.freecam.configScreen.option.interactionMode",
                FreecamConfig.InteractionMode.values(),
                (option) -> new TranslatableComponent(option.getKey()),
                (option) -> (FreecamConfig.InteractionMode) FreecamConfig.INTERACTION_MODE.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.INTERACTION_MODE.set(pValue)
        );
        interactionMode.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.interactionMode.tooltip"), 200));
        this.optionsList.addBig(interactionMode);

        ProgressOption horizontalSpeed = new ProgressOption(
                "text.freecam.configScreen.option.horizontalSpeed",
                0.0,
                10.0,
                0.1F,
                (option) -> FreecamConfig.HORIZONTAL_SPEED.get(),
                (option, value) -> {
                    if (Math.abs((Math.round(value * 1000.0) / 1000.0) - FreecamConfig.HORIZONTAL_SPEED.get()) >= 0.095) {
                        FreecamConfig.HORIZONTAL_SPEED.set(Math.round(value * 10.0) / 10.0);
                    }
                },
                (option, pOption) -> new TranslatableComponent("text.freecam.configScreen.option.horizontalSpeed").append(": " + FreecamConfig.HORIZONTAL_SPEED.get()),
                (mc) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.horizontalSpeed.tooltip"), 200)
        );
        this.optionsList.addBig(horizontalSpeed);

        ProgressOption verticalSpeed = new ProgressOption(
                "text.freecam.configScreen.option.verticalSpeed",
                0.0,
                10.0,
                0.1F,
                (option) -> FreecamConfig.VERTICAL_SPEED.get(),
                (option, value) -> {
                    if (Math.abs((Math.round(value * 1000.0) / 1000.0) - FreecamConfig.VERTICAL_SPEED.get()) >= 0.095) {
                        FreecamConfig.VERTICAL_SPEED.set(Math.round(value * 10.0) / 10.0);
                    }
                },
                (option, pOption) -> new TranslatableComponent("text.freecam.configScreen.option.verticalSpeed").append(": " + FreecamConfig.VERTICAL_SPEED.get()),
                (mc) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.verticalSpeed.tooltip"), 200)
        );
        this.optionsList.addBig(verticalSpeed);

        CycleOption<Boolean> noClip = CycleOption.createOnOff(
                "text.freecam.configScreen.option.noClip",
                (option) -> FreecamConfig.NO_CLIP.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.NO_CLIP.set(pValue)
        );
        noClip.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.noClip.tooltip"), 200));
        this.optionsList.addBig(noClip);

        CycleOption<Boolean> disableOnDamage = CycleOption.createOnOff(
                "text.freecam.configScreen.option.disableOnDamage",
                (option) -> FreecamConfig.DISABLE_ON_DAMAGE.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.DISABLE_ON_DAMAGE.set(pValue)
        );
        disableOnDamage.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.disableOnDamage.tooltip"), 200));
        this.optionsList.addBig(disableOnDamage);

        CycleOption<Boolean> allowInteract = CycleOption.createOnOff(
                "text.freecam.configScreen.option.allowInteract",
                (option) -> FreecamConfig.ALLOW_INTERACT.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.ALLOW_INTERACT.set(pValue)
        );
        allowInteract.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.allowInteract.tooltip"), 200));
        this.optionsList.addBig(allowInteract);

        CycleOption<Boolean> freezePlayer = CycleOption.createOnOff(
                "text.freecam.configScreen.option.freezePlayer",
                (option) -> FreecamConfig.FREEZE_PLAYER.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.FREEZE_PLAYER.set(pValue)
        );
        freezePlayer.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.freezePlayer.tooltip"), 200));
        this.optionsList.addBig(freezePlayer);

        CycleOption<Boolean> showPlayer = CycleOption.createOnOff(
                "text.freecam.configScreen.option.showPlayer",
                (option) -> FreecamConfig.SHOW_PLAYER.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.SHOW_PLAYER.set(pValue)
        );
        showPlayer.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.showPlayer.tooltip"), 200));
        this.optionsList.addBig(showPlayer);

        CycleOption<Boolean> showHand = CycleOption.createOnOff(
                "text.freecam.configScreen.option.showHand",
                (option) -> FreecamConfig.SHOW_HAND.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.SHOW_HAND.set(pValue)
        );
        showHand.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.showHand.tooltip"), 200));
        this.optionsList.addBig(showHand);

        CycleOption<Boolean> notifyFreecam = CycleOption.createOnOff(
                "text.freecam.configScreen.option.notifyFreecam",
                (option) -> FreecamConfig.NOTIFY_FREECAM.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.NOTIFY_FREECAM.set(pValue)
        );
        notifyFreecam.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.notifyFreecam.tooltip"), 200));
        this.optionsList.addBig(notifyFreecam);

        CycleOption<Boolean> notifyTripod = CycleOption.createOnOff(
                "text.freecam.configScreen.option.notifyTripod",
                (option) -> FreecamConfig.NOTIFY_TRIPOD.get(),
                (pOptions, pOption, pValue) -> FreecamConfig.NOTIFY_TRIPOD.set(pValue)
        );
        notifyTripod.setTooltip((mc) -> (value) -> MC.font.split(new TranslatableComponent("text.freecam.configScreen.option.notifyTripod.tooltip"), 200));
        this.optionsList.addBig(notifyTripod);

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
