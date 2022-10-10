package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityEnderEye extends Entity {
   private double field_70224_b;
   private double field_70225_c;
   private double field_70222_d;
   private int field_70223_e;
   private boolean field_70221_f;

   public EntityEnderEye(World var1) {
      super(EntityType.field_200808_v, var1);
      this.func_70105_a(0.25F, 0.25F);
   }

   public EntityEnderEye(World var1, double var2, double var4, double var6) {
      this(var1);
      this.field_70223_e = 0;
      this.func_70107_b(var2, var4, var6);
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

   public void func_180465_a(BlockPos var1) {
      double var2 = (double)var1.func_177958_n();
      int var4 = var1.func_177956_o();
      double var5 = (double)var1.func_177952_p();
      double var7 = var2 - this.field_70165_t;
      double var9 = var5 - this.field_70161_v;
      float var11 = MathHelper.func_76133_a(var7 * var7 + var9 * var9);
      if (var11 > 12.0F) {
         this.field_70224_b = this.field_70165_t + var7 / (double)var11 * 12.0D;
         this.field_70222_d = this.field_70161_v + var9 / (double)var11 * 12.0D;
         this.field_70225_c = this.field_70163_u + 8.0D;
      } else {
         this.field_70224_b = var2;
         this.field_70225_c = (double)var4;
         this.field_70222_d = var5;
      }

      this.field_70223_e = 0;
      this.field_70221_f = this.field_70146_Z.nextInt(5) > 0;
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
      this.field_70165_t += this.field_70159_w;
      this.field_70163_u += this.field_70181_x;
      this.field_70161_v += this.field_70179_y;
      float var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 57.2957763671875D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var1) * 57.2957763671875D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
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
         float var7 = (float)MathHelper.func_181159_b(var4, var2);
         double var8 = (double)var1 + (double)(var6 - var1) * 0.0025D;
         if (var6 < 1.0F) {
            var8 *= 0.8D;
            this.field_70181_x *= 0.8D;
         }

         this.field_70159_w = Math.cos((double)var7) * var8;
         this.field_70179_y = Math.sin((double)var7) * var8;
         if (this.field_70163_u < this.field_70225_c) {
            this.field_70181_x += (1.0D - this.field_70181_x) * 0.014999999664723873D;
         } else {
            this.field_70181_x += (-1.0D - this.field_70181_x) * 0.014999999664723873D;
         }
      }

      float var10 = 0.25F;
      if (this.func_70090_H()) {
         for(int var3 = 0; var3 < 4; ++var3) {
            this.field_70170_p.func_195594_a(Particles.field_197612_e, this.field_70165_t - this.field_70159_w * 0.25D, this.field_70163_u - this.field_70181_x * 0.25D, this.field_70161_v - this.field_70179_y * 0.25D, this.field_70159_w, this.field_70181_x, this.field_70179_y);
         }
      } else {
         this.field_70170_p.func_195594_a(Particles.field_197599_J, this.field_70165_t - this.field_70159_w * 0.25D + this.field_70146_Z.nextDouble() * 0.6D - 0.3D, this.field_70163_u - this.field_70181_x * 0.25D - 0.5D, this.field_70161_v - this.field_70179_y * 0.25D + this.field_70146_Z.nextDouble() * 0.6D - 0.3D, this.field_70159_w, this.field_70181_x, this.field_70179_y);
      }

      if (!this.field_70170_p.field_72995_K) {
         this.func_70107_b(this.field_70165_t, this.field_70163_u, this.field_70161_v);
         ++this.field_70223_e;
         if (this.field_70223_e > 80 && !this.field_70170_p.field_72995_K) {
            this.func_184185_a(SoundEvents.field_193777_bb, 1.0F, 1.0F);
            this.func_70106_y();
            if (this.field_70221_f) {
               this.field_70170_p.func_72838_d(new EntityItem(this.field_70170_p, this.field_70165_t, this.field_70163_u, this.field_70161_v, new ItemStack(Items.field_151061_bv)));
            } else {
               this.field_70170_p.func_175718_b(2003, new BlockPos(this), 0);
            }
         }
      }

   }

   public void func_70014_b(NBTTagCompound var1) {
   }

   public void func_70037_a(NBTTagCompound var1) {
   }

   public float func_70013_c() {
      return 1.0F;
   }

   public int func_70070_b() {
      return 15728880;
   }

   public boolean func_70075_an() {
      return false;
   }
}
