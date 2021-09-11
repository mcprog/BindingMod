package com.mcprog.bindingmod.tools;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.registries.ForgeRegistry;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Optional;

public class IntegrationGeneratorSerializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<IntegrationGeneratorRecipe> {

    public static final IntegrationGeneratorSerializer INTEGRATION_GENERATOR_SERIALIZER = new IntegrationGeneratorSerializer();

    public static final int DEFAULT_INTEGRATION_GENERATE_TIME = 100;

    static {
        INTEGRATION_GENERATOR_SERIALIZER.setRegistryName(IntegrationGeneratorRecipe.INTEGRATION_GENERATES_ID);
    }

    private IntegrationGeneratorSerializer() {
    }

    @Override
    public IntegrationGeneratorRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        ItemStack resultStack;
        String group = GsonHelper.getAsString(json, "group", "");
        JsonObject jsonIngredient = GsonHelper.getAsJsonObject(json, "ingredient");
        Ingredient ingredient = Ingredient.fromJson(jsonIngredient);

        // Try two different ways to get result
        if (!json.has("result")) {
            throw new JsonSyntaxException("Missing 'result', expected to find a string or object");
        }
        if (json.get("result").isJsonObject()) {
            resultStack = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        } else {
            String resultString = GsonHelper.getAsString(json, "result");
            ResourceLocation resultLocation = new ResourceLocation(resultString);
            resultStack = new ItemStack(Registry.ITEM.getOptional(resultLocation).orElseThrow(() -> {
               return new IllegalStateException("Item does not exist: " + resultString);
            }));
        }

        int processTime = GsonHelper.getAsInt(json, "process_time", DEFAULT_INTEGRATION_GENERATE_TIME);
        return new IntegrationGeneratorRecipe(recipeId, group, ingredient, resultStack, processTime);
    }

    @Nullable
    @Override
    public IntegrationGeneratorRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        String group = buffer.readUtf();
        Ingredient ingredient = Ingredient.fromNetwork(buffer);
        ItemStack result = buffer.readItem();
        int processTime = buffer.readVarInt();
        return new IntegrationGeneratorRecipe(recipeId, group, ingredient, result, processTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, IntegrationGeneratorRecipe recipe) {
        buffer.writeUtf(recipe.getGroup());
        recipe.getIngredient().toNetwork(buffer);
        buffer.writeItem(recipe.getResultItem());
        buffer.writeVarInt(recipe.getProcessTime());
    }
}
