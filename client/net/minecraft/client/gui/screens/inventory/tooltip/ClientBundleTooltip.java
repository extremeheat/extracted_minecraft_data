package net.minecraft.client.gui.screens.inventory.tooltip;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BundleContents;
import org.apache.commons.lang3.math.Fraction;

public class ClientBundleTooltip implements ClientTooltipComponent {
   private static final ResourceLocation PROGRESSBAR_BORDER_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_border");
   private static final ResourceLocation PROGRESSBAR_FILL_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_fill");
   private static final ResourceLocation PROGRESSBAR_FULL_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/bundle_progressbar_full");
   private static final ResourceLocation SLOT_HIGHLIGHT_BACK_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot_highlight_back");
   private static final ResourceLocation SLOT_HIGHLIGHT_FRONT_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot_highlight_front");
   private static final ResourceLocation SLOT_BACKGROUND_SPRITE = ResourceLocation.withDefaultNamespace("container/bundle/slot_background");
   private static final int SLOT_MARGIN = 4;
   private static final int SLOT_SIZE = 24;
   private static final int GRID_WIDTH = 96;
   private static final int PROGRESSBAR_HEIGHT = 13;
   private static final int PROGRESSBAR_WIDTH = 96;
   private static final int PROGRESSBAR_BORDER = 1;
   private static final int PROGRESSBAR_FILL_MAX = 94;
   private static final int PROGRESSBAR_MARGIN_Y = 4;
   private static final Component BUNDLE_FULL_TEXT = Component.translatable("item.minecraft.bundle.full");
   private static final Component BUNDLE_EMPTY_TEXT = Component.translatable("item.minecraft.bundle.empty");
   private static final Component BUNDLE_EMPTY_DESCRIPTION = Component.translatable("item.minecraft.bundle.empty.description");
   private final BundleContents contents;

   public ClientBundleTooltip(BundleContents var1) {
      super();
      this.contents = var1;
   }

   @Override
   public int getHeight(Font var1) {
      return this.contents.isEmpty() ? getEmptyBundleBackgroundHeight(var1) : this.backgroundHeight();
   }

   @Override
   public int getWidth(Font var1) {
      return 96;
   }

   @Override
   public boolean showTooltipWithItemInHand() {
      return true;
   }

   private static int getEmptyBundleBackgroundHeight(Font var0) {
      return getEmptyBundleDescriptionTextHeight(var0) + 13 + 8;
   }

   private int backgroundHeight() {
      return this.itemGridHeight() + 13 + 8;
   }

   private int itemGridHeight() {
      return this.gridSizeY() * 24;
   }

   private int getContentXOffset(int var1) {
      return (var1 - 96) / 2;
   }

   private int gridSizeY() {
      return Mth.positiveCeilDiv(this.slotCount(), 4);
   }

   private int slotCount() {
      return Math.min(12, this.contents.size());
   }

   @Override
   public void renderImage(Font var1, int var2, int var3, int var4, int var5, GuiGraphics var6) {
      if (this.contents.isEmpty()) {
         this.renderEmptyBundleTooltip(var1, var2, var3, var4, var5, var6);
      } else {
         this.renderBundleWithItemsTooltip(var1, var2, var3, var4, var5, var6);
      }
   }

   private void renderEmptyBundleTooltip(Font var1, int var2, int var3, int var4, int var5, GuiGraphics var6) {
      drawEmptyBundleDescriptionText(var2 + this.getContentXOffset(var4), var3, var1, var6);
      this.drawProgressbar(var2 + this.getContentXOffset(var4), var3 + getEmptyBundleDescriptionTextHeight(var1) + 4, var1, var6);
   }

   private void renderBundleWithItemsTooltip(Font var1, int var2, int var3, int var4, int var5, GuiGraphics var6) {
      boolean var7 = this.contents.size() > 12;
      List var8 = this.getShownItems(this.contents.getNumberOfItemsToShow());
      int var9 = var2 + this.getContentXOffset(var4) + 96;
      int var10 = var3 + this.gridSizeY() * 24;
      int var11 = 1;

      for (int var12 = 1; var12 <= this.gridSizeY(); var12++) {
         for (int var13 = 1; var13 <= 4; var13++) {
            int var14 = var9 - var13 * 24;
            int var15 = var10 - var12 * 24;
            if (shouldRenderSurplusText(var7, var13, var12)) {
               renderCount(var14, var15, this.getAmountOfHiddenItems(var8), var1, var6);
            } else if (shouldRenderItemSlot(var8, var11)) {
               this.renderSlot(var11, var14, var15, var8, var11, var1, var6);
               var11++;
            }
         }
      }

      this.drawSelectedItemTooltip(var1, var6, var2, var3, var4);
      this.drawProgressbar(var2 + this.getContentXOffset(var4), var3 + this.itemGridHeight() + 4, var1, var6);
   }

