package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;

public class Button extends AbstractButton {
   public static final Button.OnTooltip NO_TOOLTIP = (var0, var1, var2, var3) -> {
   };
   protected final Button.OnPress onPress;
   protected final Button.OnTooltip onTooltip;

   public Button(int var1, int var2, int var3, int var4, Component var5, Button.OnPress var6) {
      this(var1, var2, var3, var4, var5, var6, NO_TOOLTIP);
   }

   public Button(int var1, int var2, int var3, int var4, Component var5, Button.OnPress var6, Button.OnTooltip var7) {
      super(var1, var2, var3, var4, var5);
      this.onPress = var6;
      this.onTooltip = var7;
   }

   public void onPress() {
      this.onPress.onPress(this);
   }

   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      super.renderButton(var1, var2, var3, var4);
      if (this.isHovered()) {
         this.renderToolTip(var1, var2, var3);
      }

   }

   public void renderToolTip(PoseStack var1, int var2, int var3) {
      this.onTooltip.onTooltip(this, var1, var2, var3);
   }

   public interface OnTooltip {
      void onTooltip(Button var1, PoseStack var2, int var3, int var4);
   }

   public interface OnPress {
      void onPress(Button var1);
   }
}
