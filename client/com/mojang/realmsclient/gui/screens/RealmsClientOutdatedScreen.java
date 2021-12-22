package com.mojang.realmsclient.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsScreen;

public class RealmsClientOutdatedScreen extends RealmsScreen {
   private static final Component OUTDATED_TITLE = new TranslatableComponent("mco.client.outdated.title");
   private static final Component[] OUTDATED_MESSAGES = new Component[]{new TranslatableComponent("mco.client.outdated.msg.line1"), new TranslatableComponent("mco.client.outdated.msg.line2")};
   private static final Component INCOMPATIBLE_TITLE = new TranslatableComponent("mco.client.incompatible.title");
   private static final Component[] INCOMPATIBLE_MESSAGES = new Component[]{new TranslatableComponent("mco.client.incompatible.msg.line1"), new TranslatableComponent("mco.client.incompatible.msg.line2"), new TranslatableComponent("mco.client.incompatible.msg.line3")};
   private final Screen lastScreen;
   private final boolean outdated;

   public RealmsClientOutdatedScreen(Screen var1, boolean var2) {
      super(var2 ? OUTDATED_TITLE : INCOMPATIBLE_TITLE);
      this.lastScreen = var1;
      this.outdated = var2;
   }

   public void init() {
      this.addRenderableWidget(new Button(this.width / 2 - 100, row(12), 200, 20, CommonComponents.GUI_BACK, (var1) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, row(3), 16711680);
      Component[] var5 = this.outdated ? INCOMPATIBLE_MESSAGES : OUTDATED_MESSAGES;

      for(int var6 = 0; var6 < var5.length; ++var6) {
         drawCenteredString(var1, this.font, var5[var6], this.width / 2, row(5) + var6 * 12, 16777215);
      }

      super.render(var1, var2, var3, var4);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if (var1 != 257 && var1 != 335 && var1 != 256) {
         return super.keyPressed(var1, var2, var3);
      } else {
         this.minecraft.setScreen(this.lastScreen);
         return true;
      }
   }
}
