package com.mcprog.bindingmod.setup;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.items.BindingClayItem;
import com.mcprog.bindingmod.items.BindingModuleItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.mcprog.bindingmod.BindingMod.MODID;

public class Registration {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    public static void init(IEventBus bus) {
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item> BINDING_CLAY_ITEM = ITEMS.register("binding_clay_item", () -> new BindingClayItem(new Item.Properties()));
    public static final RegistryObject<Item> BINDING_MODULE_ITEM = ITEMS.register("binding_module_item", () -> new BindingModuleItem(new Item.Properties()));
}
