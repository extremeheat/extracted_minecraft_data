package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class WeightedConfiguredFeature {
   public static final Codec<WeightedConfiguredFeature> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(ConfiguredFeature.CODEC.fieldOf("feature").forGetter((var0x) -> {
         return var0x.feature;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((var0x) -> {
         return var0x.chance;
      })).apply(var0, WeightedConfiguredFeature::new);
   });
   public final Supplier<ConfiguredFeature<?, ?>> feature;
   public final float chance;

   public WeightedConfiguredFeature(ConfiguredFeature<?, ?> var1, float var2) {
      this(() -> {
         return var1;
      }, var2);
   }

   private WeightedConfiguredFeature(Supplier<ConfiguredFeature<?, ?>> var1, float var2) {
      super();
      this.feature = var1;
      this.chance = var2;
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, Random var3, BlockPos var4) {
      return ((ConfiguredFeature)this.feature.get()).place(var1, var2, var3, var4);
   }
}
