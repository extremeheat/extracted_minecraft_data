package net.minecraft.util.filefix.fixes;

import com.google.common.collect.Maps;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.OptionalDynamic;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.filefix.FileFix;
import net.minecraft.util.filefix.access.ChunkNbt;
import net.minecraft.util.filefix.access.CompressedNbt;
import net.minecraft.util.filefix.access.FileAccess;
import net.minecraft.util.filefix.access.FileAccessProvider;
import net.minecraft.util.filefix.access.FileRelation;
import net.minecraft.util.filefix.access.FileResourceTypes;
import net.minecraft.util.filefix.access.LevelDat;
import net.minecraft.util.filefix.access.SavedDataNbt;
import net.minecraft.util.worldupdate.UpgradeProgress;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.RegionStorageInfo;

public class LegacyStructureFileFix extends FileFix {
   public static final int STRUCTURE_RANGE = 8;
   public static final List<String> OVERWORLD_LEGACY_STRUCTURES = List.of("Monument", "Stronghold", "Mineshaft", "Temple", "Mansion");
   public static final Map<String, String> LEGACY_TO_CURRENT_MAP = (Map)Util.make(Maps.newHashMap(), (map) -> {
      map.put("Iglu", "Igloo");
      map.put("TeDP", "Desert_Pyramid");
      map.put("TeJP", "Jungle_Pyramid");
      map.put("TeSH", "Swamp_Hut");
   });
   public static final List<String> NETHER_LEGACY_STRUCTURES = List.of("Fortress");
   public static final List<String> END_LEGACY_STRUCTURES = List.of("EndCity");
   private static final ResourceKey<Level> OVERWORLD_KEY;
   private static final ResourceKey<Level> NETHER_KEY;
   private static final ResourceKey<Level> END_KEY;

   public LegacyStructureFileFix(final Schema schema) {
      super(schema);
   }

   public void makeFixer() {
      this.addFileContentFix((files) -> {
         List<FileAccess<SavedDataNbt>> overworldStructureData = OVERWORLD_LEGACY_STRUCTURES.stream().map((structureId) -> getLegacyStructureData(files, structureId)).toList();
         RegionStorageInfo overworldInfo = new RegionStorageInfo("overworld", OVERWORLD_KEY, "chunk");
         List<FileAccess<SavedDataNbt>> netherStructureData = NETHER_LEGACY_STRUCTURES.stream().map((structureId) -> getLegacyStructureData(files, structureId)).toList();
         RegionStorageInfo netherInfo = new RegionStorageInfo("the_nether", NETHER_KEY, "chunk");
         List<FileAccess<SavedDataNbt>> endStructureData = END_LEGACY_STRUCTURES.stream().map((structureId) -> getLegacyStructureData(files, structureId)).toList();
         RegionStorageInfo endInfo = new RegionStorageInfo("the_end", END_KEY, "chunk");
         FileAccess<LevelDat> levelDat = files.<LevelDat>getFileAccess(FileResourceTypes.LEVEL_DAT, FileRelation.ORIGIN.forFile("level.dat"));
         FileAccess<ChunkNbt> overworldChunks = files.<ChunkNbt>getFileAccess(FileResourceTypes.chunk(DataFixTypes.CHUNK, overworldInfo), FileRelation.OLD_OVERWORLD.resolve(FileRelation.REGION));
         FileAccess<ChunkNbt> netherChunks = files.<ChunkNbt>getFileAccess(FileResourceTypes.chunk(DataFixTypes.CHUNK, netherInfo), FileRelation.OLD_NETHER.resolve(FileRelation.REGION));
         FileAccess<ChunkNbt> endChunks = files.<ChunkNbt>getFileAccess(FileResourceTypes.chunk(DataFixTypes.CHUNK, endInfo), FileRelation.OLD_END.resolve(FileRelation.REGION));
         return (upgradeProgress) -> {
            Optional<Dynamic<Tag>> levelData = ((LevelDat)levelDat.getOnlyFile()).read();
            if (!levelData.isEmpty()) {
               upgradeProgress.setType(UpgradeProgress.Type.LEGACY_STRUCTURES);
               extractAndStoreLegacyStructureData((Dynamic)levelData.get(), List.of(new DimensionFixEntry(OVERWORLD_KEY, overworldStructureData, overworldChunks, new Long2ObjectOpenHashMap()), new DimensionFixEntry(NETHER_KEY, netherStructureData, netherChunks, new Long2ObjectOpenHashMap()), new DimensionFixEntry(END_KEY, endStructureData, endChunks, new Long2ObjectOpenHashMap())), upgradeProgress);
            }
         };
      });
   }

