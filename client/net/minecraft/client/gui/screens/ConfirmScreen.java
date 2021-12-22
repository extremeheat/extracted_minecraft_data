package net.minecraft.client.gui.screens;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class ConfirmScreen extends Screen {
   private static final int LABEL_Y = 90;
   private final Component title2;
   private MultiLineLabel message;
   protected Component yesButton;
   protected Component noButton;
   private int delayTicker;
   protected final BooleanConsumer callback;
   private final List<Button> exitButtons;

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3) {
      this(var1, var2, var3, CommonComponents.GUI_YES, CommonComponents.GUI_NO);
   }

   public ConfirmScreen(BooleanConsumer var1, Component var2, Component var3, Component var4, Component var5) {
      super(var2);
      this.message = MultiLineLabel.EMPTY;
      this.exitButtons = Lists.newArrayList();
      this.callback = var1;
      this.title2 = var3;
      this.yesButton = var4;
      this.noButton = var5;
   }

   public Component getNarrationMessage() {
      return CommonComponents.joinForNarration(super.getNarrationMessage(), this.title2);
   }

   protected void init() {
      super.init();
      this.message = MultiLineLabel.create(this.font, this.title2, this.width - 50);
      int var10000 = this.message.getLineCount();
      Objects.requireNonNull(this.font);
      int var1 = var10000 * 9;
      int var2 = Mth.clamp(90 + var1 + 12, this.height / 6 + 96, this.height - 24);
      this.exitButtons.clear();
      this.addButtons(var2);
   }

   protected void addButtons(int var1) {
      this.addExitButton(new Button(this.width / 2 - 155, var1, 150, 20, this.yesButton, (var1x) -> {
         this.callback.accept(true);
      }));
      this.addExitButton(new Button(this.width / 2 - 155 + 160, var1, 150, 20, this.noButton, (var1x) -> {
         this.callback.accept(false);
      }));
   }

   protected void addExitButton(Button var1) {
      this.exitButtons.add((Button)this.addRenderableWidget(var1));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, 70, 16777215);
      this.message.renderCentered(var1, this.width / 2, 90);
      super.render(var1, var2, var3, var4);
   }

   public void setDelay(int var1) {
      this.delayTicker = var1;

      Button var3;
      for(Iterator var2 = this.exitButtons.iterator(); var2.hasNext(); var3.active = false) {
         var3 = (Button)var2.next();
      }

   }

   public void tick() {
      super.tick();
      Button var2;
      if (--this.delayTicker == 0) {
         for(Iterator var1 = this.exitButtons.iterator(); var1.hasNext(); var2.active = true) {
            var2 = (Button)var1.next();
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
