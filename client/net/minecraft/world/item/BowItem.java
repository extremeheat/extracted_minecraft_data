package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;

public class BowItem extends ProjectileWeaponItem {
   public static final int MAX_DRAW_DURATION = 20;
   public static final int DEFAULT_RANGE = 15;

   public BowItem(Item.Properties var1) {
      super(var1);
   }

   public boolean releaseUsing(ItemStack var1, Level var2, LivingEntity var3, int var4) {
      if (!(var3 instanceof Player var5)) {
         return false;
      } else {
         ItemStack var6 = var5.getProjectile(var1);
         if (var6.isEmpty()) {
            return false;
         } else {
            int var7 = this.getUseDuration(var1, var3) - var4;
            float var8 = getPowerForTime(var7);
            if ((double)var8 < 0.1) {
               return false;
            } else {
               List var9 = draw(var1, var6, var5);
               if (var2 instanceof ServerLevel) {
                  ServerLevel var10 = (ServerLevel)var2;
                  if (!var9.isEmpty()) {
                     this.shoot(var10, var5, var5.getUsedItemHand(), var1, var9, var8 * 3.0F, 1.0F, var8 == 1.0F, (LivingEntity)null);
                  }
               }

               var2.playSound((Player)null, var5.getX(), var5.getY(), var5.getZ(), (SoundEvent)SoundEvents.ARROW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F / (var2.getRandom().nextFloat() * 0.4F + 1.2F) + var8 * 0.5F);
               var5.awardStat(Stats.ITEM_USED.get(this));
               return true;
            }
         }
      }
   }

   protected void shootProjectile(LivingEntity var1, Projectile var2, int var3, float var4, float var5, float var6, @Nullable LivingEntity var7) {
      var2.shootFromRotation(var1, var1.getXRot(), var1.getYRot() + var6, 0.0F, var4, var5);
   }

   public static float getPowerForTime(int var0) {
      float var1 = (float)var0 / 20.0F;
      var1 = (var1 * var1 + var1 * 2.0F) / 3.0F;
      if (var1 > 1.0F) {
         var1 = 1.0F;
      }

      return var1;
   }

   public int getUseDuration(ItemStack var1, LivingEntity var2) {
      return 72000;
   }

   public ItemUseAnimation getUseAnimation(ItemStack var1) {
      return ItemUseAnimation.BOW;
   }

   public InteractionResult use(Level var1, Player var2, InteractionHand var3) {
      ItemStack var4 = var2.getItemInHand(var3);
      boolean var5 = !var2.getProjectile(var4).isEmpty();
      if (!var2.hasInfiniteMaterials() && !var5) {
         return InteractionResult.FAIL;
      } else {
         var2.startUsingItem(var3);
         return InteractionResult.CONSUME;
      }
   }

   public Predicate<ItemStack> getAllSupportedProjectiles() {
      return ARROW_ONLY;
   }

   public int getDefaultProjectileRange() {
      return 15;
   }
}
