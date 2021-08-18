package com.mcprog.bindingmod.datagen;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fmllegacy.RegistryObject;

public class Items extends ItemModelProvider {
    public Items(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, BindingMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        singleTextureSimple(Registration.BINDING_CLAY_ITEM, "item/binding_clay_item");
        singleTextureSimple(Registration.BINDING_MODULE_ITEM, "item/binding_module_item");
    }

    private void singleTextureSimple(RegistryObject<Item> itemRegistryObject, String itemName) {
        singleTexture(itemRegistryObject.get().getRegistryName().getPath(), new ResourceLocation("item/generated"), "layer0", new ResourceLocation(BindingMod.MODID, itemName));
    }
}
