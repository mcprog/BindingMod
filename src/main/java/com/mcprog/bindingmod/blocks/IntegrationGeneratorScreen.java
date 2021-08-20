package com.mcprog.bindingmod.blocks;

import com.mcprog.bindingmod.BindingMod;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


/*
 * Client side only
 */
public class IntegrationGeneratorScreen extends AbstractContainerScreen<IntegrationGeneratorContainer> {

    private final ResourceLocation GUI = new ResourceLocation(BindingMod.MODID, "textures/gui/integration_generator_gui.png");
    private final Font MINECRAFT_FONT = Minecraft.getInstance().font;

    public IntegrationGeneratorScreen(IntegrationGeneratorContainer container, Inventory inventory, Component name) {
        super(container, inventory, name);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack maxtrixStack, int mouseX, int mouseY) {
        drawString(maxtrixStack, MINECRAFT_FONT, "Energy: " + menu.getEnergy(), 10, 10, 0xffffff);
    }

    @Override
    protected void renderBg(PoseStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, GUI);
        int relX = (this.width - this.imageWidth) / 2;
        int relY = (this.height - this.imageHeight) / 2;
        this.blit(matrixStack, relX, relY, 0, 0, this.imageWidth, this.imageHeight);
    }
}
