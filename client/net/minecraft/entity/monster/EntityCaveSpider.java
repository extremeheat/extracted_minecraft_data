package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityCaveSpider extends EntitySpider {
   public EntityCaveSpider(World var1) {
      super(EntityType.field_200794_h, var1);
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
               ((EntityLivingBase)var1).func_195064_c(new PotionEffect(MobEffects.field_76436_u, var2 * 20, 0));
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      return var2;
   }

   public float func_70047_e() {
      return 0.45F;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186436_r;
   }
}
