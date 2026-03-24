package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

class MatchingBlocksPredicate extends StateTestingPredicate {
   private final HolderSet<Block> blocks;
   public static final MapCodec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.mapCodec((i) -> stateTestingCodec(i).and(RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter((c) -> c.blocks)).apply(i, MatchingBlocksPredicate::new));

   public MatchingBlocksPredicate(final Vec3i offset, final HolderSet<Block> blocks) {
      super(offset);
      this.blocks = blocks;
   }

   protected boolean test(final BlockState state) {
      return state.is(this.blocks);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCKS;
   }
}
