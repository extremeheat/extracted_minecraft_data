package net.minecraft.world.entity.livingblock.behavior;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.BlockUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class CombineIntoPortalBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 100;
   public static final int PORTAL_SIZE = 12;
   private final double minDistance;
   private int lastTriggeredTick;

   public CombineIntoPortalBehavior(final double minDistance) {
      super();
      this.minDistance = minDistance;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 100 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.minDistance), (target) -> target.getBlockState().is(Blocks.OBSIDIAN));
      this.lastTriggeredTick = tickCount;
      if (entities.size() >= 12) {
         BlockPos position = entity.blockPosition();
         Vec3 forward = entity.getForward().horizontal();
         Direction direction = Direction.getApproximateNearest(forward);
         Optional<BlockUtil.FoundRectangle> portal = level.getPortalForcer().createPortal(position, direction.getAxis());
         if (portal.isEmpty()) {
            return false;
         }

         Set<ServerPlayer> players = new HashSet();

         for(int i = 0; i < 12; ++i) {
            LivingBlock obsidian = (LivingBlock)entities.get(i);
            ServerPlayer owner = obsidian.getAttributablePlayer();
            if (owner != null) {
               players.add(owner);
            }

            obsidian.discard();
         }

         ServerPlayer owner = entity.getAttributablePlayer();
         if (owner != null) {
            players.add(owner);
         }

         entity.discard();
         PlayerTrigger var10001 = CriteriaTriggers.NETHER_PORTAL_CRAFTED;
         Objects.requireNonNull(var10001);
         players.forEach(var10001::trigger);
      }

      return false;
   }

   public static LivingBlockBehaviorType combineIntoPortal(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoPortalBehavior(minDistance)));
   }
}
