package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.Products.P4;
import com.mojang.datafixers.Products.P5;
import com.mojang.datafixers.Products.P9;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import com.mojang.serialization.codecs.RecordCodecBuilder.Mu;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;

public class ConcentricRingsStructurePlacement extends StructurePlacement {
   public static final MapCodec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> codec(var0).apply(var0, ConcentricRingsStructurePlacement::new)
   );
   private final int distance;
   private final int spread;
   private final int count;
   private final HolderSet<Biome> preferredBiomes;

   private static P9<Mu<ConcentricRingsStructurePlacement>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.ExclusionZone>, Integer, Integer, Integer, HolderSet<Biome>> codec(
      Instance<ConcentricRingsStructurePlacement> var0
   ) {
      P5 var1 = placementCodec(var0);
      P4 var2 = var0.group(
         Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance),
         Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread),
         Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::count),
         RegistryCodecs.homogeneousList(Registries.BIOME).fieldOf("preferred_biomes").forGetter(ConcentricRingsStructurePlacement::preferredBiomes)
      );
      return new P9(var1.t1(), var1.t2(), var1.t3(), var1.t4(), var1.t5(), var2.t1(), var2.t2(), var2.t3(), var2.t4());
   }

   public ConcentricRingsStructurePlacement(
      Vec3i var1,
      StructurePlacement.FrequencyReductionMethod var2,
      float var3,
      int var4,
      Optional<StructurePlacement.ExclusionZone> var5,
      int var6,
      int var7,
      int var8,
      HolderSet<Biome> var9
   ) {
      super(var1, var2, var3, var4, var5);
      this.distance = var6;
      this.spread = var7;
      this.count = var8;
      this.preferredBiomes = var9;
   }

   public ConcentricRingsStructurePlacement(int var1, int var2, int var3, HolderSet<Biome> var4) {
      this(Vec3i.ZERO, StructurePlacement.FrequencyReductionMethod.DEFAULT, 1.0F, 0, Optional.empty(), var1, var2, var3, var4);
   }

   public int distance() {
      return this.distance;
   }

   public int spread() {
      return this.spread;
   }

   public int count() {
      return this.count;
   }

   public HolderSet<Biome> preferredBiomes() {
      return this.preferredBiomes;
   }

   @Override
   protected boolean isPlacementChunk(ChunkGeneratorStructureState var1, int var2, int var3) {
      List var4 = var1.getRingPositionsFor(this);
      return var4 == null ? false : var4.contains(new ChunkPos(var2, var3));
   }

   @Override
   public StructurePlacementType<?> type() {
      return StructurePlacementType.CONCENTRIC_RINGS;
   }
}
