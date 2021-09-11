package com.mcprog.bindingmod.tools;

import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class IntegrationGeneratorRecipeBuilder implements RecipeBuilder {


    private Ingredient input;
    private ItemStack result;
    private String group;
    private Advancement.Builder advancement = Advancement.Builder.advancement();
    private int processTime;

    private IntegrationGeneratorRecipeBuilder(Ingredient input, ItemStack result, int processTime) {
        this.input = input;
        this.result = result;
        this.processTime = processTime;
    }

    public static IntegrationGeneratorRecipeBuilder integrationGenerator(ItemLike input, ItemStack result, int processTime) {
        return new IntegrationGeneratorRecipeBuilder(Ingredient.of(input), result, processTime);
    }

    public static IntegrationGeneratorRecipeBuilder integrationGenerator(Tag.Named<Item> input, ItemStack result, int processTime) {
        return new IntegrationGeneratorRecipeBuilder(Ingredient.of(input), result, processTime);
    }

    @Override
    public RecipeBuilder unlockedBy(String name, CriterionTriggerInstance trigger) {
        advancement.addCriterion(name, trigger);
        return this;
    }

    @Override
    public RecipeBuilder group(@Nullable String group) {
        this.group = group;
        return this;
    }

    @Override
    public Item getResult() {
        return this.result.getItem();
    }

    @Override
    public void save(Consumer<FinishedRecipe> consumer, ResourceLocation name) {
        this.ensureValid(name);
        //consumer.accept(new Result(name, group, input, result, processTime, advancement, new ResourceLocation()));
    }

    protected void ensureValid(ResourceLocation name) {
        if (advancement.getCriteria().isEmpty()) {
            throw new IllegalStateException("No way of obtaining recipe for " + name);
        }
    }

    public static class Result implements FinishedRecipe {
        private ResourceLocation name;
        private String group;
        private Ingredient input;
        private ItemStack result;
        private int processTime;
        private Advancement.Builder advancementBuilder;
        private ResourceLocation advancementId;
        private IntegrationGeneratorSerializer serializer;

        public Result (ResourceLocation name, String group, Ingredient input, ItemStack result, int processTime, Advancement.Builder builder, ResourceLocation advancementId, IntegrationGeneratorSerializer serializer) {
            this.name = name;
            this.group = group;
            this.input = input;
            this.result = result;
            this.advancementBuilder = builder;
            this.advancementId = advancementId;
            this.serializer = serializer;
        }


        @Override
        public void serializeRecipeData(JsonObject json) {
            json.add("input", input.toJson());
        }

        @Override
        public ResourceLocation getId() {
            return name;
        }

        @Override
        public RecipeSerializer<?> getType() {
            return null;
        }

        @Nullable
        @Override
        public JsonObject serializeAdvancement() {
            return null;
        }

        @Nullable
        @Override
        public ResourceLocation getAdvancementId() {
            return null;
        }
    }
}
