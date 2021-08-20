package com.mcprog.bindingmod.setup;

import com.mcprog.bindingmod.blocks.IntegrationGeneratorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class ClientSetup {

    public static void setup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Registration.INTEGRATION_GENERATOR_CONTAINER.get(), IntegrationGeneratorScreen::new);
        });
    }
}
