package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;

public record DiskConfiguration(RuleBasedBlockStateProvider stateProvider, BlockPredicate target, IntProvider radius, int halfHeight) implements FeatureConfiguration {
   public static final Codec<DiskConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(RuleBasedBlockStateProvider.CODEC.fieldOf("state_provider").forGetter(DiskConfiguration::stateProvider), BlockPredicate.CODEC.fieldOf("target").forGetter(DiskConfiguration::target), IntProvider.codec(0, 8).fieldOf("radius").forGetter(DiskConfiguration::radius), Codec.intRange(0, 4).fieldOf("half_height").forGetter(DiskConfiguration::halfHeight)).apply(var0, DiskConfiguration::new);
   });

   public DiskConfiguration(RuleBasedBlockStateProvider var1, BlockPredicate var2, IntProvider var3, int var4) {
      super();
      this.stateProvider = var1;
      this.target = var2;
      this.radius = var3;
      this.halfHeight = var4;
   }

   public RuleBasedBlockStateProvider stateProvider() {
      return this.stateProvider;
   }

   public BlockPredicate target() {
      return this.target;
   }

   public IntProvider radius() {
      return this.radius;
   }

   public int halfHeight() {
      return this.halfHeight;
   }
}
