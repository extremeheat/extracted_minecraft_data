package net.minecraft.client.gui.screens.options;

import javax.annotation.Nullable;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public abstract class OptionsSubScreen extends Screen {
   protected final Screen lastScreen;
   protected final Options options;
   @Nullable
   protected OptionsList list;
   public final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

   public OptionsSubScreen(Screen var1, Options var2, Component var3) {
      super(var3);
      this.lastScreen = var1;
      this.options = var2;
   }

   protected void init() {
      this.addTitle();
      this.addContents();
      this.addFooter();
      this.layout.visitWidgets((var1) -> {
         AbstractWidget var10000 = (AbstractWidget)this.addRenderableWidget(var1);
      });
      this.repositionElements();
   }

   protected void addTitle() {
      this.layout.addTitleHeader(this.title, this.font);
   }

   protected void addContents() {
      this.list = (OptionsList)this.layout.addToContents(new OptionsList(this.minecraft, this.width, this));
      this.addOptions();
      AbstractWidget var2 = this.list.findOption(this.options.narrator());
      if (var2 instanceof CycleButton var1) {
         this.narratorButton = var1;
         this.narratorButton.active = this.minecraft.getNarrator().isActive();
      }

   }

   protected abstract void addOptions();

   protected void addFooter() {
      this.layout.addToFooter(Button.builder(CommonComponents.GUI_DONE, (var1) -> {
         this.onClose();
      }).width(200).build());
   }

   protected void repositionElements() {
      this.layout.arrangeElements();
      if (this.list != null) {
         this.list.updateSize(this.width, this.layout);
      }

   }

   public void removed() {
      this.minecraft.options.save();
   }

   public void onClose() {
      if (this.list != null) {
         this.list.applyUnsavedChanges();
      }

      this.minecraft.setScreen(this.lastScreen);
   }
}
