package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemDisplayWidget extends AbstractWidget {
   private final Minecraft minecraft;
   private final int offsetX;
   private final int offsetY;
   private final ItemStack itemStack;
   private final boolean decorations;
   private final boolean tooltip;

   public ItemDisplayWidget(Minecraft var1, int var2, int var3, int var4, int var5, Component var6, ItemStack var7, boolean var8, boolean var9) {
      super(0, 0, var4, var5, var6);
      this.minecraft = var1;
      this.offsetX = var2;
      this.offsetY = var3;
      this.itemStack = var7;
      this.decorations = var8;
      this.tooltip = var9;
   }

   protected void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      var1.renderItem(this.itemStack, this.getX() + this.offsetX, this.getY() + this.offsetY, 0);
      if (this.decorations) {
         var1.renderItemDecorations(this.minecraft.font, this.itemStack, this.getX() + this.offsetX, this.getY() + this.offsetY, (String)null);
      }

      if (this.isFocused()) {
         var1.renderOutline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), -1);
      }

      if (this.tooltip && this.isHoveredOrFocused()) {
         this.renderTooltip(var1, var2, var3);
      }

   }

   protected void renderTooltip(GuiGraphics var1, int var2, int var3) {
      var1.setTooltipForNextFrame(this.minecraft.font, this.itemStack, var2, var3);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)Component.translatable("narration.item", this.itemStack.getHoverName()));
   }
}
