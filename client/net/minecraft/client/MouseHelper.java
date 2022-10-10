package net.minecraft.client;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.MouseSmoother;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.glfw.GLFW;

public class MouseHelper {
   private final Minecraft field_198036_a;
   private boolean field_198037_b;
   private boolean field_198038_c;
   private boolean field_198039_d;
   private double field_198040_e;
   private double field_198041_f;
   private int field_212148_g;
   private int field_198042_g = -1;
   private boolean field_198043_h = true;
   private int field_198044_i;
   private double field_198045_j;
   private final MouseSmoother field_198046_k = new MouseSmoother();
   private final MouseSmoother field_198047_l = new MouseSmoother();
   private double field_198048_m;
   private double field_198049_n;
   private double field_200542_o;
   private double field_198050_o = 4.9E-324D;
   private boolean field_198051_p;

   public MouseHelper(Minecraft var1) {
      super();
      this.field_198036_a = var1;
   }

   private void func_198023_a(long var1, int var3, int var4, int var5) {
      if (var1 == this.field_198036_a.field_195558_d.func_198092_i()) {
         boolean var6 = var4 == 1;
         if (Minecraft.field_142025_a && var3 == 0) {
            if (var6) {
               if ((var5 & 2) == 2) {
                  var3 = 1;
                  ++this.field_212148_g;
               }
            } else if (this.field_212148_g > 0) {
               var3 = 1;
               --this.field_212148_g;
            }
         }

         if (var6) {
            if (this.field_198036_a.field_71474_y.field_85185_A && this.field_198044_i++ > 0) {
               return;
            }

            this.field_198042_g = var3;
            this.field_198045_j = GLFW.glfwGetTime();
         } else if (this.field_198042_g != -1) {
            if (this.field_198036_a.field_71474_y.field_85185_A && --this.field_198044_i > 0) {
               return;
            }

            this.field_198042_g = -1;
         }

         boolean[] var8 = new boolean[]{false};
         if (this.field_198036_a.field_71462_r == null) {
            if (!this.field_198051_p && var6) {
               this.func_198034_i();
            }
         } else {
            double var9 = this.field_198040_e * (double)this.field_198036_a.field_195558_d.func_198107_o() / (double)this.field_198036_a.field_195558_d.func_198105_m();
            double var11 = this.field_198041_f * (double)this.field_198036_a.field_195558_d.func_198087_p() / (double)this.field_198036_a.field_195558_d.func_198083_n();
            if (var6) {
               GuiScreen.func_195121_a(() -> {
                  var8[0] = this.field_198036_a.field_71462_r.mouseClicked(var9, var11, var3);
               }, "mouseClicked event handler", this.field_198036_a.field_71462_r.getClass().getCanonicalName());
            } else {
               GuiScreen.func_195121_a(() -> {
                  var8[0] = this.field_198036_a.field_71462_r.mouseReleased(var9, var11, var3);
               }, "mouseReleased event handler", this.field_198036_a.field_71462_r.getClass().getCanonicalName());
            }
         }

         if (!var8[0] && (this.field_198036_a.field_71462_r == null || this.field_198036_a.field_71462_r.field_146291_p)) {
            if (var3 == 0) {
               this.field_198037_b = var6;
            } else if (var3 == 2) {
               this.field_198038_c = var6;
            } else if (var3 == 1) {
               this.field_198039_d = var6;
            }

            KeyBinding.func_197980_a(InputMappings.Type.MOUSE.func_197944_a(var3), var6);
            if (var6) {
               if (this.field_198036_a.field_71439_g.func_175149_v() && var3 == 2) {
                  this.field_198036_a.field_71456_v.func_175187_g().func_175261_b();
               } else {
                  KeyBinding.func_197981_a(InputMappings.Type.MOUSE.func_197944_a(var3));
               }
            }
         }

      }
   }

   private void func_198020_a(long var1, double var3, double var5) {
      if (var1 == Minecraft.func_71410_x().field_195558_d.func_198092_i()) {
         double var7 = var5 * this.field_198036_a.field_71474_y.field_208033_V;
         if (this.field_198036_a.field_71462_r != null) {
            this.field_198036_a.field_71462_r.mouseScrolled(var7);
         } else if (this.field_198036_a.field_71439_g != null) {
            if (this.field_200542_o != 0.0D && Math.signum(var7) != Math.signum(this.field_200542_o)) {
               this.field_200542_o = 0.0D;
            }

            this.field_200542_o += var7;
            double var9 = (double)((int)this.field_200542_o);
            if (var9 == 0.0D) {
               return;
            }

            this.field_200542_o -= var9;
            if (this.field_198036_a.field_71439_g.func_175149_v()) {
               if (this.field_198036_a.field_71456_v.func_175187_g().func_175262_a()) {
                  this.field_198036_a.field_71456_v.func_175187_g().func_195621_a(-var9);
               } else {
                  double var11 = MathHelper.func_151237_a((double)this.field_198036_a.field_71439_g.field_71075_bZ.func_75093_a() + var9 * 0.004999999888241291D, 0.0D, 0.20000000298023224D);
                  this.field_198036_a.field_71439_g.field_71075_bZ.func_195931_a(var11);
               }
            } else {
               this.field_198036_a.field_71439_g.field_71071_by.func_195409_a(var9);
            }
         }
      }

   }

