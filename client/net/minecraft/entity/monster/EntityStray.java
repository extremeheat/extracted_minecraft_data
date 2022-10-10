package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityStray extends AbstractSkeleton {
   public EntityStray(World var1) {
      super(EntityType.field_200750_ap, var1);
   }

   public boolean func_205020_a(IWorld var1, boolean var2) {
      return super.func_205020_a(var1, var2) && (var2 || var1.func_175678_i(new BlockPos(this)));
   }

   @Nullable
   protected ResourceLocation func_184647_J() {
      return LootTableList.field_189968_an;
   }

   protected SoundEvent func_184639_G() {
      return SoundEvents.field_190032_gu;
   }

   protected SoundEvent func_184601_bQ(DamageSource var1) {
      return SoundEvents.field_190034_gw;
   }

   protected SoundEvent func_184615_bR() {
      return SoundEvents.field_190033_gv;
   }

   SoundEvent func_190727_o() {
      return SoundEvents.field_190035_gx;
   }

   protected EntityArrow func_190726_a(float var1) {
      EntityArrow var2 = super.func_190726_a(var1);
      if (var2 instanceof EntityTippedArrow) {
         ((EntityTippedArrow)var2).func_184558_a(new PotionEffect(MobEffects.field_76421_d, 600));
      }

      return var2;
   }
}
