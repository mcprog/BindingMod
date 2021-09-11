package com.mcprog.bindingmod.tools;

import com.mcprog.bindingmod.setup.Registration;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.wrapper.RecipeWrapper;

public class IntegrationGeneratorRecipe implements Recipe<RecipeWrapper> {

    static final String INTEGRATION_GENERATES_ID = "integration_generates";

    public static final RecipeType<IntegrationGeneratorRecipe> INTEGRATION_GENERATES = RecipeType.register("INTEGRATION_GENERATES_ID");

    private ResourceLocation id;
    private String group;
    private Ingredient ingredient;
    private ItemStack result;
    private int processTime;

    public IntegrationGeneratorRecipe(ResourceLocation id, String group, Ingredient ingredient, ItemStack result, int processTime) {
        this.id = id;
        this.group = group;
        this.ingredient = ingredient;
        this.result = result;
        this.processTime = processTime;

    }

    @Override
    public boolean matches(RecipeWrapper inv, Level world) {
        return this.ingredient.test(inv.getItem(0));
    }


    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return this.result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return true;
    }


    public Ingredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public ItemStack getResultItem() {
        return this.result;
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(Registration.INTEGRATION_GENERATOR_BLOCK.get());
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RecipeSerializer.SMELTING_RECIPE;
    }


    public int getProcessTime() {
        return this.processTime;
    }

    @Override
    public RecipeType<?> getType() {
        return INTEGRATION_GENERATES;
    }
}
