package net.minecraft.world.level.levelgen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.configurations.ProbabilityFeatureConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;

public class CarverConfiguration extends ProbabilityFeatureConfiguration {
   public static final MapCodec<CarverConfiguration> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((var0x) -> {
         return var0x.probability;
      }), HeightProvider.CODEC.fieldOf("y").forGetter((var0x) -> {
         return var0x.y;
      }), FloatProvider.CODEC.fieldOf("yScale").forGetter((var0x) -> {
         return var0x.yScale;
      }), VerticalAnchor.CODEC.fieldOf("lava_level").forGetter((var0x) -> {
         return var0x.lavaLevel;
      }), CarverDebugSettings.CODEC.optionalFieldOf("debug_settings", CarverDebugSettings.DEFAULT).forGetter((var0x) -> {
         return var0x.debugSettings;
      }), RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("replaceable").forGetter((var0x) -> {
         return var0x.replaceable;
      })).apply(var0, CarverConfiguration::new);
   });
   public final HeightProvider y;
   public final FloatProvider yScale;
   public final VerticalAnchor lavaLevel;
   public final CarverDebugSettings debugSettings;
   public final HolderSet<Block> replaceable;

   public CarverConfiguration(float var1, HeightProvider var2, FloatProvider var3, VerticalAnchor var4, CarverDebugSettings var5, HolderSet<Block> var6) {
      super(var1);
      this.y = var2;
      this.yScale = var3;
      this.lavaLevel = var4;
      this.debugSettings = var5;
      this.replaceable = var6;
   }
}
