package com.mcprog.bindingmod.blocks;

import com.mcprog.bindingmod.BindingMod;
import com.mcprog.bindingmod.datagen.ItemTags;
import com.mcprog.bindingmod.setup.Registration;
import com.mcprog.bindingmod.tools.CustomEnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class IntegrationGeneratorContainer extends AbstractContainerMenu {

    private BlockEntity blockEntity;
    private Player playerEntity;
    private IItemHandler playerInventory;

    protected static final int SLOT_DX = 18;
    protected static final int SLOT_DY = 18;

    public IntegrationGeneratorContainer(int windowId, Level world, BlockPos pos, Inventory playerInv, Player player) {
        super(Registration.INTEGRATION_GENERATOR_CONTAINER.get(), windowId);
        blockEntity = world.getBlockEntity(pos);
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInv);

        if (blockEntity != null) {
            blockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler -> {
                addSlot(new SlotItemHandler(handler, 0, 32, 34));
                addSlot(new SlotItemHandler(handler, 1, 129, 34));
            });
        }

        layoutPlayerInventorySlots(8, 84);
        trackPower();
    }

    private void trackPower() {
        final int leastSignificantModifier = 0xffff;
        final int first16bits = 0xffff0000;
        final int last16bits = 0x0000ffff;
        // Least significant 16 bits of the energy value
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return getEnergy() & leastSignificantModifier;
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent((handler -> {
                    int energyStored = handler.getEnergyStored() & first16bits;
                    ((CustomEnergyStorage) handler).setEnergy(energyStored + (value & leastSignificantModifier));
                }));
            }
        });
        // Most significant 16 bits of energy value
        addDataSlot(new DataSlot() {
            @Override
            public int get() {
                return (getEnergy() >> 16) & leastSignificantModifier;
            }

            @Override
            public void set(int value) {
                blockEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent((handler -> {
                    int energyStored = handler.getEnergyStored() & last16bits;
                    ((CustomEnergyStorage) handler).setEnergy(energyStored | (value << 16));
                }));
            }
        });
    }

    public int getEnergy() {
        return blockEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }


    /*
     * Checks if player is still in range of container.
     */
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos()), playerEntity, Registration.INTEGRATION_GENERATOR_BLOCK.get());
    }

    /*
     * On shift-click into this container
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        final int startPlayerInv = 2;
        final int endPlayerInv = 38;
        final int invHotbarBorder = 29;

        ItemStack stack = ItemStack.EMPTY;
        Slot srcSlot = this.slots.get(index);
        if (srcSlot != null && srcSlot.hasItem()) {
            ItemStack originalStack = srcSlot.getItem();
            stack = originalStack.copy();
            // If shift-click from generator input or output slot, put anywhere in player inventory
            if (index == 0 || index == 1) {
                if (!this.moveItemStackTo(originalStack, startPlayerInv, endPlayerInv, true)) {
                    return ItemStack.EMPTY;
                }
                srcSlot.onQuickCraft(originalStack, stack);
            } else {
                BindingMod.LOGGER.info(originalStack.getItem().getTags());
                if (originalStack.getItem().getTags().contains(ItemTags.GENERATES_VIA_INTEGRATION.getName())) {
                    BindingMod.LOGGER.info("integration generates true");
                    if (!this.moveItemStackTo(originalStack, 0, 2, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < invHotbarBorder) { // Shift-clicking from player inv to hotbar
                    if (!this.moveItemStackTo(originalStack, invHotbarBorder, endPlayerInv, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                else if (index < endPlayerInv) { // Shift-clicking from hotbar to player inv
                    if (!this.moveItemStackTo(originalStack, startPlayerInv, invHotbarBorder, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (originalStack.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                srcSlot.setChanged();
            }

            if (originalStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            srcSlot.onTake(player, originalStack);
        }

        return stack;
    }

    protected int addSlotRow(IItemHandler handler, int index, int x, int y, int width) {
        for (int i = 0; i < width; ++i) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += SLOT_DX;
            index++;
        }
        return index;
    }

    protected void addSlotBox(IItemHandler handler, int index, int x, int y, int numWide, int numTall) {
        for (int i = 0; i < numTall; ++i) {
            index = addSlotRow(handler, index, x, y, numWide);
            y += SLOT_DY;
        }
    }

    protected void layoutPlayerInventorySlots(int leftCol, int topRow) {
        final int playerInvWidth = 9;
        final int playerInvHeight = 3;
        final int playerInvStartIndex = 9;
        final int playerHotbarStartIndex = 0;
        final int invToHotbarDeltaY = 58;

        addSlotBox(playerInventory, playerInvStartIndex, leftCol, topRow, playerInvWidth, playerInvHeight);
        topRow +=invToHotbarDeltaY;
        addSlotRow(playerInventory, playerHotbarStartIndex, leftCol, topRow, playerInvWidth);
    }
}
