package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
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

   @Override
   public int func_94087_l() {
      return 3;
   }

   @Override
   public Block func_145817_o() {
      return Blocks.field_150335_W;
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (this.field_94106_a > 0) {
         --this.field_94106_a;
         this.field_70170_p.func_72869_a("smoke", this.field_70165_t, this.field_70163_u + 0.5, this.field_70161_v, 0.0, 0.0, 0.0);
      } else if (this.field_94106_a == 0) {
         this.func_94103_c(this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y);
      }

      if (this.field_70123_F) {
         double var1 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
         if (var1 >= 0.009999999776482582) {
            this.func_94103_c(var1);
         }
      }
   }

   @Override
   public void func_94095_a(DamageSource var1) {
      super.func_94095_a(var1);
      double var2 = this.field_70159_w * this.field_70159_w + this.field_70179_y * this.field_70179_y;
      if (!var1.func_94541_c()) {
         this.func_70099_a(new ItemStack(Blocks.field_150335_W, 1), 0.0F);
      }

      if (var1.func_76347_k() || var1.func_94541_c() || var2 >= 0.009999999776482582) {
         this.func_94103_c(var2);
      }
   }

   protected void func_94103_c(double var1) {
      if (!this.field_70170_p.field_72995_K) {
         double var3 = Math.sqrt(var1);
         if (var3 > 5.0) {
            var3 = 5.0;
         }

         this.field_70170_p
            .func_72876_a(this, this.field_70165_t, this.field_70163_u, this.field_70161_v, (float)(4.0 + this.field_70146_Z.nextDouble() * 1.5 * var3), true);
         this.func_70106_y();
      }
   }

   @Override
   protected void func_70069_a(float var1) {
      if (var1 >= 3.0F) {
         float var2 = var1 / 10.0F;
         this.func_94103_c((double)(var2 * var2));
      }

      super.func_70069_a(var1);
   }

   @Override
   public void func_96095_a(int var1, int var2, int var3, boolean var4) {
      if (var4 && this.field_94106_a < 0) {
         this.func_94105_c();
      }
   }

   @Override
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
         this.field_70170_p.func_72956_a(this, "game.tnt.primed", 1.0F, 1.0F);
      }
   }

   public int func_94104_d() {
      return this.field_94106_a;
   }

   public boolean func_96096_ay() {
      return this.field_94106_a > -1;
   }

   @Override
   public float func_145772_a(Explosion var1, World var2, int var3, int var4, int var5, Block var6) {
      return !this.func_96096_ay() || !BlockRailBase.func_150051_a(var6) && !BlockRailBase.func_150049_b_(var2, var3, var4 + 1, var5)
         ? super.func_145772_a(var1, var2, var3, var4, var5, var6)
         : 0.0F;
   }

   @Override
   public boolean func_145774_a(Explosion var1, World var2, int var3, int var4, int var5, Block var6, float var7) {
      return !this.func_96096_ay() || !BlockRailBase.func_150051_a(var6) && !BlockRailBase.func_150049_b_(var2, var3, var4 + 1, var5)
         ? super.func_145774_a(var1, var2, var3, var4, var5, var6, var7)
         : false;
   }

   @Override
   protected void func_70037_a(NBTTagCompound var1) {
      super.func_70037_a(var1);
      if (var1.func_150297_b("TNTFuse", 99)) {
         this.field_94106_a = var1.func_74762_e("TNTFuse");
      }
   }

   @Override
   protected void func_70014_b(NBTTagCompound var1) {
      super.func_70014_b(var1);
      var1.func_74768_a("TNTFuse", this.field_94106_a);
   }
}
