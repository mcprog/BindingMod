package com.mcprog.bindingmod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

import javax.annotation.Nullable;

public class IntegrationGeneratorBlock extends Block implements EntityBlock {
    public IntegrationGeneratorBlock() {
        super(Properties.of(Material.METAL)
                .sound(SoundType.METAL)
                .lightLevel(state -> state.getValue(BlockStateProperties.POWERED) ? 12 : 0)
                .strength(2.0f));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> beType) {
        if (level.isClientSide()) {
            return null;
        }
        return ((level1, blockPos, blockState, t) -> {
            if (t instanceof IntegrationGeneratorBE) {
                IntegrationGeneratorBE integrationGeneratorBE = (IntegrationGeneratorBE) t;
                integrationGeneratorBE.tickServer();
            }
        });
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return defaultBlockState().setValue(BlockStateProperties.FACING, placeContext.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new IntegrationGeneratorBE(blockPos, blockState);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        if (!level.isClientSide()) {
           BlockEntity blockEntity = level.getBlockEntity(pos);
           if (blockEntity instanceof  IntegrationGeneratorBE) {
               MenuProvider containerProvider = new MenuProvider() {
                   @Override
                   public Component getDisplayName() {
                       return new TranslatableComponent("screen.bindingmod.integration_generator");
                   }

                   @Nullable
                   @Override
                   public AbstractContainerMenu createMenu(int id, Inventory inventory, Player playerEntity) {
                       return new IntegrationGeneratorContainer(id, level, pos, inventory, playerEntity);
                   }
               };
               NetworkHooks.openGui((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
           } else {
               throw new IllegalStateException("Missing named container provider");
           }
        }
        return InteractionResult.SUCCESS;
    }
}