   private static void extractAndStoreLegacyStructureData(final Dynamic<Tag> levelData, final List<DimensionFixEntry> dimensionFixEntries, final UpgradeProgress upgradeProgress) throws IOException {
      upgradeProgress.setStatus(UpgradeProgress.Status.COUNTING);

      for(DimensionFixEntry dimensionFixEntry : dimensionFixEntries) {
         Long2ObjectOpenHashMap<LegacyStructureData> structures = dimensionFixEntry.structures;

         for(FileAccess<SavedDataNbt> structureDataFileAccess : dimensionFixEntry.structureFileAccess) {
            SavedDataNbt targetFile = structureDataFileAccess.getOnlyFile();
            Optional<Dynamic<Tag>> structureData = targetFile.read();
            if (!structureData.isEmpty()) {
               extractLegacyStructureData((Dynamic)structureData.get(), structures);
            }
         }

         upgradeProgress.addTotalFileFixOperations(structures.size());
      }

      upgradeProgress.setStatus(UpgradeProgress.Status.UPGRADING);

      for(DimensionFixEntry dimensionFixEntry : dimensionFixEntries) {
         ResourceKey<Level> dimensionKey = dimensionFixEntry.dimensionKey;
         ChunkNbt chunkNbt = dimensionFixEntry.chunkFileAccess.getOnlyFile();
         String chunkGeneratorType;
         if (dimensionKey == OVERWORLD_KEY) {
            String var10000;
            switch (levelData.get("generatorName").asString("buffet")) {
               case "flat" -> var10000 = "minecraft:flat";
               case "debug_all_block_states" -> var10000 = "minecraft:debug";
               default -> var10000 = "minecraft:noise";
            }

            chunkGeneratorType = var10000;
         } else {
            chunkGeneratorType = "minecraft:noise";
         }

         Optional<Identifier> generatorIdentifier = Optional.ofNullable(Identifier.tryParse(chunkGeneratorType));
         CompoundTag dataFixContext = ChunkMap.getChunkDataFixContextTag(dimensionKey, generatorIdentifier);
         storeLegacyStructureDataToChunks(dimensionFixEntry.structures, chunkNbt, dataFixContext, upgradeProgress);
      }

   }

   private static FileAccess<SavedDataNbt> getLegacyStructureData(final FileAccessProvider files, final String structureId) {
      return files.<SavedDataNbt>getFileAccess(FileResourceTypes.savedData(References.SAVED_DATA_STRUCTURE_FEATURE_INDICES, CompressedNbt.MissingSeverity.MINOR), FileRelation.DATA.forFile(structureId + ".dat"));
   }

