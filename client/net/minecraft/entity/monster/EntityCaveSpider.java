package net.minecraft.entity.monster;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityCaveSpider extends EntitySpider {
   public EntityCaveSpider(World var1) {
      super(var1);
      this.func_70105_a(0.7F, 0.5F);
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.func_110148_a(SharedMonsterAttributes.field_111267_a).func_111128_a(12.0D);
   }

   public boolean func_70652_k(Entity var1) {
      if (super.func_70652_k(var1)) {
         if (var1 instanceof EntityLivingBase) {
            byte var2 = 0;
            if (this.field_70170_p.func_175659_aa() == EnumDifficulty.NORMAL) {
               var2 = 7;
            } else if (this.field_70170_p.func_175659_aa() == EnumDifficulty.HARD) {
               var2 = 15;
            }

            if (var2 > 0) {
               ((EntityLivingBase)var1).func_70690_d(new PotionEffect(Potion.field_76436_u.field_76415_H, var2 * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public IEntityLivingData func_180482_a(DifficultyInstance var1, IEntityLivingData var2) {
      return var2;
   }

   public float func_70047_e() {
      return 0.45F;
   }
}
