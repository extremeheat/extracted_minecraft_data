package net.minecraft.client.gui.screens;

import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;
   public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public OptionsSubScreen(Screen var1, Options var2, Component var3) {
      super(var3);
      this.lastScreen = var1;
      this.options = var2;
   }

   @Override
   protected void init() {
      this.addTitle();
      this.addFooter();
      this.layout.visitWidgets(this::addRenderableWidget);
      this.repositionElements();
   }

   protected void addTitle() {
      this.layout.addTitleHeader(this.title, this.font);
   }

   protected void addFooter() {
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, var1 -> this.onClose()).width(200).build());
   }

   @Override
   protected void repositionElements() {
      this.layout.arrangeElements();
   }

   @Override
   public void removed() {
      this.minecraft.options.save();
   }

   @Override
   public void onClose() {
      for (GuiEventListener var2 : this.children()) {
         if (var2 instanceof OptionsList var3) {
            var3.applyUnsavedChanges();
         }
      }

      this.minecraft.setScreen(this.lastScreen);
   }
}
