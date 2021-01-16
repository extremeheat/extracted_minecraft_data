package net.minecraft.world.level.chunk.storage;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
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

   public static void convertToAnvilFormat(RegistryAccess.RegistryHolder var0, OldChunkStorage.OldLevelChunk var1, CompoundTag var2, BiomeSource var3) {
      var2.putInt("xPos", var1.x);
      var2.putInt("zPos", var1.z);
      var2.putLong("LastUpdate", var1.lastUpdated);
      int[] var4 = new int[var1.heightmap.length];

      for(int var5 = 0; var5 < var1.heightmap.length; ++var5) {
         var4[var5] = var1.heightmap[var5];
      }

      var2.putIntArray("HeightMap", var4);
      var2.putBoolean("TerrainPopulated", var1.terrainPopulated);
      ListTag var17 = new ListTag();

      for(int var6 = 0; var6 < 8; ++var6) {
         boolean var7 = true;

         for(int var8 = 0; var8 < 16 && var7; ++var8) {
            for(int var9 = 0; var9 < 16 && var7; ++var9) {
               for(int var10 = 0; var10 < 16; ++var10) {
                  int var11 = var8 << 11 | var10 << 7 | var9 + (var6 << 4);
                  byte var12 = var1.blocks[var11];
                  if (var12 != 0) {
                     var7 = false;
                     break;
                  }
               }
            }
         }

         if (!var7) {
            byte[] var18 = new byte[4096];
            DataLayer var19 = new DataLayer();
            DataLayer var20 = new DataLayer();
            DataLayer var21 = new DataLayer();

            for(int var22 = 0; var22 < 16; ++var22) {
               for(int var13 = 0; var13 < 16; ++var13) {
                  for(int var14 = 0; var14 < 16; ++var14) {
                     int var15 = var22 << 11 | var14 << 7 | var13 + (var6 << 4);
                     byte var16 = var1.blocks[var15];
                     var18[var13 << 8 | var14 << 4 | var22] = (byte)(var16 & 255);
                     var19.set(var22, var13, var14, var1.data.get(var22, var13 + (var6 << 4), var14));
                     var20.set(var22, var13, var14, var1.skyLight.get(var22, var13 + (var6 << 4), var14));
                     var21.set(var22, var13, var14, var1.blockLight.get(var22, var13 + (var6 << 4), var14));
                  }
               }
            }

            CompoundTag var23 = new CompoundTag();
            var23.putByte("Y", (byte)(var6 & 255));
            var23.putByteArray("Blocks", var18);
            var23.putByteArray("Data", var19.getData());
            var23.putByteArray("SkyLight", var20.getData());
            var23.putByteArray("BlockLight", var21.getData());
            var17.add(var23);
         }
      }

      var2.put("Sections", var17);
      var2.putIntArray("Biomes", (new ChunkBiomeContainer(var0.registryOrThrow(Registry.BIOME_REGISTRY), new ChunkPos(var1.x, var1.z), var3)).writeBiomes());
      var2.put("Entities", var1.entities);
      var2.put("TileEntities", var1.blockEntities);
      if (var1.blockTicks != null) {
         var2.put("TileTicks", var1.blockTicks);
      }

      var2.putBoolean("convertedFromAlphaFormat", true);
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
