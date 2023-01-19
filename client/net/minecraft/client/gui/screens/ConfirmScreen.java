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
   private static final int MARGIN = 20;
   private final Component message;
   private MultiLineLabel multilineMessage = MultiLineLabel.EMPTY;
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
      this.message = var3;
      this.yesButton = var4;
      this.noButton = var5;
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.message);
   }

   @Override
   protected void init() {
      super.init();
      this.multilineMessage = MultiLineLabel.create(this.font, this.message, this.width - 50);
      int var1 = Mth.clamp(this.messageTop() + this.messageHeight() + 20, this.height / 6 + 96, this.height - 24);
      this.exitButtons.clear();
      this.addButtons(var1);
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
      drawCenteredString(var1, this.font, this.title, this.width / 2, this.titleTop(), 16777215);
      this.multilineMessage.renderCentered(var1, this.width / 2, this.messageTop());
      super.render(var1, var2, var3, var4);
   }

   private int titleTop() {
      int var1 = (this.height - this.messageHeight()) / 2;
      return Mth.clamp(var1 - 20 - 9, 10, 80);
   }

   private int messageTop() {
      return this.titleTop() + 20;
   }

   private int messageHeight() {
      return this.multilineMessage.getLineCount() * 9;
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
