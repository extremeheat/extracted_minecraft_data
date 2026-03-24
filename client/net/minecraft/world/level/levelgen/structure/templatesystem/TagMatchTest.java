package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMatchTest extends RuleTest {
   public static final MapCodec<TagMatchTest> CODEC;
   private final TagKey<Block> tag;

   public TagMatchTest(final TagKey<Block> tag) {
      super();
      this.tag = tag;
   }

   public boolean test(final BlockState blockState, final RandomSource random) {
      return blockState.is(this.tag);
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.TAG_TEST;
   }

   static {
      CODEC = TagKey.codec(Registries.BLOCK).fieldOf("tag").xmap(TagMatchTest::new, (t) -> t.tag);
   }
}
