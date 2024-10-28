package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen extends RealmsScreen {
   protected BooleanConsumer callback;
   private final Component title1;
   private final Component title2;

   public RealmsConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      super(GameNarrator.NO_TITLE);
      this.callback = var1;
      this.title1 = var2;
      this.title2 = var3;
   }

   public void init() {
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_YES, (var1) -> {
         this.callback.accept(true);
      }).bounds(this.width / 2 - 105, row(9), 100, 20).build());
      this.addRenderableWidget(Button.builder(CommonComponents.GUI_NO, (var1) -> {
         this.callback.accept(false);
      }).bounds(this.width / 2 + 5, row(9), 100, 20).build());
   }

   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, (Component)this.title1, this.width / 2, row(3), -1);
      var1.drawCenteredString(this.font, (Component)this.title2, this.width / 2, row(5), -1);
   }
}
