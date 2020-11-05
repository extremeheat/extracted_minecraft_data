package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfirmScreen extends Screen {
   private final Component title2;
   private MultiLineLabel message;
   protected Component yesButton;
   protected Component noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      this(var1, var2, var3, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
   }

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3, Component var4, Component var5) {
      super(var2);
      this.message = MultiLineLabel.EMPTY;
      this.callback = var1;
      this.title2 = var3;
      this.yesButton = var4;
      this.noButton = var5;
   }

   public String getNarrationMessage() {
      return super.getNarrationMessage() + ". " + this.title2.getString();
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96, 150, 20, this.yesButton, (var1) -> {
         this.callback.accept(true);
      }));
      this.addButton(new Button(this.width / 2 - 155 + 160, this.height / 6 + 96, 150, 20, this.noButton, (var1) -> {
         this.callback.accept(false);
      }));
      this.message = MultiLineLabel.create(this.font, this.title2, this.width - 50);
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(var1, this.width / 2, 90);
      super.render(var1, var2, var3, var4);
   }

   public void setDelay(int var1) {
      this.delayTicker = var1;

      AbstractWidget var3;
      for(Iterator var2 = this.buttons.iterator(); var2.hasNext(); var3.active = false) {
         var3 = (AbstractWidget)var2.next();
      }

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

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }
}
