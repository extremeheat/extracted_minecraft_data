package net.minecraft.client.gui.render.state;

import javax.annotation.Nullable;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.GuiLayer;
import org.joml.Matrix3x2f;

public record GuiTextRenderState(TextRenderState textRenderState, Matrix3x2f pose, int x, int y, float backgroundZ, float textShadowZ, float effectShadowZ, float textZ, float effectZ, GuiLayer layer, @Nullable ScreenRectangle scissorArea) {
   public GuiTextRenderState(TextRenderState var1, Matrix3x2f var2, int var3, int var4, float var5, float var6, float var7, float var8, float var9, GuiLayer var10, @Nullable ScreenRectangle var11) {
      super();
      this.textRenderState = var1;
      this.pose = var2;
      this.x = var3;
      this.y = var4;
      this.backgroundZ = var5;
      this.textShadowZ = var6;
      this.effectShadowZ = var7;
      this.textZ = var8;
      this.effectZ = var9;
      this.layer = var10;
      this.scissorArea = var11;
   }
}
