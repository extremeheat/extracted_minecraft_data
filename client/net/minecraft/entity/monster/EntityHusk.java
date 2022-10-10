package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityHusk extends EntityZombie {
   public EntityHusk(World var1) {
      super(EntityType.field_200763_C, var1);
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      return super.func_205020_a(var1, var2) && (var2 || var1.func_175678_i(new BlockPos(this)));
   }

   protected boolean func_190730_o() {
      return false;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_190022_cI;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_190024_cK;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_190023_cJ;
   }

   protected SoundEvent func_190731_di() {
      return SoundEvents.field_190025_cL;
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_191182_ar;
   }

   public boolean func_70652_k(Entity var1) {
      boolean var2 = super.func_70652_k(var1);
      if (var2 && this.func_184614_ca().func_190926_b() && var1 instanceof EntityLivingBase) {
         float var3 = this.field_70170_p.func_175649_E(new BlockPos(this)).func_180168_b();
         ((EntityLivingBase)var1).func_195064_c(new PotionEffect(MobEffects.field_76438_s, 140 * (int)var3));
      }

      return var2;
   }

   protected boolean func_204703_dA() {
      return true;
   }

   protected void func_207302_dI() {
      this.func_207305_a(new EntityZombie(this.field_70170_p));
      this.field_70170_p.func_180498_a((EntityPlayer)null, 1041, new BlockPos((int)this.field_70165_t, (int)this.field_70163_u, (int)this.field_70161_v), 0);
   }

   protected ItemStack func_190732_dj() {
      return ItemStack.field_190927_a;
   }
}
