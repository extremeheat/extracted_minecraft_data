package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

public class GuiSlider extends GuiButton {
   private float field_175227_p = 1.0F;
   public boolean field_175228_o;
   private String field_175226_q;
   private final float field_175225_r;
   private final float field_175224_s;
   private final GuiPageButtonList.GuiResponder field_175223_t;
   private GuiSlider.FormatHelper field_175222_u;

   public GuiSlider(GuiPageButtonList.GuiResponder var1, int var2, int var3, int var4, String var5, float var6, float var7, float var8, GuiSlider.FormatHelper var9) {
      super(var2, var3, var4, 150, 20, "");
      this.field_175226_q = var5;
      this.field_175225_r = var6;
      this.field_175224_s = var7;
      this.field_175227_p = (var8 - var6) / (var7 - var6);
      this.field_175222_u = var9;
      this.field_175223_t = var1;
      this.field_146126_j = this.func_175221_e();
   }

   public float func_175220_c() {
      return this.field_175225_r + (this.field_175224_s - this.field_175225_r) * this.field_175227_p;
   }

   public void func_175218_a(float var1, boolean var2) {
      this.field_175227_p = (var1 - this.field_175225_r) / (this.field_175224_s - this.field_175225_r);
      this.field_146126_j = this.func_175221_e();
      if (var2) {
         this.field_175223_t.func_175320_a(this.field_146127_k, this.func_175220_c());
      }

   }

   public float func_175217_d() {
      return this.field_175227_p;
   }

   private String func_175221_e() {
      return this.field_175222_u == null ? I18n.func_135052_a(this.field_175226_q) + ": " + this.func_175220_c() : this.field_175222_u.func_175318_a(this.field_146127_k, I18n.func_135052_a(this.field_175226_q), this.func_175220_c());
   }

   protected int func_146114_a(boolean var1) {
      return 0;
   }

   protected void func_146119_b(Minecraft var1, int var2, int var3) {
      if (this.field_146125_m) {
         if (this.field_175228_o) {
            this.field_175227_p = (float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);
            if (this.field_175227_p < 0.0F) {
               this.field_175227_p = 0.0F;
            }

            if (this.field_175227_p > 1.0F) {
               this.field_175227_p = 1.0F;
            }

            this.field_146126_j = this.func_175221_e();
            this.field_175223_t.func_175320_a(this.field_146127_k, this.func_175220_c());
         }

         GlStateManager.func_179131_c(1.0F, 1.0F, 1.0F, 1.0F);
         this.func_73729_b(this.field_146128_h + (int)(this.field_175227_p * (float)(this.field_146120_f - 8)), this.field_146129_i, 0, 66, 4, 20);
         this.func_73729_b(this.field_146128_h + (int)(this.field_175227_p * (float)(this.field_146120_f - 8)) + 4, this.field_146129_i, 196, 66, 4, 20);
      }
   }

   public void func_175219_a(float var1) {
      this.field_175227_p = var1;
      this.field_146126_j = this.func_175221_e();
      this.field_175223_t.func_175320_a(this.field_146127_k, this.func_175220_c());
   }

   public boolean func_146116_c(Minecraft var1, int var2, int var3) {
      if (super.func_146116_c(var1, var2, var3)) {
         this.field_175227_p = (float)(var2 - (this.field_146128_h + 4)) / (float)(this.field_146120_f - 8);
         if (this.field_175227_p < 0.0F) {
            this.field_175227_p = 0.0F;
         }

         if (this.field_175227_p > 1.0F) {
            this.field_175227_p = 1.0F;
         }

         this.field_146126_j = this.func_175221_e();
         this.field_175223_t.func_175320_a(this.field_146127_k, this.func_175220_c());
         this.field_175228_o = true;
         return true;
      } else {
         return false;
      }
   }

   public void func_146118_a(int var1, int var2) {
      this.field_175228_o = false;
   }

   public interface FormatHelper {
      String func_175318_a(int var1, String var2, float var3);
   }
}
