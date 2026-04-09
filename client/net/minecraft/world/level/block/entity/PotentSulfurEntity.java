package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.Nullable;

public class PotentSulfurEntity extends BlockEntity {
   private static final int EFFECT_APPLICATION_FREQUENCY_TICKS = 10;
   private static final float EFFECT_DURATION_IN_SECONDS = 4.0F;
   private static final int EFFECT_DURATION_IN_TICKS = 80;
   public static final float EFFECT_RANGE = 3.0F;
   private static final Predicate<Entity> EFFECT_PREDICATE;
   public static final int PARTICLE_FREQUENCY_TICKS = 20;

   public PotentSulfurEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityType.POTENT_SULFUR, worldPosition, blockState);
   }

   public static void serverTick(final Level level, final BlockPos pos, final BlockState state, final PotentSulfurEntity potentSulfur) {
      if (level.getGameTime() % 10L == 0L && isUnderwater(level, pos)) {
         BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
         if (sourceBlock != null) {
            for(LivingEntity entity : getNearbyLivingEntities(level, sourceBlock)) {
               if (canBeReachedByNoxiousGas(level, sourceBlock, entity.getEyePosition())) {
                  applyNauseaEffect(entity);
               }
            }

         }
      }
   }

   private static void applyNauseaEffect(final LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 80, 0, true, true));
   }

   private static List<LivingEntity> getNearbyLivingEntities(final Level level, final BlockPos pos) {
      AABB aabb = (new AABB(pos)).inflate(2.5, 0.0, 2.5);
      return level.getEntitiesOfClass(LivingEntity.class, aabb, EFFECT_PREDICATE);
   }

   public static void clientTick(final Level level, final BlockPos pos, final BlockState state, final PotentSulfurEntity entity) {
      if (level.getGameTime() % 20L == 0L && isUnderwater(level, pos)) {
         BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
         if (sourceBlock != null) {
            spawnNoxiousGasCloudParticle(level, sourceBlock.getCenter());
         }

      }
   }

   private static void spawnNoxiousGasCloudParticle(final Level level, final Vec3 pos) {
      level.addParticle(ParticleTypes.NOXIOUS_GAS_CLOUD, pos.x, pos.y, pos.z, 0.0, 0.0, 0.0);
   }

   private static @Nullable BlockPos findNoxiousGasSourceBlock(final Level level, final BlockPos origin) {
      int maxY = origin.getY() + 4 + 1;
      BlockPos.MutableBlockPos pos = origin.above(2).mutable();

      while(pos.getY() <= maxY) {
         if (!level.getFluidState(pos).isSourceOfType(Fluids.WATER)) {
            if (level.getBlockState(pos).isAir()) {
               return pos.immutable();
            }
            break;
         }

         pos.move(Direction.UP);
      }

      return null;
   }

   public static boolean canBeReachedByNoxiousGas(final Level level, final BlockPos sourceBlock, final Vec3 pos) {
      if (!isAir(level, pos)) {
         return false;
      } else if (pos.distanceToSqr(sourceBlock.getCenter()) > 9.0) {
         return false;
      } else {
         Vec3 belowSource = sourceBlock.below().getCenter();
         Vec3 belowPos = pos.with(Direction.Axis.Y, pos.y - 1.0);
         return isWater(level, belowPos) && haveLineOfSight(level, belowSource, belowPos);
      }
   }

   private static boolean haveLineOfSight(final Level level, final Vec3 a, final Vec3 b) {
      HitResult hitResult = level.clip(new ClipContext(a, b, ClipContext.Block.VISUAL, ClipContext.Fluid.NONE, CollisionContext.empty()));
      return hitResult.getType() != HitResult.Type.BLOCK;
   }

   private static boolean isUnderwater(final Level level, final BlockPos pos) {
      return level.getFluidState(pos.above()).isSourceOfType(Fluids.WATER);
   }

   private static boolean isWater(final Level level, final Vec3 pos) {
      return level.getFluidState(BlockPos.containing(pos)).isSourceOfType(Fluids.WATER);
   }

   private static boolean isAir(final Level level, final Vec3 pos) {
      return level.getBlockState(BlockPos.containing(pos)).isAir();
   }

   static {
      EFFECT_PREDICATE = EntitySelector.NO_SPECTATORS.and(EntitySelector.ENTITY_STILL_ALIVE);
   }
}
