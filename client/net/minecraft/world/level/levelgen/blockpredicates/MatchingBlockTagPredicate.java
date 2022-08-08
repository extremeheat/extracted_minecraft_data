package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MatchingBlockTagPredicate extends StateTestingPredicate {
   final TagKey<Block> tag;
   public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return stateTestingCodec(var0).and(TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("tag").forGetter((var0x) -> {
         return var0x.tag;
      })).apply(var0, MatchingBlockTagPredicate::new);
   });

   protected MatchingBlockTagPredicate(Vec3i var1, TagKey<Block> var2) {
      super(var1);
      this.tag = var2;
   }

   protected boolean test(BlockState var1) {
      return var1.is(this.tag);
   }

   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCK_TAG;
   }
}
