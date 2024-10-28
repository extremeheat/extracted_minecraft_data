package net.minecraft.client.gui.screens;

import javax.annotation.Nullable;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.Component;

public abstract class SimpleOptionsSubScreen extends OptionsSubScreen {
   protected final OptionInstance<?>[] smallOptions;
   @Nullable
   private AbstractWidget narratorButton;
   protected OptionsList list;

   public SimpleOptionsSubScreen(Screen var1, Options var2, Component var3, OptionInstance<?>[] var4) {
      super(var1, var2, var3);
      this.smallOptions = var4;
   }

   protected void init() {
      this.list = (OptionsList)this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height, this));
      this.list.addSmall(this.smallOptions);
      this.narratorButton = this.list.findOption(this.options.narrator());
      if (this.narratorButton != null) {
         this.narratorButton.active = this.minecraft.getNarrator().isActive();
      }

      super.init();
   }

   protected void repositionElements() {
      super.repositionElements();
      this.list.updateSize(this.width, this.layout);
   }

   public void updateNarratorButton() {
      if (this.narratorButton instanceof CycleButton) {
         ((CycleButton)this.narratorButton).setValue((NarratorStatus)this.options.narrator().get());
      }

   }
}
