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

public record RuleBasedBlockStateProvider(BlockStateProvider fallback, List<RuleBasedBlockStateProvider.Rule> rules) {
   public static final Codec<RuleBasedBlockStateProvider> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               BlockStateProvider.CODEC.fieldOf("fallback").forGetter(RuleBasedBlockStateProvider::fallback),
               RuleBasedBlockStateProvider.Rule.CODEC.listOf().fieldOf("rules").forGetter(RuleBasedBlockStateProvider::rules)
            )
            .apply(var0, RuleBasedBlockStateProvider::new)
   );

   public RuleBasedBlockStateProvider(BlockStateProvider fallback, List<RuleBasedBlockStateProvider.Rule> rules) {
      super();
      this.fallback = fallback;
      this.rules = rules;
   }

   public static RuleBasedBlockStateProvider simple(BlockStateProvider var0) {
      return new RuleBasedBlockStateProvider(var0, List.of());
   }

   public static RuleBasedBlockStateProvider simple(Block var0) {
      return simple(BlockStateProvider.simple(var0));
   }

   public BlockState getState(WorldGenLevel var1, RandomSource var2, BlockPos var3) {
      for (RuleBasedBlockStateProvider.Rule var5 : this.rules) {
         if (var5.ifTrue().test(var1, var3)) {
            return var5.then().getState(var2, var3);
         }
      }

      return this.fallback.getState(var2, var3);
   }

   public static record Rule(BlockPredicate ifTrue, BlockStateProvider then) {
      public static final Codec<RuleBasedBlockStateProvider.Rule> CODEC = RecordCodecBuilder.create(
         var0 -> var0.group(
                  BlockPredicate.CODEC.fieldOf("if_true").forGetter(RuleBasedBlockStateProvider.Rule::ifTrue),
                  BlockStateProvider.CODEC.fieldOf("then").forGetter(RuleBasedBlockStateProvider.Rule::then)
               )
               .apply(var0, RuleBasedBlockStateProvider.Rule::new)
      );

      public Rule(BlockPredicate ifTrue, BlockStateProvider then) {
         super();
         this.ifTrue = ifTrue;
         this.then = then;
      }
   }
}
