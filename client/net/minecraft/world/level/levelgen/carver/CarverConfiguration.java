package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CarverConfiguration extends ProbabilityFeatureConfiguration {
   public static final MapCodec<CarverConfiguration> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((var0x) -> {
         return var0x.probability;
      }), HeightProvider.CODEC.fieldOf("y").forGetter((var0x) -> {
         return var0x.field_252;
      }), FloatProvider.CODEC.fieldOf("yScale").forGetter((var0x) -> {
         return var0x.yScale;
      }), VerticalAnchor.CODEC.fieldOf("lava_level").forGetter((var0x) -> {
         return var0x.lavaLevel;
      }), CarverDebugSettings.CODEC.optionalFieldOf("debug_settings", CarverDebugSettings.DEFAULT).forGetter((var0x) -> {
         return var0x.debugSettings;
      })).apply(var0, CarverConfiguration::new);
   });
   // $FF: renamed from: y net.minecraft.world.level.levelgen.heightproviders.HeightProvider
   public final HeightProvider field_252;
   public final FloatProvider yScale;
   public final VerticalAnchor lavaLevel;
   public final CarverDebugSettings debugSettings;

   public CarverConfiguration(float var1, HeightProvider var2, FloatProvider var3, VerticalAnchor var4, CarverDebugSettings var5) {
      super(var1);
      this.field_252 = var2;
      this.yScale = var3;
      this.lavaLevel = var4;
      this.debugSettings = var5;
   }
}
