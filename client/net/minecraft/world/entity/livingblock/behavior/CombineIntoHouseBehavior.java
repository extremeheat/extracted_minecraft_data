package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class CombineIntoHouseBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 100;
   public static final int HOUSE_SIZE = 23;
   private final double minDistance;
   private int lastTriggeredTick;

   public CombineIntoHouseBehavior(final double minDistance) {
      super();
      this.minDistance = minDistance;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 100 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (entity.getBlockState().getBlock() instanceof DoorBlock) {
         List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.minDistance), (target) -> target.getBlockState().is(BlockTags.BUILDS_INTO_HOUSE));
         this.lastTriggeredTick = tickCount;
         if (entities.size() >= 23) {
            WorldBorder worldBorder = level.getWorldBorder();
            BlockPos position = entity.blockPosition();
            AABB houseBounds = new AABB(new Vec3((double)(position.getX() - 10), (double)(position.getY() - 10), (double)(position.getZ() - 10)), new Vec3((double)(position.getX() + 10), (double)(position.getY() + 10), (double)(position.getZ() + 10)));
            if (!worldBorder.isWithinBounds(houseBounds)) {
               return false;
            }

            level.getHouseForcer().createHouse(position, entity, entities);
         }
      }

      return false;
   }

   public static LivingBlockBehaviorType combineIntoHouse(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoHouseBehavior(minDistance)));
   }
}
