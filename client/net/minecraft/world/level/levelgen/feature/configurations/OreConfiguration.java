package net.minecraft.world.level.levelgen.feature.configurations;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;

public class OreConfiguration implements FeatureConfiguration {
   public static final Codec<OreConfiguration> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.list(OreConfiguration.TargetBlockState.CODEC).fieldOf("targets").forGetter((var0x) -> {
         return var0x.targetStates;
      }), Codec.intRange(0, 64).fieldOf("size").forGetter((var0x) -> {
         return var0x.size;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter((var0x) -> {
         return var0x.discardChanceOnAirExposure;
      })).apply(var0, OreConfiguration::new);
   });
   public final List<OreConfiguration.TargetBlockState> targetStates;
   public final int size;
   public final float discardChanceOnAirExposure;

   public OreConfiguration(List<OreConfiguration.TargetBlockState> var1, int var2, float var3) {
      super();
      this.size = var2;
      this.targetStates = var1;
      this.discardChanceOnAirExposure = var3;
   }

   public OreConfiguration(List<OreConfiguration.TargetBlockState> var1, int var2) {
      this(var1, var2, 0.0F);
   }

   public OreConfiguration(RuleTest var1, BlockState var2, int var3, float var4) {
      this(ImmutableList.of(new OreConfiguration.TargetBlockState(var1, var2)), var3, var4);
   }

   public OreConfiguration(RuleTest var1, BlockState var2, int var3) {
      this(ImmutableList.of(new OreConfiguration.TargetBlockState(var1, var2)), var3, 0.0F);
   }

   public static OreConfiguration.TargetBlockState target(RuleTest var0, BlockState var1) {
      return new OreConfiguration.TargetBlockState(var0, var1);
   }

   public static class TargetBlockState {
      public static final Codec<OreConfiguration.TargetBlockState> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(RuleTest.CODEC.fieldOf("target").forGetter((var0x) -> {
            return var0x.target;
         }), BlockState.CODEC.fieldOf("state").forGetter((var0x) -> {
            return var0x.state;
         })).apply(var0, OreConfiguration.TargetBlockState::new);
      });
      public final RuleTest target;
      public final BlockState state;

      TargetBlockState(RuleTest var1, BlockState var2) {
         super();
         this.target = var1;
         this.state = var2;
      }
   }
}
