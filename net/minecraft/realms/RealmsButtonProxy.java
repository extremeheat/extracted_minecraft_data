package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;

public class RealmsButtonProxy extends Button implements RealmsAbstractButtonProxy {
   private final RealmsButton button;

   public RealmsButtonProxy(RealmsButton var1, int var2, int var3, String var4, int var5, int var6, Button.OnPress var7) {
      super(var2, var3, var5, var6, var4, var7);
      this.button = var1;
   }

   public boolean active() {
      return this.active;
   }

   public void active(boolean var1) {
      this.active = var1;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public void setVisible(boolean var1) {
      this.visible = var1;
   }

   public void setMessage(String var1) {
      super.setMessage(var1);
   }

   public int getWidth() {
      return super.getWidth();
   }

   public int y() {
      return this.y;
   }

   public void onClick(double var1, double var3) {
      this.button.onPress();
   }

   public void onRelease(double var1, double var3) {
      this.button.onRelease(var1, var3);
   }

   public void renderBg(Minecraft var1, int var2, int var3) {
      this.button.renderBg(var2, var3);
   }

   public void renderButton(int var1, int var2, float var3) {
      this.button.renderButton(var1, var2, var3);
   }

   public void superRenderButton(int var1, int var2, float var3) {
      super.renderButton(var1, var2, var3);
   }

   public RealmsButton getButton() {
      return this.button;
   }

   public int getYImage(boolean var1) {
      return this.button.getYImage(var1);
   }

   public int getSuperYImage(boolean var1) {
      return super.getYImage(var1);
   }

   public int getHeight() {
      return this.height;
   }

   public boolean isHovered() {
      return super.isHovered();
   }
}
