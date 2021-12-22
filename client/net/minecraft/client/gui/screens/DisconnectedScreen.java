package net.minecraft.client.gui.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class DisconnectedScreen extends Screen {
   private final Component reason;
   private MultiLineLabel message;
   private final Screen parent;
   private int textHeight;

   public DisconnectedScreen(Screen var1, Component var2, Component var3) {
      super(var2);
      this.message = MultiLineLabel.EMPTY;
      this.parent = var1;
      this.reason = var3;
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   protected void init() {
      this.message = MultiLineLabel.create(this.font, this.reason, this.width - 50);
      int var10001 = this.message.getLineCount();
      Objects.requireNonNull(this.font);
      this.textHeight = var10001 * 9;
      int var10003 = this.width / 2 - 100;
      int var10004 = this.height / 2 + this.textHeight / 2;
      Objects.requireNonNull(this.font);
      this.addRenderableWidget(new Button(var10003, Math.min(var10004 + 9, this.height - 30), 200, 20, new TranslatableComponent("gui.toMenu"), (var1) -> {
         this.minecraft.setScreen(this.parent);
      }));
   }

   public void render(PoseStack var1, int var2, int var3, float var4) {
      this.renderBackground(var1);
      Font var10001 = this.font;
      Component var10002 = this.title;
      int var10003 = this.width / 2;
      int var10004 = this.height / 2 - this.textHeight / 2;
      Objects.requireNonNull(this.font);
      drawCenteredString(var1, var10001, var10002, var10003, var10004 - 9 * 2, 11184810);
      this.message.renderCentered(var1, this.width / 2, this.height / 2 - this.textHeight / 2);
      super.render(var1, var2, var3, var4);
   }
}
