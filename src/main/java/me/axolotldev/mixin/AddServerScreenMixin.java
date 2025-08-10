package me.axolotldev.mixin;

import me.axolotldev.LunarstarConnector;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(AddServerScreen.class)
public class AddServerScreenMixin extends Screen implements ButtonWidget.PressAction {

    @Unique
    private static final String[] OFFICIAL = {"mc.lunarstar.axolotldev.me", "mc.lunarstar.axolotldev.me:25565"};

    @Unique
    private static final String ADDRESS = "lunarstar.lc.axurl.cc";

    @Shadow
    private TextFieldWidget serverNameField;

    @Shadow
    private TextFieldWidget addressField;

    @Shadow
    private ButtonWidget addButton;

    @Unique
    private PressableWidget resourcePackButton;

    @Shadow @Final private ServerInfo server;

    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        for (Element element : this.children()) {
            if (element instanceof CyclingButtonWidget<?> btn) {
                resourcePackButton = btn;
                break;
            }
        }

        addressField.setChangedListener(newText -> {
            String host = newText.split(":")[0];
            boolean isOfficial = Arrays.stream(OFFICIAL).anyMatch(h -> h.equalsIgnoreCase(newText));
            boolean isAddress = ADDRESS.equalsIgnoreCase(host);

            if (isOfficial || isAddress) {
                setLunarstarOnly();
            } else {
                resourcePackButton.active = true;
            }
        });

        String initialHost = addressField.getText().split(":")[0];
        boolean initialIsOfficial = Arrays.stream(OFFICIAL).anyMatch(h -> h.equalsIgnoreCase(addressField.getText()));
        boolean initialIsAddress = ADDRESS.equalsIgnoreCase(initialHost);
        if (initialIsOfficial || initialIsAddress) {
            setLunarstarOnly();
        }

        ButtonWidget button = ButtonWidget.builder(Text.of("連線至星月紀"), this)
                .position(10, this.height - 30)
                .size(80, 20)
                .build();

        this.addDrawableChild(button);
    }

    @Override
    public void onPress(ButtonWidget button) {
        serverNameField.setText("星月紀伺服器");
        final int port = LunarstarConnector.Companion.getCONNECT_PORT();
        addressField.setText(ADDRESS + (port == 25565 ? "" : ":" + port));
        addressField.setFocused(false);
        setLunarstarOnly();
    }

    @Unique
    private void setLunarstarOnly() {
        resourcePackButton.active = false;
        resourcePackButton.setTooltip(Tooltip.of(Text.of("此伺服器必須安裝資源包才能進入遊玩")));
        this.server.setResourcePackPolicy(ServerInfo.ResourcePackPolicy.ENABLED);
        addButton.active = true;
    }
}