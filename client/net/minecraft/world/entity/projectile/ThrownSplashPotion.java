package net.minecraft.world.entity.projectile;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

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

   public void onHitAsPotion(ServerLevel var1, ItemStack var2, @Nullable Entity var3) {
      PotionContents var4 = (PotionContents)var2.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
      float var5 = (Float)var2.getOrDefault(DataComponents.POTION_DURATION_SCALE, 1.0F);
      Iterable var6 = var4.getAllEffects();
      AABB var7 = this.getBoundingBox().inflate(4.0, 2.0, 4.0);
      List var8 = this.level().getEntitiesOfClass(LivingEntity.class, var7);
      if (!var8.isEmpty()) {
         Entity var9 = this.getEffectSource();

         for(LivingEntity var11 : var8) {
            if (var11.isAffectedByPotions()) {
               double var12 = this.distanceToSqr(var11);
               if (var12 < 16.0) {
                  double var14;
                  if (var11 == var3) {
                     var14 = 1.0;
                  } else {
                     var14 = 1.0 - Math.sqrt(var12) / 4.0;
                  }

                  for(MobEffectInstance var17 : var6) {
                     Holder var18 = var17.getEffect();
                     if (((MobEffect)var18.value()).isInstantenous()) {
                        ((MobEffect)var18.value()).applyInstantenousEffect(var1, this, this.getOwner(), var11, var17.getAmplifier(), var14);
                     } else {
                        int var19 = var17.mapDuration((var3x) -> (int)(var14 * (double)var3x * (double)var5 + 0.5));
                        MobEffectInstance var20 = new MobEffectInstance(var18, var19, var17.getAmplifier(), var17.isAmbient(), var17.isVisible());
                        if (!var20.endsWithin(20)) {
                           var11.addEffect(var20, var9);
                        }
                     }
                  }
               }
            }
         }
      }

   }
}
