package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.jspecify.annotations.Nullable;

public record RuleBasedBlockStateProvider(@Nullable BlockStateProvider fallback, List<Rule> rules) {
   public static final Codec<RuleBasedBlockStateProvider> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockStateProvider.CODEC.optionalFieldOf("fallback").forGetter((provider) -> Optional.ofNullable(provider.fallback())), RuleBasedBlockStateProvider.Rule.CODEC.listOf().fieldOf("rules").forGetter(RuleBasedBlockStateProvider::rules)).apply(i, RuleBasedBlockStateProvider::new));

   private RuleBasedBlockStateProvider(final Optional<BlockStateProvider> fallback, final List<Rule> rules) {
      this((BlockStateProvider)fallback.orElse((Object)null), rules);
   }

   public RuleBasedBlockStateProvider {
      super();
   }

   public static RuleBasedBlockStateProvider always(final BlockStateProvider provider) {
      return new RuleBasedBlockStateProvider(provider, List.of());
   }

   public static RuleBasedBlockStateProvider always(final Block block) {
      return always((BlockStateProvider)BlockStateProvider.simple(block));
   }

   public static RuleBasedBlockStateProvider ifTrueThenProvide(final BlockPredicate ifTrue, final Block thenProvide) {
      return ifTrueThenProvide(ifTrue, (BlockStateProvider)BlockStateProvider.simple(thenProvide));
   }

   public static RuleBasedBlockStateProvider ifTrueThenProvide(final BlockPredicate ifTrue, final BlockStateProvider thenProvide) {
      return new RuleBasedBlockStateProvider((BlockStateProvider)null, List.of(new Rule(ifTrue, thenProvide)));
   }

   public @Nullable BlockState getState(final WorldGenLevel level, final RandomSource random, final BlockPos pos) {
      for(Rule rule : this.rules) {
         if (rule.ifTrue().test(level, pos)) {
            return rule.then().getState(random, pos);
         }
      }

      return this.fallback == null ? null : this.fallback.getState(random, pos);
   }

   public static record Rule(BlockPredicate ifTrue, BlockStateProvider then) {
      public static final Codec<Rule> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPredicate.CODEC.fieldOf("if_true").forGetter(Rule::ifTrue), BlockStateProvider.CODEC.fieldOf("then").forGetter(Rule::then)).apply(i, Rule::new));

      public Rule {
         super();
      }
   }
}
