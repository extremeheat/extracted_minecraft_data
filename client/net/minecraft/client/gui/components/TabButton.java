package net.minecraft.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;

public class TabButton extends AbstractWidget.WithInactiveMessage {
   private static final WidgetSprites SPRITES = new WidgetSprites(Identifier.withDefaultNamespace("widget/tab_selected"), Identifier.withDefaultNamespace("widget/tab"), Identifier.withDefaultNamespace("widget/tab_selected_highlighted"), Identifier.withDefaultNamespace("widget/tab_highlighted"));
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

   public void renderWidget(GuiGraphics var1, int var2, int var3, float var4) {
      var1.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.isSelected(), this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
      Font var5 = Minecraft.getInstance().font;
      int var6 = this.active ? -1 : -6250336;
      if (this.isSelected()) {
         this.renderMenuBackground(var1, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom());
         this.renderFocusUnderline(var1, var5, var6);
      }

      this.renderLabel(var1.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
      this.handleCursor(var1);
   }

   protected void renderMenuBackground(GuiGraphics var1, int var2, int var3, int var4, int var5) {
      Screen.renderMenuBackgroundTexture(var1, Screen.MENU_BACKGROUND, var2, var3, 0.0F, 0.0F, var4 - var2, var5 - var3);
   }

   private void renderLabel(ActiveTextCollector var1) {
      int var2 = this.getX() + 1;
      int var3 = this.getY() + (this.isSelected() ? 0 : 3);
      int var4 = this.getX() + this.getWidth() - 1;
      int var5 = this.getY() + this.getHeight();
      var1.acceptScrollingWithDefaultCenter(this.getMessage(), var2, var4, var3, var5);
   }

   private void renderFocusUnderline(GuiGraphics var1, Font var2, int var3) {
      int var4 = Math.min(var2.width((FormattedText)this.getMessage()), this.getWidth() - 4);
      int var5 = this.getX() + (this.getWidth() - var4) / 2;
      int var6 = this.getY() + this.getHeight() - 2;
      var1.fill(var5, var6, var5 + var4, var6 + 1, var3);
   }

   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, (Component)Component.translatable("gui.narrate.tab", this.tab.getTabTitle()));
      var1.add(NarratedElementType.HINT, this.tab.getTabExtraNarration());
   }

   public void playDownSound(SoundManager var1) {
   }

   public Tab tab() {
      return this.tab;
   }

   public boolean isSelected() {
      return this.tabManager.getCurrentTab() == this.tab;
   }
}
