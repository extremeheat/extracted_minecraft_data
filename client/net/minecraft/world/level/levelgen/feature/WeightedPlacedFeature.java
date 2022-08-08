package net.minecraft.world.level.levelgen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class WeightedPlacedFeature {
   public static final Codec<WeightedPlacedFeature> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(PlacedFeature.CODEC.fieldOf("feature").forGetter((var0x) -> {
         return var0x.feature;
      }), Codec.floatRange(0.0F, 1.0F).fieldOf("chance").forGetter((var0x) -> {
         return var0x.chance;
      })).apply(var0, WeightedPlacedFeature::new);
   });
   public final Holder<PlacedFeature> feature;
   public final float chance;

   public WeightedPlacedFeature(Holder<PlacedFeature> var1, float var2) {
      super();
      this.feature = var1;
      this.chance = var2;
   }

   public boolean place(WorldGenLevel var1, ChunkGenerator var2, RandomSource var3, BlockPos var4) {
      return ((PlacedFeature)this.feature.value()).place(var1, var2, var3, var4);
   }
}
