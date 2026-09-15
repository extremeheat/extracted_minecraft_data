package net.minecraft.world.level.levelgen.feature.stateproviders;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import org.jspecify.annotations.Nullable;

public record RuleBasedStateProvider(@Nullable Holder<BlockStateProvider> fallback, List<Rule> rules) implements BlockStateProvider {
   public static final MapCodec<RuleBasedStateProvider> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(BlockStateProvider.CODEC.optionalFieldOf("fallback").forGetter((provider) -> Optional.ofNullable(provider.fallback)), RuleBasedStateProvider.Rule.CODEC.listOf().fieldOf("rules").forGetter((p) -> p.rules)).apply(i, RuleBasedStateProvider::new));

   private RuleBasedStateProvider(final Optional<Holder<BlockStateProvider>> fallback, final List<Rule> rules) {
      this((Holder)fallback.orElse((Object)null), rules);
   }

   public RuleBasedStateProvider {
      super();
   }

   public static RuleBasedStateProvider ifTrueThenProvide(final BlockPredicate ifTrue, final Block thenProvide) {
      return ifTrueThenProvide(ifTrue, (BlockStateProvider)BlockStateProvider.of(thenProvide));
   }

   public static RuleBasedStateProvider ifTrueThenProvide(final BlockPredicate ifTrue, final BlockStateProvider thenProvide) {
      return new RuleBasedStateProvider((Holder)null, List.of(new Rule(ifTrue, Holder.direct(thenProvide))));
   }

   public MapCodec<RuleBasedStateProvider> codec() {
      return CODEC;
   }

   public BlockState getState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      BlockState result = this.getOptionalState(level, random, pos);
      return result != null ? result : level.getBlockState(pos);
   }

   public @Nullable BlockState getOptionalState(final LevelAccessor level, final RandomSource random, final BlockPos pos) {
      for(Rule rule : this.rules) {
         if (rule.ifTrue().test(level, pos)) {
            BlockState optionalState = ((BlockStateProvider)rule.then().value()).getOptionalState(level, random, pos);
            if (optionalState != null) {
               return optionalState;
            }
         }
      }

      return this.fallback == null ? null : ((BlockStateProvider)this.fallback.value()).getOptionalState(level, random, pos);
   }

   public static Builder builder() {
      return new Builder((BlockStateProvider)null);
   }

   public static Builder builder(final @Nullable BlockStateProvider fallback) {
      return new Builder(fallback);
   }

   public static record Rule(BlockPredicate ifTrue, Holder<BlockStateProvider> then) {
      public static final Codec<Rule> CODEC = RecordCodecBuilder.create((i) -> i.group(BlockPredicate.CODEC.fieldOf("if_true").forGetter(Rule::ifTrue), BlockStateProvider.CODEC.fieldOf("then").forGetter(Rule::then)).apply(i, Rule::new));

      public Rule {
         super();
      }
   }

   public static class Builder {
      private final @Nullable BlockStateProvider fallback;
      private final List<Rule> rules = new ArrayList();

      public Builder(final @Nullable BlockStateProvider fallback) {
         super();
         this.fallback = fallback;
      }

      public Builder ifTrueThenProvide(final BlockPredicate ifTrue, final BlockStateProvider thenProvide) {
         this.rules.add(new Rule(ifTrue, Holder.direct(thenProvide)));
         return this;
      }

      public Builder ifTrueThenProvide(final BlockPredicate ifTrue, final Block thenProvide) {
         this.rules.add(new Rule(ifTrue, BlockStateProvider.holderOf(thenProvide)));
         return this;
      }

      public Builder ifTrueThenProvide(final BlockPredicate ifTrue, final BlockState thenProvide) {
         this.rules.add(new Rule(ifTrue, BlockStateProvider.holderOf(thenProvide)));
         return this;
      }

      public RuleBasedStateProvider build() {
         return new RuleBasedStateProvider(this.fallback == null ? null : Holder.direct(this.fallback), this.rules);
      }
   }
}
