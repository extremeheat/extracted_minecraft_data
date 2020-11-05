package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockMatchTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;

public class OreConfiguration implements FeatureConfiguration {
   public static final Codec<OreConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RuleTest.CODEC.fieldOf("target").forGetter((var0x) -> {
         return var0x.target;
      }), BlockState.CODEC.fieldOf("state").forGetter((var0x) -> {
         return var0x.state;
      }), Codec.intRange(0, 64).fieldOf("size").forGetter((var0x) -> {
         return var0x.size;
      })).apply(var0, OreConfiguration::new);
   });
   public final RuleTest target;
   public final int size;
   public final BlockState state;

   public OreConfiguration(RuleTest var1, BlockState var2, int var3) {
      super();
      this.size = var3;
      this.state = var2;
      this.target = var1;
   }

   public static final class Predicates {
      public static final RuleTest NATURAL_STONE;
      public static final RuleTest NETHERRACK;
      public static final RuleTest NETHER_ORE_REPLACEABLES;

      static {
         NATURAL_STONE = new TagMatchTest(BlockTags.BASE_STONE_OVERWORLD);
         NETHERRACK = new BlockMatchTest(Blocks.NETHERRACK);
         NETHER_ORE_REPLACEABLES = new TagMatchTest(BlockTags.BASE_STONE_NETHER);
      }
   }
}
