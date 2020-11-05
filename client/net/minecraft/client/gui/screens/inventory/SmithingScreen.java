package net.minecraft.client.gui.screens.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.SmithingMenu;

public class SmithingScreen extends ItemCombinerScreen<SmithingMenu> {
   private static final ResourceLocation SMITHING_LOCATION = new ResourceLocation("textures/gui/container/smithing.png");

   public SmithingScreen(SmithingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3, SMITHING_LOCATION);
      this.titleLabelX = 60;
      this.titleLabelY = 18;
   }

   protected void renderLabels(PoseStack var1, int var2, int var3) {
      RenderSystem.disableBlend();
      super.renderLabels(var1, var2, var3);
   }
}
