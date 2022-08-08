package net.minecraft.world.level.levelgen.structure.placement;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.RandomState;

public class ConcentricRingsStructurePlacement extends StructurePlacement {
   public static final Codec<ConcentricRingsStructurePlacement> CODEC = RecordCodecBuilder.create((var0) -> {
      return codec(var0).apply(var0, ConcentricRingsStructurePlacement::new);
   });
   private final int distance;
   private final int spread;
   private final int count;
   private final HolderSet<Biome> preferredBiomes;

   private static Products.P9<RecordCodecBuilder.Mu<ConcentricRingsStructurePlacement>, Vec3i, StructurePlacement.FrequencyReductionMethod, Float, Integer, Optional<StructurePlacement.ExclusionZone>, Integer, Integer, Integer, HolderSet<Biome>> codec(RecordCodecBuilder.Instance<ConcentricRingsStructurePlacement> var0) {
      Products.P5 var1 = placementCodec(var0);
      Products.P4 var2 = var0.group(Codec.intRange(0, 1023).fieldOf("distance").forGetter(ConcentricRingsStructurePlacement::distance), Codec.intRange(0, 1023).fieldOf("spread").forGetter(ConcentricRingsStructurePlacement::spread), Codec.intRange(1, 4095).fieldOf("count").forGetter(ConcentricRingsStructurePlacement::count), RegistryCodecs.homogeneousList(Registry.BIOME_REGISTRY).fieldOf("preferred_biomes").forGetter(ConcentricRingsStructurePlacement::preferredBiomes));
      return new Products.P9(var1.t1(), var1.t2(), var1.t3(), var1.t4(), var1.t5(), var2.t1(), var2.t2(), var2.t3(), var2.t4());
   }

   public ConcentricRingsStructurePlacement(Vec3i var1, StructurePlacement.FrequencyReductionMethod var2, float var3, int var4, Optional<StructurePlacement.ExclusionZone> var5, int var6, int var7, int var8, HolderSet<Biome> var9) {
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

   protected boolean isPlacementChunk(ChunkGenerator var1, RandomState var2, long var3, int var5, int var6) {
      List var7 = var1.getRingPositionsFor(this, var2);
      return var7 == null ? false : var7.contains(new ChunkPos(var5, var6));
   }

   public StructurePlacementType<?> type() {
      return StructurePlacementType.CONCENTRIC_RINGS;
   }
}
