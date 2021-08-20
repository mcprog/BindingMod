package com.mcprog.bindingmod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class BindingModuleItem extends Item {

    static final String MODULE_TIER_TAG = "tier";

    public BindingModuleItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag flags) {
        super.appendHoverText(stack, level, list, flags);
        int tier = stack.hasTag() ? stack.getTag().getInt(MODULE_TIER_TAG) : 0;
        list.add(new TranslatableComponent("message.binding_module_item", Integer.toString(tier)).withStyle(ChatFormatting.BLUE));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        int tier = stack.getOrCreateTag().getInt(MODULE_TIER_TAG);
        tier++;
        stack.getTag().putInt(MODULE_TIER_TAG, tier);
        return  InteractionResultHolder.success(stack);
    }
}
