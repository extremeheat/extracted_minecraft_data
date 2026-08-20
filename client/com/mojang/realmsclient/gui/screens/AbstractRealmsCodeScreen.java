package com.mojang.realmsclient.gui.screens;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.Identifier;

public abstract class AbstractRealmsCodeScreen extends RealmsScreen {
   private static final Identifier MENU_LIST_BACKGROUND = Identifier.withDefaultNamespace("textures/gui/menu_list_background.png");
   protected final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   protected AbstractRealmsCodeScreen(final Component title) {
      super(title);
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   protected void extractMenuBackground(final GuiGraphicsExtractor graphics) {
      super.extractMenuBackground(graphics, 0, 0, this.width, this.height);
      if (this.shouldRenderListBackgroundAndSeparators()) {
         graphics.blit(RenderPipelines.GUI_TEXTURED, MENU_LIST_BACKGROUND, 0, this.layout.getHeaderHeight(), 0.0F, 0.0F, this.width, this.layout.getContentHeight(), 32, 32);
      }

   }

   public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
      super.extractRenderState(graphics, mouseX, mouseY, a);
      if (this.shouldRenderListBackgroundAndSeparators()) {
         graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.HEADER_SEPARATOR, 0, this.layout.getHeaderHeight() - 2, 0.0F, 0.0F, this.width, 2, 32, 2);
         graphics.blit(RenderPipelines.GUI_TEXTURED, Screen.FOOTER_SEPARATOR, 0, this.height - this.layout.getFooterHeight(), 0.0F, 0.0F, this.width, 2, 32, 2);
      }

   }

   protected boolean shouldRenderListBackgroundAndSeparators() {
      return true;
   }
}
