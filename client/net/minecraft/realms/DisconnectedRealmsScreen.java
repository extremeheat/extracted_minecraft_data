package net.minecraft.realms;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class DisconnectedRealmsScreen extends RealmsScreen {
   private final Component reason;
   private MultiLineLabel message = MultiLineLabel.EMPTY;
   private final Screen parent;
   private int textHeight;

   public DisconnectedRealmsScreen(Screen var1, Component var2, Component var3) {
      super(var2);
      this.parent = var1;
      this.reason = var3;
   }

   @Override
   public void init() {
      Minecraft var1 = Minecraft.getInstance();
      var1.setConnectedToRealms(false);
      var1.getDownloadedPackSource().clearServerPack();
      this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
      this.textHeight = this.message.getLineCount() * 9;
      this.addRenderableWidget(
         Button.builder(CommonComponents.GUI_BACK, var2 -> var1.setScreen(this.parent))
            .bounds(this.width / 2 - 100, this.height / 2 + this.textHeight / 2 + 9, 200, 20)
            .build()
      );
   }

   @Override
   public Component getNarrationMessage() {
      return Component.empty().append(this.title).append(": ").append(this.reason);
   }

   @Override
   public void onClose() {
      Minecraft.getInstance().setScreen(this.parent);
   }

   @Override
   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      drawCenteredString(var1, this.font, this.title, this.width / 2, this.height / 2 - this.textHeight / 2 - 9 * 2, 11184810);
      this.message.renderCentered(var1, this.width / 2, this.height / 2 - this.textHeight / 2);
      super.render(var1, var2, var3, var4);
   }
}
