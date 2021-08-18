package com.mcprog.bindingmod.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void GatherData(GatherDataEvent event) {

        DataGenerator generator = event.getGenerator();
        if (event.includeClient()) {
            generator.addProvider(new Items(generator, event.getExistingFileHelper()));
        }

    }
}