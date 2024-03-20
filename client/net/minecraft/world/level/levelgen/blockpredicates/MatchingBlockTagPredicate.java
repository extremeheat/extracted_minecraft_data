package net.minecraft.world.level.levelgen.blockpredicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MatchingBlockTagPredicate extends StateTestingPredicate {
   final TagKey<Block> tag;
   public static final Codec<MatchingBlockTagPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> stateTestingCodec(var0)
            .and(TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(var0x -> var0x.tag))
            .apply(var0, MatchingBlockTagPredicate::new)
   );

   protected MatchingBlockTagPredicate(Vec3i var1, TagKey<Block> var2) {
      super(var1);
      this.tag = var2;
   }

   @Override
   protected boolean test(BlockState var1) {
      return var1.is(this.tag);
   }

   @Override
   public BlockPredicateType<?> type() {
      return BlockPredicateType.MATCHING_BLOCK_TAG;
   }
}