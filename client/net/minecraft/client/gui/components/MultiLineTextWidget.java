package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class MultiLineTextWidget extends AbstractWidget {
   private final MultiLineLabel multiLineLabel;
   private final int lineHeight;
   private final boolean centered;

   private MultiLineTextWidget(MultiLineLabel var1, Font var2, Component var3, boolean var4) {
      super(0, 0, var1.getWidth(), var1.getLineCount() * 9, var3);
      this.multiLineLabel = var1;
      this.lineHeight = 9;
      this.centered = var4;
      this.active = false;
   }

   public static MultiLineTextWidget createCentered(int var0, Font var1, Component var2) {
      MultiLineLabel var3 = MultiLineLabel.create(var1, var2, var0);
      return new MultiLineTextWidget(var3, var1, var2, true);
   }

   public static MultiLineTextWidget create(int var0, Font var1, Component var2) {
      MultiLineLabel var3 = MultiLineLabel.create(var1, var2, var0);
      return new MultiLineTextWidget(var3, var1, var2, false);
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput var1) {
   }

   @Override
   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      if (this.centered) {
         this.multiLineLabel.renderCentered(var1, this.getX() + this.getWidth() / 2, this.getY(), this.lineHeight, 16777215);
      } else {
         this.multiLineLabel.renderLeftAligned(var1, this.getX(), this.getY(), this.lineHeight, 16777215);
      }
   }
}
