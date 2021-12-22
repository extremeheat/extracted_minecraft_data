package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongConfirmationScreen extends RealmsScreen {
   private final RealmsLongConfirmationScreen.Type type;
   private final Component line2;
   private final Component line3;
   protected final BooleanConsumer callback;
   private final boolean yesNoQuestion;

   public RealmsLongConfirmationScreen(BooleanConsumer var1, RealmsLongConfirmationScreen.Type var2, Component var3, Component var4, boolean var5) {
      super(NarratorChatListener.NO_TITLE);
      this.callback = var1;
      this.type = var2;
      this.line2 = var3;
      this.line3 = var4;
      this.yesNoQuestion = var5;
   }

   public void init() {
      if (this.yesNoQuestion) {
         this.addRenderableWidget(new Button(this.width / 2 - 105, row(8), 100, 20, CommonComponents.GUI_YES, (var1) -> {
            this.callback.accept(true);
         }));
         this.addRenderableWidget(new Button(this.width / 2 + 5, row(8), 100, 20, CommonComponents.GUI_NO, (var1) -> {
            this.callback.accept(false);
         }));
      } else {
         this.addRenderableWidget(new Button(this.width / 2 - 50, row(8), 100, 20, new TranslatableComponent("mco.gui.ok"), (var1) -> {
            this.callback.accept(true);
         }));
      }

   }

   public Component getNarrationMessage() {
      return CommonComponents.joinLines(this.type.text, this.line2, this.line3);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 == 256) {
         this.callback.accept(false);
         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.type.text, this.width / 2, row(2), this.type.colorCode);
      drawCenteredString(var1, this.font, this.line2, this.width / 2, row(4), 16777215);
      drawCenteredString(var1, this.font, this.line3, this.width / 2, row(6), 16777215);
      super.render(var1, var2, var3, var4);
   }

   public static enum Type {
      Warning("Warning!", 16711680),
      Info("Info!", 8226750);

      public final int colorCode;
      public final Component text;

      private Type(String var3, int var4) {
         this.text = new TextComponent(var3);
         this.colorCode = var4;
      }

      // $FF: synthetic method
      private static RealmsLongConfirmationScreen.Type[] $values() {
         return new RealmsLongConfirmationScreen.Type[]{Warning, Info};
      }
   }
}
