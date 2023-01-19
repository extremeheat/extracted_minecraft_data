package net.minecraft.world.level.levelgen.structure;

import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.visitors.CollectFields;
import net.minecraft.nbt.visitors.FieldSelector;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.storage.ChunkScanAccess;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.slf4j.Logger;

public class StructureCheck {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final int NO_STRUCTURE = -1;
   private final ChunkScanAccess storageAccess;
   private final RegistryAccess registryAccess;
   private final Registry<Biome> biomes;
   private final Registry<Structure> structureConfigs;
   private final StructureTemplateManager structureTemplateManager;
   private final ResourceKey<Level> dimension;
   private final ChunkGenerator chunkGenerator;
   private final RandomState randomState;
   private final LevelHeightAccessor heightAccessor;
   private final BiomeSource biomeSource;
   private final long seed;
   private final DataFixer fixerUpper;
   private final Long2ObjectMap<Object2IntMap<Structure>> loadedChunks = new Long2ObjectOpenHashMap();
   private final Map<Structure, Long2BooleanMap> featureChecks = new HashMap<>();

   public StructureCheck(
      ChunkScanAccess var1,
      RegistryAccess var2,
      StructureTemplateManager var3,
      ResourceKey<Level> var4,
      ChunkGenerator var5,
      RandomState var6,
      LevelHeightAccessor var7,
      BiomeSource var8,
      long var9,
      DataFixer var11
   ) {
      super();
      this.storageAccess = var1;
      this.registryAccess = var2;
      this.structureTemplateManager = var3;
      this.dimension = var4;
      this.chunkGenerator = var5;
      this.randomState = var6;
      this.heightAccessor = var7;
      this.biomeSource = var8;
      this.seed = var9;
      this.fixerUpper = var11;
      this.biomes = var2.registryOrThrow(Registries.BIOME);
      this.structureConfigs = var2.registryOrThrow(Registries.STRUCTURE);
   }

   public StructureCheckResult checkStart(ChunkPos var1, Structure var2, boolean var3) {
      long var4 = var1.toLong();
      Object2IntMap var6 = (Object2IntMap)this.loadedChunks.get(var4);
      if (var6 != null) {
         return this.checkStructureInfo(var6, var2, var3);
      } else {
         StructureCheckResult var7 = this.tryLoadFromStorage(var1, var2, var3, var4);
         if (var7 != null) {
            return var7;
         } else {
            boolean var8 = ((Long2BooleanMap)this.featureChecks.computeIfAbsent(var2, var0 -> new Long2BooleanOpenHashMap()))
               .computeIfAbsent(var4, var3x -> this.canCreateStructure(var1, var2));
            return !var8 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.CHUNK_LOAD_NEEDED;
         }
      }
   }

   private boolean canCreateStructure(ChunkPos var1, Structure var2) {
      return var2.findValidGenerationPoint(
            new Structure.GenerationContext(
               this.registryAccess,
               this.chunkGenerator,
               this.biomeSource,
               this.randomState,
               this.structureTemplateManager,
               this.seed,
               var1,
               this.heightAccessor,
               var2.biomes()::contains
            )
         )
         .isPresent();
   }

