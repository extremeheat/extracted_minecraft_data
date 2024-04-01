package net.minecraft.client.gui.screens.inventory;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.FletchingMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.FletchingBlockEntity;

public class FletchingScreen extends AbstractContainerScreen<FletchingMenu> {
   private static final ResourceLocation FLETCHING_PROGRESS_SPRITE = new ResourceLocation("container/fletching/progresss");
   private static final ResourceLocation FLETCHING_LOCATION = new ResourceLocation("textures/gui/container/fletching.png");
   private int processsTime = 100;
   private final long startTick;
   @Nullable
   private Component customTitle = null;
   private boolean wasExplored = false;

   public FletchingScreen(FletchingMenu var1, Inventory var2, Component var3) {
      super(var1, var2, var3);
      this.imageWidth += 320;
      this.inventoryLabelX += 160;
      this.startTick = Minecraft.getInstance().level.getGameTime();
   }

   @Override
   protected void init() {
      super.init();
      this.titleLabelX = (this.imageWidth - this.font.width(this.getTitle())) / 2;
   }

   private Component makeTitle(char var1, char var2, char var3, boolean var4) {
      MutableComponent var5 = Component.empty()
         .append(FletchingBlockEntity.Resin.getQualityComponent(var3), ", ", FletchingBlockEntity.Resin.getImpuritiesComponent(var1));
      MutableComponent var6 = var3 >= 'j'
         ? Component.translatable("item.minecraft.amber_gem")
         : Component.empty()
            .append(
               FletchingBlockEntity.Resin.getQualityComponent((char)(var3 + 1)),
               ", ",
               var4 ? FletchingBlockEntity.Resin.getImpuritiesComponent(var2) : FletchingBlockEntity.Resin.getImpuritiesComponent("unknown")
            );
      return Component.translatable("screen.fletching.title", var5, var6);
   }

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      int var5 = this.menu.getProcessTime();
      boolean var6 = this.menu.isExplored();
      if (var5 != 0 && this.customTitle == null || var6 != this.wasExplored) {
         this.customTitle = this.makeTitle(this.menu.getSourceImpurities(), this.menu.getResultImpurities(), this.menu.getSourceQuality(), var6);
         this.processsTime = var5;
         this.wasExplored = var6;
         this.titleLabelX = (this.imageWidth - this.font.width(this.getTitle())) / 2;
      }

      super.render(var1, var2, var3, var4);
      this.renderTooltip(var1, var2, var3);
   }

   @Override
   public Component getTitle() {
      return this.customTitle != null ? this.customTitle : super.getTitle();
   }

   @Override
   protected void renderBg(GuiGraphics var1, float var2, int var3, int var4) {
      int var5 = (this.width - this.imageWidth) / 2;
      int var6 = (this.height - this.imageHeight) / 2;
      long var7 = Minecraft.getInstance().level.getGameTime() - this.startTick;
      var7 = Math.max(0L, var7 - 20L);
      if (var7 > 160L) {
         var1.blit(FLETCHING_LOCATION, var5, var6, 0, 0.0F, 0.0F, this.imageWidth, this.imageHeight, 512, 512);
      } else {
         var1.blit(FLETCHING_LOCATION, var5 + 160, var6 + 4, 0, 160.0F, 4.0F, this.imageWidth - 320, this.imageHeight - 4, 512, 512);
         int var9 = 160 - (int)var7;
         var1.blit(FLETCHING_LOCATION, var5 + var9, var6, 0, 0.0F, 0.0F, 164, 19, 512, 512);
         var1.blit(FLETCHING_LOCATION, var5 + this.imageWidth - 160 - var9 - 4, var6, 0, (float)(this.imageWidth - 160 - 4), 0.0F, 164, 19, 512, 512);
         var1.blit(FLETCHING_LOCATION, var5 + 160 + 4, var6, 0, 164.0F, 0.0F, this.imageWidth - 320 - 8, this.imageHeight, 512, 512);
      }

      int var19 = this.menu.getProgresss();
      if (var19 > 0) {
         float var10 = ((float)var19 + var2) / (float)this.processsTime;
         double var11 = 6.283185307179586 * (double)var10;
         double var13 = (1.0 - Math.cos(var11)) * 59.0;
         double var15 = Math.sin(2.0 * var11) * 21.0;
         this.renderFloatingItem(
            var1, Items.FEATHER.getDefaultInstance(), (float)(var5 + 160 + 79 - 59) + (float)var13, (float)(var6 + 38) + (float)var15, (float)var11
         );
         int var17 = (int)(21.0F * (1.0F - (float)var19 / (float)this.processsTime));
         if (var17 > 0) {
            var1.blitSprite(FLETCHING_PROGRESS_SPRITE, 9, 21, 0, 0, var5 + 160 + 83, var6 + 35, 9, var17);
         }
      }
   }
}
