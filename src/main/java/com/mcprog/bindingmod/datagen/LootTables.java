package com.mcprog.bindingmod.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.setup.Registration;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyNameFunction;
import net.minecraft.world.level.storage.loot.functions.CopyNbtFunction;
import net.minecraft.world.level.storage.loot.functions.SetContainerContents;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class LootTables extends LootTableProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator generator;
    public LootTables(DataGenerator generator) {
        super(generator);
        this.generator = generator;
    }

    @Override
    public void run(HashCache cache) {
        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        addEnergyBlockToTable(tables, Registration.INTEGRATION_GENERATOR_BLOCK.get(), "integration_generator");

        writeTables(cache, tables);
    }

    private void addEnergyBlockToTable(Map<ResourceLocation, LootTable> tables, Block block, String name) {
        tables.put(block.getLootTable(),
                createEnergyBlockTable(name, block).setParamSet(LootContextParamSets.BLOCK).build());
    }

    protected LootTable.Builder createEnergyBlockTable(String name, Block block) {

        CopyNbtFunction.MergeStrategy mergeStrategy = CopyNbtFunction.MergeStrategy.REPLACE;
        LootPool.Builder builder = LootPool.lootPool()
                .name(name)
                .setRolls(ConstantValue.exactly(1))
                .add(LootItem.lootTableItem(block))
                .apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY))
                .apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY)
                    .copy("inv", getSpecificBlockEntityTag("inv"), mergeStrategy)
                    .copy("energy", getSpecificBlockEntityTag("energy"), mergeStrategy))
                .apply(SetContainerContents.setContents()
                    .withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents")))
                );
        return LootTable.lootTable().withPool(builder);
    }

    protected void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                DataProvider.save(GSON, cache, net.minecraft.world.level.storage.loot.LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                BindingMod.LOGGER.error("Could not write loot table {}", path, e);
            }
        });
    }

    @Override
    public String getName() {
        return "Binding Mod Loot Tables";
    }

    private String getSpecificBlockEntityTag(String subTag) {
        return BlockItem.BLOCK_ENTITY_TAG + "." + subTag;
    }
}
