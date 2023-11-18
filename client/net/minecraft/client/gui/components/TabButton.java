package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class TabButton extends AbstractWidget {
   private static final ResourceLocation TEXTURE_LOCATION = new ResourceLocation("textures/gui/tab_button.png");
   private static final int TEXTURE_WIDTH = 130;
   private static final int TEXTURE_HEIGHT = 24;
   private static final int TEXTURE_BORDER = 2;
   private static final int TEXTURE_BORDER_BOTTOM = 0;
   private static final int SELECTED_OFFSET = 3;
   private static final int TEXT_MARGIN = 1;
   private static final int UNDERLINE_HEIGHT = 1;
   private static final int UNDERLINE_MARGIN_X = 4;
   private static final int UNDERLINE_MARGIN_BOTTOM = 2;
   private final TabManager tabManager;
   private final Tab tab;

   public TabButton(TabManager var1, Tab var2, int var3, int var4) {
      super(0, 0, var3, var4, var2.getTabTitle());
      this.tabManager = var1;
      this.tab = var2;
   }

   @Override
   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      var1.blitNineSliced(TEXTURE_LOCATION, this.getX(), this.getY(), this.width, this.height, 2, 2, 2, 0, 130, 24, 0, this.getTextureY());
      Font var5 = Minecraft.getInstance().font;
      int var6 = this.active ? -1 : -6250336;
      this.renderString(var1, var5, var6);
      if (this.isSelected()) {
         this.renderFocusUnderline(var1, var5, var6);
      }
   }

   public void renderString(GuiGraphics var1, Font var2, int var3) {
      int var4 = this.getX() + 1;
      int var5 = this.getY() + (this.isSelected() ? 0 : 3);
      int var6 = this.getX() + this.getWidth() - 1;
      int var7 = this.getY() + this.getHeight();
      renderScrollingString(var1, var2, this.getMessage(), var4, var5, var6, var7, var3);
   }

   private void renderFocusUnderline(GuiGraphics var1, Font var2, int var3) {
      int var4 = Math.min(var2.width(this.getMessage()), this.getWidth() - 4);
      int var5 = this.getX() + (this.getWidth() - var4) / 2;
      int var6 = this.getY() + this.getHeight() - 2;
      var1.fill(var5, var6, var5 + var4, var6 + 1, var3);
   }

   protected int getTextureY() {
      byte var1 = 2;
      if (this.isSelected() && this.isHoveredOrFocused()) {
         var1 = 1;
      } else if (this.isSelected()) {
         var1 = 0;
      } else if (this.isHoveredOrFocused()) {
         var1 = 3;
      }

      return var1 * 24;
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, Component.translatable("gui.narrate.tab", this.tab.getTabTitle()));
   }

   @Override
   public void playDownSound(SoundManager var1) {
   }

   public Tab tab() {
      return this.tab;
   }

   public boolean isSelected() {
      return this.tabManager.getCurrentTab() == this.tab;
   }
}
