package net.minecraft.entity.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityFireball extends Entity {
   public EntityLivingBase field_70235_a;
   private int field_70236_j;
   private int field_70234_an;
   public double field_70232_b;
   public double field_70233_c;
   public double field_70230_d;

   protected EntityFireball(EntityType<?> var1, World var2, float var3, float var4) {
      super(var1, var2);
      this.func_70105_a(var3, var4);
   }

   public EntityFireball(EntityType<?> var1, double var2, double var4, double var6, double var8, double var10, double var12, World var14, float var15, float var16) {
      this(var1, var14, var15, var16);
      this.func_70012_b(var2, var4, var6, this.field_70177_z, this.field_70125_A);
      this.func_70107_b(var2, var4, var6);
      double var17 = (double)MathHelper.func_76133_a(var8 * var8 + var10 * var10 + var12 * var12);
      this.field_70232_b = var8 / var17 * 0.1D;
      this.field_70233_c = var10 / var17 * 0.1D;
      this.field_70230_d = var12 / var17 * 0.1D;
   }

   public EntityFireball(EntityType<?> var1, EntityLivingBase var2, double var3, double var5, double var7, World var9, float var10, float var11) {
      this(var1, var9, var10, var11);
      this.field_70235_a = var2;
      this.func_70012_b(var2.field_70165_t, var2.field_70163_u, var2.field_70161_v, var2.field_70177_z, var2.field_70125_A);
      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      this.field_70159_w = 0.0D;
      this.field_70181_x = 0.0D;
      this.field_70179_y = 0.0D;
      var3 += this.field_70146_Z.nextGaussian() * 0.4D;
      var5 += this.field_70146_Z.nextGaussian() * 0.4D;
      var7 += this.field_70146_Z.nextGaussian() * 0.4D;
      double var12 = (double)MathHelper.func_76133_a(var3 * var3 + var5 * var5 + var7 * var7);
      this.field_70232_b = var3 / var12 * 0.1D;
      this.field_70233_c = var5 / var12 * 0.1D;
      this.field_70230_d = var7 / var12 * 0.1D;
   }

   protected void func_70088_a() {
   }

   public boolean func_70112_a(double var1) {
      double var3 = this.func_174813_aQ().func_72320_b() * 4.0D;
      if (Double.isNaN(var3)) {
         var3 = 4.0D;
      }

      var3 *= 64.0D;
      return var1 < var3 * var3;
   }

   public void func_70071_h_() {
      if (!this.field_70170_p.field_72995_K && (this.field_70235_a != null && this.field_70235_a.field_70128_L || !this.field_70170_p.func_175667_e(new BlockPos(this)))) {
         this.func_70106_y();
      } else {
         super.func_70071_h_();
         if (this.func_184564_k()) {
            this.func_70015_d(1);
         }

         ++this.field_70234_an;
         RayTraceResult var1 = ProjectileHelper.func_188802_a(this, true, this.field_70234_an >= 25, this.field_70235_a);
         if (var1 != null) {
            this.func_70227_a(var1);
         }

         this.field_70165_t += this.field_70159_w;
         this.field_70163_u += this.field_70181_x;
         this.field_70161_v += this.field_70179_y;
         ProjectileHelper.func_188803_a(this, 0.2F);
         float var2 = this.func_82341_c();
         if (this.func_70090_H()) {
            for(int var3 = 0; var3 < 4; ++var3) {
               float var4 = 0.25F;
               this.field_70170_p.func_195594_a(Particles.field_197612_e, this.field_70165_t - this.field_70159_w * 0.25D, this.field_70163_u - this.field_70181_x * 0.25D, this.field_70161_v - this.field_70179_y * 0.25D, this.field_70159_w, this.field_70181_x, this.field_70179_y);
            }

            var2 = 0.8F;
         }

         this.field_70159_w += this.field_70232_b;
         this.field_70181_x += this.field_70233_c;
         this.field_70179_y += this.field_70230_d;
         this.field_70159_w *= (double)var2;
         this.field_70181_x *= (double)var2;
         this.field_70179_y *= (double)var2;
         this.field_70170_p.func_195594_a(this.func_195057_f(), this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, 0.0D, 0.0D, 0.0D);
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }
   }

   protected boolean func_184564_k() {
      return true;
   }

   protected IParticleData func_195057_f() {
      return Particles.field_197601_L;
   }

   protected float func_82341_c() {
      return 0.95F;
   }

   protected abstract void func_70227_a(RayTraceResult var1);

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74782_a("direction", this.func_70087_a(new double[]{this.field_70159_w, this.field_70181_x, this.field_70179_y}));
      var1.func_74782_a("power", this.func_70087_a(new double[]{this.field_70232_b, this.field_70233_c, this.field_70230_d}));
      var1.func_74768_a("life", this.field_70236_j);
   }

   public void func_70037_a(NBTTagCompound var1) {
      NBTTagList var2;
      if (var1.func_150297_b("power", 9)) {
         var2 = var1.func_150295_c("power", 6);
         if (var2.size() == 3) {
            this.field_70232_b = var2.func_150309_d(0);
            this.field_70233_c = var2.func_150309_d(1);
            this.field_70230_d = var2.func_150309_d(2);
         }
      }

      this.field_70236_j = var1.func_74762_e("life");
      if (var1.func_150297_b("direction", 9) && var1.func_150295_c("direction", 6).size() == 3) {
         var2 = var1.func_150295_c("direction", 6);
         this.field_70159_w = var2.func_150309_d(0);
         this.field_70181_x = var2.func_150309_d(1);
         this.field_70179_y = var2.func_150309_d(2);
      } else {
         this.func_70106_y();
      }

   }

   public boolean func_70067_L() {
      return true;
   }

   public float func_70111_Y() {
      return 1.0F;
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.func_70018_K();
         if (var1.func_76346_g() != null) {
            Vec3d var3 = var1.func_76346_g().func_70040_Z();
            if (var3 != null) {
               this.field_70159_w = var3.field_72450_a;
               this.field_70181_x = var3.field_72448_b;
               this.field_70179_y = var3.field_72449_c;
               this.field_70232_b = this.field_70159_w * 0.1D;
               this.field_70233_c = this.field_70181_x * 0.1D;
               this.field_70230_d = this.field_70179_y * 0.1D;
            }

            if (var1.func_76346_g() instanceof EntityLivingBase) {
               this.field_70235_a = (EntityLivingBase)var1.func_76346_g();
            }

            return true;
         } else {
            return false;
         }
      }
   }

   public float func_70013_c() {
      return 1.0F;
   }

   public int func_70070_b() {
      return 15728880;
   }
}
