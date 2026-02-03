package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.LegacyTagFixer;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.jspecify.annotations.Nullable;

public class LegacyStructureDataHandler implements LegacyTagFixer {
   public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
   private static final Map<String, String> CURRENT_TO_LEGACY_MAP = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put("Village", "Village");
      map.put("Mineshaft", "Mineshaft");
      map.put("Mansion", "Mansion");
      map.put("Igloo", "Temple");
      map.put("Desert_Pyramid", "Temple");
      map.put("Jungle_Pyramid", "Temple");
      map.put("Swamp_Hut", "Temple");
      map.put("Stronghold", "Stronghold");
      map.put("Monument", "Monument");
      map.put("Fortress", "Fortress");
      map.put("EndCity", "EndCity");
   });
   private static final Map<String, String> LEGACY_TO_CURRENT_MAP = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put("Iglu", "Igloo");
      map.put("TeDP", "Desert_Pyramid");
      map.put("TeJP", "Jungle_Pyramid");
      map.put("TeSH", "Swamp_Hut");
   });
   private static final Set<String> OLD_STRUCTURE_REGISTRY_KEYS = Set.of("pillager_outpost", "mineshaft", "mansion", "jungle_pyramid", "desert_pyramid", "igloo", "ruined_portal", "shipwreck", "swamp_hut", "stronghold", "monument", "ocean_ruin", "fortress", "endcity", "buried_treasure", "village", "nether_fossil", "bastion_remnant");
   private final boolean hasLegacyData;
   private final Map<String, Long2ObjectMap<CompoundTag>> dataMap = Maps.newHashMap();
   private final Map<String, StructureFeatureIndexSavedData> indexMap = Maps.newHashMap();
   private final @Nullable DimensionDataStorage dimensionDataStorage;
   private final List<String> legacyKeys;
   private final List<String> currentKeys;
   private final DataFixer dataFixer;
   private boolean cachesInitialized;

   public LegacyStructureDataHandler(final @Nullable DimensionDataStorage dimensionDataStorage, final List<String> legacyKeys, final List<String> currentKeys, final DataFixer dataFixer) {
      super();
      this.dimensionDataStorage = dimensionDataStorage;
      this.legacyKeys = legacyKeys;
      this.currentKeys = currentKeys;
      this.dataFixer = dataFixer;
      boolean b = false;

      for(String legacyKey : this.currentKeys) {
         b |= this.dataMap.get(legacyKey) != null;
      }

      this.hasLegacyData = b;
   }

   public void markChunkDone(final ChunkPos pos) {
      long index = pos.pack();

      for(String legacyKey : this.legacyKeys) {
         StructureFeatureIndexSavedData savedData = (StructureFeatureIndexSavedData)this.indexMap.get(legacyKey);
         if (savedData != null && savedData.hasUnhandledIndex(index)) {
            savedData.removeIndex(index);
         }
      }

   }

   public int targetDataVersion() {
      return 1493;
   }

   public CompoundTag applyFix(CompoundTag chunkTag) {
      int version = NbtUtils.getDataVersion(chunkTag);
      if (version < 1493) {
         chunkTag = DataFixTypes.CHUNK.update(this.dataFixer, chunkTag, version, 1493);
         if ((Boolean)chunkTag.getCompound("Level").flatMap((level) -> level.getBoolean("hasLegacyStructureData")).orElse(false)) {
            if (!this.cachesInitialized && this.dimensionDataStorage != null) {
               this.populateCaches(this.dimensionDataStorage);
            }

            chunkTag = this.updateFromLegacy(chunkTag);
         }
      }

      return chunkTag;
   }

   private CompoundTag updateFromLegacy(CompoundTag tag) {
      CompoundTag levelTag = tag.getCompoundOrEmpty("Level");
      ChunkPos pos = new ChunkPos(levelTag.getIntOr("xPos", 0), levelTag.getIntOr("zPos", 0));
      if (this.isUnhandledStructureStart(pos.x(), pos.z())) {
         tag = this.updateStructureStart(tag, pos);
      }

      CompoundTag structureTag = levelTag.getCompoundOrEmpty("Structures");
      CompoundTag referencesTag = structureTag.getCompoundOrEmpty("References");

      for(String key : this.currentKeys) {
         boolean featureExists = OLD_STRUCTURE_REGISTRY_KEYS.contains(key.toLowerCase(Locale.ROOT));
         if (!referencesTag.getLongArray(key).isPresent() && featureExists) {
            int lookupRange = 8;
            LongList references = new LongArrayList();

            for(int sourceX = pos.x() - 8; sourceX <= pos.x() + 8; ++sourceX) {
               for(int sourceZ = pos.z() - 8; sourceZ <= pos.z() + 8; ++sourceZ) {
                  if (this.hasLegacyStart(sourceX, sourceZ, key)) {
                     references.add(ChunkPos.pack(sourceX, sourceZ));
                  }
               }
            }

            referencesTag.putLongArray(key, references.toLongArray());
         }
      }

      structureTag.put("References", referencesTag);
      levelTag.put("Structures", structureTag);
      tag.put("Level", levelTag);
      return tag;
   }

   private boolean hasLegacyStart(final int x, final int z, final String feature) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         return this.dataMap.get(feature) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(feature))).hasStartIndex(ChunkPos.pack(x, z));
      }
   }

   private boolean isUnhandledStructureStart(final int x, final int z) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         for(String key : this.currentKeys) {
            if (this.dataMap.get(key) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(key))).hasUnhandledIndex(ChunkPos.pack(x, z))) {
               return true;
            }
         }

         return false;
      }
   }

   private CompoundTag updateStructureStart(final CompoundTag tag, final ChunkPos pos) {
      CompoundTag levelTag = tag.getCompoundOrEmpty("Level");
      CompoundTag structureTag = levelTag.getCompoundOrEmpty("Structures");
      CompoundTag startTag = structureTag.getCompoundOrEmpty("Starts");

      for(String key : this.currentKeys) {
         Long2ObjectMap<CompoundTag> tagMap = (Long2ObjectMap)this.dataMap.get(key);
         if (tagMap != null) {
            long longPos = pos.pack();
            if (((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(key))).hasUnhandledIndex(longPos)) {
               CompoundTag featureTag = (CompoundTag)tagMap.get(longPos);
               if (featureTag != null) {
                  startTag.put(key, featureTag);
               }
            }
         }
      }

      structureTag.put("Starts", startTag);
      levelTag.put("Structures", structureTag);
      tag.put("Level", levelTag);
      return tag;
   }

   private synchronized void populateCaches(final DimensionDataStorage dimensionDataStorage) {
      if (!this.cachesInitialized) {
         for(String legacyKey : this.legacyKeys) {
            CompoundTag legacyData = new CompoundTag();

            try {
               legacyData = dimensionDataStorage.readTagFromDisk(legacyKey, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES, 1493).getCompoundOrEmpty("data").getCompoundOrEmpty("Features");
               if (legacyData.isEmpty()) {
                  continue;
               }
            } catch (IOException var8) {
            }

            legacyData.forEach((tagKey, tag) -> {
               if (tag instanceof CompoundTag compoundTag) {
                  long longPos = ChunkPos.pack(compoundTag.getIntOr("ChunkX", 0), compoundTag.getIntOr("ChunkZ", 0));
                  ListTag childList = compoundTag.getListOrEmpty("Children");
                  if (!childList.isEmpty()) {
                     Optional<String> startId = childList.getCompound(0).flatMap((t) -> t.getString("id"));
                     Map var10001 = LEGACY_TO_CURRENT_MAP;
                     Objects.requireNonNull(var10001);
                     startId.map(var10001::get).ifPresent((newId) -> compoundTag.putString("id", newId));
                  }

                  compoundTag.getString("id").ifPresent((entryId) -> ((Long2ObjectMap)this.dataMap.computeIfAbsent(entryId, (k) -> new Long2ObjectOpenHashMap())).put(longPos, compoundTag));
               }
            });
            String indexesID = legacyKey + "_index";
            StructureFeatureIndexSavedData legacyIndexes = (StructureFeatureIndexSavedData)dimensionDataStorage.computeIfAbsent(StructureFeatureIndexSavedData.type(indexesID));
            if (legacyIndexes.getAll().isEmpty()) {
               StructureFeatureIndexSavedData indexSaveData = new StructureFeatureIndexSavedData();
               this.indexMap.put(legacyKey, indexSaveData);
               legacyData.forEach((key, tag) -> {
                  if (tag instanceof CompoundTag entryTag) {
                     indexSaveData.addIndex(ChunkPos.pack(entryTag.getIntOr("ChunkX", 0), entryTag.getIntOr("ChunkZ", 0)));
                  }

               });
            } else {
               this.indexMap.put(legacyKey, legacyIndexes);
            }
         }

         this.cachesInitialized = true;
      }
   }

   public static Supplier<LegacyTagFixer> getLegacyTagFixer(final ResourceKey<Level> dimension, final Supplier<@Nullable DimensionDataStorage> dimensionDataStorage, final DataFixer dataFixer) {
      if (dimension == Level.OVERWORLD) {
         return () -> new LegacyStructureDataHandler((DimensionDataStorage)dimensionDataStorage.get(), ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"), dataFixer);
      } else if (dimension == Level.NETHER) {
         List<String> netherKeys = ImmutableList.of("Fortress");
         return () -> new LegacyStructureDataHandler((DimensionDataStorage)dimensionDataStorage.get(), netherKeys, netherKeys, dataFixer);
      } else if (dimension == Level.END) {
         List<String> endKeys = ImmutableList.of("EndCity");
         return () -> new LegacyStructureDataHandler((DimensionDataStorage)dimensionDataStorage.get(), endKeys, endKeys, dataFixer);
      } else {
         return LegacyTagFixer.EMPTY;
      }
   }
}
