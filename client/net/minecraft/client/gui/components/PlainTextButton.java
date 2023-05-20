package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.Style;
import net.minecraft.util.Mth;

public class PlainTextButton extends Button {
   private final Font font;
   private final Component message;
   private final Component underlinedMessage;

   public PlainTextButton(int var1, int var2, int var3, int var4, Component var5, Button.OnPress var6, Font var7) {
      super(var1, var2, var3, var4, var5, var6, DEFAULT_NARRATION);
      this.font = var7;
      this.message = var5;
      this.underlinedMessage = ComponentUtils.mergeStyles(var5.copy(), Style.EMPTY.withUnderlined(true));
   }

   @Override
   public void renderWidget(PoseStack var1, int var2, int var3, float var4) {
      Component var5 = this.isHoveredOrFocused() ? this.underlinedMessage : this.message;
      drawString(var1, this.font, var5, this.getX(), this.getY(), 16777215 | Mth.ceil(this.alpha * 255.0F) << 24);
   }
}
