package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
   protected Block field_175506_bl;
   private int field_70881_d;
   private EntityPlayer field_146084_br;

   public EntityAnimal(World var1) {
      super(var1);
      this.field_175506_bl = Blocks.field_150349_c;
   }

   protected void func_70619_bc() {
      if (this.func_70874_b() != 0) {
         this.field_70881_d = 0;
      }

      super.func_70619_bc();
   }

   public void func_70636_d() {
      super.func_70636_d();
      if (this.func_70874_b() != 0) {
         this.field_70881_d = 0;
      }

      if (this.field_70881_d > 0) {
         --this.field_70881_d;
         if (this.field_70881_d % 10 == 0) {
            double var1 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var3 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var5 = this.field_70146_Z.nextGaussian() * 0.02D;
            this.field_70170_p.func_175688_a(EnumParticleTypes.HEART, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var1, var3, var5);
         }
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else {
         this.field_70881_d = 0;
         return super.func_70097_a(var1, var2);
      }
   }

   public float func_180484_a(BlockPos var1) {
      return this.field_70170_p.func_180495_p(var1.func_177977_b()).func_177230_c() == Blocks.field_150349_c ? 10.0F : this.field_70170_p.func_175724_o(var1) - 0.5F;
   }

   public void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("InLove", this.field_70881_d);
   }

   public void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      this.field_70881_d = var1.func_74762_e("InLove");
   }

   public boolean func_70601_bi() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.func_174813_aQ().field_72338_b);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      BlockPos var4 = new BlockPos(var1, var2, var3);
      return this.field_70170_p.func_180495_p(var4.func_177977_b()).func_177230_c() == this.field_175506_bl && this.field_70170_p.func_175699_k(var4) > 8 && super.func_70601_bi();
   }

   public int func_70627_aG() {
      return 120;
   }

   protected boolean func_70692_ba() {
      return false;
   }

   protected int func_70693_a(EntityPlayer var1) {
      return 1 + this.field_70170_p.field_73012_v.nextInt(3);
   }

   public boolean func_70877_b(ItemStack var1) {
      if (var1 == null) {
         return false;
      } else {
         return var1.func_77973_b() == Items.field_151015_O;
      }
   }

   public boolean func_70085_c(EntityPlayer var1) {
      ItemStack var2 = var1.field_71071_by.func_70448_g();
      if (var2 != null) {
         if (this.func_70877_b(var2) && this.func_70874_b() == 0 && this.field_70881_d <= 0) {
            this.func_175505_a(var1, var2);
            this.func_146082_f(var1);
            return true;
         }

         if (this.func_70631_g_() && this.func_70877_b(var2)) {
            this.func_175505_a(var1, var2);
            this.func_175501_a((int)((float)(-this.func_70874_b() / 20) * 0.1F), true);
            return true;
         }
      }

      return super.func_70085_c(var1);
   }

   protected void func_175505_a(EntityPlayer var1, ItemStack var2) {
      if (!var1.field_71075_bZ.field_75098_d) {
         --var2.field_77994_a;
         if (var2.field_77994_a <= 0) {
            var1.field_71071_by.func_70299_a(var1.field_71071_by.field_70461_c, (ItemStack)null);
         }
      }

   }

   public void func_146082_f(EntityPlayer var1) {
      this.field_70881_d = 600;
      this.field_146084_br = var1;
      this.field_70170_p.func_72960_a(this, (byte)18);
   }

   public EntityPlayer func_146083_cb() {
      return this.field_146084_br;
   }

   public boolean func_70880_s() {
      return this.field_70881_d > 0;
   }

   public void func_70875_t() {
      this.field_70881_d = 0;
   }

   public boolean func_70878_b(EntityAnimal var1) {
      if (var1 == this) {
         return false;
      } else if (var1.getClass() != this.getClass()) {
         return false;
      } else {
         return this.func_70880_s() && var1.func_70880_s();
      }
   }

   public void func_70103_a(byte var1) {
      if (var1 == 18) {
         for(int var2 = 0; var2 < 7; ++var2) {
            double var3 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var5 = this.field_70146_Z.nextGaussian() * 0.02D;
            double var7 = this.field_70146_Z.nextGaussian() * 0.02D;
            this.field_70170_p.func_175688_a(EnumParticleTypes.HEART, this.field_70165_t + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, this.field_70163_u + 0.5D + (double)(this.field_70146_Z.nextFloat() * this.field_70131_O), this.field_70161_v + (double)(this.field_70146_Z.nextFloat() * this.field_70130_N * 2.0F) - (double)this.field_70130_N, var3, var5, var7);
         }
      } else {
         super.func_70103_a(var1);
      }

   }
}
