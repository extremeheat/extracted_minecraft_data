package net.minecraft.world.level.chunk.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.biome.BiomeSource;
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

      int var7;
      int var8;
      for(int var5 = 0; var5 < 8; ++var5) {
         boolean var6 = true;

         for(var7 = 0; var7 < 16 && var6; ++var7) {
            for(var8 = 0; var8 < 16 && var6; ++var8) {
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
            byte[] var19 = new byte[4096];
            DataLayer var20 = new DataLayer();
            DataLayer var21 = new DataLayer();
            DataLayer var22 = new DataLayer();

            for(int var23 = 0; var23 < 16; ++var23) {
               for(int var12 = 0; var12 < 16; ++var12) {
                  for(int var13 = 0; var13 < 16; ++var13) {
                     int var14 = var23 << 11 | var13 << 7 | var12 + (var5 << 4);
                     byte var15 = var0.blocks[var14];
                     var19[var12 << 8 | var13 << 4 | var23] = (byte)(var15 & 255);
                     var20.set(var23, var12, var13, var0.data.get(var23, var12 + (var5 << 4), var13));
                     var21.set(var23, var12, var13, var0.skyLight.get(var23, var12 + (var5 << 4), var13));
                     var22.set(var23, var12, var13, var0.blockLight.get(var23, var12 + (var5 << 4), var13));
                  }
               }
            }

            CompoundTag var24 = new CompoundTag();
            var24.putByte("Y", (byte)(var5 & 255));
            var24.putByteArray("Blocks", var19);
            var24.putByteArray("Data", var20.getData());
            var24.putByteArray("SkyLight", var21.getData());
            var24.putByteArray("BlockLight", var22.getData());
            var16.add(var24);
         }
      }

      var1.put("Sections", var16);
      byte[] var17 = new byte[256];
      BlockPos.MutableBlockPos var18 = new BlockPos.MutableBlockPos();

      for(var7 = 0; var7 < 16; ++var7) {
         for(var8 = 0; var8 < 16; ++var8) {
            var18.set(var0.x << 4 | var7, 0, var0.z << 4 | var8);
            var17[var8 << 4 | var7] = (byte)(Registry.BIOME.getId(var2.getBiome(var18)) & 255);
         }
      }

      var1.putByteArray("Biomes", var17);
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
         super();
         this.x = var1;
         this.z = var2;
      }
   }
}
