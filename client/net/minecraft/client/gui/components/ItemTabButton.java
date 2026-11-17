package net.minecraft.client.gui.components;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.GuiPipeline;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class ItemTabButton extends TabButton {
   private static final int ITEM_SIZE = 16;
   private static final float ANIMATION_TIME = 15.0F;
   private static final float ANIMATION_SCALE = 0.1F;
   private final WidgetSprites sprites;
   private final ItemStack icon;
   private final @Nullable ItemStack secondaryIcon;
   private final int secondaryIconSpacing;
   private final int iconXOffset;
   private final int iconYOffset;
   private final int xOffsetWhenSelected;
   private float animationTime;

   private ItemTabButton(final TabManager tabManager, final Tab tab, final int width, final int height, final Component message, final WidgetSprites sprites, final ItemStack icon, final @Nullable ItemStack secondaryIcon, final int secondaryIconSpacing, final int iconXOffset, final int iconYOffset, final int xOffsetWhenSelected) {
      super(tabManager, tab, width, height, message);
      this.sprites = sprites;
      this.icon = icon;
      this.secondaryIcon = secondaryIcon;
      this.secondaryIconSpacing = secondaryIconSpacing;
      this.iconXOffset = iconXOffset;
      this.iconYOffset = iconYOffset;
      this.xOffsetWhenSelected = xOffsetWhenSelected;
      this.setTooltip(Tooltip.create(message, (Component)null));
   }

   public static Builder builder(final TabManager tabManager, final Tab tab, final WidgetSprites sprites) {
      return new Builder(tabManager, tab, sprites);
   }

   public void startAnimation() {
      this.animationTime = 15.0F;
   }

   protected void extractWidgetRenderState(final GuiGraphicsExtractor graphics) {
      boolean isAnimating = this.animationTime > 0.0F;
      float centerX = (float)this.getX() + (float)this.getWidth() / 2.0F;
      float centerY = (float)this.getY() + (float)this.getHeight() / 2.0F;
      if (isAnimating) {
         float squeeze = 1.0F + 0.1F * (float)Math.sin((double)(this.animationTime / 15.0F * 3.1415927F));
         graphics.pose().pushMatrix();
         graphics.pose().translate(centerX, centerY);
         graphics.pose().scale(1.0F, squeeze);
         graphics.pose().translate(-centerX, -centerY);
      }

      int xOffset = this.isSelected() ? this.xOffsetWhenSelected : 0;
      graphics.blitSprite(GuiPipeline.TEXTURED, this.sprites.get(true, this.isSelected()), this.getX() + xOffset, this.getY(), this.getWidth(), this.getHeight());
      int iconsWidth = this.secondaryIcon == null ? 16 : 16 + this.secondaryIconSpacing;
      int iconX = this.getX() + xOffset + (this.getWidth() - iconsWidth) / 2 + this.iconXOffset;
      int iconY = this.getY() + (this.getHeight() - 16) / 2 + this.iconYOffset;
      graphics.fakeItem(this.icon, iconX, iconY);
      if (this.secondaryIcon != null) {
         graphics.fakeItem(this.secondaryIcon, iconX + this.secondaryIconSpacing, iconY);
      }

      if (!this.isSelected()) {
         this.handleCursor(graphics);
      }

      if (isAnimating) {
         graphics.pose().popMatrix();
         this.animationTime -= graphics.partialTicks();
      }

   }

   public static class Builder {
      private final TabManager tabManager;
      private final Tab tab;
      private final WidgetSprites sprites;
      private ItemStack icon;
      private @Nullable ItemStack secondaryIcon;
      private int secondaryIconSpacing;
      private int iconXOffset;
      private int iconYOffset;
      private int width;
      private int height;
      private int xOffsetWhenSelected;
      private Component message;

      private Builder(final TabManager tabManager, final Tab tab, final WidgetSprites sprites) {
         super();
         this.icon = ItemStack.EMPTY;
         this.message = CommonComponents.EMPTY;
         this.tabManager = tabManager;
         this.tab = tab;
         this.sprites = sprites;
      }

      public Builder size(final int width, final int height) {
         this.width = width;
         this.height = height;
         return this;
      }

      public Builder icon(final ItemStack icon) {
         this.icon = icon;
         return this;
      }

      public Builder icons(final ItemStack icon, final ItemStack secondaryIcon, final int spacing) {
         this.icon = icon;
         this.secondaryIcon = secondaryIcon;
         this.secondaryIconSpacing = spacing;
         return this;
      }

      public Builder iconXOffset(final int iconXOffset) {
         this.iconXOffset = iconXOffset;
         return this;
      }

      public Builder iconYOffset(final int iconYOffset) {
         this.iconYOffset = iconYOffset;
         return this;
      }

      public Builder xOffsetWhenSelected(final int xOffsetWhenSelected) {
         this.xOffsetWhenSelected = xOffsetWhenSelected;
         return this;
      }

      public Builder message(final Component message) {
         this.message = message;
         return this;
      }

      public ItemTabButton build() {
         return new ItemTabButton(this.tabManager, this.tab, this.width, this.height, this.message, this.sprites, this.icon, this.secondaryIcon, this.secondaryIconSpacing, this.iconXOffset, this.iconYOffset, this.xOffsetWhenSelected);
      }
   }
}
