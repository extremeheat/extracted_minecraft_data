package com.mojang.realmsclient.gui.screens;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongConfirmationScreen extends RealmsScreen {
   static final Component WARNING = Component.translatable("mco.warning");
   static final Component INFO = Component.translatable("mco.info");
   private final RealmsLongConfirmationScreen.Type type;
   private final Component line2;
   private final Component line3;
   protected final BooleanConsumer callback;
   private final boolean yesNoQuestion;

   public RealmsLongConfirmationScreen(BooleanConsumer var1, RealmsLongConfirmationScreen.Type var2, Component var3, Component var4, boolean var5) {
      super(GameNarrator.NO_TITLE);
      this.callback = var1;
      this.type = var2;
      this.line2 = var3;
      this.line3 = var4;
      this.yesNoQuestion = var5;
   }

   @Override
   public void init() {
      if (this.yesNoQuestion) {
         this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_YES, var1 -> this.callback.accept(true)).bounds(this.width / 2 - 105, row(8), 100, 20).build()
         );
         this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_NO, var1 -> this.callback.accept(false)).bounds(this.width / 2 + 5, row(8), 100, 20).build()
         );
      } else {
         this.addRenderableWidget(
            Button.builder(CommonComponents.GUI_OK, var1 -> this.callback.accept(true)).bounds(this.width / 2 - 50, row(8), 100, 20).build()
         );
      }
   }

   @Override
   public Component getNarrationMessage() {
      return CommonComponents.joinLines(this.type.text, this.line2, this.line3);
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

   @Override
   public void render(GuiGraphics var1, int var2, int var3, float var4) {
      super.render(var1, var2, var3, var4);
      var1.drawCenteredString(this.font, this.type.text, this.width / 2, row(2), this.type.colorCode);
      var1.drawCenteredString(this.font, this.line2, this.width / 2, row(4), -1);
      var1.drawCenteredString(this.font, this.line3, this.width / 2, row(6), -1);
   }

   public static enum Type {
      WARNING(RealmsLongConfirmationScreen.WARNING, -65536),
      INFO(RealmsLongConfirmationScreen.INFO, 8226750);

      public final int colorCode;
      public final Component text;

      private Type(final Component nullxx, final int nullxxx) {
         this.text = nullxx;
         this.colorCode = nullxxx;
      }
   }
}
