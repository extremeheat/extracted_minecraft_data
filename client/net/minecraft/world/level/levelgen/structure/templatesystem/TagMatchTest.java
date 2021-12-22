package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.core.Registry;
import net.minecraft.tags.SerializationTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMatchTest extends RuleTest {
   public static final Codec<TagMatchTest> CODEC = Tag.codec(() -> {
      return SerializationTags.getInstance().getOrEmpty(Registry.BLOCK_REGISTRY);
   }).fieldOf("tag").xmap(TagMatchTest::new, (var0) -> {
      return var0.tag;
   }).codec();
   private final Tag<Block> tag;

   public TagMatchTest(Tag<Block> var1) {
      super();
      this.tag = var1;
   }

   public boolean test(BlockState var1, Random var2) {
      return var1.is(this.tag);
   }

   protected RuleTestType<?> getType() {
      return RuleTestType.TAG_TEST;
   }
}
