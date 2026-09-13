package net.minecraft.entity.monster;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
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

   @Override
   public void func_70636_d() {
      this.func_82168_bl();
      float var1 = this.func_70013_c(1.0F);
      if (var1 > 0.5F) {
         this.field_70708_bq += 2;
      }

      super.func_70636_d();
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K && this.field_70170_p.field_73013_u == EnumDifficulty.PEACEFUL) {
         this.func_70106_y();
      }
   }

   @Override
   protected String func_145776_H() {
      return "game.hostile.swim";
   }

   @Override
   protected String func_145777_O() {
      return "game.hostile.swim.splash";
   }

   @Override
   protected Entity func_70782_k() {
      EntityPlayer var1 = this.field_70170_p.func_72856_b(this, 16.0);
      return var1 != null && this.func_70685_l(var1) ? var1 : null;
   }

   @Override
   public boolean func_70097_a(DamageSource var1, float var2) {
      if (this.func_85032_ar()) {
         return false;
      } else if (super.func_70097_a(var1, var2)) {
         Entity var3 = var1.func_76346_g();
         if (this.field_70153_n != var3 && this.field_70154_o != var3) {
            if (var3 != this) {
               this.field_70789_a = var3;
            }

            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   protected String func_70621_aR() {
      return "game.hostile.hurt";
   }

   @Override
   protected String func_70673_aS() {
      return "game.hostile.die";
   }

   @Override
   protected String func_146067_o(int var1) {
      return var1 > 4 ? "game.hostile.hurt.fall.big" : "game.hostile.hurt.fall.small";
   }

   @Override
   public boolean func_70652_k(Entity var1) {
      float var2 = (float)this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111126_e();
      int var3 = 0;
      if (var1 instanceof EntityLivingBase) {
         var2 += EnchantmentHelper.func_77512_a(this, (EntityLivingBase)var1);
         var3 += EnchantmentHelper.func_77507_b(this, (EntityLivingBase)var1);
      }

      boolean var4 = var1.func_70097_a(DamageSource.func_76358_a(this), var2);
      if (var4) {
         if (var3 > 0) {
            var1.func_70024_g(
               (double)(-MathHelper.func_76126_a(this.field_70177_z * 3.1415927F / 180.0F) * (float)var3 * 0.5F),
               0.1,
               (double)(MathHelper.func_76134_b(this.field_70177_z * 3.1415927F / 180.0F) * (float)var3 * 0.5F)
            );
            this.field_70159_w *= 0.6;
            this.field_70179_y *= 0.6;
         }

         int var5 = EnchantmentHelper.func_90036_a(this);
         if (var5 > 0) {
            var1.func_70015_d(var5 * 4);
         }

         if (var1 instanceof EntityLivingBase) {
            EnchantmentHelper.func_151384_a((EntityLivingBase)var1, this);
         }

         EnchantmentHelper.func_151385_b(this, var1);
      }

      return var4;
   }

   @Override
   protected void func_70785_a(Entity var1, float var2) {
      if (this.field_70724_aR <= 0
         && var2 < 2.0F
         && var1.field_70121_D.field_72337_e > this.field_70121_D.field_72338_b
         && var1.field_70121_D.field_72338_b < this.field_70121_D.field_72337_e) {
         this.field_70724_aR = 20;
         this.func_70652_k(var1);
      }
   }

   @Override
   public float func_70783_a(int var1, int var2, int var3) {
      return 0.5F - this.field_70170_p.func_72801_o(var1, var2, var3);
   }

   protected boolean func_70814_o() {
      int var1 = MathHelper.func_76128_c(this.field_70165_t);
      int var2 = MathHelper.func_76128_c(this.field_70121_D.field_72338_b);
      int var3 = MathHelper.func_76128_c(this.field_70161_v);
      if (this.field_70170_p.func_72972_b(EnumSkyBlock.Sky, var1, var2, var3) > this.field_70146_Z.nextInt(32)) {
         return false;
      } else {
         int var4 = this.field_70170_p.func_72957_l(var1, var2, var3);
         if (this.field_70170_p.func_72911_I()) {
            int var5 = this.field_70170_p.field_73008_k;
            this.field_70170_p.field_73008_k = 10;
            var4 = this.field_70170_p.func_72957_l(var1, var2, var3);
            this.field_70170_p.field_73008_k = var5;
         }

         return var4 <= this.field_70146_Z.nextInt(8);
      }
   }

   @Override
   public boolean func_70601_bi() {
      return this.field_70170_p.field_73013_u != EnumDifficulty.PEACEFUL && this.func_70814_o() && super.func_70601_bi();
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110140_aT().func_111150_b(SharedMonsterAttributes.field_111264_e);
   }

   @Override
   protected boolean func_146066_aG() {
      return true;
   }
}
