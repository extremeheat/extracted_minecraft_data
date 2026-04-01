package net.minecraft.world.entity.livingblock.behavior;

import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.phys.AABB;

public record DamageEntitiesOnTouchBehavior(ResourceKey<DamageType> damageType, float damageAmount, boolean ignoreLivingBlocks) implements LivingBlockBehavior {
   public DamageEntitiesOnTouchBehavior {
      super();
   }

   public static LivingBlockBehaviorType damageEntitiesOnTouch(final ResourceKey<DamageType> damageType, final float damageAmount, final boolean ignoreLivingBlocks) {
      return LivingBlockBehaviorType.behaviorType((Function)((var3) -> new DamageEntitiesOnTouchBehavior(damageType, damageAmount, ignoreLivingBlocks)));
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return true;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      AABB bb = entity.getBoundingBox().inflate(0.1);
      DamageSource damageSource = new DamageSource(level.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(this.damageType), entity);

      for(Entity damageReceiver : level.getEntitiesOfClass(Entity.class, bb, (filterEntity) -> filterEntity.getClass() != LivingBlock.class && !filterEntity.isSpectator())) {
         damageReceiver.hurtServer(level, damageSource, this.damageAmount);
      }

      if (!this.ignoreLivingBlocks) {
         Predicate<LivingBlock> livingBlockPredicate = (filterEntity) -> !filterEntity.getBlockState().is(entity.getBlockState().getBlock()) && filterEntity != entity;

         for(Entity damageReceiver : level.getEntitiesOfClass(LivingBlock.class, bb, livingBlockPredicate)) {
            damageReceiver.hurtServer(level, damageSource, this.damageAmount);
         }
      }

      return true;
   }
}
