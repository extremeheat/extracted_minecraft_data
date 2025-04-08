package net.minecraft.world.level.chunk;

import com.google.common.base.Stopwatch;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.slf4j.Logger;

public class ChunkGeneratorStructureState {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final RandomState randomState;
   private final BiomeSource biomeSource;
   private final long levelSeed;
   private final long concentricRingsSeed;
   private final Map<Structure, List<StructurePlacement>> placementsForStructure = new Object2ObjectOpenHashMap();
   private final Map<ConcentricRingsStructurePlacement, CompletableFuture<List<ChunkPos>>> ringPositions = new Object2ObjectArrayMap();
   private boolean hasGeneratedPositions;
   private final List<Holder<StructureSet>> possibleStructureSets;

   public static ChunkGeneratorStructureState createForFlat(RandomState var0, long var1, BiomeSource var3, Stream<Holder<StructureSet>> var4) {
      List var5 = var4.filter((var1x) -> hasBiomesForStructureSet((StructureSet)var1x.value(), var3)).toList();
      return new ChunkGeneratorStructureState(var0, var3, var1, 0L, var5);
   }

   public static ChunkGeneratorStructureState createForNormal(RandomState var0, long var1, BiomeSource var3, HolderLookup<StructureSet> var4) {
      List var5 = (List)var4.listElements().filter((var1x) -> hasBiomesForStructureSet((StructureSet)var1x.value(), var3)).collect(Collectors.toUnmodifiableList());
      return new ChunkGeneratorStructureState(var0, var3, var1, var1, var5);
   }

   private static boolean hasBiomesForStructureSet(StructureSet var0, BiomeSource var1) {
      Stream var2 = var0.structures().stream().flatMap((var0x) -> {
         Structure var1 = (Structure)var0x.structure().value();
         return var1.biomes().stream();
      });
      Set var10001 = var1.possibleBiomes();
      Objects.requireNonNull(var10001);
      return var2.anyMatch(var10001::contains);
   }

   private ChunkGeneratorStructureState(RandomState var1, BiomeSource var2, long var3, long var5, List<Holder<StructureSet>> var7) {
      super();
      this.randomState = var1;
      this.levelSeed = var3;
      this.biomeSource = var2;
      this.concentricRingsSeed = var5;
      this.possibleStructureSets = var7;
   }

   public List<Holder<StructureSet>> possibleStructureSets() {
      return this.possibleStructureSets;
   }

   private void generatePositions() {
      Set var1 = this.biomeSource.possibleBiomes();
      this.possibleStructureSets().forEach((var2) -> {
         StructureSet var3 = (StructureSet)var2.value();
         boolean var4 = false;

         for(StructureSet.StructureSelectionEntry var6 : var3.structures()) {
            Structure var7 = (Structure)var6.structure().value();
            Stream var10000 = var7.biomes().stream();
            Objects.requireNonNull(var1);
            if (var10000.anyMatch(var1::contains)) {
               ((List)this.placementsForStructure.computeIfAbsent(var7, (var0) -> new ArrayList())).add(var3.placement());
               var4 = true;
            }
         }

         if (var4) {
            StructurePlacement var9 = var3.placement();
            if (var9 instanceof ConcentricRingsStructurePlacement) {
               ConcentricRingsStructurePlacement var8 = (ConcentricRingsStructurePlacement)var9;
               this.ringPositions.put(var8, this.generateRingPositions(var2, var8));
            }
         }

      });
   }

   private CompletableFuture<List<ChunkPos>> generateRingPositions(Holder<StructureSet> var1, ConcentricRingsStructurePlacement var2) {
      if (var2.count() == 0) {
         return CompletableFuture.completedFuture(List.of());
      } else {
         Stopwatch var3 = Stopwatch.createStarted(Util.TICKER);
         int var4 = var2.distance();
         int var5 = var2.count();
         ArrayList var6 = new ArrayList(var5);
         int var7 = var2.spread();
         HolderSet var8 = var2.preferredBiomes();
         RandomSource var9 = RandomSource.create();
         var9.setSeed(this.concentricRingsSeed);
         double var10 = var9.nextDouble() * 3.141592653589793 * 2.0;
         int var12 = 0;
         int var13 = 0;

         for(int var14 = 0; var14 < var5; ++var14) {
            double var15 = (double)(4 * var4 + var4 * var13 * 6) + (var9.nextDouble() - 0.5) * (double)var4 * 2.5;
            int var17 = (int)Math.round(Math.cos(var10) * var15);
            int var18 = (int)Math.round(Math.sin(var10) * var15);
            RandomSource var19 = var9.fork();
            var6.add(CompletableFuture.supplyAsync(() -> {
               BiomeSource var10000 = this.biomeSource;
               int var10001 = SectionPos.sectionToBlockCoord(var17, 8);
               int var10003 = SectionPos.sectionToBlockCoord(var18, 8);
               Objects.requireNonNull(var8);
               Pair var5 = var10000.findBiomeHorizontal(var10001, 0, var10003, 112, var8::contains, var19, this.randomState.sampler());
               if (var5 != null) {
                  BlockPos var6 = (BlockPos)var5.getFirst();
                  return new ChunkPos(SectionPos.blockToSectionCoord(var6.getX()), SectionPos.blockToSectionCoord(var6.getZ()));
               } else {
                  return new ChunkPos(var17, var18);
               }
            }, Util.backgroundExecutor().forName("structureRings")));
            var10 += 6.283185307179586 / (double)var7;
            ++var12;
            if (var12 == var7) {
               ++var13;
               var12 = 0;
               var7 += 2 * var7 / (var13 + 1);
               var7 = Math.min(var7, var5 - var14);
               var10 += var9.nextDouble() * 3.141592653589793 * 2.0;
            }
         }

         return Util.sequence(var6).thenApply((var2x) -> {
            double var3x = (double)var3.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
            LOGGER.debug("Calculation for {} took {}s", var1, var3x);
            return var2x;
         });
      }
   }

   public void ensureStructuresGenerated() {
      if (!this.hasGeneratedPositions) {
         this.generatePositions();
         this.hasGeneratedPositions = true;
      }

   }

   @Nullable
   public List<ChunkPos> getRingPositionsFor(ConcentricRingsStructurePlacement var1) {
      this.ensureStructuresGenerated();
      CompletableFuture var2 = (CompletableFuture)this.ringPositions.get(var1);
      return var2 != null ? (List)var2.join() : null;
   }

   public List<StructurePlacement> getPlacementsForStructure(Holder<Structure> var1) {
      this.ensureStructuresGenerated();
      return (List)this.placementsForStructure.getOrDefault(var1.value(), List.of());
   }

   public RandomState randomState() {
      return this.randomState;
   }

   public boolean hasStructureChunkInRange(Holder<StructureSet> var1, int var2, int var3, int var4) {
      StructurePlacement var5 = ((StructureSet)var1.value()).placement();

      for(int var6 = var2 - var4; var6 <= var2 + var4; ++var6) {
         for(int var7 = var3 - var4; var7 <= var3 + var4; ++var7) {
            if (var5.isStructureChunk(this, var6, var7)) {
               return true;
            }
         }
      }

      return false;
   }

   public long getLevelSeed() {
      return this.levelSeed;
   }
}
