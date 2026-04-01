package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;

public class PrimesTntBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 10;
   private final double minDistance;
   private int lastTriggeredTick;

   public PrimesTntBehavior(final double minDistance) {
      super();
      this.minDistance = minDistance;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 10 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      List<LivingBlock> nearbyTnts = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.minDistance), (target) -> target.getBlockState().is(Blocks.TNT));
      this.lastTriggeredTick = tickCount;

      for(LivingBlock block : nearbyTnts) {
         PrimedTnt tnt = new PrimedTnt(level, block.getX(), block.getY(), block.getZ(), (LivingEntity)null);
         level.addFreshEntity(tnt);
         level.playSound((Entity)null, block.getX(), block.getY(), block.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
         level.gameEvent(tnt, GameEvent.PRIME_FUSE, BlockPos.containing(tnt.position()));
         block.discard();
      }

      return false;
   }

   public static LivingBlockBehaviorType primesTnt(final double minDistance) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new PrimesTntBehavior(minDistance)));
   }
}
