package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;

public class RuleTestTypes {
   public RuleTestTypes() {
      super();
   }

   public static MapCodec<? extends RuleTest> bootstrap(final Registry<MapCodec<? extends RuleTest>> registry) {
      Registry.register(registry, (String)"all_of", AllOfRuleTest.CODEC);
      Registry.register(registry, (String)"always_true", AlwaysTrueTest.CODEC);
      Registry.register(registry, (String)"any_of", AnyOfRuleTest.CODEC);
      Registry.register(registry, (String)"block_match", BlockMatchTest.CODEC);
      Registry.register(registry, (String)"blockstate_match", BlockStateMatchTest.CODEC);
      Registry.register(registry, (String)"height_match", HeightMatchTest.CODEC);
      Registry.register(registry, (String)"not", NotRuleTest.CODEC);
      Registry.register(registry, (String)"random_block_match", RandomBlockMatchTest.CODEC);
      Registry.register(registry, (String)"random_blockstate_match", RandomBlockStateMatchTest.CODEC);
      return (MapCodec)Registry.register(registry, (String)"tag_match", TagMatchTest.CODEC);
   }
}
