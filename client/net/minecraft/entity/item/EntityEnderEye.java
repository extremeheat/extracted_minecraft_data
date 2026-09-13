package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity {
   private double field_70224_b;
   private double field_70225_c;
   private double field_70222_d;
   private int field_70223_e;
   private boolean field_70221_f;

   public EntityEnderEye(World var1) {
      super(var1);
      this.func_70105_a(0.25F, 0.25F);
   }

   @Override
   protected void func_70088_a() {
   }

   @Override
   public boolean func_70112_a(double var1) {
      double var3 = this.field_70121_D.func_72320_b() * 4.0;
      var3 *= 64.0;
      return var1 < var3 * var3;
   }

   public EntityEnderEye(World var1, double var2, double var4, double var6) {
      super(var1);
      this.field_70223_e = 0;
      this.func_70105_a(0.25F, 0.25F);
      this.func_70107_b(var2, var4, var6);
      this.field_70129_M = 0.0F;
   }

   public void func_70220_a(double var1, int var3, double var4) {
      double var6 = var1 - this.field_70165_t;
      double var8 = var4 - this.field_70161_v;
      float var10 = MathHelper.func_76133_a(var6 * var6 + var8 * var8);
      if (var10 > 12.0F) {
         this.field_70224_b = this.field_70165_t + var6 / (double)var10 * 12.0;
         this.field_70222_d = this.field_70161_v + var8 / (double)var10 * 12.0;
         this.field_70225_c = this.field_70163_u + 8.0;
      } else {
         this.field_70224_b = var1;
         this.field_70225_c = (double)var3;
         this.field_70222_d = var4;
      }

      this.field_70223_e = 0;
      this.field_70221_f = this.field_70146_Z.nextInt(5) > 0;
   }

   @Override
   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70126_B = this.field_70177_z = (float)(Math.atan2(var1, var5) * 180.0 / 3.1415927410125732);
         this.field_70127_C = this.field_70125_A = (float)(Math.atan2(var3, (double)var7) * 180.0 / 3.1415927410125732);
      }
   }

   @Override
   public void func_70071_h_() {
      this.field_70142_S = this.field_70165_t;
      this.field_70137_T = this.field_70163_u;
      this.field_70136_U = this.field_70161_v;
      super.func_70071_h_();
      this.field_70165_t += this.field_70159_w;
      this.field_70163_u += this.field_70181_x;
      this.field_70161_v += this.field_70179_y;
      float var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(Math.atan2(this.field_70159_w, this.field_70179_y) * 180.0 / 3.1415927410125732);
      this.field_70125_A = (float)(Math.atan2(this.field_70181_x, (double)var1) * 180.0 / 3.1415927410125732);

      while(this.field_70125_A - this.field_70127_C < -180.0F) {
         this.field_70127_C -= 360.0F;
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
      if (!this.field_70170_p.field_72995_K) {
         double var2 = this.field_70224_b - this.field_70165_t;
         double var4 = this.field_70222_d - this.field_70161_v;
         float var6 = (float)Math.sqrt(var2 * var2 + var4 * var4);
         float var7 = (float)Math.atan2(var4, var2);
         double var8 = (double)var1 + (double)(var6 - var1) * 0.0025;
         if (var6 < 1.0F) {
            var8 *= 0.8;
            this.field_70181_x *= 0.8;
         }

         this.field_70159_w = Math.cos((double)var7) * var8;
         this.field_70179_y = Math.sin((double)var7) * var8;
         if (this.field_70163_u < this.field_70225_c) {
            this.field_70181_x += (1.0 - this.field_70181_x) * 0.014999999664723873;
         } else {
            this.field_70181_x += (-1.0 - this.field_70181_x) * 0.014999999664723873;
         }
      }

      float var10 = 0.25F;
      if (this.func_70090_H()) {
         for(int var3 = 0; var3 < 4; ++var3) {
            this.field_70170_p
               .func_72869_a(
                  "bubble",
                  this.field_70165_t - this.field_70159_w * (double)var10,
                  this.field_70163_u - this.field_70181_x * (double)var10,
                  this.field_70161_v - this.field_70179_y * (double)var10,
                  this.field_70159_w,
                  this.field_70181_x,
                  this.field_70179_y
               );
         }
      } else {
         this.field_70170_p
            .func_72869_a(
               "portal",
               this.field_70165_t - this.field_70159_w * (double)var10 + this.field_70146_Z.nextDouble() * 0.6 - 0.3,
               this.field_70163_u - this.field_70181_x * (double)var10 - 0.5,
               this.field_70161_v - this.field_70179_y * (double)var10 + this.field_70146_Z.nextDouble() * 0.6 - 0.3,
               this.field_70159_w,
               this.field_70181_x,
               this.field_70179_y
            );
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         ++this.field_70223_e;
         if (this.field_70223_e > 80 && !this.field_70170_p.field_72995_K) {
            this.func_70106_y();
            if (this.field_70221_f) {
               this.field_70170_p
                  .func_72838_d(
                     new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, new ItemStack(Items.field_151061_bv))
                  );
            } else {
               this.field_70170_p
                  .func_72926_e(2003, (int)Math.round(this.field_70165_t), (int)Math.round(this.field_70163_u), (int)Math.round(this.field_70161_v), 0);
            }
         }
      }
   }

   @Override
   public void func_70014_b(NBTTagCompound var1) {
   }

   @Override
   public void func_70037_a(NBTTagCompound var1) {
   }

   @Override
   public float func_70053_R() {
      return 0.0F;
   }

   @Override
   public float func_70013_c(float var1) {
      return 1.0F;
   }

   @Override
   public int func_70070_b(float var1) {
      return 15728880;
   }

   @Override
   public boolean func_70075_an() {
      return false;
   }
}
