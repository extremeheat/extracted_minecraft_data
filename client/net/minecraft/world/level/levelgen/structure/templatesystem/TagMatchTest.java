package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public record TagMatchTest(TagKey<Block> tag) implements RuleTest {
   public static final MapCodec<TagMatchTest> CODEC;

   public TagMatchTest {
      super();
   }

   public boolean test(final BlockState blockState, final BlockPos pos, final RandomSource random) {
      return blockState.is(this.tag);
   }

   public MapCodec<TagMatchTest> codec() {
      return CODEC;
   }

   static {
      CODEC = TagKey.codec(Registries.BLOCK).fieldOf("tag").xmap(TagMatchTest::new, (t) -> t.tag);
   }
}
