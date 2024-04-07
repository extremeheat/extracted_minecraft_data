package net.minecraft.client.gui.screens.inventory.tooltip;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

public class ClientBundleTooltip implements ClientTooltipComponent {
   private static final ResourceLocation BACKGROUND_SPRITE = new ResourceLocation("container/bundle/background");
   private static final int MARGIN_Y = 4;
   private static final int BORDER_WIDTH = 1;
   private static final int SLOT_SIZE_X = 18;
   private static final int SLOT_SIZE_Y = 20;
   private final BundleContents contents;

   public ClientBundleTooltip(BundleContents var1) {
      super();
      this.contents = var1;
   }

   @Override
   public int getHeight() {
      return this.backgroundHeight() + 4;
   }

   @Override
   public int getWidth(Font var1) {
      return this.backgroundWidth();
   }

   private int backgroundWidth() {
      return this.gridSizeX() * 18 + 2;
   }

   private int backgroundHeight() {
      return this.gridSizeY() * 20 + 2;
   }

   @Override
   public void renderImage(Font var1, int var2, int var3, GuiGraphics var4) {
      int var5 = this.gridSizeX();
      int var6 = this.gridSizeY();
      var4.blitSprite(BACKGROUND_SPRITE, var2, var3, this.backgroundWidth(), this.backgroundHeight());
      boolean var7 = this.contents.weight().compareTo(Fraction.ONE) >= 0;
      int var8 = 0;

      for (int var9 = 0; var9 < var6; var9++) {
         for (int var10 = 0; var10 < var5; var10++) {
            int var11 = var2 + var10 * 18 + 1;
            int var12 = var3 + var9 * 20 + 1;
            this.renderSlot(var11, var12, var8++, var7, var4, var1);
         }
      }
   }

   private void renderSlot(int var1, int var2, int var3, boolean var4, GuiGraphics var5, Font var6) {
      if (var3 >= this.contents.size()) {
         this.blit(var5, var1, var2, var4 ? ClientBundleTooltip.Texture.BLOCKED_SLOT : ClientBundleTooltip.Texture.SLOT);
      } else {
         ItemStack var7 = this.contents.getItemUnsafe(var3);
         this.blit(var5, var1, var2, ClientBundleTooltip.Texture.SLOT);
         var5.renderItem(var7, var1 + 1, var2 + 1, var3);
         var5.renderItemDecorations(var6, var7, var1 + 1, var2 + 1);
         if (var3 == 0) {
            AbstractContainerScreen.renderSlotHighlight(var5, var1 + 1, var2 + 1, 0);
         }
      }
   }

   private void blit(GuiGraphics var1, int var2, int var3, ClientBundleTooltip.Texture var4) {
      var1.blitSprite(var4.sprite, var2, var3, 0, var4.w, var4.h);
   }

   private int gridSizeX() {
      return Math.max(2, (int)Math.ceil(Math.sqrt((double)this.contents.size() + 1.0)));
   }

   private int gridSizeY() {
      return (int)Math.ceil(((double)this.contents.size() + 1.0) / (double)this.gridSizeX());
   }

   static enum Texture {
      BLOCKED_SLOT(new ResourceLocation("container/bundle/blocked_slot"), 18, 20),
      SLOT(new ResourceLocation("container/bundle/slot"), 18, 20);

      public final ResourceLocation sprite;
      public final int w;
      public final int h;

      private Texture(ResourceLocation var3, int var4, int var5) {
         this.sprite = var3;
         this.w = var4;
         this.h = var5;
      }
   }
}
