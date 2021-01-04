package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMatchTest extends RuleTest {
   private final Tag<Block> tag;

   public TagMatchTest(Tag<Block> var1) {
      super();
      this.tag = var1;
   }

   public <T> TagMatchTest(Dynamic<T> var1) {
      this(BlockTags.getAllTags().getTag(new ResourceLocation(var1.get("tag").asString(""))));
   }

   public boolean test(BlockState var1, Random var2) {
      return var1.is(this.tag);
   }

   protected RuleTestType getType() {
      return RuleTestType.TAG_TEST;
   }

   protected <T> Dynamic<T> getDynamic(DynamicOps<T> var1) {
      return new Dynamic(var1, var1.createMap(ImmutableMap.of(var1.createString("tag"), var1.createString(this.tag.getId().toString()))));
   }
}
