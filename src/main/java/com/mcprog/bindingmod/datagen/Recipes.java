package com.mcprog.bindingmod.datagen;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.setup.Registration;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.common.Tags;

import java.util.function.Consumer;

import static net.minecraft.world.item.Items.CYAN_CONCRETE_POWDER;

public class Recipes extends RecipeProvider {
    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
        ShapedRecipeBuilder.shaped(Registration.BINDING_MODULE_ITEM.get())
                .pattern("eoe")
                .pattern("ooo")
                .define('e', Tags.Items.GEMS_EMERALD)
                .define('o', Tags.Items.OBSIDIAN)
                .group(BindingMod.MODID)
                .unlockedBy("obsidian", InventoryChangeTrigger.TriggerInstance.hasItems(Items.OBSIDIAN))
                .save(consumer);

        ShapedRecipeBuilder.shaped(Registration.INTEGRATION_GENERATOR_BLOCK.get())
                .pattern("sss")
                .pattern("shs")
                .pattern("---")
                .define('s', Items.STONE)
                .define('h', Items.HOPPER)
                .define('-', Items.SMOOTH_STONE_SLAB)
                .group(BindingMod.MODID)
                .unlockedBy("hopper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HOPPER))
                .save(consumer);

    }

}
