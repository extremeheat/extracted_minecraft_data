package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.storage.LegacyTagFixer;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler implements LegacyTagFixer {
   public static final int LAST_MONOLYTH_STRUCTURE_DATA_VERSION = 1493;
   private static final Map<String, String> CURRENT_TO_LEGACY_MAP = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put("Village", "Village");
      var0.put("Mineshaft", "Mineshaft");
      var0.put("Mansion", "Mansion");
      var0.put("Igloo", "Temple");
      var0.put("Desert_Pyramid", "Temple");
      var0.put("Jungle_Pyramid", "Temple");
      var0.put("Swamp_Hut", "Temple");
      var0.put("Stronghold", "Stronghold");
      var0.put("Monument", "Monument");
      var0.put("Fortress", "Fortress");
      var0.put("EndCity", "EndCity");
   });
   private static final Map<String, String> LEGACY_TO_CURRENT_MAP = (Map)Util.make(Maps.newHashMap(), (var0) -> {
      var0.put("Iglu", "Igloo");
      var0.put("TeDP", "Desert_Pyramid");
      var0.put("TeJP", "Jungle_Pyramid");
      var0.put("TeSH", "Swamp_Hut");
   });
   private static final Set<String> OLD_STRUCTURE_REGISTRY_KEYS = Set.of("pillager_outpost", "mineshaft", "mansion", "jungle_pyramid", "desert_pyramid", "igloo", "ruined_portal", "shipwreck", "swamp_hut", "stronghold", "monument", "ocean_ruin", "fortress", "endcity", "buried_treasure", "village", "nether_fossil", "bastion_remnant");
   private final boolean hasLegacyData;
   private final Map<String, Long2ObjectMap<CompoundTag>> dataMap = Maps.newHashMap();
   private final Map<String, StructureFeatureIndexSavedData> indexMap = Maps.newHashMap();
   private final List<String> legacyKeys;
   private final List<String> currentKeys;
   private final DataFixer dataFixer;

   public LegacyStructureDataHandler(@Nullable DimensionDataStorage var1, List<String> var2, List<String> var3, DataFixer var4) {
      super();
      this.legacyKeys = var2;
      this.currentKeys = var3;
      this.dataFixer = var4;
      this.populateCaches(var1);
      boolean var5 = false;

      for(String var7 : this.currentKeys) {
         var5 |= this.dataMap.get(var7) != null;
      }

      this.hasLegacyData = var5;
   }

   public void markChunkDone(ChunkPos var1) {
      long var2 = var1.toLong();

      for(String var5 : this.legacyKeys) {
         StructureFeatureIndexSavedData var6 = (StructureFeatureIndexSavedData)this.indexMap.get(var5);
         if (var6 != null && var6.hasUnhandledIndex(var2)) {
            var6.removeIndex(var2);
         }
      }

   }

   public int targetDataVersion() {
      return 1493;
   }

   public CompoundTag applyFix(CompoundTag var1) {
      int var2 = NbtUtils.getDataVersion(var1);
      if (var2 < 1493) {
         var1 = DataFixTypes.CHUNK.update(this.dataFixer, var1, var2, 1493);
         if ((Boolean)var1.getCompound("Level").flatMap((var0) -> var0.getBoolean("hasLegacyStructureData")).orElse(false)) {
            var1 = this.updateFromLegacy(var1);
         }
      }

      return var1;
   }

   private CompoundTag updateFromLegacy(CompoundTag var1) {
      CompoundTag var2 = var1.getCompoundOrEmpty("Level");
      ChunkPos var3 = new ChunkPos(var2.getIntOr("xPos", 0), var2.getIntOr("zPos", 0));
      if (this.isUnhandledStructureStart(var3.x, var3.z)) {
         var1 = this.updateStructureStart(var1, var3);
      }

      CompoundTag var4 = var2.getCompoundOrEmpty("Structures");
      CompoundTag var5 = var4.getCompoundOrEmpty("References");

      for(String var7 : this.currentKeys) {
         boolean var8 = OLD_STRUCTURE_REGISTRY_KEYS.contains(var7.toLowerCase(Locale.ROOT));
         if (!var5.getLongArray(var7).isPresent() && var8) {
            boolean var9 = true;
            LongArrayList var10 = new LongArrayList();

            for(int var11 = var3.x - 8; var11 <= var3.x + 8; ++var11) {
               for(int var12 = var3.z - 8; var12 <= var3.z + 8; ++var12) {
                  if (this.hasLegacyStart(var11, var12, var7)) {
                     var10.add(ChunkPos.asLong(var11, var12));
                  }
               }
            }

            var5.putLongArray(var7, var10.toLongArray());
         }
      }

      var4.put("References", var5);
      var2.put("Structures", var4);
      var1.put("Level", var2);
      return var1;
   }

   private boolean hasLegacyStart(int var1, int var2, String var3) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         return this.dataMap.get(var3) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(var3))).hasStartIndex(ChunkPos.asLong(var1, var2));
      }
   }

   private boolean isUnhandledStructureStart(int var1, int var2) {
      if (!this.hasLegacyData) {
         return false;
      } else {
         for(String var4 : this.currentKeys) {
            if (this.dataMap.get(var4) != null && ((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(var4))).hasUnhandledIndex(ChunkPos.asLong(var1, var2))) {
               return true;
            }
         }

         return false;
      }
   }

   private CompoundTag updateStructureStart(CompoundTag var1, ChunkPos var2) {
      CompoundTag var3 = var1.getCompoundOrEmpty("Level");
      CompoundTag var4 = var3.getCompoundOrEmpty("Structures");
      CompoundTag var5 = var4.getCompoundOrEmpty("Starts");

      for(String var7 : this.currentKeys) {
         Long2ObjectMap var8 = (Long2ObjectMap)this.dataMap.get(var7);
         if (var8 != null) {
            long var9 = var2.toLong();
            if (((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(var7))).hasUnhandledIndex(var9)) {
               CompoundTag var11 = (CompoundTag)var8.get(var9);
               if (var11 != null) {
                  var5.put(var7, var11);
               }
            }
         }
      }

      var4.put("Starts", var5);
      var3.put("Structures", var4);
      var1.put("Level", var3);
      return var1;
   }

   private void populateCaches(@Nullable DimensionDataStorage var1) {
      if (var1 != null) {
         for(String var3 : this.legacyKeys) {
            CompoundTag var4 = new CompoundTag();

            try {
               var4 = var1.readTagFromDisk(var3, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES, 1493).getCompoundOrEmpty("data").getCompoundOrEmpty("Features");
               if (var4.isEmpty()) {
                  continue;
               }
            } catch (IOException var8) {
            }

            var4.forEach((var1x, var2) -> {
               if (var2 instanceof CompoundTag var3) {
                  long var4 = ChunkPos.asLong(var3.getIntOr("ChunkX", 0), var3.getIntOr("ChunkZ", 0));
                  ListTag var6 = var3.getListOrEmpty("Children");
                  if (!var6.isEmpty()) {
                     Optional var7 = var6.getCompound(0).flatMap((var0) -> var0.getString("id"));
                     Map var10001 = LEGACY_TO_CURRENT_MAP;
                     Objects.requireNonNull(var10001);
                     var7.map(var10001::get).ifPresent((var1) -> var3.putString("id", var1));
                  }

                  var3.getString("id").ifPresent((var4x) -> ((Long2ObjectMap)this.dataMap.computeIfAbsent(var4x, (var0) -> new Long2ObjectOpenHashMap())).put(var4, var3));
               }
            });
            String var5 = var3 + "_index";
            StructureFeatureIndexSavedData var6 = (StructureFeatureIndexSavedData)var1.computeIfAbsent(StructureFeatureIndexSavedData.type(var5));
            if (var6.getAll().isEmpty()) {
               StructureFeatureIndexSavedData var7 = new StructureFeatureIndexSavedData();
               this.indexMap.put(var3, var7);
               var4.forEach((var1x, var2) -> {
                  if (var2 instanceof CompoundTag var3) {
                     var7.addIndex(ChunkPos.asLong(var3.getIntOr("ChunkX", 0), var3.getIntOr("ChunkZ", 0)));
                  }

               });
            } else {
               this.indexMap.put(var3, var6);
            }
         }

      }
   }

   public static LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> var0, @Nullable DimensionDataStorage var1, DataFixer var2) {
      if (var0 == Level.OVERWORLD) {
         return new LegacyStructureDataHandler(var1, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"), var2);
      } else if (var0 == Level.NETHER) {
         ImmutableList var4 = ImmutableList.of("Fortress");
         return new LegacyStructureDataHandler(var1, var4, var4, var2);
      } else if (var0 == Level.END) {
         ImmutableList var3 = ImmutableList.of("EndCity");
         return new LegacyStructureDataHandler(var1, var3, var3, var2);
      } else {
         throw new RuntimeException(String.format(Locale.ROOT, "Unknown dimension type : %s", var0));
      }
   }
}
