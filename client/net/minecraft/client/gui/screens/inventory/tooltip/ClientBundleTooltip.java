package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.item.ItemStack;

public class ClientBundleTooltip implements ClientTooltipComponent {
   public static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/container/bundle.png");
   private static final int MARGIN_Y = 4;
   private static final int BORDER_WIDTH = 1;
   private static final int TEX_SIZE = 128;
   private static final int SLOT_SIZE_X = 18;
   private static final int SLOT_SIZE_Y = 20;
   private final NonNullList<ItemStack> items;
   private final int weight;

   public ClientBundleTooltip(BundleTooltip var1) {
      super();
      this.items = var1.getItems();
      this.weight = var1.getWeight();
   }

   @Override
   public int getHeight() {
      return this.gridSizeY() * 20 + 2 + 4;
   }

   @Override
   public int getWidth(Font var1) {
      return this.gridSizeX() * 18 + 2;
   }

   @Override
   public void renderImage(Font var1, int var2, int var3, GuiGraphics var4) {
      int var5 = this.gridSizeX();
      int var6 = this.gridSizeY();
      boolean var7 = this.weight >= 64;
      int var8 = 0;

      for(int var9 = 0; var9 < var6; ++var9) {
         for(int var10 = 0; var10 < var5; ++var10) {
            int var11 = var2 + var10 * 18 + 1;
            int var12 = var3 + var9 * 20 + 1;
            this.renderSlot(var11, var12, var8++, var7, var4, var1);
         }
      }

      this.drawBorder(var2, var3, var5, var6, var4);
   }

   private void renderSlot(int var1, int var2, int var3, boolean var4, GuiGraphics var5, Font var6) {
      if (var3 >= this.items.size()) {
         this.blit(var5, var1, var2, var4 ? ClientBundleTooltip.Texture.BLOCKED_SLOT : ClientBundleTooltip.Texture.SLOT);
      } else {
         ItemStack var7 = this.items.get(var3);
         this.blit(var5, var1, var2, ClientBundleTooltip.Texture.SLOT);
         var5.renderItem(var7, var1 + 1, var2 + 1, var3);
         var5.renderItemDecorations(var6, var7, var1 + 1, var2 + 1);
         if (var3 == 0) {
            AbstractContainerScreen.renderSlotHighlight(var5, var1 + 1, var2 + 1, 0);
         }
      }
   }

   private void drawBorder(int var1, int var2, int var3, int var4, GuiGraphics var5) {
      this.blit(var5, var1, var2, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);
      this.blit(var5, var1 + var3 * 18 + 1, var2, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);

      for(int var6 = 0; var6 < var3; ++var6) {
         this.blit(var5, var1 + 1 + var6 * 18, var2, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_TOP);
         this.blit(var5, var1 + 1 + var6 * 18, var2 + var4 * 20, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
      }

      for(int var7 = 0; var7 < var4; ++var7) {
         this.blit(var5, var1, var2 + var7 * 20 + 1, ClientBundleTooltip.Texture.BORDER_VERTICAL);
         this.blit(var5, var1 + var3 * 18 + 1, var2 + var7 * 20 + 1, ClientBundleTooltip.Texture.BORDER_VERTICAL);
      }

      this.blit(var5, var1, var2 + var4 * 20, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
      this.blit(var5, var1 + var3 * 18 + 1, var2 + var4 * 20, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
   }

   private void blit(GuiGraphics var1, int var2, int var3, ClientBundleTooltip.Texture var4) {
      var1.blit(TEXTURE_LOCATION, var2, var3, 0, (float)var4.x, (float)var4.y, var4.w, var4.h, 128, 128);
   }

   private int gridSizeX() {
      return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0)));
   }

   private int gridSizeY() {
      return (int)Math.ceil(((double)this.items.size() + 1.0) / (double)this.gridSizeX());
   }

   static enum Texture {
      SLOT(0, 0, 18, 20),
      BLOCKED_SLOT(0, 40, 18, 20),
      BORDER_VERTICAL(0, 18, 1, 20),
      BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
      BORDER_CORNER_TOP(0, 20, 1, 1),
      BORDER_CORNER_BOTTOM(0, 60, 1, 1);

      public final int x;
      public final int y;
      public final int w;
      public final int h;

      private Texture(int var3, int var4, int var5, int var6) {
         this.x = var3;
         this.y = var4;
         this.w = var5;
         this.h = var6;
      }
   }
}
