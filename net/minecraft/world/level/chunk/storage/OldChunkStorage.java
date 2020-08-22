package net.minecraft.world.level.chunk.storage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.OldDataLayer;

public class OldChunkStorage {
   public static OldChunkStorage.OldLevelChunk load(CompoundTag var0) {
      int var1 = var0.getInt("xPos");
      int var2 = var0.getInt("zPos");
      OldChunkStorage.OldLevelChunk var3 = new OldChunkStorage.OldLevelChunk(var1, var2);
      var3.blocks = var0.getByteArray("Blocks");
      var3.data = new OldDataLayer(var0.getByteArray("Data"), 7);
      var3.skyLight = new OldDataLayer(var0.getByteArray("SkyLight"), 7);
      var3.blockLight = new OldDataLayer(var0.getByteArray("BlockLight"), 7);
      var3.heightmap = var0.getByteArray("HeightMap");
      var3.terrainPopulated = var0.getBoolean("TerrainPopulated");
      var3.entities = var0.getList("Entities", 10);
      var3.blockEntities = var0.getList("TileEntities", 10);
      var3.blockTicks = var0.getList("TileTicks", 10);

      try {
         var3.lastUpdated = var0.getLong("LastUpdate");
      } catch (ClassCastException var5) {
         var3.lastUpdated = (long)var0.getInt("LastUpdate");
      }

      return var3;
   }

   public static void convertToAnvilFormat(OldChunkStorage.OldLevelChunk var0, CompoundTag var1, BiomeSource var2) {
      var1.putInt("xPos", var0.x);
      var1.putInt("zPos", var0.z);
      var1.putLong("LastUpdate", var0.lastUpdated);
      int[] var3 = new int[var0.heightmap.length];

      for(int var4 = 0; var4 < var0.heightmap.length; ++var4) {
         var3[var4] = var0.heightmap[var4];
      }

      var1.putIntArray("HeightMap", var3);
      var1.putBoolean("TerrainPopulated", var0.terrainPopulated);
      ListTag var16 = new ListTag();

      for(int var5 = 0; var5 < 8; ++var5) {
         boolean var6 = true;

         for(int var7 = 0; var7 < 16 && var6; ++var7) {
            for(int var8 = 0; var8 < 16 && var6; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  int var10 = var7 << 11 | var9 << 7 | var8 + (var5 << 4);
                  byte var11 = var0.blocks[var10];
                  if (var11 != 0) {
                     var6 = false;
                     break;
                  }
               }
            }
         }

         if (!var6) {
            byte[] var17 = new byte[4096];
            DataLayer var18 = new DataLayer();
            DataLayer var19 = new DataLayer();
            DataLayer var20 = new DataLayer();

            for(int var21 = 0; var21 < 16; ++var21) {
               for(int var12 = 0; var12 < 16; ++var12) {
                  for(int var13 = 0; var13 < 16; ++var13) {
                     int var14 = var21 << 11 | var13 << 7 | var12 + (var5 << 4);
                     byte var15 = var0.blocks[var14];
                     var17[var12 << 8 | var13 << 4 | var21] = (byte)(var15 & 255);
                     var18.set(var21, var12, var13, var0.data.get(var21, var12 + (var5 << 4), var13));
                     var19.set(var21, var12, var13, var0.skyLight.get(var21, var12 + (var5 << 4), var13));
                     var20.set(var21, var12, var13, var0.blockLight.get(var21, var12 + (var5 << 4), var13));
                  }
               }
            }

            CompoundTag var22 = new CompoundTag();
            var22.putByte("Y", (byte)(var5 & 255));
            var22.putByteArray("Blocks", var17);
            var22.putByteArray("Data", var18.getData());
            var22.putByteArray("SkyLight", var19.getData());
            var22.putByteArray("BlockLight", var20.getData());
            var16.add(var22);
         }
      }

      var1.put("Sections", var16);
      var1.putIntArray("Biomes", (new ChunkBiomeContainer(new ChunkPos(var0.x, var0.z), var2)).writeBiomes());
      var1.put("Entities", var0.entities);
      var1.put("TileEntities", var0.blockEntities);
      if (var0.blockTicks != null) {
         var1.put("TileTicks", var0.blockTicks);
      }

      var1.putBoolean("convertedFromAlphaFormat", true);
   }

   public static class OldLevelChunk {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public OldDataLayer blockLight;
      public OldDataLayer skyLight;
      public OldDataLayer data;
      public byte[] blocks;
      public ListTag entities;
      public ListTag blockEntities;
      public ListTag blockTicks;
      public final int x;
      public final int z;

      public OldLevelChunk(int var1, int var2) {
         this.x = var1;
         this.z = var2;
      }
   }
}
