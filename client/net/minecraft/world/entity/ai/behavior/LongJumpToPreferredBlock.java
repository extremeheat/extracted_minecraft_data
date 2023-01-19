package net.minecraft.world.entity.ai.behavior;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;

public class LongJumpToPreferredBlock<E extends Mob> extends LongJumpToRandomPos<E> {
   private final TagKey<Block> preferredBlockTag;
   private final float preferredBlocksChance;
   private final List<LongJumpToRandomPos.PossibleJump> notPrefferedJumpCandidates = new ArrayList<>();
   private boolean currentlyWantingPreferredOnes;

   public LongJumpToPreferredBlock(
      UniformInt var1, int var2, int var3, float var4, Function<E, SoundEvent> var5, TagKey<Block> var6, float var7, BiPredicate<E, BlockPos> var8
   ) {
      super(var1, var2, var3, var4, var5, var8);
      this.preferredBlockTag = var6;
      this.preferredBlocksChance = var7;
   }

   @Override
   protected void start(ServerLevel var1, E var2, long var3) {
      super.start(var1, (E)var2, var3);
      this.notPrefferedJumpCandidates.clear();
      this.currentlyWantingPreferredOnes = var2.getRandom().nextFloat() < this.preferredBlocksChance;
   }

   @Override
   protected Optional<LongJumpToRandomPos.PossibleJump> getJumpCandidate(ServerLevel var1) {
      if (!this.currentlyWantingPreferredOnes) {
         return super.getJumpCandidate(var1);
      } else {
         BlockPos.MutableBlockPos var2 = new BlockPos.MutableBlockPos();

         while(!this.jumpCandidates.isEmpty()) {
            Optional var3 = super.getJumpCandidate(var1);
            if (var3.isPresent()) {
               LongJumpToRandomPos.PossibleJump var4 = (LongJumpToRandomPos.PossibleJump)var3.get();
               if (var1.getBlockState(var2.setWithOffset(var4.getJumpTarget(), Direction.DOWN)).is(this.preferredBlockTag)) {
                  return var3;
               }

               this.notPrefferedJumpCandidates.add(var4);
            }
         }

         return !this.notPrefferedJumpCandidates.isEmpty() ? Optional.of(this.notPrefferedJumpCandidates.remove(0)) : Optional.empty();
      }
   }
}
