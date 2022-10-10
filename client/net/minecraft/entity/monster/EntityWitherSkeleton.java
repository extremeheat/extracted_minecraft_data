package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityWitherSkeleton extends AbstractSkeleton {
   public EntityWitherSkeleton(World var1) {
      super(EntityType.field_200722_aA, var1);
      this.func_70105_a(0.7F, 2.4F);
      this.field_70178_ae = true;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_186386_ak;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_190036_ha;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_190038_hc;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_190037_hb;
   }

   SoundEvent func_190727_o() {
      return SoundEvents.field_190039_hd;
   }

   public void func_70645_a(DamageSource var1) {
      super.func_70645_a(var1);
      if (var1.func_76346_g() instanceof EntityCreeper) {
         EntityCreeper var2 = (EntityCreeper)var1.func_76346_g();
         if (var2.func_70830_n() && var2.func_70650_aV()) {
            var2.func_175493_co();
            this.func_199703_a(Items.field_196183_dw);
         }
      }

   }

   protected void func_180481_a(DifficultyInstance var1) {
      this.func_184201_a(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.field_151052_q));
   }

   protected void func_180483_b(DifficultyInstance var1) {
   }

   @Nullable
   public IEntityLivingData func_204210_a(DifficultyInstance var1, @Nullable IEntityLivingData var2, @Nullable NBTTagCompound var3) {
      IEntityLivingData var4 = super.func_204210_a(var1, var2, var3);
      this.func_110148_a(SharedMonsterAttributes.field_111264_e).func_111128_a(4.0D);
      this.func_85036_m();
      return var4;
   }

   public float func_70047_e() {
      return 2.1F;
   }

   public boolean func_70652_k(Entity var1) {
      if (!super.func_70652_k(var1)) {
         return false;
      } else {
         if (var1 instanceof EntityLivingBase) {
            ((EntityLivingBase)var1).func_195064_c(new PotionEffect(MobEffects.field_82731_v, 200));
         }

         return true;
      }
   }

   protected EntityArrow func_190726_a(float var1) {
      EntityArrow var2 = super.func_190726_a(var1);
      var2.func_70015_d(100);
      return var2;
   }
}
