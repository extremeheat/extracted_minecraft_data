package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;

public record RuleBasedBlockStateProvider(BlockStateProvider fallback, List<Rule> rules) {
   public static final Codec<RuleBasedBlockStateProvider> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.fieldOf("fallback").forGetter(RuleBasedBlockStateProvider::fallback), RuleBasedBlockStateProvider.Rule.CODEC.listOf().fieldOf("rules").forGetter(RuleBasedBlockStateProvider::rules)).apply(i, RuleBasedBlockStateProvider::new));

   public RuleBasedBlockStateProvider {
      super();
   }

   public static RuleBasedBlockStateProvider simple(final BlockStateProvider provider) {
      return new RuleBasedBlockStateProvider(provider, List.of());
   }

   public static RuleBasedBlockStateProvider simple(final Block block) {
      return simple((BlockStateProvider)BlockStateProvider.simple(block));
   }

   public BlockState getState(final WorldGenLevel level, final RandomSource random, final BlockPos pos) {
      for(Rule rule : this.rules) {
         if (rule.ifTrue().test(level, pos)) {
            return rule.then().getState(random, pos);
         }
      }

      return this.fallback.getState(random, pos);
   }

   public static record Rule(BlockPredicate ifTrue, BlockStateProvider then) {
      public static final Codec<Rule> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPredicate.CODEC.fieldOf("if_true").forGetter(Rule::ifTrue), BlockStateProvider.CODEC.fieldOf("then").forGetter(Rule::then)).apply(i, Rule::new));

      public Rule {
         super();
      }
   }
}
