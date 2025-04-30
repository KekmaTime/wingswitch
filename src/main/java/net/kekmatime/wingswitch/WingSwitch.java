package net.kekmatime.wingswitch;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class WingSwitch implements ModInitializer, ClientModInitializer {
    public static final String MOD_ID = "wingswitch";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static KeyBinding armorSwapKey;

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing WingSwitch Mod (Common)");
        // Common initialization code (if needed)
    }

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing WingSwitch Mod (Client)");

        // Register keybind
        armorSwapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.wingswitch.armor_swap",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_GRAVE_ACCENT,
            "category.wingswitch.main"
        ));

        // Register tick event
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (armorSwapKey.wasPressed()) {
                ArmorSwapHandler.swapArmor();
            }
        });
    }
} 