   private List<ItemStack> getShownItems(int var1) {
      int var2 = Math.min(this.contents.size(), var1);
      return this.contents.itemCopyStream().toList().subList(0, var2);
   }

   private static boolean shouldRenderSurplusText(boolean var0, int var1, int var2) {
      return var0 && var1 * var2 == 1;
   }

   private static boolean shouldRenderItemSlot(List<ItemStack> var0, int var1) {
      return var0.size() >= var1;
   }

   private int getAmountOfHiddenItems(List<ItemStack> var1) {
      return this.contents.itemCopyStream().skip((long)var1.size()).mapToInt(ItemStack::getCount).sum();
   }

   private void renderSlot(int var1, int var2, int var3, List<ItemStack> var4, int var5, Font var6, GuiGraphics var7) {
      int var8 = var4.size() - var1;
      boolean var9 = var8 == this.contents.getSelectedItem();
      ItemStack var10 = (ItemStack)var4.get(var8);
      if (var9) {
         var7.blitSprite(RenderType::guiTextured, SLOT_HIGHLIGHT_BACK_SPRITE, var2, var3, 24, 24);
      } else {
         var7.blitSprite(RenderType::guiTextured, SLOT_BACKGROUND_SPRITE, var2, var3, 24, 24);
      }

      var7.renderItem(var10, var2 + 4, var3 + 4, var5);
      var7.renderItemDecorations(var6, var10, var2 + 4, var3 + 4);
      if (var9) {
         var7.blitSprite(RenderType::guiTexturedOverlay, SLOT_HIGHLIGHT_FRONT_SPRITE, var2, var3, 24, 24);
      }
   }

   private static void renderCount(int var0, int var1, int var2, Font var3, GuiGraphics var4) {
      var4.drawCenteredString(var3, "+" + var2, var0 + 12, var1 + 10, 16777215);
   }

   private void drawSelectedItemTooltip(Font var1, GuiGraphics var2, int var3, int var4, int var5) {
      if (this.contents.hasSelectedItem()) {
         ItemStack var6 = this.contents.getItemUnsafe(this.contents.getSelectedItem());
         Component var7 = var6.getStyledHoverName();
         int var8 = var1.width(var7.getVisualOrderText());
         int var9 = var3 + var5 / 2 - 12;
         var2.renderTooltip(var1, var7, var9 - var8 / 2, var4 - 15, var6.get(DataComponents.TOOLTIP_STYLE));
      }
   }

   private void drawProgressbar(int var1, int var2, Font var3, GuiGraphics var4) {
      var4.blitSprite(RenderType::guiTextured, this.getProgressBarTexture(), var1 + 1, var2, this.getProgressBarFill(), 13);
      var4.blitSprite(RenderType::guiTextured, PROGRESSBAR_BORDER_SPRITE, var1, var2, 96, 13);
      Component var5 = this.getProgressBarFillText();
      if (var5 != null) {
         var4.drawCenteredString(var3, var5, var1 + 48, var2 + 3, 16777215);
      }
   }

   private static void drawEmptyBundleDescriptionText(int var0, int var1, Font var2, GuiGraphics var3) {
      var3.drawWordWrap(var2, BUNDLE_EMPTY_DESCRIPTION, var0, var1, 96, 11184810);
   }

   private static int getEmptyBundleDescriptionTextHeight(Font var0) {
      return var0.split(BUNDLE_EMPTY_DESCRIPTION, 96).size() * 9;
   }

   private int getProgressBarFill() {
      return Mth.clamp(Mth.mulAndTruncate(this.contents.weight(), 94), 0, 94);
   }

   private ResourceLocation getProgressBarTexture() {
      return this.contents.weight().compareTo(Fraction.ONE) >= 0 ? PROGRESSBAR_FULL_SPRITE : PROGRESSBAR_FILL_SPRITE;
   }

   @Nullable
   private Component getProgressBarFillText() {
      if (this.contents.isEmpty()) {
         return BUNDLE_EMPTY_TEXT;
      } else {
         return this.contents.weight().compareTo(Fraction.ONE) >= 0 ? BUNDLE_FULL_TEXT : null;
      }
   }
}