   @Nullable
   private StructureCheckResult tryLoadFromStorage(ChunkPos var1, Structure var2, boolean var3, long var4) {
      CollectFields var6 = new CollectFields(
         new FieldSelector(IntTag.TYPE, "DataVersion"),
         new FieldSelector("Level", "Structures", CompoundTag.TYPE, "Starts"),
         new FieldSelector("structures", CompoundTag.TYPE, "starts")
      );

      try {
         this.storageAccess.scanChunk(var1, var6).join();
      } catch (Exception var13) {
         LOGGER.warn("Failed to read chunk {}", var1, var13);
         return StructureCheckResult.CHUNK_LOAD_NEEDED;
      }

      Tag var7 = var6.getResult();
      if (!(var7 instanceof CompoundTag)) {
         return null;
      } else {
         CompoundTag var8 = (CompoundTag)var7;
         int var9 = ChunkStorage.getVersion(var8);
         if (var9 <= 1493) {
            return StructureCheckResult.CHUNK_LOAD_NEEDED;
         } else {
            ChunkStorage.injectDatafixingContext(var8, this.dimension, this.chunkGenerator.getTypeNameForDataFixer());

            CompoundTag var10;
            try {
               var10 = NbtUtils.update(this.fixerUpper, DataFixTypes.CHUNK, var8, var9);
            } catch (Exception var12) {
               LOGGER.warn("Failed to partially datafix chunk {}", var1, var12);
               return StructureCheckResult.CHUNK_LOAD_NEEDED;
            }

            Object2IntMap var11 = this.loadStructures(var10);
            if (var11 == null) {
               return null;
            } else {
               this.storeFullResults(var4, var11);
               return this.checkStructureInfo(var11, var2, var3);
            }
         }
      }
   }

   @Nullable
   private Object2IntMap<Structure> loadStructures(CompoundTag var1) {
      if (!var1.contains("structures", 10)) {
         return null;
      } else {
         CompoundTag var2 = var1.getCompound("structures");
         if (!var2.contains("starts", 10)) {
            return null;
         } else {
            CompoundTag var3 = var2.getCompound("starts");
            if (var3.isEmpty()) {
               return Object2IntMaps.emptyMap();
            } else {
               Object2IntOpenHashMap var4 = new Object2IntOpenHashMap();
               Registry var5 = this.registryAccess.registryOrThrow(Registries.STRUCTURE);

               for(String var7 : var3.getAllKeys()) {
                  ResourceLocation var8 = ResourceLocation.tryParse(var7);
                  if (var8 != null) {
                     Structure var9 = (Structure)var5.get(var8);
                     if (var9 != null) {
                        CompoundTag var10 = var3.getCompound(var7);
                        if (!var10.isEmpty()) {
                           String var11 = var10.getString("id");
                           if (!"INVALID".equals(var11)) {
                              int var12 = var10.getInt("references");
                              var4.put(var9, var12);
                           }
                        }
                     }
                  }
               }

               return var4;
            }
         }
      }
   }

   private static Object2IntMap<Structure> deduplicateEmptyMap(Object2IntMap<Structure> var0) {
      return var0.isEmpty() ? Object2IntMaps.emptyMap() : var0;
   }

   private StructureCheckResult checkStructureInfo(Object2IntMap<Structure> var1, Structure var2, boolean var3) {
      int var4 = var1.getOrDefault(var2, -1);
      return var4 == -1 || var3 && var4 != 0 ? StructureCheckResult.START_NOT_PRESENT : StructureCheckResult.START_PRESENT;
   }

   public void onStructureLoad(ChunkPos var1, Map<Structure, StructureStart> var2) {
      long var3 = var1.toLong();
      Object2IntOpenHashMap var5 = new Object2IntOpenHashMap();
      var2.forEach((var1x, var2x) -> {
         if (var2x.isValid()) {
            var5.put(var1x, var2x.getReferences());
         }
      });
      this.storeFullResults(var3, var5);
   }

   private void storeFullResults(long var1, Object2IntMap<Structure> var3) {
      this.loadedChunks.put(var1, deduplicateEmptyMap(var3));
      this.featureChecks.values().forEach(var2 -> var2.remove(var1));
   }

   public void incrementReference(ChunkPos var1, Structure var2) {
      this.loadedChunks.compute(var1.toLong(), (var1x, var2x) -> {
         if (var2x == null || var2x.isEmpty()) {
            var2x = new Object2IntOpenHashMap();
         }

         var2x.computeInt(var2, (var0x, var1xx) -> var1xx == null ? 1 : var1xx + 1);
         return var2x;
      });
   }
}
