package net.minecraft.realms;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public abstract class RealmsSliderButton extends AbstractRealmsButton<RealmsSliderButtonProxy> {
   protected static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/widgets.png");
   private final int id;
   private final RealmsSliderButtonProxy proxy;
   private final double minValue;
   private final double maxValue;

   public RealmsSliderButton(int var1, int var2, int var3, int var4, int var5, double var6, double var8) {
      super();
      this.id = var1;
      this.minValue = var6;
      this.maxValue = var8;
      this.proxy = new RealmsSliderButtonProxy(this, var2, var3, var4, 20, this.toPct((double)var5));
      this.getProxy().setMessage(this.getMessage());
   }

   public String getMessage() {
      return "";
   }

   public double toPct(double var1) {
      return Mth.clamp((this.clamp(var1) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double var1) {
      return this.clamp(Mth.lerp(Mth.clamp(var1, 0.0D, 1.0D), this.minValue, this.maxValue));
   }

   public double clamp(double var1) {
      return Mth.clamp(var1, this.minValue, this.maxValue);
   }

   public int getYImage(boolean var1) {
      return 0;
   }

   public void onClick(double var1, double var3) {
   }

   public void onRelease(double var1, double var3) {
   }

   public RealmsSliderButtonProxy getProxy() {
      return this.proxy;
   }

   public double getValue() {
      return this.proxy.getValue();
   }

   public void setValue(double var1) {
      this.proxy.setValue(var1);
   }

   public int id() {
      return this.id;
   }

   public void setMessage(String var1) {
      this.proxy.setMessage(var1);
   }

   public int getWidth() {
      return this.proxy.getWidth();
   }

   public int getHeight() {
      return this.proxy.getHeight();
   }

   public int y() {
      return this.proxy.y();
   }

   public abstract void applyValue();

   public void updateMessage() {
      this.proxy.setMessage(this.getMessage());
   }
}
