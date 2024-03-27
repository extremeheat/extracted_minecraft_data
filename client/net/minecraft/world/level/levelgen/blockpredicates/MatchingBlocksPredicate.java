package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

class MatchingBlocksPredicate extends StateTestingPredicate {
   private final HolderSet<Block> blocks;
   public static final MapCodec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> stateTestingCodec(var0)
            .and(RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("blocks").forGetter(var0x -> var0x.blocks))
            .apply(var0, MatchingBlocksPredicate::new)
   );

   public MatchingBlocksPredicate(Vec3i var1, HolderSet<Block> var2) {
      super(var1);
      this.blocks = var2;
   }

   @Override
   protected boolean test(BlockState var1) {
      return var1.is(this.blocks);
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCKS;
   }
}
