package net.minecraft.world.entity.livingblock.behavior;

import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.livingblock.LivingBlock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public class RandomlyUseItemBehavior implements LivingBlockBehavior {
   final float chance;
   final float reach;
   final ValidBlockTest validBlocks;

   public static LivingBlockBehaviorType randomlyUseOn(final float chance, final float reach, final ValidBlockTest predicate) {
      return LivingBlockBehaviorType.behaviorType((Supplier)(() -> new RandomlyUseItemBehavior(chance, reach, predicate)));
   }

   public RandomlyUseItemBehavior(final float chance, final float reach, final ValidBlockTest validBlocks) {
      super();
      this.chance = chance;
      this.reach = reach;
      this.validBlocks = validBlocks;
   }

   public boolean canStartUsing(final LivingBlock entity) {
      return entity.getRandom().nextFloat() < this.chance;
   }

   public boolean tick(final LivingBlock entity, final ServerLevel level, final int tickCount) {
      Player player = entity.getAttributablePlayer();
      if (player == null) {
         return false;
      } else {
         AABB reach = entity.getBoundingBox().inflate((double)this.reach);
         Optional<BlockPos> first = BlockPos.betweenClosedStream(reach).filter((posx) -> this.validBlocks.test(level, posx, level.getBlockState(posx))).findFirst();
         if (first.isPresent()) {
            BlockPos pos = (BlockPos)first.get();
            ItemStack itemStack = entity.getItemStack();
            Vec3 directionToBlock = Vec3.atCenterOf(pos).subtract(entity.position());
            Direction hitDirection = Direction.getApproximateNearest(directionToBlock);
            Vec3 surfaceHit = Vec3.atCenterOf(pos).add(-0.5 * (double)hitDirection.getStepX(), -0.5 * (double)hitDirection.getStepY(), -0.5 * (double)hitDirection.getStepZ());
            BlockHitResult hitResult = new BlockHitResult(surfaceHit, hitDirection, pos, false);
            UseOnContext useOnContext = new UseOnContext(level, player, InteractionHand.MAIN_HAND, itemStack, hitResult);
            InteractionResult interactionResult = itemStack.useOn(useOnContext);
            if (interactionResult == InteractionResult.SUCCESS) {
               entity.hurtServer(level, entity.damageSources().generic(), 1.0F);
            }
         }

         return false;
      }
   }

   public interface ValidBlockTest {
      boolean test(Level level, BlockPos pos, BlockState state);

      static ValidBlockTest tag(final TagKey<Block> tag) {
         return (level, pos, state) -> state.is(tag);
      }
   }
}
