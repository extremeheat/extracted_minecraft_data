package net.minecraft.world.entity.livingblock.behavior;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;

public class CombineIntoEndPortalBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 100;
   public static final int PORTAL_SIZE = 12;
   private final double minDistance;
   private int lastTriggeredTick;

   public CombineIntoEndPortalBehavior(final double minDistance) {
      super();
      this.minDistance = minDistance;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 100 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.minDistance), (target) -> {
         BlockState blockState = target.getBlockState();
         return blockState.is(Blocks.END_PORTAL_FRAME) && (Boolean)blockState.getValueOrElse(EndPortalFrameBlock.HAS_EYE, false);
      });
      this.lastTriggeredTick = tickCount;
      if (entities.size() >= 12) {
         BlockPos position = entity.blockPosition();
         this.spawnPortal(position, level);
         Set<ServerPlayer> players = new HashSet();

         for(int i = 0; i < 12; ++i) {
            LivingBlock frame = (LivingBlock)entities.get(i);
            ServerPlayer owner = frame.getAttributablePlayer();
            if (owner != null) {
               players.add(owner);
            }

            frame.discard();
         }

         ServerPlayer owner = entity.getAttributablePlayer();
         if (owner != null) {
            players.add(owner);
         }

         entity.discard();
         PlayerTrigger var10001 = CriteriaTriggers.END_PORTAL_CRAFTED;
         Objects.requireNonNull(var10001);
         players.forEach(var10001::trigger);
      }

      return false;
   }

   private void spawnPortal(final BlockPos position, final Level level) {
      BlockState eyeFrame = (BlockState)Blocks.END_PORTAL_FRAME.defaultBlockState().setValue(EndPortalFrameBlock.HAS_EYE, true);

      for(int x = 0; x < 5; ++x) {
         for(int z = 0; z < 5; ++z) {
            if ((x != 0 || z != 0) && (x != 0 || z != 4) && (x != 4 || z != 0) && (x != 4 || z != 4)) {
               BlockPos portalBlockPos = position.offset(x, 0, z);
               level.setBlock(portalBlockPos, eyeFrame, 2);
            }
         }
      }

      for(int x = 0; x < 3; ++x) {
         for(int z = 0; z < 3; ++z) {
            BlockPos portalBlockPos = position.offset(x + 1, 0, z + 1);
            level.destroyBlock(portalBlockPos, true, (Entity)null);
            level.setBlock(portalBlockPos, Blocks.END_PORTAL.defaultBlockState(), 2);
         }
      }

      level.globalLevelEvent(1038, position.offset(1, 0, 1), 0);
   }

   public static LivingBlockBehaviorType combineIntoEndPortal(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineIntoEndPortalBehavior(minDistance)));
   }
}
