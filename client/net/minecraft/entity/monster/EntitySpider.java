package net.minecraft.entity.monster;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntitySpider extends EntityMob {
   public EntitySpider(World var1) {
      super(var1);
      this.func_70105_a(1.4F, 0.9F);
   }

   @Override
   protected void func_70088_a() {
      super.func_70088_a();
      this.field_70180_af.func_75682_a(16, new Byte((byte)0));
   }

   @Override
   public void func_70071_h_() {
      super.func_70071_h_();
      if (!this.field_70170_p.field_72995_K) {
         this.func_70839_e(this.field_70123_F);
      }
   }

   @Override
   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(16.0);
      this.func_110148_a(SharedMonsterAttributes.field_111263_d).func_111128_a(0.800000011920929);
   }

   @Override
   protected Entity func_70782_k() {
      float var1 = this.func_70013_c(1.0F);
      if (var1 < 0.5F) {
         double var2 = 16.0;
         return this.field_70170_p.func_72856_b(this, var2);
      } else {
         return null;
      }
   }

   @Override
   protected String func_70639_aQ() {
      return "mob.spider.say";
   }

   @Override
   protected String func_70621_aR() {
      return "mob.spider.say";
   }

   @Override
   protected String func_70673_aS() {
      return "mob.spider.death";
   }

   @Override
   protected void func_145780_a(int var1, int var2, int var3, Block var4) {
      this.func_85030_a("mob.spider.step", 0.15F, 1.0F);
   }

   @Override
   protected void func_70785_a(Entity var1, float var2) {
      float var3 = this.func_70013_c(1.0F);
      if (var3 > 0.5F && this.field_70146_Z.nextInt(100) == 0) {
         this.field_70789_a = null;
      } else {
         if (!(var2 > 2.0F) || !(var2 < 6.0F) || this.field_70146_Z.nextInt(10) != 0) {
            super.func_70785_a(var1, var2);
         } else if (this.field_70122_E) {
            double var4 = var1.field_70165_t - this.field_70165_t;
            double var6 = var1.field_70161_v - this.field_70161_v;
            float var8 = MathHelper.func_76133_a(var4 * var4 + var6 * var6);
            this.field_70159_w = var4 / (double)var8 * 0.5 * 0.800000011920929 + this.field_70159_w * 0.20000000298023224;
            this.field_70179_y = var6 / (double)var8 * 0.5 * 0.800000011920929 + this.field_70179_y * 0.20000000298023224;
            this.field_70181_x = 0.4000000059604645;
         }
      }
   }

   @Override
   protected Item func_146068_u() {
      return Items.field_151007_F;
   }

   @Override
   protected void func_70628_a(boolean var1, int var2) {
      super.func_70628_a(var1, var2);
      if (var1 && (this.field_70146_Z.nextInt(3) == 0 || this.field_70146_Z.nextInt(1 + var2) > 0)) {
         this.func_145779_a(Items.field_151070_bp, 1);
      }
   }

   @Override
   public boolean func_70617_f_() {
      return this.func_70841_p();
   }

   @Override
   public void func_70110_aj() {
   }

   @Override
   public EnumCreatureAttribute func_70668_bt() {
      return EnumCreatureAttribute.ARTHROPOD;
   }

   @Override
   public boolean func_70687_e(PotionEffect var1) {
      return var1.func_76456_a() == Potion.field_76436_u.field_76415_H ? false : super.func_70687_e(var1);
   }

   public boolean func_70841_p() {
      return (this.field_70180_af.func_75683_a(16) & 1) != 0;
   }

   public void func_70839_e(boolean var1) {
      byte var2 = this.field_70180_af.func_75683_a(16);
      if (var1) {
         var2 = (byte)(var2 | 1);
      } else {
         var2 = (byte)(var2 & -2);
      }

      this.field_70180_af.func_75692_b(16, var2);
   }

   @Override
   public IEntityLivingData func_110161_a(IEntityLivingData var1) {
      var1 = super.func_110161_a(var1);
      if (this.field_70170_p.field_73012_v.nextInt(100) == 0) {
         EntitySkeleton var2 = new EntitySkeleton(this.field_70170_p);
         var2.func_70012_b(this.field_70165_t, this.field_70163_u, this.field_70161_v, this.field_70177_z, 0.0F);
         var2.func_110161_a(null);
         this.field_70170_p.func_72838_d(var2);
         var2.func_70078_a(this);
      }

      if (var1 == null) {
         var1 = new EntitySpider$GroupData();
         if (this.field_70170_p.field_73013_u == EnumDifficulty.HARD
            && this.field_70170_p.field_73012_v.nextFloat()
               < 0.1F * this.field_70170_p.func_147462_b(this.field_70165_t, this.field_70163_u, this.field_70161_v)) {
            ((EntitySpider$GroupData)var1).func_111104_a(this.field_70170_p.field_73012_v);
         }
      }

      if (var1 instanceof EntitySpider$GroupData) {
         int var4 = ((EntitySpider$GroupData)var1).field_111105_a;
         if (var4 > 0 && Potion.field_76425_a[var4] != null) {
            this.func_70690_d(new PotionEffect(var4, 2147483647));
         }
      }

      return var1;
   }
}