   public void func_198029_a(long var1) {
      GLFW.glfwSetCursorPosCallback(var1, this::func_198022_b);
      GLFW.glfwSetMouseButtonCallback(var1, this::func_198023_a);
      GLFW.glfwSetScrollCallback(var1, this::func_198020_a);
   }

   private void func_198022_b(long var1, double var3, double var5) {
      if (var1 == Minecraft.func_71410_x().field_195558_d.func_198092_i()) {
         if (this.field_198043_h) {
            this.field_198040_e = var3;
            this.field_198041_f = var5;
            this.field_198043_h = false;
         }

         GuiScreen var7 = this.field_198036_a.field_71462_r;
         if (this.field_198042_g != -1 && this.field_198045_j > 0.0D && var7 != null) {
            double var8 = var3 * (double)this.field_198036_a.field_195558_d.func_198107_o() / (double)this.field_198036_a.field_195558_d.func_198105_m();
            double var10 = var5 * (double)this.field_198036_a.field_195558_d.func_198087_p() / (double)this.field_198036_a.field_195558_d.func_198083_n();
            double var12 = (var3 - this.field_198040_e) * (double)this.field_198036_a.field_195558_d.func_198107_o() / (double)this.field_198036_a.field_195558_d.func_198105_m();
            double var14 = (var5 - this.field_198041_f) * (double)this.field_198036_a.field_195558_d.func_198087_p() / (double)this.field_198036_a.field_195558_d.func_198083_n();
            GuiScreen.func_195121_a(() -> {
               var7.mouseDragged(var8, var10, this.field_198042_g, var12, var14);
            }, "mouseDragged event handler", var7.getClass().getCanonicalName());
         }

         this.field_198036_a.field_71424_I.func_76320_a("mouse");
         if (this.func_198035_h() && this.field_198036_a.func_195544_aj()) {
            this.field_198048_m += var3 - this.field_198040_e;
            this.field_198049_n += var5 - this.field_198041_f;
         }

         this.func_198028_a();
         this.field_198040_e = var3;
         this.field_198041_f = var5;
         this.field_198036_a.field_71424_I.func_76319_b();
      }
   }

   public void func_198028_a() {
      double var1 = GLFW.glfwGetTime();
      double var3 = var1 - this.field_198050_o;
      this.field_198050_o = var1;
      if (this.func_198035_h() && this.field_198036_a.func_195544_aj()) {
         double var9 = this.field_198036_a.field_71474_y.field_74341_c * 0.6000000238418579D + 0.20000000298023224D;
         double var11 = var9 * var9 * var9 * 8.0D;
         double var5;
         double var7;
         if (this.field_198036_a.field_71474_y.field_74326_T) {
            double var13 = this.field_198046_k.func_199102_a(this.field_198048_m * var11, var3 * var11);
            double var15 = this.field_198047_l.func_199102_a(this.field_198049_n * var11, var3 * var11);
            var5 = var13;
            var7 = var15;
         } else {
            this.field_198046_k.func_199101_a();
            this.field_198047_l.func_199101_a();
            var5 = this.field_198048_m * var11;
            var7 = this.field_198049_n * var11;
         }

         this.field_198048_m = 0.0D;
         this.field_198049_n = 0.0D;
         byte var17 = 1;
         if (this.field_198036_a.field_71474_y.field_74338_d) {
            var17 = -1;
         }

         this.field_198036_a.func_193032_ao().func_195872_a(var5, var7);
         if (this.field_198036_a.field_71439_g != null) {
            this.field_198036_a.field_71439_g.func_195049_a(var5, var7 * (double)var17);
         }

      } else {
         this.field_198048_m = 0.0D;
         this.field_198049_n = 0.0D;
      }
   }

   public boolean func_198030_b() {
      return this.field_198037_b;
   }

   public boolean func_198031_d() {
      return this.field_198039_d;
   }

   public double func_198024_e() {
      return this.field_198040_e;
   }

   public double func_198026_f() {
      return this.field_198041_f;
   }

   public void func_198021_g() {
      this.field_198043_h = true;
   }

   public boolean func_198035_h() {
      return this.field_198051_p;
   }

   public void func_198034_i() {
      if (this.field_198036_a.func_195544_aj()) {
         if (!this.field_198051_p) {
            if (!Minecraft.field_142025_a) {
               KeyBinding.func_186704_a();
            }

            this.field_198051_p = true;
            this.field_198040_e = (double)(this.field_198036_a.field_195558_d.func_198105_m() / 2);
            this.field_198041_f = (double)(this.field_198036_a.field_195558_d.func_198083_n() / 2);
            GLFW.glfwSetCursorPos(this.field_198036_a.field_195558_d.func_198092_i(), this.field_198040_e, this.field_198041_f);
            GLFW.glfwSetInputMode(this.field_198036_a.field_195558_d.func_198092_i(), 208897, 212995);
            this.field_198036_a.func_147108_a((GuiScreen)null);
            this.field_198036_a.field_71429_W = 10000;
         }
      }
   }

   public void func_198032_j() {
      if (this.field_198051_p) {
         this.field_198051_p = false;
         GLFW.glfwSetInputMode(this.field_198036_a.field_195558_d.func_198092_i(), 208897, 212993);
         this.field_198040_e = (double)(this.field_198036_a.field_195558_d.func_198105_m() / 2);
         this.field_198041_f = (double)(this.field_198036_a.field_195558_d.func_198083_n() / 2);
         GLFW.glfwSetCursorPos(this.field_198036_a.field_195558_d.func_198092_i(), this.field_198040_e, this.field_198041_f);
      }
   }
}
