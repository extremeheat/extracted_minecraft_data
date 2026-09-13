package net.minecraft.client.particle;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFireworkSparkFX extends EntityFX {
   private int field_92049_a = 160;
   private boolean field_92054_ax;
   private boolean field_92048_ay;
   private final EffectRenderer field_92047_az;
   private float field_92050_aA;
   private float field_92051_aB;
   private float field_92052_aC;
   private boolean field_92053_aD;

   public EntityFireworkSparkFX(World var1, double var2, double var4, double var6, double var8, double var10, double var12, EffectRenderer var14) {
      super(var1, var2, var4, var6);
      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
      this.field_92047_az = var14;
      this.field_70544_f *= 0.75F;
      this.field_70547_e = 48 + this.field_70146_Z.nextInt(12);
      this.field_70145_X = false;
   }

   public void func_92045_e(boolean var1) {
      this.field_92054_ax = var1;
   }

   public void func_92043_f(boolean var1) {
      this.field_92048_ay = var1;
   }

   public void func_92044_a(int var1) {
      float var2 = (float)((var1 & 0xFF0000) >> 16) / 255.0F;
      float var3 = (float)((var1 & 0xFF00) >> 8) / 255.0F;
      float var4 = (float)((var1 & 0xFF) >> 0) / 255.0F;
      float var5 = 1.0F;
      this.func_70538_b(var2 * var5, var3 * var5, var4 * var5);
   }

   public void func_92046_g(int var1) {
      this.field_92050_aA = (float)((var1 & 0xFF0000) >> 16) / 255.0F;
      this.field_92051_aB = (float)((var1 & 0xFF00) >> 8) / 255.0F;
      this.field_92052_aC = (float)((var1 & 0xFF) >> 0) / 255.0F;
      this.field_92053_aD = true;
   }

   @Override
   public AxisAlignedBB func_70046_E() {
      return null;
   }

   @Override
   public boolean func_70104_M() {
      return false;
   }

   @Override
   public void func_70539_a(Tessellator var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      if (!this.field_92048_ay || this.field_70546_d < this.field_70547_e / 3 || (this.field_70546_d + this.field_70547_e) / 3 % 2 == 0) {
         super.func_70539_a(var1, var2, var3, var4, var5, var6, var7);
      }
   }

   @Override
   public void func_70071_h_() {
      this.field_70169_q = this.field_70165_t;
      this.field_70167_r = this.field_70163_u;
      this.field_70166_s = this.field_70161_v;
      if (this.field_70546_d++ >= this.field_70547_e) {
         this.func_70106_y();
      }

      if (this.field_70546_d > this.field_70547_e / 2) {
         this.func_82338_g(1.0F - ((float)this.field_70546_d - (float)(this.field_70547_e / 2)) / (float)this.field_70547_e);
         if (this.field_92053_aD) {
            this.field_70552_h += (this.field_92050_aA - this.field_70552_h) * 0.2F;
            this.field_70553_i += (this.field_92051_aB - this.field_70553_i) * 0.2F;
            this.field_70551_j += (this.field_92052_aC - this.field_70551_j) * 0.2F;
         }
      }

      this.func_70536_a(this.field_92049_a + (7 - this.field_70546_d * 8 / this.field_70547_e));
      this.field_70181_x -= 0.004;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      this.field_70159_w *= 0.9100000262260437;
      this.field_70181_x *= 0.9100000262260437;
      this.field_70179_y *= 0.9100000262260437;
      if (this.field_70122_E) {
         this.field_70159_w *= 0.699999988079071;
         this.field_70179_y *= 0.699999988079071;
      }

      if (this.field_92054_ax && this.field_70546_d < this.field_70547_e / 2 && (this.field_70546_d + this.field_70547_e) % 2 == 0) {
         EntityFireworkSparkFX var1 = new EntityFireworkSparkFX(
            this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, 0.0, 0.0, 0.0, this.field_92047_az
         );
         var1.func_70538_b(this.field_70552_h, this.field_70553_i, this.field_70551_j);
         var1.field_70546_d = var1.field_70547_e / 2;
         if (this.field_92053_aD) {
            var1.field_92053_aD = true;
            var1.field_92050_aA = this.field_92050_aA;
            var1.field_92051_aB = this.field_92051_aB;
            var1.field_92052_aC = this.field_92052_aC;
         }

         var1.field_92048_ay = this.field_92048_ay;
         this.field_92047_az.func_78873_a(var1);
      }
   }

   @Override
   public int func_70070_b(float var1) {
      return 15728880;
   }

   @Override
   public float func_70013_c(float var1) {
      return 1.0F;
   }
}
