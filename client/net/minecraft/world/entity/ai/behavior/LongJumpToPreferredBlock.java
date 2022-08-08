package net.minecraft.world.entity.ai.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class LongJumpToPreferredBlock<E extends Mob> extends LongJumpToRandomPos<E> {
   private final TagKey<Block> preferredBlockTag;
   private final float preferredBlocksChance;
   private final List<LongJumpToRandomPos.PossibleJump> notPrefferedJumpCandidates = new ArrayList();
   private boolean currentlyWantingPreferredOnes;

   public LongJumpToPreferredBlock(UniformInt var1, int var2, int var3, float var4, Function<E, SoundEvent> var5, TagKey<Block> var6, float var7, Predicate<BlockState> var8) {
      super(var1, var2, var3, var4, var5, var8);
      this.preferredBlockTag = var6;
      this.preferredBlocksChance = var7;
   }

   protected void start(ServerLevel var1, E var2, long var3) {
      super.start(var1, var2, var3);
      this.notPrefferedJumpCandidates.clear();
      this.currentlyWantingPreferredOnes = var2.getRandom().nextFloat() < this.preferredBlocksChance;
   }

   protected Optional<LongJumpToRandomPos.PossibleJump> getJumpCandidate(ServerLevel var1) {
      if (!this.currentlyWantingPreferredOnes) {
         return super.getJumpCandidate(var1);
      } else {
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();

         while(!this.jumpCandidates.isEmpty()) {
            Optional var3 = super.getJumpCandidate(var1);
            if (var3.isPresent()) {
               LongJumpToRandomPos.PossibleJump var4 = (LongJumpToRandomPos.PossibleJump)var3.get();
               if (var1.getBlockState(var2.setWithOffset(var4.getJumpTarget(), (Direction)Direction.DOWN)).is(this.preferredBlockTag)) {
                  return var3;
               }

               this.notPrefferedJumpCandidates.add(var4);
            }
         }

         if (!this.notPrefferedJumpCandidates.isEmpty()) {
            return Optional.of((LongJumpToRandomPos.PossibleJump)this.notPrefferedJumpCandidates.remove(0));
         } else {
            return Optional.empty();
         }
      }
   }

   protected boolean isAcceptableLandingPosition(ServerLevel var1, E var2, BlockPos var3) {
      return super.isAcceptableLandingPosition(var1, var2, var3) && this.willNotLandInFluid(var1, var3);
   }

   private boolean willNotLandInFluid(ServerLevel var1, BlockPos var2) {
      return var1.getFluidState(var2).isEmpty() && var1.getFluidState(var2.below()).isEmpty();
   }
}
