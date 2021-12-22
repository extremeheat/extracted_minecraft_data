package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

class MatchingBlocksPredicate extends StateTestingPredicate {
   private final List<Block> blocks;
   public static final Codec<MatchingBlocksPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return stateTestingCodec(var0).and(Registry.BLOCK.byNameCodec().listOf().fieldOf("blocks").forGetter((var0x) -> {
         return var0x.blocks;
      })).apply(var0, MatchingBlocksPredicate::new);
   });

   public MatchingBlocksPredicate(Vec3i var1, List<Block> var2) {
      super(var1);
      this.blocks = var2;
   }

   protected boolean test(BlockState var1) {
      return this.blocks.contains(var1.getBlock());
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCKS;
   }
}
