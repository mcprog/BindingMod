package com.mcprog.bindingmod.datagen;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Function;

public class BlockStates extends BlockStateProvider {
    public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, BindingMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerIntegrationGeneratorBlock();
    }

    private void registerIntegrationGeneratorBlock() {
        ResourceLocation front = new ResourceLocation(BindingMod.MODID, "block/integration_generator_front");
        ResourceLocation frontOn = new ResourceLocation(BindingMod.MODID, "block/integration_generator_front_on");
        ResourceLocation side = new ResourceLocation(BindingMod.MODID, "block/integration_generator_side");
        ResourceLocation top = new ResourceLocation(BindingMod.MODID, "block/integration_generator_top");
        BlockModelBuilder modelOff = models().cube("integration_generator_off", top, top, front, side, side, side);
        BlockModelBuilder modelOn = models().cube("integration_generator_on", top, top, frontOn, side, side, side);
        orientedBlock(Registration.INTEGRATION_GENERATOR_BLOCK.get(), state -> {
            if (state.getValue(BlockStateProperties.POWERED)) {
                return modelOn;
            }
            return modelOff;
        });
    }

    private void orientedBlock(Block block, Function<BlockState, ModelFile> modelFunc) {
        getVariantBuilder(block).forAllStates(state -> {
            Direction direction = state.getValue(BlockStateProperties.FACING);
            return ConfiguredModel.builder()
                    .modelFile(modelFunc.apply(state))
                    .rotationX(getRotationXY(direction, false))
                    .rotationY(getRotationXY(direction, true))
                    .build();
        });
    }

    private int getRotationXY(Direction direction, boolean rotationY) {
        if (rotationY) {
            return direction.getAxis() != Direction.Axis.Y ? ((direction.get2DDataValue() + 2) % 4) * 90 : 0;
        }
        return direction.getAxis() == Direction.Axis.Y ? direction.getAxisDirection().getStep() * -90 : 0;
    }
}
