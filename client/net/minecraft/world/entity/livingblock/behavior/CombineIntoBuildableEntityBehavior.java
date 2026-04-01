package net.minecraft.world.entity.livingblock.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class CombineIntoBuildableEntityBehavior<E extends Entity> implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 100;
   private final BlockMatcher bodyBlockMatcher;
   private final BlockMatcher headBlockMatcher;
   private final double minDistance;
   private final EntityType<E> entityType;
   private int lastTriggeredTick;

   protected CombineIntoBuildableEntityBehavior(final BlockMatcher bodyBlockMatcher, final BlockMatcher headBlockMatcher, final double minDistance, final EntityType<E> entityType) {
      super();
      this.bodyBlockMatcher = bodyBlockMatcher;
      this.headBlockMatcher = headBlockMatcher;
      this.minDistance = minDistance;
      this.entityType = entityType;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 100 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      this.lastTriggeredTick = tickCount;
      List<LivingBlock> nearbyEntities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.minDistance), (target) -> this.bodyBlockMatcher.matches(target) || this.headBlockMatcher.matches(target));
      List<LivingBlock> bodyBlockEntities = new ArrayList();
      List<LivingBlock> headBlockEntities = new ArrayList();

      for(LivingBlock livingBlock : nearbyEntities) {
         if (this.bodyBlockMatcher.matches(livingBlock)) {
            bodyBlockEntities.add(livingBlock);
         } else if (this.headBlockMatcher.matches(livingBlock)) {
            headBlockEntities.add(livingBlock);
         }
      }

      if (!headBlockEntities.contains(entity)) {
         headBlockEntities.add(entity);
      }

      int requiredBodyBlocks = this.bodyBlockMatcher.requiredAmount;
      int requiredHeadBlocks = this.headBlockMatcher.requiredAmount;
      if (bodyBlockEntities.stream().filter(Entity::isAlive).count() >= (long)requiredBodyBlocks && headBlockEntities.stream().filter(Entity::isAlive).count() >= (long)requiredHeadBlocks) {
         BlockPos spawnPos = entity.blockPosition();
         E builtEntity = this.entityType.create(level, EntitySpawnReason.TRIGGERED);
         if (builtEntity == null) {
            return false;
         } else {
            builtEntity.setPos((double)spawnPos.getX() + 0.5, (double)spawnPos.getY() + 0.5, (double)spawnPos.getZ() + 0.5);
            level.addFreshEntity(builtEntity);
            this.onCreate(entity, builtEntity, level, bodyBlockEntities, headBlockEntities);

            for(int i = 0; i < requiredBodyBlocks; ++i) {
               ((LivingBlock)bodyBlockEntities.get(i)).discard();
            }

            for(int i = 0; i < requiredHeadBlocks; ++i) {
               ((LivingBlock)headBlockEntities.get(i)).discard();
            }

            if (!entity.isRemoved()) {
               entity.discard();
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public void onCreate(final LivingBlock entity, final E builtEntity, final ServerLevel level, final List<LivingBlock> bodyBlockEntities, final List<LivingBlock> headBlockEntities) {
   }

   public static LivingBlockBehaviorType combineIntoWither(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoBuildableEntityBehavior(new BlockMatcher((state) -> state.is(Blocks.SOUL_SAND) || state.is(Blocks.SOUL_SOIL), 4), new BlockMatcher((state) -> state.is(Blocks.WITHER_SKELETON_SKULL), 3), minDistance, EntityType.WITHER)));
   }

   public static LivingBlockBehaviorType combineIntoIronGolem(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoBuildableEntityBehavior(new BlockMatcher((state) -> state.is(Blocks.IRON_BLOCK), 4), new BlockMatcher((state) -> state.is(Blocks.CARVED_PUMPKIN), 1), minDistance, EntityType.IRON_GOLEM)));
   }

   public static LivingBlockBehaviorType combineIntoSnowGolem(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoBuildableEntityBehavior(new BlockMatcher((state) -> state.is(Blocks.SNOW_BLOCK), 2), new BlockMatcher((state) -> state.is(Blocks.CARVED_PUMPKIN), 1), minDistance, EntityType.SNOW_GOLEM)));
   }

   public static record BlockMatcher(Predicate<BlockState> matcher, int requiredAmount) {
      public BlockMatcher {
         super();
      }

      public boolean matches(final LivingBlock livingBlock) {
         return this.matcher.test(livingBlock.getBlockState());
      }
   }
}
