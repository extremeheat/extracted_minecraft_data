package net.minecraft.entity.item;

import net.minecraft.block.BlockRailBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public class EntityMinecartTNT extends EntityMinecart {
   private int field_94106_a = -1;

   public EntityMinecartTNT(World var1) {
      super(var1);
   }

   public EntityMinecartTNT(World var1, double var2, double var4, double var6) {
      super(var1, var2, var4, var6);
   }

   public EntityMinecart.EnumMinecartType func_180456_s() {
      return EntityMinecart.EnumMinecartType.TNT;
   }

   public IBlockState func_180457_u() {
      return Blocks.field_150335_W.func_176223_P();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_94106_a > 0) {
         --this.field_94106_a;
         this.field_70170_p.func_175688_a(EnumParticleTypes.SMOKE_NORMAL, this.field_70165_t, this.field_70163_u + 0.5D, this.field_70161_v, 0.0D, 0.0D, 0.0D);
      } else if (this.field_94106_a == 0) {
         this.func_94103_c(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      }

      if (this.field_70123_F) {
         double var1 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
         if (var1 >= 0.009999999776482582D) {
            this.func_94103_c(var1);
         }
      }

   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      Entity var3 = var1.func_76364_f();
      if (var3 instanceof EntityArrow) {
         EntityArrow var4 = (EntityArrow)var3;
         if (var4.func_70027_ad()) {
            this.func_94103_c(var4.field_70159_w * var4.field_70159_w + var4.field_70181_x * var4.field_70181_x + var4.field_70179_y * var4.field_70179_y);
         }
      }

      return super.func_70097_a(var1, var2);
   }

   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      double var2 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
      if (!var1.func_94541_c() && this.field_70170_p.func_82736_K().func_82766_b("doEntityDrops")) {
         this.func_70099_a(new ItemStack(Blocks.field_150335_W, 1), 0.0F);
      }

      if (var1.func_76347_k() || var1.func_94541_c() || var2 >= 0.009999999776482582D) {
         this.func_94103_c(var2);
      }

   }

   protected void func_94103_c(double var1) {
      if (!this.field_70170_p.field_72995_K) {
         double var3 = Math.sqrt(var1);
         if (var3 > 5.0D) {
            var3 = 5.0D;
         }

         this.field_70170_p.func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float)(4.0D + this.field_70146_Z.nextDouble() * 1.5D * var3), true);
         this.func_70106_y();
      }

   }

   public void func_180430_e(float var1, float var2) {
      if (var1 >= 3.0F) {
         float var3 = var1 / 10.0F;
         this.func_94103_c((double)(var3 * var3));
      }

      super.func_180430_e(var1, var2);
   }

   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.field_94106_a < 0) {
         this.func_94105_c();
      }

   }

   public void func_70103_a(byte var1) {
      if (var1 == 10) {
         this.func_94105_c();
      } else {
         super.func_70103_a(var1);
      }

   }

   public void func_94105_c() {
      this.field_94106_a = 80;
      if (!this.field_70170_p.field_72995_K) {
         this.field_70170_p.func_72960_a(this, (byte)10);
         if (!this.func_174814_R()) {
            this.field_70170_p.func_72956_a(this, "game.tnt.primed", 1.0F, 1.0F);
         }
      }

   }

   public int func_94104_d() {
      return this.field_94106_a;
   }

   public boolean func_96096_ay() {
      return this.field_94106_a > -1;
   }

   public float func_180428_a(Explosion var1, World var2, BlockPos var3, IBlockState var4) {
      return !this.func_96096_ay() || !BlockRailBase.func_176563_d(var4) && !BlockRailBase.func_176562_d(var2, var3.func_177984_a()) ? super.func_180428_a(var1, var2, var3, var4) : 0.0F;
   }

   public boolean func_174816_a(Explosion var1, World var2, BlockPos var3, IBlockState var4, float var5) {
      return !this.func_96096_ay() || !BlockRailBase.func_176563_d(var4) && !BlockRailBase.func_176562_d(var2, var3.func_177984_a()) ? super.func_174816_a(var1, var2, var3, var4, var5) : false;
   }

   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("TNTFuse", 99)) {
         this.field_94106_a = var1.func_74762_e("TNTFuse");
      }

   }

   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("TNTFuse", this.field_94106_a);
   }
}
