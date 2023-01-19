package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.List;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ConfirmScreen extends Screen {
   private static final int LABEL_Y = 90;
   private final Component title2;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   protected Component yesButton;
   protected Component noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;
   private final List<Button> exitButtons = Lists.newArrayList();

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      this(var1, var2, var3, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
   }

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3, Component var4, Component var5) {
      super(var2);
      this.callback = var1;
      this.title2 = var3;
      this.yesButton = var4;
      this.noButton = var5;
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.title2);
   }

   @Override
   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.title2, this.width - 50);
      int var1 = this.message.getLineCount() * 9;
      int var2 = Mth.clamp(90 + var1 + 12, this.height / 6 + 96, this.height - 24);
      this.exitButtons.clear();
      this.addButtons(var2);
   }

   protected void addButtons(int var1) {
      this.addExitButton(new Button(this.width / 2 - 155, var1, 150, 20, this.yesButton, var1x -> this.callback.accept(true)));
      this.addExitButton(new Button(this.width / 2 - 155 + 160, var1, 150, 20, this.noButton, var1x -> this.callback.accept(false)));
   }

   protected void addExitButton(Button var1) {
      this.exitButtons.add(this.addRenderableWidget(var1));
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(var1, this.width / 2, 90);
      super.render(var1, var2, var3, var4);
   }

   public void setDelay(int var1) {
      this.delayTicker = var1;

      for(Button var3 : this.exitButtons) {
         var3.active = false;
      }
   }

   @Override
   public void tick() {
      super.tick();
      if (--this.delayTicker == 0) {
         for(Button var2 : this.exitButtons) {
            var2.active = true;
         }
      }
   }

   @Override
   public boolean shouldCloseOnEsc() {
      return false;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }
}
