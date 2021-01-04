package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSliderButton;

public class RealmsSliderButtonProxy extends AbstractSliderButton implements RealmsAbstractButtonProxy<RealmsSliderButton> {
   private final RealmsSliderButton button;

   public RealmsSliderButtonProxy(RealmsSliderButton var1, int var2, int var3, int var4, int var5, double var6) {
      super(var2, var3, var4, var5, var6);
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
      this.button.onClick(var1, var3);
   }

   public void onRelease(double var1, double var3) {
      this.button.onRelease(var1, var3);
   }

   public void updateMessage() {
      this.button.updateMessage();
   }

   public void applyValue() {
      this.button.applyValue();
   }

   public double getValue() {
      return this.value;
   }

   public void setValue(double var1) {
      this.value = var1;
   }

   public void renderBg(Minecraft var1, int var2, int var3) {
      super.renderBg(var1, var2, var3);
   }

   public RealmsSliderButton getButton() {
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
}
