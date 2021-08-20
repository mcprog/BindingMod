package com.mcprog.bindingmod.blocks;

import com.mcprog.bindingmod.datagen.ItemTags;
import com.mcprog.bindingmod.setup.Registration;
import com.mcprog.bindingmod.tools.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class IntegrationGeneratorBE extends BlockEntity {

    protected final ItemStackHandler itemHandler = createHandler();
    protected final CustomEnergyStorage energyStorage = createEnergy();

    protected final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    protected final LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    public static final int PROCESS_TIME = 100;
    public static final int ENERGY_CAPACITY = 640;


    private int counter;

    public IntegrationGeneratorBE(BlockPos pos, BlockState state) {
        super(Registration.INTEGRATION_GENERATOR_BE.get(), pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        handler.invalidate();
        energy.invalidate();
    }

    public void tickServer() {
        if (counter > 0) {
            counter--;
            energyStorage.addEnergy(20);
            setChanged();
        }

        if (counter <= 0) {
            ItemStack inputStack = itemHandler.getStackInSlot(0);
            if (inputStack != ItemStack.EMPTY) {
                itemHandler.extractItem(0, 1, false);
                counter = PROCESS_TIME;
                setChanged();
            }
        }

        BlockState state = level.getBlockState(worldPosition);
        if (state.getValue(BlockStateProperties.POWERED) != counter > 0) {
            level.setBlock(worldPosition, state.setValue(BlockStateProperties.POWERED, counter > 0),
                    Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
        }

        sendOutPower();
    }

    /*
     * Forge energy power works in a way where blocks that need power do not pull power from adjacent blocks.
     * Instead, that power must be pushed from the source of that power.
     */
    private void sendOutPower() {
        // Atomic since modified within a lambda
        AtomicInteger sourceEnergy = new AtomicInteger(energyStorage.getEnergyStored());
        if (sourceEnergy.get() > 0) {
            for (Direction direction : Direction.values()) {
                // Search for be one block in direction relative to our position (worldPosition)
                BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
                if (blockEntity != null) {
                    boolean doContinue = blockEntity.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                        if (handler.canReceive()) {
                            // Energy received can be at most the max output rate of our own energyStorage instance.
                            int received = handler.receiveEnergy(Math.min(sourceEnergy.get(), energyStorage.getMaxOut()), false);
                            // We have given that energy away, will not have it for other sides.
                            sourceEnergy.addAndGet(-received); // External tracking of our energy
                            energyStorage.removeEnergy(received); // Internal tracking of our energy
                            setChanged();
                            return sourceEnergy.get() > 0;
                        } else {
                            return true;
                        }
                    }).orElse(true);
                    if (!doContinue) {
                        return;
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        // Eg: this case with be called when hopper attaches to this block entity
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
           return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(2) {

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                if (slot != 0) {
                    return true; // output slot don't care
                }
                if (stack.getItem().getTags().contains(ItemTags.GENERATES_VIA_INTEGRATION.getName())) {

                    return true;
                }
                return false;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (slot != 0) {
                    return stack; // output slot never insert here
                }
                if (!stack.getItem().getTags().contains(ItemTags.GENERATES_VIA_INTEGRATION.getName())) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(ENERGY_CAPACITY, 100) {
            @Override
            protected void onEnergyChanged() {
                setChanged();
            }
        };
    }

    @Override
    public void load(CompoundTag tag) {
        itemHandler.deserializeNBT(tag.getCompound("inv"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        counter = tag.getInt("counter");
        super.load(tag);
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.put("inv", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("counter", counter);

        return super.save(tag);
    }
}
