package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;

public abstract class EntityMob extends EntityCreature implements IMob {
   public EntityMob(World var1) {
      super(var1);
      this.field_70728_aV = 5;
   }

   public void func_70636_d() {
      this.func_82168_bl();
      float var1 = this.func_70013_c(1.0F);
      if (var1 > 0.5F) {
         this.field_70708_bq += 2;
      }

      super.func_70636_d();
   }

   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.func_175659_aa() == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      }

   }

   protected String func_145776_H() {
      return "game.hostile.swim";
   }

   protected String func_145777_O() {
      return "game.hostile.swim.splash";
   }

   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_180431_b(var1)) {
         return false;
      } else if (super.func_70097_a(var1, var2)) {
         Entity var3 = var1.func_76346_g();
         return this.field_70153_n != var3 && this.field_70154_o != var3 ? true : true;
      } else {
         return false;
      }
   }

   protected String func_70621_aR() {
      return "game.hostile.hurt";
   }

   protected String func_70673_aS() {
      return "game.hostile.die";
   }

   protected String func_146067_o(int var1) {
      return var1 > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
   }

   public boolean func_70652_k(Entity var1) {
      float var2 = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
      int var3 = 0;
      if (var1 instanceof EntityLivingBase) {
         var2 += EnchantmentHelper.func_152377_a(this.func_70694_bm(), ((EntityLivingBase)var1).func_70668_bt());
         var3 += EnchantmentHelper.func_77501_a(this);
      }

      boolean var4 = var1.func_70097_a(DamageSource.func_76358_a(this), var2);
      if (var4) {
         if (var3 > 0) {
            var1.func_70024_g((double)(-MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F) * (float)var3 * 0.5F), 0.1D, (double)(MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F) * (float)var3 * 0.5F));
            this.field_70159_w *= 0.6D;
            this.field_70179_y *= 0.6D;
         }

         int var5 = EnchantmentHelper.func_90036_a(this);
         if (var5 > 0) {
            var1.func_70015_d(var5 * 4);
         }

         this.func_174815_a(this, var1);
      }

      return var4;
   }

   public float func_180484_a(BlockPos var1) {
      return 0.5F - this.field_70170_p.func_175724_o(var1);
   }

   protected boolean func_70814_o() {
      BlockPos var1 = new BlockPos(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
      if (this.field_70170_p.func_175642_b(EnumSkyBlock.SKY, var1) > this.field_70146_Z.nextInt(32)) {
         return false;
      } else {
         int var2 = this.field_70170_p.func_175671_l(var1);
         if (this.field_70170_p.func_72911_I()) {
            int var3 = this.field_70170_p.func_175657_ab();
            this.field_70170_p.func_175692_b(10);
            var2 = this.field_70170_p.func_175671_l(var1);
            this.field_70170_p.func_175692_b(var3);
         }

         return var2 <= this.field_70146_Z.nextInt(8);
      }
   }

   public boolean func_70601_bi() {
      return this.field_70170_p.func_175659_aa() != EnumDifficulty.PEACEFUL && this.func_70814_o() && super.func_70601_bi();
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
   }

   protected boolean func_146066_aG() {
      return true;
   }
}
