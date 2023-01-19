package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;

public class RealmsClientOutdatedScreen extends RealmsScreen {
   private static final Component INCOMPATIBLE_TITLE = Component.translatable("mco.client.incompatible.title");
   private static final Component[] INCOMPATIBLE_MESSAGES_SNAPSHOT = new Component[]{
      Component.translatable("mco.client.incompatible.msg.line1"),
      Component.translatable("mco.client.incompatible.msg.line2"),
      Component.translatable("mco.client.incompatible.msg.line3")
   };
   private static final Component[] INCOMPATIBLE_MESSAGES = new Component[]{
      Component.translatable("mco.client.incompatible.msg.line1"), Component.translatable("mco.client.incompatible.msg.line2")
   };
   private final Screen lastScreen;

   public RealmsClientOutdatedScreen(Screen var1) {
      super(INCOMPATIBLE_TITLE);
      this.lastScreen = var1;
   }

   @Override
   public void init() {
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var1 -> this.minecraft.setScreen(this.lastScreen)).bounds(this.width / 2 - 100, row(12), 200, 20).build()
      );
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, row(3), 16711680);
      Component[] var5 = this.getMessages();

      for(int var6 = 0; var6 < var5.length; ++var6) {
         drawCenteredString(var1, this.font, var5[var6], this.width / 2, row(5) + var6 * 12, 16777215);
      }

      super.render(var1, var2, var3, var4);
   }

   private Component[] getMessages() {
      return this.minecraft.getGame().getVersion().isStable() ? INCOMPATIBLE_MESSAGES : INCOMPATIBLE_MESSAGES_SNAPSHOT;
   }

   @Override
   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 257 && var1 != 335 && var1 != 256) {
         return super.keyPressed(var1, var2, var3);
      } else {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      }
   }
}
