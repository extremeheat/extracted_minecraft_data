package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsConfirmScreen extends RealmsScreen {
   protected BooleanConsumer callback;
   private final Component title1;
   private final Component title2;
   private int delayTicker;

   public RealmsConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      super();
      this.callback = var1;
      this.title1 = var2;
      this.title2 = var3;
   }

   public void init() {
      this.addButton(new Button(this.width / 2 - 105, row(9), 100, 20, CommonComponents.GUI_YES, (var1) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 + 5, row(9), 100, 20, CommonComponents.GUI_NO, (var1) -> {
         this.callback.accept(false);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title1, this.width / 2, row(3), 16777215);
      drawCenteredString(var1, this.font, this.title2, this.width / 2, row(5), 16777215);
      super.render(var1, var2, var3, var4);
   }

   public void tick() {
      super.tick();
      AbstractWidget var2;
      if (--this.delayTicker == 0) {
         for(Iterator var1 = this.buttons.iterator(); var1.hasNext(); var2.active = true) {
            var2 = (AbstractWidget)var1.next();
         }
      }

   }
}
