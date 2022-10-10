package net.minecraft.entity.projectile;

import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityThrowable extends Entity implements IProjectile {
   private int field_145788_c;
   private int field_145786_d;
   private int field_145787_e;
   protected boolean field_174854_a;
   public int field_70191_b;
   protected EntityLivingBase field_70192_c;
   private UUID field_200218_h;
   public Entity field_184539_c;
   private int field_184540_av;

   protected EntityThrowable(EntityType<?> var1, World var2) {
      super(var1, var2);
      this.field_145788_c = -1;
      this.field_145786_d = -1;
      this.field_145787_e = -1;
      this.func_70105_a(0.25F, 0.25F);
   }

   protected EntityThrowable(EntityType<?> var1, double var2, double var4, double var6, World var8) {
      this(var1, var8);
      this.func_70107_b(var2, var4, var6);
   }

   protected EntityThrowable(EntityType<?> var1, EntityLivingBase var2, World var3) {
      this(var1, var2.field_70165_t, var2.field_70163_u + (double)var2.func_70047_e() - 0.10000000149011612D, var2.field_70161_v, var3);
      this.field_70192_c = var2;
      this.field_200218_h = var2.func_110124_au();
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

   public void func_184538_a(Entity var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = -MathHelper.func_76126_a(var3 * 0.017453292F) * MathHelper.func_76134_b(var2 * 0.017453292F);
      float var8 = -MathHelper.func_76126_a((var2 + var4) * 0.017453292F);
      float var9 = MathHelper.func_76134_b(var3 * 0.017453292F) * MathHelper.func_76134_b(var2 * 0.017453292F);
      this.func_70186_c((double)var7, (double)var8, (double)var9, var5, var6);
      this.field_70159_w += var1.field_70159_w;
      this.field_70179_y += var1.field_70179_y;
      if (!var1.field_70122_E) {
         this.field_70181_x += var1.field_70181_x;
      }

   }

   public void func_70186_c(double var1, double var3, double var5, float var7, float var8) {
      float var9 = MathHelper.func_76133_a(var1 * var1 + var3 * var3 + var5 * var5);
      var1 /= (double)var9;
      var3 /= (double)var9;
      var5 /= (double)var9;
      var1 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double)var8;
      var3 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double)var8;
      var5 += this.field_70146_Z.nextGaussian() * 0.007499999832361937D * (double)var8;
      var1 *= (double)var7;
      var3 *= (double)var7;
      var5 *= (double)var7;
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      float var10 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
      this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 57.2957763671875D);
      this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var10) * 57.2957763671875D);
      this.field_70126_B = this.field_70177_z;
      this.field_70127_C = this.field_70125_A;
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 57.2957763671875D);
         this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var7) * 57.2957763671875D);
         this.field_70126_B = this.field_70177_z;
         this.field_70127_C = this.field_70125_A;
      }

   }

   public void func_70071_h_() {
      this.field_70142_S = this.field_70165_t;
      this.field_70137_T = this.field_70163_u;
      this.field_70136_U = this.field_70161_v;
      super.func_70071_h_();
      if (this.field_70191_b > 0) {
         --this.field_70191_b;
      }

      if (this.field_174854_a) {
         this.field_174854_a = false;
         this.field_70159_w *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
         this.field_70181_x *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
         this.field_70179_y *= (double)(this.field_70146_Z.nextFloat() * 0.2F);
      }

      Vec3d var1 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      Vec3d var2 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      RayTraceResult var3 = this.field_70170_p.func_72933_a(var1, var2);
      var1 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      var2 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      if (var3 != null) {
         var2 = new Vec3d(var3.field_72307_f.field_72450_a, var3.field_72307_f.field_72448_b, var3.field_72307_f.field_72449_c);
      }

      Entity var4 = null;
      List var5 = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_186662_g(1.0D));
      double var6 = 0.0D;
      boolean var8 = false;

      for(int var9 = 0; var9 < var5.size(); ++var9) {
         Entity var10 = (Entity)var5.get(var9);
         if (var10.func_70067_L()) {
            if (var10 == this.field_184539_c) {
               var8 = true;
            } else if (this.field_70192_c != null && this.field_70173_aa < 2 && this.field_184539_c == null) {
               this.field_184539_c = var10;
               var8 = true;
            } else {
               var8 = false;
               AxisAlignedBB var11 = var10.func_174813_aQ().func_186662_g(0.30000001192092896D);
               RayTraceResult var12 = var11.func_72327_a(var1, var2);
               if (var12 != null) {
                  double var13 = var1.func_72436_e(var12.field_72307_f);
                  if (var13 < var6 || var6 == 0.0D) {
                     var4 = var10;
                     var6 = var13;
                  }
               }
            }
         }
      }

      if (this.field_184539_c != null) {
         if (var8) {
            this.field_184540_av = 2;
         } else if (this.field_184540_av-- <= 0) {
            this.field_184539_c = null;
         }
      }

      if (var4 != null) {
         var3 = new RayTraceResult(var4);
      }

      if (var3 != null) {
         if (var3.field_72313_a == RayTraceResult.Type.BLOCK && this.field_70170_p.func_180495_p(var3.func_178782_a()).func_177230_c() == Blocks.field_150427_aO) {
            this.func_181015_d(var3.func_178782_a());
         } else {
            this.func_70184_a(var3);
         }
      }

      this.field_70165_t += this.field_70159_w;
      this.field_70163_u += this.field_70181_x;
      this.field_70161_v += this.field_70179_y;
      float var15 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var15) * 57.2957763671875D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
      }

      while(this.field_70125_A - this.field_70127_C >= 180.0F) {
         this.field_70127_C += 360.0F;
      }

      while(this.field_70177_z - this.field_70126_B < -180.0F) {
         this.field_70126_B -= 360.0F;
      }

      while(this.field_70177_z - this.field_70126_B >= 180.0F) {
         this.field_70126_B += 360.0F;
      }

      this.field_70125_A = this.field_70127_C + (this.field_70125_A - this.field_70127_C) * 0.2F;
      this.field_70177_z = this.field_70126_B + (this.field_70177_z - this.field_70126_B) * 0.2F;
      float var16 = 0.99F;
      float var17 = this.func_70185_h();
      if (this.func_70090_H()) {
         for(int var18 = 0; var18 < 4; ++var18) {
            float var19 = 0.25F;
            this.field_70170_p.func_195594_a(Particles.field_197612_e, this.field_70165_t - this.field_70159_w * 0.25D, this.field_70163_u - this.field_70181_x * 0.25D, this.field_70161_v - this.field_70179_y * 0.25D, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         }

         var16 = 0.8F;
      }

      this.field_70159_w *= (double)var16;
      this.field_70181_x *= (double)var16;
      this.field_70179_y *= (double)var16;
      if (!this.func_189652_ae()) {
         this.field_70181_x -= (double)var17;
      }

      this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   protected float func_70185_h() {
      return 0.03F;
   }

   protected abstract void func_70184_a(RayTraceResult var1);

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74768_a("xTile", this.field_145788_c);
      var1.func_74768_a("yTile", this.field_145786_d);
      var1.func_74768_a("zTile", this.field_145787_e);
      var1.func_74774_a("shake", (byte)this.field_70191_b);
      var1.func_74774_a("inGround", (byte)(this.field_174854_a ? 1 : 0));
      if (this.field_200218_h != null) {
         var1.func_74782_a("owner", NBTUtil.func_186862_a(this.field_200218_h));
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_145788_c = var1.func_74762_e("xTile");
      this.field_145786_d = var1.func_74762_e("yTile");
      this.field_145787_e = var1.func_74762_e("zTile");
      this.field_70191_b = var1.func_74771_c("shake") & 255;
      this.field_174854_a = var1.func_74771_c("inGround") == 1;
      this.field_70192_c = null;
      if (var1.func_150297_b("owner", 10)) {
         this.field_200218_h = NBTUtil.func_186860_b(var1.func_74775_l("owner"));
      }

   }

   @Nullable
   public EntityLivingBase func_85052_h() {
      if (this.field_70192_c == null && this.field_200218_h != null && this.field_70170_p instanceof WorldServer) {
         Entity var1 = ((WorldServer)this.field_70170_p).func_175733_a(this.field_200218_h);
         if (var1 instanceof EntityLivingBase) {
            this.field_70192_c = (EntityLivingBase)var1;
         } else {
            this.field_200218_h = null;
         }
      }

      return this.field_70192_c;
   }
}
