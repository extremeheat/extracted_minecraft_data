package net.minecraft.world.entity.livingblock.behavior;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;

public class ConsumeNearbyLivingBlockBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 100;
   private final double maxDistance;
   private final boolean consumeAll;
   private final Predicate<? super LivingBlock> predicate;
   private final Predicate<? super LivingBlock> selector;
   private final Consumer<LivingBlock> output;
   private int lastTriggeredTick;

   public ConsumeNearbyLivingBlockBehavior(final double maxDistance, final boolean consumeAll, final Predicate<? super LivingBlock> predicate, final Predicate<? super LivingBlock> selector, final Consumer<LivingBlock> output) {
      super();
      this.maxDistance = maxDistance;
      this.consumeAll = consumeAll;
      this.predicate = predicate;
      this.selector = selector;
      this.output = output;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 100 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (!this.predicate.test(entity)) {
      }

      List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.maxDistance), this.selector);
      this.lastTriggeredTick = tickCount;
      if (!entities.isEmpty()) {
         for(LivingBlock livingBlock : entities) {
            livingBlock.discard();
            this.output.accept(entity);
            if (!this.consumeAll) {
               return false;
            }
         }
      }

      return false;
   }

   public static LivingBlockBehaviorType consumeLivingBlock(final double maxDistance, final boolean consumeAll, final Predicate<? super LivingBlock> predicate, final Predicate<? super LivingBlock> selector, final Consumer<LivingBlock> output) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new ConsumeNearbyLivingBlockBehavior(maxDistance, consumeAll, predicate, selector, output)));
   }
}
