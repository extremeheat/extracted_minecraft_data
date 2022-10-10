package net.minecraft.entity.projectile;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityLlamaSpit extends Entity implements IProjectile {
   public EntityLlama field_190539_a;
   private NBTTagCompound field_190540_b;

   public EntityLlamaSpit(World var1) {
      super(EntityType.field_200770_J, var1);
      this.func_70105_a(0.25F, 0.25F);
   }

   public EntityLlamaSpit(World var1, EntityLlama var2) {
      this(var1);
      this.field_190539_a = var2;
      this.func_70107_b(var2.field_70165_t - (double)(var2.field_70130_N + 1.0F) * 0.5D * (double)MathHelper.func_76126_a(var2.field_70761_aq * 0.017453292F), var2.field_70163_u + (double)var2.func_70047_e() - 0.10000000149011612D, var2.field_70161_v + (double)(var2.field_70130_N + 1.0F) * 0.5D * (double)MathHelper.func_76134_b(var2.field_70761_aq * 0.017453292F));
   }

   public EntityLlamaSpit(World var1, double var2, double var4, double var6, double var8, double var10, double var12) {
      this(var1);
      this.func_70107_b(var2, var4, var6);

      for(int var14 = 0; var14 < 7; ++var14) {
         double var15 = 0.4D + 0.1D * (double)var14;
         var1.func_195594_a(Particles.field_197602_M, var2, var4, var6, var8 * var15, var10, var12 * var15);
      }

      this.field_70159_w = var8;
      this.field_70181_x = var10;
      this.field_70179_y = var12;
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_190540_b != null) {
         this.func_190537_j();
      }

      Vec3d var1 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      Vec3d var2 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      RayTraceResult var3 = this.field_70170_p.func_72933_a(var1, var2);
      var1 = new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      var2 = new Vec3d(this.field_70165_t + this.field_70159_w, this.field_70163_u + this.field_70181_x, this.field_70161_v + this.field_70179_y);
      if (var3 != null) {
         var2 = new Vec3d(var3.field_72307_f.field_72450_a, var3.field_72307_f.field_72448_b, var3.field_72307_f.field_72449_c);
      }

      Entity var4 = this.func_190538_a(var1, var2);
      if (var4 != null) {
         var3 = new RayTraceResult(var4);
      }

      if (var3 != null) {
         this.func_190536_a(var3);
      }

      this.field_70165_t += this.field_70159_w;
      this.field_70163_u += this.field_70181_x;
      this.field_70161_v += this.field_70179_y;
      float var5 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var5) * 57.2957763671875D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
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
      float var6 = 0.99F;
      float var7 = 0.06F;
      if (!this.field_70170_p.func_72875_a(this.func_174813_aQ(), Material.field_151579_a)) {
         this.func_70106_y();
      } else if (this.func_203005_aq()) {
         this.func_70106_y();
      } else {
         this.field_70159_w *= 0.9900000095367432D;
         this.field_70181_x *= 0.9900000095367432D;
         this.field_70179_y *= 0.9900000095367432D;
         if (!this.func_189652_ae()) {
            this.field_70181_x -= 0.05999999865889549D;
         }

         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
      }
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var7) * 57.2957763671875D);
         this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 57.2957763671875D);
         this.field_70127_C = this.field_70125_A;
         this.field_70126_B = this.field_70177_z;
         this.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, this.field_70125_A);
      }

   }

   @Nullable
   private Entity func_190538_a(Vec3d var1, Vec3d var2) {
      Entity var3 = null;
      List var4 = this.field_70170_p.func_72839_b(this, this.func_174813_aQ().func_72321_a(this.field_70159_w, this.field_70181_x, this.field_70179_y).func_186662_g(1.0D));
      double var5 = 0.0D;
      Iterator var7 = var4.iterator();

      while(true) {
         Entity var8;
         double var11;
         do {
            RayTraceResult var10;
            do {
               do {
                  if (!var7.hasNext()) {
                     return var3;
                  }

                  var8 = (Entity)var7.next();
               } while(var8 == this.field_190539_a);

               AxisAlignedBB var9 = var8.func_174813_aQ().func_186662_g(0.30000001192092896D);
               var10 = var9.func_72327_a(var1, var2);
            } while(var10 == null);

            var11 = var1.func_72436_e(var10.field_72307_f);
         } while(var11 >= var5 && var5 != 0.0D);

         var3 = var8;
         var5 = var11;
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

   public void func_190536_a(RayTraceResult var1) {
      if (var1.field_72308_g != null && this.field_190539_a != null) {
         var1.field_72308_g.func_70097_a(DamageSource.func_188403_a(this, this.field_190539_a).func_76349_b(), 1.0F);
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70106_y();
      }

   }

   protected void func_70088_a() {
   }

   protected void func_70037_a(NBTTagCompound var1) {
      if (var1.func_150297_b("Owner", 10)) {
         this.field_190540_b = var1.func_74775_l("Owner");
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      if (this.field_190539_a != null) {
         NBTTagCompound var2 = new NBTTagCompound();
         UUID var3 = this.field_190539_a.func_110124_au();
         var2.func_186854_a("OwnerUUID", var3);
         var1.func_74782_a("Owner", var2);
      }

   }

   private void func_190537_j() {
      if (this.field_190540_b != null && this.field_190540_b.func_186855_b("OwnerUUID")) {
         UUID var1 = this.field_190540_b.func_186857_a("OwnerUUID");
         List var2 = this.field_70170_p.func_72872_a(EntityLlama.class, this.func_174813_aQ().func_186662_g(15.0D));
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            EntityLlama var4 = (EntityLlama)var3.next();
            if (var4.func_110124_au().equals(var1)) {
               this.field_190539_a = var4;
               break;
            }
         }
      }

      this.field_190540_b = null;
   }
}
