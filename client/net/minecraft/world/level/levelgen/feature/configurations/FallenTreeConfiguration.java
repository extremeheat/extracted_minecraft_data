package net.minecraft.world.level.levelgen.feature.configurations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecorator;

public class FallenTreeConfiguration implements FeatureConfiguration {
   public static final Codec<FallenTreeConfiguration> CODEC = RecordCodecBuilder.create((var0) -> var0.group(BlockStateProvider.CODEC.fieldOf("trunk_provider").forGetter((var0x) -> var0x.trunkProvider), IntProvider.codec(0, 16).fieldOf("log_length").forGetter((var0x) -> var0x.logLength), TreeDecorator.CODEC.listOf().fieldOf("stump_decorators").forGetter((var0x) -> var0x.stumpDecorators), TreeDecorator.CODEC.listOf().fieldOf("log_decorators").forGetter((var0x) -> var0x.logDecorators)).apply(var0, FallenTreeConfiguration::new));
   public final BlockStateProvider trunkProvider;
   public final IntProvider logLength;
   public final List<TreeDecorator> stumpDecorators;
   public final List<TreeDecorator> logDecorators;

   protected FallenTreeConfiguration(BlockStateProvider var1, IntProvider var2, List<TreeDecorator> var3, List<TreeDecorator> var4) {
      super();
      this.trunkProvider = var1;
      this.logLength = var2;
      this.stumpDecorators = var3;
      this.logDecorators = var4;
   }

   public static class FallenTreeConfigurationBuilder {
      private final BlockStateProvider trunkProvider;
      private final IntProvider logLength;
      private List<TreeDecorator> stumpDecorators = new ArrayList();
      private List<TreeDecorator> logDecorators = new ArrayList();

      public FallenTreeConfigurationBuilder(BlockStateProvider var1, IntProvider var2) {
         super();
         this.trunkProvider = var1;
         this.logLength = var2;
      }

      public FallenTreeConfigurationBuilder stumpDecorators(List<TreeDecorator> var1) {
         this.stumpDecorators = var1;
         return this;
      }

      public FallenTreeConfigurationBuilder logDecorators(List<TreeDecorator> var1) {
         this.logDecorators = var1;
         return this;
      }

      public FallenTreeConfiguration build() {
         return new FallenTreeConfiguration(this.trunkProvider, this.logLength, this.stumpDecorators, this.logDecorators);
      }
   }
}
