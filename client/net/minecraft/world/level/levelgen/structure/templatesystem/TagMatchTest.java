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

   public TagMatchTest(TagKey<Block> var1) {
      super();
      this.tag = var1;
   }

   public boolean test(BlockState var1, RandomSource var2) {
      return var1.is(this.tag);
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.TAG_TEST;
   }

   static {
      CODEC = TagKey.codec(Registries.BLOCK).fieldOf("tag").xmap(TagMatchTest::new, (var0) -> {
         return var0.tag;
      });
   }
}
