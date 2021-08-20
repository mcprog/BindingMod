package com.mcprog.bindingmod.datagen;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.setup.Registration;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.nio.file.Path;

public class Tags extends BlockTagsProvider {

    public Tags(DataGenerator generator, @Nullable ExistingFileHelper existingFileHelper) {
        super(generator, BindingMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(Registration.INTEGRATION_GENERATOR_BLOCK.get());
    }

    @Override
    public String getName() {
        return "Binding Mod Tags";
    }
}