   private static void extractLegacyStructureData(final Dynamic<Tag> structureData, final Long2ObjectMap<LegacyStructureData> extractedDataContainer) {
      OptionalDynamic<Tag> features = structureData.get("Features");
      Map<Dynamic<Tag>, Dynamic<Tag>> map = features.asMap(Function.identity(), Function.identity());

      for(Dynamic<Tag> value : map.values()) {
         long pos = ChunkPos.pack(value.get("ChunkX").asInt(0), value.get("ChunkZ").asInt(0));
         List<Dynamic<Tag>> childList = value.get("Children").asList(Function.identity());
         if (!childList.isEmpty()) {
            Optional var10000 = ((Dynamic)childList.getFirst()).get("id").asString().result();
            Map var10001 = LEGACY_TO_CURRENT_MAP;
            Objects.requireNonNull(var10001);
            Optional<String> id = var10000.map(var10001::get);
            if (id.isPresent()) {
               value = value.set("id", value.createString((String)id.get()));
            }
         }

         value.get("id").asString().ifSuccess((idx) -> {
            ((LegacyStructureData)extractedDataContainer.computeIfAbsent(pos, (l) -> new LegacyStructureData())).addStart(idx, value);

            for(int neighborX = ChunkPos.getX(pos) - 8; neighborX <= ChunkPos.getX(pos) + 8; ++neighborX) {
               for(int neighborZ = ChunkPos.getZ(pos) - 8; neighborZ <= ChunkPos.getZ(pos) + 8; ++neighborZ) {
                  ((LegacyStructureData)extractedDataContainer.computeIfAbsent(ChunkPos.pack(neighborX, neighborZ), (l) -> new LegacyStructureData())).addIndex(idx, pos);
               }
            }

         });
      }

   }

   private static void storeLegacyStructureDataToChunks(final Long2ObjectMap<LegacyStructureData> structures, final ChunkNbt chunksAccess, final CompoundTag dataFixContext, final UpgradeProgress upgradeProgress) {
      for(Long2ObjectMap.Entry<LegacyStructureData> entry : structures.long2ObjectEntrySet().stream().sorted(Comparator.comparingLong((entryx) -> ChunkPos.pack(ChunkPos.getRegionX(entryx.getLongKey()), ChunkPos.getRegionZ(entryx.getLongKey())))).toList()) {
         long pos = entry.getLongKey();
         LegacyStructureData legacyData = (LegacyStructureData)entry.getValue();
         chunksAccess.updateChunk(ChunkPos.unpack(pos), dataFixContext, (tag) -> {
            CompoundTag levelTag = tag.getCompoundOrEmpty("Level");
            CompoundTag structureTag = levelTag.getCompoundOrEmpty("Structures");
            CompoundTag startTag = structureTag.getCompoundOrEmpty("Starts");
            CompoundTag referencesTag = structureTag.getCompoundOrEmpty("References");
            legacyData.starts().forEach((id, value) -> startTag.put(id, (Tag)value.convert(NbtOps.INSTANCE).getValue()));
            legacyData.indexes().forEach((id, indexes) -> referencesTag.putLongArray(id, indexes.toLongArray()));
            structureTag.put("Starts", startTag);
            structureTag.put("References", referencesTag);
            levelTag.put("Structures", structureTag);
            tag.put("Level", levelTag);
            return tag;
         });
         upgradeProgress.incrementFinishedOperationsBy(1);
      }

   }

   static {
      OVERWORLD_KEY = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("overworld"));
      NETHER_KEY = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("the_nether"));
      END_KEY = ResourceKey.create(Registries.DIMENSION, Identifier.withDefaultNamespace("the_end"));
   }

   public static record LegacyStructureData(Map<String, Dynamic<?>> starts, Map<String, LongList> indexes) {
      public LegacyStructureData() {
         this(new HashMap(), new HashMap());
      }

      public LegacyStructureData {
         super();
      }

      public void addStart(final String id, final Dynamic<Tag> data) {
         this.starts.put(id, data);
      }

      public void addIndex(final String id, final long sourcePos) {
         ((LongList)this.indexes.computeIfAbsent(id, (l) -> new LongArrayList())).add(sourcePos);
      }
   }

   private static record DimensionFixEntry(ResourceKey<Level> dimensionKey, List<FileAccess<SavedDataNbt>> structureFileAccess, FileAccess<ChunkNbt> chunkFileAccess, Long2ObjectOpenHashMap<LegacyStructureData> structures) {
      private DimensionFixEntry {
         super();
      }
   }
}
