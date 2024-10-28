package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class LegacyStructureDataHandler {
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

   public LegacyStructureDataHandler(@Nullable DimensionDataStorage var1, List<String> var2, List<String> var3) {
      super();
      this.legacyKeys = var2;
      this.currentKeys = var3;
      this.populateCaches(var1);
      boolean var4 = false;

      String var6;
      for(Iterator var5 = this.currentKeys.iterator(); var5.hasNext(); var4 |= this.dataMap.get(var6) != null) {
         var6 = (String)var5.next();
      }

      this.hasLegacyData = var4;
   }

   public void removeIndex(long var1) {
      Iterator var3 = this.legacyKeys.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         StructureFeatureIndexSavedData var5 = (StructureFeatureIndexSavedData)this.indexMap.get(var4);
         if (var5 != null && var5.hasUnhandledIndex(var1)) {
            var5.removeIndex(var1);
            var5.setDirty();
         }
      }

   }

   public CompoundTag updateFromLegacy(CompoundTag var1) {
      CompoundTag var2 = var1.getCompound("Level");
      ChunkPos var3 = new ChunkPos(var2.getInt("xPos"), var2.getInt("zPos"));
      if (this.isUnhandledStructureStart(var3.x, var3.z)) {
         var1 = this.updateStructureStart(var1, var3);
      }

      CompoundTag var4 = var2.getCompound("Structures");
      CompoundTag var5 = var4.getCompound("References");
      Iterator var6 = this.currentKeys.iterator();

      while(true) {
         String var7;
         boolean var8;
         do {
            do {
               if (!var6.hasNext()) {
                  var4.put("References", var5);
                  var2.put("Structures", var4);
                  var1.put("Level", var2);
                  return var1;
               }

               var7 = (String)var6.next();
               var8 = OLD_STRUCTURE_REGISTRY_KEYS.contains(var7.toLowerCase(Locale.ROOT));
            } while(var5.contains(var7, 12));
         } while(!var8);

         boolean var9 = true;
         LongArrayList var10 = new LongArrayList();

         for(int var11 = var3.x - 8; var11 <= var3.x + 8; ++var11) {
            for(int var12 = var3.z - 8; var12 <= var3.z + 8; ++var12) {
               if (this.hasLegacyStart(var11, var12, var7)) {
                  var10.add(ChunkPos.asLong(var11, var12));
               }
            }
         }

         var5.putLongArray(var7, (List)var10);
      }
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
         Iterator var3 = this.currentKeys.iterator();

         String var4;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (String)var3.next();
         } while(this.dataMap.get(var4) == null || !((StructureFeatureIndexSavedData)this.indexMap.get(CURRENT_TO_LEGACY_MAP.get(var4))).hasUnhandledIndex(ChunkPos.asLong(var1, var2)));

         return true;
      }
   }

   private CompoundTag updateStructureStart(CompoundTag var1, ChunkPos var2) {
      CompoundTag var3 = var1.getCompound("Level");
      CompoundTag var4 = var3.getCompound("Structures");
      CompoundTag var5 = var4.getCompound("Starts");
      Iterator var6 = this.currentKeys.iterator();

      while(var6.hasNext()) {
         String var7 = (String)var6.next();
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
         Iterator var2 = this.legacyKeys.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            CompoundTag var4 = new CompoundTag();

            try {
               var4 = var1.readTagFromDisk(var3, DataFixTypes.SAVED_DATA_STRUCTURE_FEATURE_INDICES, 1493).getCompound("data").getCompound("Features");
               if (var4.isEmpty()) {
                  continue;
               }
            } catch (IOException var13) {
            }

            Iterator var5 = var4.getAllKeys().iterator();

            while(var5.hasNext()) {
               String var6 = (String)var5.next();
               CompoundTag var7 = var4.getCompound(var6);
               long var8 = ChunkPos.asLong(var7.getInt("ChunkX"), var7.getInt("ChunkZ"));
               ListTag var10 = var7.getList("Children", 10);
               String var11;
               if (!var10.isEmpty()) {
                  var11 = var10.getCompound(0).getString("id");
                  String var12 = (String)LEGACY_TO_CURRENT_MAP.get(var11);
                  if (var12 != null) {
                     var7.putString("id", var12);
                  }
               }

               var11 = var7.getString("id");
               ((Long2ObjectMap)this.dataMap.computeIfAbsent(var11, (var0) -> {
                  return new Long2ObjectOpenHashMap();
               })).put(var8, var7);
            }

            String var14 = var3 + "_index";
            StructureFeatureIndexSavedData var15 = (StructureFeatureIndexSavedData)var1.computeIfAbsent(StructureFeatureIndexSavedData.factory(), var14);
            if (!var15.getAll().isEmpty()) {
               this.indexMap.put(var3, var15);
            } else {
               StructureFeatureIndexSavedData var16 = new StructureFeatureIndexSavedData();
               this.indexMap.put(var3, var16);
               Iterator var17 = var4.getAllKeys().iterator();

               while(var17.hasNext()) {
                  String var9 = (String)var17.next();
                  CompoundTag var18 = var4.getCompound(var9);
                  var16.addIndex(ChunkPos.asLong(var18.getInt("ChunkX"), var18.getInt("ChunkZ")));
               }

               var16.setDirty();
            }
         }

      }
   }

   public static LegacyStructureDataHandler getLegacyStructureHandler(ResourceKey<Level> var0, @Nullable DimensionDataStorage var1) {
      if (var0 == Level.OVERWORLD) {
         return new LegacyStructureDataHandler(var1, ImmutableList.of("Monument", "Stronghold", "Village", "Mineshaft", "Temple", "Mansion"), ImmutableList.of("Village", "Mineshaft", "Mansion", "Igloo", "Desert_Pyramid", "Jungle_Pyramid", "Swamp_Hut", "Stronghold", "Monument"));
      } else {
         ImmutableList var2;
         if (var0 == Level.NETHER) {
            var2 = ImmutableList.of("Fortress");
            return new LegacyStructureDataHandler(var1, var2, var2);
         } else if (var0 == Level.END) {
            var2 = ImmutableList.of("EndCity");
            return new LegacyStructureDataHandler(var1, var2, var2);
         } else {
            throw new RuntimeException(String.format(Locale.ROOT, "Unknown dimension type : %s", var0));
         }
      }
   }
}
