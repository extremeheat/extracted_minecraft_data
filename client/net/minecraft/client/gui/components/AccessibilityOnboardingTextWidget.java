package net.minecraft.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;

public class AccessibilityOnboardingTextWidget extends MultiLineTextWidget {
   private final Component message;
   private static final int BORDER_COLOR_FOCUSED = -1;
   private static final int BORDER_COLOR = -6250336;
   private static final int BACKGROUND_COLOR = 1426063360;
   private static final int PADDING = 3;
   private static final int BORDER = 1;

   public AccessibilityOnboardingTextWidget(Font var1, Component var2, int var3) {
      super(MultiLineLabel.create(var1, var2, var3), var1, var2, true);
      this.message = var2;
      this.active = true;
   }

   @Override
   protected void updateWidgetNarration(NarrationElementOutput var1) {
      var1.add(NarratedElementType.TITLE, this.message);
   }

   @Override
   protected void renderBg(PoseStack var1, Minecraft var2, int var3, int var4) {
      int var5 = this.getX() - 3;
      int var6 = this.getY() - 3;
      int var7 = this.getX() + this.width + 3;
      int var8 = this.getY() + this.height + 3;
      int var9 = this.isFocused() ? -1 : -6250336;
      fill(var1, var5 - 1, var6 - 1, var5, var8 + 1, var9);
      fill(var1, var7, var6 - 1, var7 + 1, var8 + 1, var9);
      fill(var1, var5, var6, var7, var6 - 1, var9);
      fill(var1, var5, var8, var7, var8 + 1, var9);
      fill(var1, var5, var6, var7, var8, 1426063360);
      super.renderBg(var1, var2, var3, var4);
   }

   @Override
   public void renderButton(PoseStack var1, int var2, int var3, float var4) {
      this.renderBg(var1, Minecraft.getInstance(), var2, var3);
      super.renderButton(var1, var2, var3, var4);
   }

   @Override
   public void playDownSound(SoundManager var1) {
   }
}
