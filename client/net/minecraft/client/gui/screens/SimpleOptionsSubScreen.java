package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import javax.annotation.Nullable;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.network.chat.CommonComponents;
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

   @Override
   protected void init() {
      this.list = new OptionsList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
      this.list.addSmall(this.smallOptions);
      this.addWidget(this.list);
      this.createFooter();
      this.narratorButton = this.list.findOption(this.options.narrator());
      if (this.narratorButton != null) {
         this.narratorButton.active = this.minecraft.getNarrator().isActive();
      }
   }

   protected void createFooter() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_DONE, var1 -> this.minecraft.setScreen(this.lastScreen))
            .bounds(this.width / 2 - 100, this.height - 27, 200, 20)
            .build()
      );
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.basicListRender(var1, this.list, var2, var3, var4);
   }

   public void updateNarratorButton() {
      if (this.narratorButton instanceof CycleButton) {
         ((CycleButton)this.narratorButton).setValue(this.options.narrator().get());
      }
   }
}
