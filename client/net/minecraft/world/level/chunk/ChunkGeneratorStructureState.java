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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import org.jspecify.annotations.Nullable;
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

   public static ChunkGeneratorStructureState createForFlat(final RandomState randomState, final long levelSeed, final BiomeSource biomeSource, final Stream<Holder<StructureSet>> structureOverrides) {
      List<Holder<StructureSet>> structures = structureOverrides.filter((structureSet) -> hasBiomesForStructureSet((StructureSet)structureSet.value(), biomeSource)).toList();
      return new ChunkGeneratorStructureState(randomState, biomeSource, levelSeed, 0L, structures);
   }

   public static ChunkGeneratorStructureState createForNormal(final RandomState randomState, final long levelSeed, final BiomeSource biomeSource, final HolderLookup<StructureSet> allStructures) {
      List<Holder<StructureSet>> structures = (List)allStructures.listElements().filter((structureSet) -> hasBiomesForStructureSet((StructureSet)structureSet.value(), biomeSource)).collect(Collectors.toUnmodifiableList());
      return new ChunkGeneratorStructureState(randomState, biomeSource, levelSeed, levelSeed, structures);
   }

   private static boolean hasBiomesForStructureSet(final StructureSet structureSet, final BiomeSource biomeSource) {
      Stream<Holder<Biome>> structureBiomes = structureSet.structures().stream().flatMap((entry) -> {
         Structure structure = (Structure)entry.structure().value();
         return structure.biomes().stream();
      });
      Set var10001 = biomeSource.possibleBiomes();
      Objects.requireNonNull(var10001);
      return structureBiomes.anyMatch(var10001::contains);
   }

   private ChunkGeneratorStructureState(final RandomState randomState, final BiomeSource biomeSource, final long levelSeed, final long concentricRingsSeed, final List<Holder<StructureSet>> possibleStructureSets) {
      super();
      this.randomState = randomState;
      this.levelSeed = levelSeed;
      this.biomeSource = biomeSource;
      this.concentricRingsSeed = concentricRingsSeed;
      this.possibleStructureSets = possibleStructureSets;
   }

   public List<Holder<StructureSet>> possibleStructureSets() {
      return this.possibleStructureSets;
   }

   private void generatePositions() {
      Set<Holder<Biome>> possibleBiomes = this.biomeSource.possibleBiomes();
      this.possibleStructureSets().forEach((setHolder) -> {
         StructureSet set = (StructureSet)setHolder.value();
         boolean hasAnyPlaceableStructures = false;

         for(StructureSet.StructureSelectionEntry entry : set.structures()) {
            Structure structure = (Structure)entry.structure().value();
            Stream var10000 = structure.biomes().stream();
            Objects.requireNonNull(possibleBiomes);
            if (var10000.anyMatch(possibleBiomes::contains)) {
               ((List)this.placementsForStructure.computeIfAbsent(structure, (s) -> new ArrayList())).add(set.placement());
               hasAnyPlaceableStructures = true;
            }
         }

         if (hasAnyPlaceableStructures) {
            StructurePlacement patt0$temp = set.placement();
            if (patt0$temp instanceof ConcentricRingsStructurePlacement) {
               ConcentricRingsStructurePlacement ringsPlacement = (ConcentricRingsStructurePlacement)patt0$temp;
               this.ringPositions.put(ringsPlacement, this.generateRingPositions(setHolder, ringsPlacement));
            }
         }

      });
   }

   private CompletableFuture<List<ChunkPos>> generateRingPositions(final Holder<StructureSet> structureSet, final ConcentricRingsStructurePlacement placement) {
      if (placement.count() == 0) {
         return CompletableFuture.completedFuture(List.of());
      } else {
         Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
         int distance = placement.distance();
         int count = placement.count();
         List<CompletableFuture<ChunkPos>> tasks = new ArrayList(count);
         int spread = placement.spread();
         HolderSet<Biome> preferredBiomes = placement.preferredBiomes();
         RandomSource random = RandomSource.create();
         random.setSeed(this.concentricRingsSeed);
         double angle = random.nextDouble() * 3.141592653589793 * 2.0;
         int positionInCircle = 0;
         int circle = 0;

         for(int i = 0; i < count; ++i) {
            double dist = (double)(4 * distance + distance * circle * 6) + (random.nextDouble() - 0.5) * (double)distance * 2.5;
            int initialX = (int)Math.round(Math.cos(angle) * dist);
            int initialZ = (int)Math.round(Math.sin(angle) * dist);
            RandomSource biomeSearchGenerator = random.fork();
            tasks.add(CompletableFuture.supplyAsync(() -> {
               BiomeSource var10000 = this.biomeSource;
               int var10001 = SectionPos.sectionToBlockCoord(initialX, 8);
               int var10003 = SectionPos.sectionToBlockCoord(initialZ, 8);
               Objects.requireNonNull(preferredBiomes);
               Pair<BlockPos, Holder<Biome>> closestBiome = var10000.findBiomeHorizontal(var10001, 0, var10003, 112, preferredBiomes::contains, biomeSearchGenerator, this.randomState.sampler());
               if (closestBiome != null) {
                  BlockPos position = (BlockPos)closestBiome.getFirst();
                  return new ChunkPos(SectionPos.blockToSectionCoord(position.getX()), SectionPos.blockToSectionCoord(position.getZ()));
               } else {
                  return new ChunkPos(initialX, initialZ);
               }
            }, Util.backgroundExecutor().forName("structureRings")));
            angle += 6.283185307179586 / (double)spread;
            ++positionInCircle;
            if (positionInCircle == spread) {
               ++circle;
               positionInCircle = 0;
               spread += 2 * spread / (circle + 1);
               spread = Math.min(spread, count - i);
               angle += random.nextDouble() * 3.141592653589793 * 2.0;
            }
         }

         return Util.sequence(tasks).thenApply((ringPositions) -> {
            double elapsedSeconds = (double)stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0;
            LOGGER.debug("Calculation for {} took {}s", structureSet, elapsedSeconds);
            return ringPositions;
         });
      }
   }

   public void ensureStructuresGenerated() {
      if (!this.hasGeneratedPositions) {
         this.generatePositions();
         this.hasGeneratedPositions = true;
      }

   }

   public @Nullable List<ChunkPos> getRingPositionsFor(final ConcentricRingsStructurePlacement placement) {
      this.ensureStructuresGenerated();
      CompletableFuture<List<ChunkPos>> result = (CompletableFuture)this.ringPositions.get(placement);
      return result != null ? (List)result.join() : null;
   }

   public List<StructurePlacement> getPlacementsForStructure(final Holder<Structure> structure) {
      this.ensureStructuresGenerated();
      return (List)this.placementsForStructure.getOrDefault(structure.value(), List.of());
   }

   public RandomState randomState() {
      return this.randomState;
   }

   public boolean hasStructureChunkInRange(final Holder<StructureSet> structureSet, final int sourceX, final int sourceZ, final int range) {
      StructurePlacement placement = ((StructureSet)structureSet.value()).placement();

      for(int testX = sourceX - range; testX <= sourceX + range; ++testX) {
         for(int testZ = sourceZ - range; testZ <= sourceZ + range; ++testZ) {
            if (placement.isStructureChunk(this, testX, testZ)) {
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
