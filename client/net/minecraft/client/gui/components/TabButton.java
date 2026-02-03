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

   public TabButton(final TabManager tabManager, final Tab tab, final int width, final int height) {
      super(0, 0, width, height, tab.getTabTitle());
      this.tabManager = tabManager;
      this.tab = tab;
   }

   public void renderWidget(final GuiGraphics graphics, final int mouseX, final int mouseY, final float a) {
      graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.isSelected(), this.isHoveredOrFocused()), this.getX(), this.getY(), this.width, this.height);
      Font font = Minecraft.getInstance().font;
      int underlineColor = this.active ? -1 : -6250336;
      if (this.isSelected()) {
         this.renderMenuBackground(graphics, this.getX() + 2, this.getY() + 2, this.getRight() - 2, this.getBottom());
         this.renderFocusUnderline(graphics, font, underlineColor);
      }

      this.renderLabel(graphics.textRendererForWidget(this, GuiGraphics.HoveredTextEffects.NONE));
      this.handleCursor(graphics);
   }

   protected void renderMenuBackground(final GuiGraphics graphics, final int x0, final int y0, final int x1, final int y1) {
      Screen.renderMenuBackgroundTexture(graphics, Screen.MENU_BACKGROUND, x0, y0, 0.0F, 0.0F, x1 - x0, y1 - y0);
   }

   private void renderLabel(final ActiveTextCollector output) {
      int left = this.getX() + 1;
      int top = this.getY() + (this.isSelected() ? 0 : 3);
      int right = this.getX() + this.getWidth() - 1;
      int bottom = this.getY() + this.getHeight();
      output.acceptScrollingWithDefaultCenter(this.getMessage(), left, right, top, bottom);
   }

   private void renderFocusUnderline(final GuiGraphics graphics, final Font font, final int color) {
      int width = Math.min(font.width((FormattedText)this.getMessage()), this.getWidth() - 4);
      int left = this.getX() + (this.getWidth() - width) / 2;
      int top = this.getY() + this.getHeight() - 2;
      graphics.fill(left, top, left + width, top + 1, color);
   }

   protected void updateWidgetNarration(final NarrationElementOutput output) {
      output.add(NarratedElementType.TITLE, (Component)Component.translatable("gui.narrate.tab", this.tab.getTabTitle()));
      output.add(NarratedElementType.HINT, this.tab.getTabExtraNarration());
   }

   public void playDownSound(final SoundManager soundManager) {
   }

   public Tab tab() {
      return this.tab;
   }

   public boolean isSelected() {
      return this.tabManager.getCurrentTab() == this.tab;
   }
}
