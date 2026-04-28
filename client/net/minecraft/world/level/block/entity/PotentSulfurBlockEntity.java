package net.minecraft.world.level.block.entity;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.GeyserParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.PotentSulfurBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PotentSulfurState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jspecify.annotations.Nullable;

public class PotentSulfurBlockEntity extends BlockEntity {
   private static final int EFFECT_APPLICATION_FREQUENCY_TICKS = 10;
   private static final float EFFECT_DURATION_IN_SECONDS = 4.0F;
   private static final int EFFECT_DURATION_IN_TICKS = 80;
   public static final float EFFECT_RANGE = 3.0F;
   private static final Predicate<Entity> EFFECT_PREDICATE;
   public static final int PARTICLE_FREQUENCY_TICKS = 20;
   private static final float GEYSER_BASE_LAUNCH_SPEED = 0.3F;
   private static final float GEYSER_LAUNCH_FORCE = 0.2F;
   public int waitingCountdown = -1;
   public int geyserEruptionTime = -1;
   public int dormantGeyserTime = -1;
   public long eruptionTick = -1L;
   public static BlockEntityTicker<PotentSulfurBlockEntity> SERVER_NAUSEA_EFFECT_TICKER;
   public static BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_NOXIOUS_GAS_TICKER;
   public static BlockEntityTicker<PotentSulfurBlockEntity> CLIENT_GEYSER_PLUME_TICKER;
   public static BlockEntityTicker<PotentSulfurBlockEntity> SERVER_WAITING_COUNTDOWN_TICKER;
   public static BlockEntityTicker<PotentSulfurBlockEntity> SERVER_LAUNCH_ENTITY_TICKER;

   public PotentSulfurBlockEntity(final BlockPos worldPosition, final BlockState blockState) {
      super(BlockEntityTypes.POTENT_SULFUR, worldPosition, blockState);
   }

   protected void saveAdditional(final ValueOutput output) {
      super.saveAdditional(output);
      output.putInt("dormant_time", this.dormantGeyserTime);
      output.putInt("eruption_time", this.geyserEruptionTime);
      output.putInt("countdown", this.waitingCountdown);
   }

   protected void loadAdditional(final ValueInput input) {
      super.loadAdditional(input);
      input.getInt("dormant_time").ifPresent((value) -> this.dormantGeyserTime = value);
      input.getInt("eruption_time").ifPresent((value) -> this.geyserEruptionTime = value);
      input.getInt("countdown").ifPresent((value) -> this.waitingCountdown = value);
   }

   public void setLevel(final Level level) {
      super.setLevel(level);
      if (this.geyserEruptionTime == -1) {
         this.geyserEruptionTime = level.getRandom().nextIntBetweenInclusive(1, 2);
      }

      if (this.dormantGeyserTime == -1) {
         this.dormantGeyserTime = level.getRandom().nextIntBetweenInclusive(15, 30);
      }

      if (this.eruptionTick == -1L) {
         this.eruptionTick = level.getGameTime();
      }

   }

   public void resetCountdown() {
      this.waitingCountdown = -1;
   }

   private static void applyNauseaEffect(final LivingEntity entity) {
      entity.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 80, 0, true, true));
   }

   private static List<LivingEntity> getNearbyLivingEntities(final Level level, final BlockPos pos) {
      AABB aabb = (new AABB(pos)).inflate(2.5, 0.0, 2.5);
      return level.getEntitiesOfClass(LivingEntity.class, aabb, EFFECT_PREDICATE);
   }

   private static void spawnGeyserParticle(final Level level, final Vec3 sulfurPos, final Vec3 sourcePos) {
      int waterBlocks = (int)Math.floor(sourcePos.y - sulfurPos.y);
      level.addParticle(new GeyserParticleOptions(ParticleTypes.GEYSER, waterBlocks), sourcePos.x, sourcePos.y, sourcePos.z, 0.0, 0.0, 0.0);
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
      SERVER_NAUSEA_EFFECT_TICKER = (level, pos, state, potentSulfur) -> {
         if (level.getGameTime() % 10L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
               for(LivingEntity entity : getNearbyLivingEntities(level, sourceBlock)) {
                  if (canBeReachedByNoxiousGas(level, sourceBlock, entity.getEyePosition())) {
                     applyNauseaEffect(entity);
                  }
               }

            }
         }
      };
      CLIENT_NOXIOUS_GAS_TICKER = (level, pos, state, entity) -> {
         if (level.getGameTime() % 20L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
               spawnNoxiousGasCloudParticle(level, sourceBlock.getCenter());
            }

         }
      };
      CLIENT_GEYSER_PLUME_TICKER = (level, pos, state, entity) -> {
         BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
         if (sourceBlock != null) {
            if ((level.getGameTime() - entity.eruptionTick) % 20L == 0L) {
               spawnGeyserParticle(level, pos.getCenter(), sourceBlock.getBottomCenter());
            }

         }
      };
      SERVER_WAITING_COUNTDOWN_TICKER = (level, pos, state, entity) -> {
         if (level.getGameTime() % 20L == 0L) {
            BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
            if (sourceBlock != null) {
               if (entity.waitingCountdown <= 0) {
                  int waterBlocks = (int)Math.floor(sourceBlock.getBottomCenter().y - pos.getCenter().y);
                  if (state.getValue(PotentSulfurBlock.STATE) == PotentSulfurState.DORMANT) {
                     entity.waitingCountdown = 10 * (waterBlocks - 1) + entity.dormantGeyserTime;
                  } else {
                     entity.waitingCountdown = waterBlocks - 1 + entity.geyserEruptionTime;
                  }
               }

               if (entity.waitingCountdown > 0) {
                  --entity.waitingCountdown;
               }

               if (entity.waitingCountdown == 0) {
                  level.setBlock(pos, (BlockState)state.setValue(PotentSulfurBlock.STATE, state.getValue(PotentSulfurBlock.STATE) == PotentSulfurState.DORMANT ? PotentSulfurState.ERUPTING : PotentSulfurState.DORMANT), 3);
               }

            }
         }
      };
      SERVER_LAUNCH_ENTITY_TICKER = (level, pos, state, entity) -> {
         BlockPos sourceBlock = findNoxiousGasSourceBlock(level, pos);
         if (sourceBlock != null) {
            int waterBlocks = (int)Math.floor(sourceBlock.getBottomCenter().y - pos.getCenter().y);
            AABB aabb = (new AABB(pos)).inflate(0.0, (double)(waterBlocks * 5), 0.0).move(0.0, (double)waterBlocks, 0.0);

            for(Entity entityToBeLaunched : level.getEntitiesOfClass(Entity.class, aabb, EFFECT_PREDICATE)) {
               Vec3 entityVelocity = entityToBeLaunched.getDeltaMovement();
               if (entityVelocity.y < 0.30000001192092896 + (double)waterBlocks * 0.1 && haveLineOfSight(level, sourceBlock.below().getCenter(), entityToBeLaunched.getEyePosition())) {
                  entityToBeLaunched.addDeltaMovement(new Vec3(0.0, 0.20000000298023224, 0.0));
                  entityToBeLaunched.hurtMarked = true;
                  entityToBeLaunched.needsSync = true;
               }
            }

            if (level.getGameTime() % 20L == 0L) {
               level.playSound((Entity)null, (BlockPos)pos, SoundEvents.GEYSER_ERUPTION_ACTIVE, SoundSource.BLOCKS, 1.0F * (float)waterBlocks, 1.0F);
            }

         }
      };
   }
}
