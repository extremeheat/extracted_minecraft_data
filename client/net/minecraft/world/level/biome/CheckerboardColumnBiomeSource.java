package net.minecraft.world.level.biome;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;

public class CheckerboardColumnBiomeSource extends BiomeSource {
   public static final MapCodec<CheckerboardColumnBiomeSource> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(Biome.LIST_CODEC.fieldOf("biomes").forGetter((var0x) -> {
         return var0x.allowedBiomes;
      }), Codec.intRange(0, 62).fieldOf("scale").orElse(2).forGetter((var0x) -> {
         return var0x.size;
      })).apply(var0, CheckerboardColumnBiomeSource::new);
   });
   private final HolderSet<Biome> allowedBiomes;
   private final int bitShift;
   private final int size;

   public CheckerboardColumnBiomeSource(HolderSet<Biome> var1, int var2) {
      super();
      this.allowedBiomes = var1;
      this.bitShift = var2 + 2;
      this.size = var2;
   }

   protected Stream<Holder<Biome>> collectPossibleBiomes() {
      return this.allowedBiomes.stream();
   }

   protected MapCodec<? extends BiomeSource> codec() {
      return CODEC;
   }

   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      return this.allowedBiomes.get(Math.floorMod((var1 >> this.bitShift) + (var3 >> this.bitShift), this.allowedBiomes.size()));
   }
}
