package com.mcprog.bindingmod.setup;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.blocks.IntegrationGeneratorBE;
import com.mcprog.bindingmod.blocks.IntegrationGeneratorBlock;
import com.mcprog.bindingmod.blocks.IntegrationGeneratorContainer;
import com.mcprog.bindingmod.items.BindingClayItem;
import com.mcprog.bindingmod.items.BindingModuleItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static com.mcprog.bindingmod.BindingMod.MODID;

public class Registration {

    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    private static final DeferredRegister<Item> BLOCK_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    private static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);


    public static void init(IEventBus bus) {
        ITEMS.register(bus);
        BLOCKS.register(bus);
        BLOCK_ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        CONTAINERS.register(bus);
    }

    public static final RegistryObject<Item> BINDING_CLAY_ITEM = ITEMS.register("binding_clay_item", () -> new BindingClayItem(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS)));
    public static final RegistryObject<Item> BINDING_MODULE_ITEM = ITEMS.register("binding_module_item", () -> new BindingModuleItem(new Item.Properties().tab(CreativeModeTab.TAB_MATERIALS).stacksTo(1)));

    public static final RegistryObject<Block> INTEGRATION_GENERATOR_BLOCK = BLOCKS.register("integration_generator", IntegrationGeneratorBlock::new);
    public static final RegistryObject<Item> INTEGRATION_GENERATOR_ITEM = BLOCK_ITEMS.register("integration_generator", () -> new BlockItem(INTEGRATION_GENERATOR_BLOCK.get(), new Item.Properties().stacksTo(1).tab(CreativeModeTab.TAB_REDSTONE)));
    public static final RegistryObject<BlockEntityType<IntegrationGeneratorBE>> INTEGRATION_GENERATOR_BE = BLOCK_ENTITIES.register("integration_generator",
            () -> BlockEntityType.Builder.of(IntegrationGeneratorBE::new, INTEGRATION_GENERATOR_BLOCK.get()).build(null));

    public static final RegistryObject<MenuType<IntegrationGeneratorContainer>> INTEGRATION_GENERATOR_CONTAINER = CONTAINERS.register("integration_generator",
            () -> IForgeContainerType.create((windowId, inv, data) -> {
                // Called client side but data comes from server
                BlockPos pos = data.readBlockPos();
                Level world = inv.player.getCommandSenderWorld();
                return new IntegrationGeneratorContainer(windowId, world, pos, inv, inv.player);
            }));
}
