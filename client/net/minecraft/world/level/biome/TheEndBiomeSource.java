package net.minecraft.world.level.biome;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.levelgen.DensityFunction;

public class TheEndBiomeSource extends BiomeSource {
   public static final Codec<TheEndBiomeSource> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(var0x -> null)).apply(var0, var0.stable(TheEndBiomeSource::new))
   );
   private final Holder<Biome> end;
   private final Holder<Biome> highlands;
   private final Holder<Biome> midlands;
   private final Holder<Biome> islands;
   private final Holder<Biome> barrens;

   public TheEndBiomeSource(Registry<Biome> var1) {
      this(
         var1.getOrCreateHolderOrThrow(Biomes.THE_END),
         var1.getOrCreateHolderOrThrow(Biomes.END_HIGHLANDS),
         var1.getOrCreateHolderOrThrow(Biomes.END_MIDLANDS),
         var1.getOrCreateHolderOrThrow(Biomes.SMALL_END_ISLANDS),
         var1.getOrCreateHolderOrThrow(Biomes.END_BARRENS)
      );
   }

   private TheEndBiomeSource(Holder<Biome> var1, Holder<Biome> var2, Holder<Biome> var3, Holder<Biome> var4, Holder<Biome> var5) {
      super(ImmutableList.of(var1, var2, var3, var4, var5));
      this.end = var1;
      this.highlands = var2;
      this.midlands = var3;
      this.islands = var4;
      this.barrens = var5;
   }

   @Override
   protected Codec<? extends BiomeSource> codec() {
      return CODEC;
   }

   @Override
   public Holder<Biome> getNoiseBiome(int var1, int var2, int var3, Climate.Sampler var4) {
      int var5 = QuartPos.toBlock(var1);
      int var6 = QuartPos.toBlock(var2);
      int var7 = QuartPos.toBlock(var3);
      int var8 = SectionPos.blockToSectionCoord(var5);
      int var9 = SectionPos.blockToSectionCoord(var7);
      if ((long)var8 * (long)var8 + (long)var9 * (long)var9 <= 4096L) {
         return this.end;
      } else {
         int var10 = (SectionPos.blockToSectionCoord(var5) * 2 + 1) * 8;
         int var11 = (SectionPos.blockToSectionCoord(var7) * 2 + 1) * 8;
         double var12 = var4.erosion().compute(new DensityFunction.SinglePointContext(var10, var6, var11));
         if (var12 > 0.25) {
            return this.highlands;
         } else if (var12 >= -0.0625) {
            return this.midlands;
         } else {
            return var12 < -0.21875 ? this.islands : this.barrens;
         }
      }
   }
}
