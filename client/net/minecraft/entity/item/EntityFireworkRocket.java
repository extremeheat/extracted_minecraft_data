package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkRocket extends Entity {
   private int field_92056_a;
   private int field_92055_b;

   public EntityFireworkRocket(World var1) {
      super(var1);
      this.func_70105_a(0.25F, 0.25F);
   }

   protected void func_70088_a() {
      this.field_70180_af.func_82709_a(8, 5);
   }

   public boolean func_70112_a(double var1) {
      return var1 < 4096.0D;
   }

   public EntityFireworkRocket(World var1, double var2, double var4, double var6, ItemStack var8) {
      super(var1);
      this.field_92056_a = 0;
      this.func_70105_a(0.25F, 0.25F);
      this.func_70107_b(var2, var4, var6);
      int var9 = 1;
      if (var8 != null && var8.func_77942_o()) {
         this.field_70180_af.func_75692_b(8, var8);
         NBTTagCompound var10 = var8.func_77978_p();
         NBTTagCompound var11 = var10.func_74775_l("Fireworks");
         if (var11 != null) {
            var9 += var11.func_74771_c("Flight");
         }
      }

      this.field_70159_w = this.field_70146_Z.nextGaussian() * 0.001D;
      this.field_70179_y = this.field_70146_Z.nextGaussian() * 0.001D;
      this.field_70181_x = 0.05D;
      this.field_92055_b = 10 * var9 + this.field_70146_Z.nextInt(6) + this.field_70146_Z.nextInt(7);
   }

   public void func_70016_h(double var1, double var3, double var5) {
      this.field_70159_w = var1;
      this.field_70181_x = var3;
      this.field_70179_y = var5;
      if (this.field_70127_C == 0.0F && this.field_70126_B == 0.0F) {
         float var7 = MathHelper.func_76133_a(var1 * var1 + var5 * var5);
         this.field_70126_B = this.field_70177_z = (float)(MathHelper.func_181159_b(var1, var5) * 180.0D / 3.1415927410125732D);
         this.field_70127_C = this.field_70125_A = (float)(MathHelper.func_181159_b(var3, (double)var7) * 180.0D / 3.1415927410125732D);
      }

   }

   public void func_70071_h_() {
      this.field_70142_S = this.field_70165_t;
      this.field_70137_T = this.field_70163_u;
      this.field_70136_U = this.field_70161_v;
      super.func_70071_h_();
      this.field_70159_w *= 1.15D;
      this.field_70179_y *= 1.15D;
      this.field_70181_x += 0.04D;
      this.func_70091_d(this.field_70159_w, this.field_70181_x, this.field_70179_y);
      float var1 = MathHelper.func_76133_a(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      this.field_70177_z = (float)(MathHelper.func_181159_b(this.field_70159_w, this.field_70179_y) * 180.0D / 3.1415927410125732D);

      for(this.field_70125_A = (float)(MathHelper.func_181159_b(this.field_70181_x, (double)var1) * 180.0D / 3.1415927410125732D); this.field_70125_A - this.field_70127_C < -180.0F; this.field_70127_C -= 360.0F) {
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
      if (this.field_92056_a == 0 && !this.func_174814_R()) {
         this.field_70170_p.func_72956_a(this, "fireworks.launch", 3.0F, 1.0F);
      }

      ++this.field_92056_a;
      if (this.field_70170_p.field_72995_K && this.field_92056_a % 2 < 2) {
         this.field_70170_p.func_175688_a(EnumParticleTypes.FIREWORKS_SPARK, this.field_70165_t, this.field_70163_u - 0.3D, this.field_70161_v, this.field_70146_Z.nextGaussian() * 0.05D, -this.field_70181_x * 0.5D, this.field_70146_Z.nextGaussian() * 0.05D);
      }

      if (!this.field_70170_p.field_72995_K && this.field_92056_a > this.field_92055_b) {
         this.field_70170_p.func_72960_a(this, (byte)17);
         this.func_70106_y();
      }

   }

   public void func_70103_a(byte var1) {
      if (var1 == 17 && this.field_70170_p.field_72995_K) {
         ItemStack var2 = this.field_70180_af.func_82710_f(8);
         NBTTagCompound var3 = null;
         if (var2 != null && var2.func_77942_o()) {
            var3 = var2.func_77978_p().func_74775_l("Fireworks");
         }

         this.field_70170_p.func_92088_a(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70159_w, this.field_70181_x, this.field_70179_y, var3);
      }

      super.func_70103_a(var1);
   }

   public void func_70014_b(NBTTagCompound var1) {
      var1.func_74768_a("Life", this.field_92056_a);
      var1.func_74768_a("LifeTime", this.field_92055_b);
      ItemStack var2 = this.field_70180_af.func_82710_f(8);
      if (var2 != null) {
         NBTTagCompound var3 = new NBTTagCompound();
         var2.func_77955_b(var3);
         var1.func_74782_a("FireworksItem", var3);
      }

   }

   public void func_70037_a(NBTTagCompound var1) {
      this.field_92056_a = var1.func_74762_e("Life");
      this.field_92055_b = var1.func_74762_e("LifeTime");
      NBTTagCompound var2 = var1.func_74775_l("FireworksItem");
      if (var2 != null) {
         ItemStack var3 = ItemStack.func_77949_a(var2);
         if (var3 != null) {
            this.field_70180_af.func_75692_b(8, var3);
         }
      }

   }

   public float func_70013_c(float var1) {
      return super.func_70013_c(var1);
   }

   public int func_70070_b(float var1) {
      return super.func_70070_b(var1);
   }

   public boolean func_70075_an() {
      return false;
   }
}
