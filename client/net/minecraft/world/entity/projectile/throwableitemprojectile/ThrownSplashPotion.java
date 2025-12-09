package net.minecraft.world.entity.projectile.throwableitemprojectile;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class ThrownSplashPotion extends AbstractThrownPotion {
   public ThrownSplashPotion(EntityType<? extends ThrownSplashPotion> var1, Level var2) {
      super(var1, var2);
   }

   public ThrownSplashPotion(Level var1, LivingEntity var2, ItemStack var3) {
      super(EntityType.SPLASH_POTION, var1, var2, var3);
   }

   public ThrownSplashPotion(Level var1, double var2, double var4, double var6, ItemStack var8) {
      super(EntityType.SPLASH_POTION, var1, var2, var4, var6, var8);
   }

   protected Item getDefaultItem() {
      return Items.SPLASH_POTION;
   }

   public void onHitAsPotion(ServerLevel var1, ItemStack var2, HitResult var3) {
      PotionContents var4 = (PotionContents)var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      float var5 = (Float)var2.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
      Iterable var6 = var4.getAllEffects();
      AABB var7 = this.getBoundingBox().move(var3.getLocation().subtract(this.position()));
      AABB var8 = var7.inflate(4.0, 2.0, 4.0);
      List var9 = this.level().getEntitiesOfClass(LivingEntity.class, var8);
      float var10 = ProjectileUtil.computeMargin(this);
      if (!var9.isEmpty()) {
         Entity var11 = this.getEffectSource();

         for(LivingEntity var13 : var9) {
            if (var13.isAffectedByPotions()) {
               double var14 = var7.distanceToSqr(var13.getBoundingBox().inflate((double)var10));
               if (var14 < 16.0) {
                  double var16 = 1.0 - Math.sqrt(var14) / 4.0;

                  for(MobEffectInstance var19 : var6) {
                     Holder var20 = var19.getEffect();
                     if (((MobEffect)var20.value()).isInstantenous()) {
                        ((MobEffect)var20.value()).applyInstantenousEffect(var1, this, this.getOwner(), var13, var19.getAmplifier(), var16);
                     } else {
                        int var21 = var19.mapDuration((var3x) -> (int)(var16 * (double)var3x * (double)var5 + 0.5));
                        MobEffectInstance var22 = new MobEffectInstance(var20, var21, var19.getAmplifier(), var19.isAmbient(), var19.isVisible());
                        if (!var22.endsWithin(20)) {
                           var13.addEffect(var22, var11);
                        }
                     }
                  }
               }
            }
         }
      }

   }
}
