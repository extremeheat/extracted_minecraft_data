package net.minecraft.client.gui.screens.inventory.tooltip;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
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

   public int getHeight() {
      return this.gridSizeY() * 20 + 2 + 4;
   }

   public int getWidth(Font var1) {
      return this.gridSizeX() * 18 + 2;
   }

   public void renderImage(Font var1, int var2, int var3, PoseStack var4, ItemRenderer var5, int var6) {
      int var7 = this.gridSizeX();
      int var8 = this.gridSizeY();
      boolean var9 = this.weight >= 64;
      int var10 = 0;

      for(int var11 = 0; var11 < var8; ++var11) {
         for(int var12 = 0; var12 < var7; ++var12) {
            int var13 = var2 + var12 * 18 + 1;
            int var14 = var3 + var11 * 20 + 1;
            this.renderSlot(var13, var14, var10++, var9, var1, var4, var5, var6);
         }
      }

      this.drawBorder(var2, var3, var7, var8, var4, var6);
   }

   private void renderSlot(int var1, int var2, int var3, boolean var4, Font var5, PoseStack var6, ItemRenderer var7, int var8) {
      if (var3 >= this.items.size()) {
         this.blit(var6, var1, var2, var8, var4 ? ClientBundleTooltip.Texture.BLOCKED_SLOT : ClientBundleTooltip.Texture.SLOT);
      } else {
         ItemStack var9 = (ItemStack)this.items.get(var3);
         this.blit(var6, var1, var2, var8, ClientBundleTooltip.Texture.SLOT);
         var7.renderAndDecorateItem(var9, var1 + 1, var2 + 1, var3);
         var7.renderGuiItemDecorations(var5, var9, var1 + 1, var2 + 1);
         if (var3 == 0) {
            AbstractContainerScreen.renderSlotHighlight(var6, var1 + 1, var2 + 1, var8);
         }

      }
   }

   private void drawBorder(int var1, int var2, int var3, int var4, PoseStack var5, int var6) {
      this.blit(var5, var1, var2, var6, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);
      this.blit(var5, var1 + var3 * 18 + 1, var2, var6, ClientBundleTooltip.Texture.BORDER_CORNER_TOP);

      int var7;
      for(var7 = 0; var7 < var3; ++var7) {
         this.blit(var5, var1 + 1 + var7 * 18, var2, var6, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_TOP);
         this.blit(var5, var1 + 1 + var7 * 18, var2 + var4 * 20, var6, ClientBundleTooltip.Texture.BORDER_HORIZONTAL_BOTTOM);
      }

      for(var7 = 0; var7 < var4; ++var7) {
         this.blit(var5, var1, var2 + var7 * 20 + 1, var6, ClientBundleTooltip.Texture.BORDER_VERTICAL);
         this.blit(var5, var1 + var3 * 18 + 1, var2 + var7 * 20 + 1, var6, ClientBundleTooltip.Texture.BORDER_VERTICAL);
      }

      this.blit(var5, var1, var2 + var4 * 20, var6, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
      this.blit(var5, var1 + var3 * 18 + 1, var2 + var4 * 20, var6, ClientBundleTooltip.Texture.BORDER_CORNER_BOTTOM);
   }

   private void blit(PoseStack var1, int var2, int var3, int var4, ClientBundleTooltip.Texture var5) {
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, TEXTURE_LOCATION);
      GuiComponent.blit(var1, var2, var3, var4, (float)var5.field_474, (float)var5.field_475, var5.field_476, var5.field_477, 128, 128);
   }

   private int gridSizeX() {
      return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.items.size() + 1.0D)));
   }

   private int gridSizeY() {
      return (int)Math.ceil(((double)this.items.size() + 1.0D) / (double)this.gridSizeX());
   }

   static enum Texture {
      SLOT(0, 0, 18, 20),
      BLOCKED_SLOT(0, 40, 18, 20),
      BORDER_VERTICAL(0, 18, 1, 20),
      BORDER_HORIZONTAL_TOP(0, 20, 18, 1),
      BORDER_HORIZONTAL_BOTTOM(0, 60, 18, 1),
      BORDER_CORNER_TOP(0, 20, 1, 1),
      BORDER_CORNER_BOTTOM(0, 60, 1, 1);

      // $FF: renamed from: x int
      public final int field_474;
      // $FF: renamed from: y int
      public final int field_475;
      // $FF: renamed from: w int
      public final int field_476;
      // $FF: renamed from: h int
      public final int field_477;

      private Texture(int var3, int var4, int var5, int var6) {
         this.field_474 = var3;
         this.field_475 = var4;
         this.field_476 = var5;
         this.field_477 = var6;
      }

      // $FF: synthetic method
      private static ClientBundleTooltip.Texture[] $values() {
         return new ClientBundleTooltip.Texture[]{SLOT, BLOCKED_SLOT, BORDER_VERTICAL, BORDER_HORIZONTAL_TOP, BORDER_HORIZONTAL_BOTTOM, BORDER_CORNER_TOP, BORDER_CORNER_BOTTOM};
      }
   }
}
