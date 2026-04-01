package net.minecraft.world.entity.livingblock.behavior;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

public class CombineWithBehavior implements LivingBlockBehavior {
   private static final int REEVALUATION_TICKS = 100;
   private final double maxDistance;
   private final Predicate<? super LivingBlock> selector;
   private final UnaryOperator<BlockState> output;
   private final int amount;
   private final double chance;
   private final boolean spawnAtTarget;
   private final @Nullable PlayerTrigger advancementTrigger;
   private int lastTriggeredTick;

   public CombineWithBehavior(final double maxDistance, final Predicate<? super LivingBlock> selector, final UnaryOperator<BlockState> output) {
      this(maxDistance, selector, output, 1, 1.0);
   }

   public CombineWithBehavior(final double maxDistance, final Predicate<? super LivingBlock> selector, final UnaryOperator<BlockState> output, final int amount, final double chance) {
      this(maxDistance, selector, output, amount, chance, false, (PlayerTrigger)null);
   }

   public CombineWithBehavior(final double maxDistance, final Predicate<? super LivingBlock> selector, final UnaryOperator<BlockState> output, final int amount, final double chance, final boolean spawnAtTarget, final @Nullable PlayerTrigger advancementTrigger) {
      super();
      this.maxDistance = maxDistance;
      this.selector = selector;
      this.output = output;
      this.amount = amount;
      this.chance = chance;
      this.spawnAtTarget = spawnAtTarget;
      this.advancementTrigger = advancementTrigger;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return this.lastTriggeredTick + 100 < entity.tickCount;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      if (level.getRandom().nextDouble() < this.chance) {
         List<LivingBlock> entities = level.getEntities(EntityType.LIVING_BLOCK, entity.getBoundingBox().inflate(this.maxDistance), this.selector).stream().filter(Entity::isAlive).limit((long)this.amount).toList();
         this.lastTriggeredTick = tickCount;
         if (entities.size() == this.amount) {
            LivingBlock livingBlock = (LivingBlock)entities.get(0);
            BlockState blockState = (BlockState)this.output.apply(livingBlock.getBlockState());
            if (!blockState.isAir()) {
               LivingBlock newLivingBlock = LivingBlock.create(level, (BlockState)blockState);
               if (newLivingBlock != null) {
                  Set<ServerPlayer> players = new HashSet();
                  newLivingBlock.snapTo(this.spawnAtTarget ? livingBlock.position() : entity.position());
                  level.addFreshEntity(newLivingBlock);
                  entities.forEach((e) -> {
                     ServerPlayer owner = e.getAttributablePlayer();
                     if (owner != null) {
                        players.add(owner);
                     }

                     e.discard();
                  });
                  ServerPlayer owner = entity.getAttributablePlayer();
                  if (owner != null) {
                     players.add(owner);
                  }

                  entity.discard();
                  players.forEach((player) -> {
                     CriteriaTriggers.SUMMONED_ENTITY.trigger(player, newLivingBlock);
                     if (this.advancementTrigger != null) {
                        this.advancementTrigger.trigger(player);
                     }

                  });
               }
            }
         }
      }

      return false;
   }

   public static LivingBlockBehaviorType combineWith(final double maxDistance, final Predicate<? super LivingBlock> selector, final UnaryOperator<BlockState> output) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineWithBehavior(maxDistance, selector, output)));
   }

   public static LivingBlockBehaviorType combineWith(final double maxDistance, final Predicate<? super LivingBlock> selector, final UnaryOperator<BlockState> output, final int amount, final double chance, final PlayerTrigger advancementTrigger) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineWithBehavior(maxDistance, selector, output, amount, chance, false, advancementTrigger)));
   }

   public static LivingBlockBehaviorType combineWith(final double maxDistance, final Predicate<? super LivingBlock> selector, final UnaryOperator<BlockState> output, final boolean spawnAtTarget, final PlayerTrigger advancementTrigger) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new CombineWithBehavior(maxDistance, selector, output, 1, 1.0, spawnAtTarget, advancementTrigger)));
   }

   public static LivingBlockBehaviorType combineWithBlock(final double maxDistance, final TagKey<Block> tagKey, final UnaryOperator<BlockState> output) {
      return combineWith(maxDistance, (target) -> target.isBlock(tagKey), output);
   }

   public static LivingBlockBehaviorType combineWithEndPortalFrame(final double maxDistance) {
      return combineWith(maxDistance, (target) -> target.isBlock(Blocks.END_PORTAL_FRAME) && !(Boolean)target.getBlockState().getValueOrElse(EndPortalFrameBlock.HAS_EYE, false), (current) -> (BlockState)current.setValue(EndPortalFrameBlock.HAS_EYE, true), true, CriteriaTriggers.EYE_FRAME_COMBINATION);
   }
}
