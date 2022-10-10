package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;

public abstract class RealmsSliderButton extends RealmsButton {
   public double value;
   public boolean sliding;
   private final double minValue;
   private final double maxValue;
   private int steps;

   public RealmsSliderButton(int var1, int var2, int var3, int var4, int var5, int var6) {
      this(var1, var2, var3, var4, var6, 0, 1.0D, (double)var5);
   }

   public RealmsSliderButton(int var1, int var2, int var3, int var4, int var5, int var6, double var7, double var9) {
      super(var1, var2, var3, var4, 20, "");
      this.value = 1.0D;
      this.minValue = var7;
      this.maxValue = var9;
      this.value = this.toPct((double)var6);
      this.getProxy().field_146126_j = this.getMessage();
   }

   public String getMessage() {
      return "";
   }

   public double toPct(double var1) {
      return MathHelper.func_151237_a((this.clamp(var1) - this.minValue) / (this.maxValue - this.minValue), 0.0D, 1.0D);
   }

   public double toValue(double var1) {
      return this.clamp(this.minValue + (this.maxValue - this.minValue) * MathHelper.func_151237_a(var1, 0.0D, 1.0D));
   }

   public double clamp(double var1) {
      var1 = this.clampSteps(var1);
      return MathHelper.func_151237_a(var1, this.minValue, this.maxValue);
   }

   protected double clampSteps(double var1) {
      if (this.steps > 0) {
         var1 = (double)((long)this.steps * Math.round(var1 / (double)this.steps));
      }

      return var1;
   }

   public int getYImage(boolean var1) {
      return 0;
   }

   public void renderBg(int var1, int var2) {
      if (this.getProxy().field_146125_m) {
         if (this.sliding) {
            this.value = (double)((float)(var1 - (this.getProxy().field_146128_h + 4)) / (float)(this.getProxy().func_146117_b() - 8));
            this.value = MathHelper.func_151237_a(this.value, 0.0D, 1.0D);
            double var3 = this.toValue(this.value);
            this.clicked(var3);
            this.value = this.toPct(var3);
            this.getProxy().field_146126_j = this.getMessage();
         }

         Minecraft.func_71410_x().func_110434_K().func_110577_a(WIDGETS_LOCATION);
         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.blit(this.getProxy().field_146128_h + (int)(this.value * (double)(this.getProxy().func_146117_b() - 8)), this.getProxy().field_146129_i, 0, 66, 4, 20);
         this.blit(this.getProxy().field_146128_h + (int)(this.value * (double)(this.getProxy().func_146117_b() - 8)) + 4, this.getProxy().field_146129_i, 196, 66, 4, 20);
      }
   }

   public void onClick(double var1, double var3) {
      this.value = (var1 - (double)(this.getProxy().field_146128_h + 4)) / (double)(this.getProxy().func_146117_b() - 8);
      this.value = MathHelper.func_151237_a(this.value, 0.0D, 1.0D);
      this.clicked(this.toValue(this.value));
      this.getProxy().field_146126_j = this.getMessage();
      this.sliding = true;
   }

   public void clicked(double var1) {
   }

   public void onRelease(double var1, double var3) {
      this.sliding = false;
   }
}
