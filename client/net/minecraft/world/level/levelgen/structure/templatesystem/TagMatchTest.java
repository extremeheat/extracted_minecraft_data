package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMatchTest extends RuleTest {
   public static final Codec<TagMatchTest> CODEC = TagKey.codec(Registry.BLOCK_REGISTRY).fieldOf("tag").xmap(TagMatchTest::new, var0 -> var0.tag).codec();
   private final TagKey<Block> tag;

   public TagMatchTest(TagKey<Block> var1) {
      super();
      this.tag = var1;
   }

   @Override
   public boolean test(BlockState var1, RandomSource var2) {
      return var1.is(this.tag);
   }

   @Override
   protected RuleTestType<?> getType() {
      return RuleTestType.TAG_TEST;
   }
}
