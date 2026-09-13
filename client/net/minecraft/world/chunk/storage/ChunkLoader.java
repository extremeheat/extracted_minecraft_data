package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
   public static ChunkLoader$AnvilConverterData func_76691_a(NBTTagCompound var0) {
      int var1 = var0.func_74762_e("xPos");
      int var2 = var0.func_74762_e("zPos");
      ChunkLoader$AnvilConverterData var3 = new ChunkLoader$AnvilConverterData(var1, var2);
      var3.field_76693_g = var0.func_74770_j("Blocks");
      var3.field_76692_f = new NibbleArrayReader(var0.func_74770_j("Data"), 7);
      var3.field_76695_e = new NibbleArrayReader(var0.func_74770_j("SkyLight"), 7);
      var3.field_76694_d = new NibbleArrayReader(var0.func_74770_j("BlockLight"), 7);
      var3.field_76697_c = var0.func_74770_j("HeightMap");
      var3.field_76696_b = var0.func_74767_n("TerrainPopulated");
      var3.field_76702_h = var0.func_150295_c("Entities", 10);
      var3.field_151564_i = var0.func_150295_c("TileEntities", 10);
      var3.field_151563_j = var0.func_150295_c("TileTicks", 10);

      try {
         var3.field_76698_a = var0.func_74763_f("LastUpdate");
      } catch (ClassCastException var5) {
         var3.field_76698_a = (long)var0.func_74762_e("LastUpdate");
      }

      return var3;
   }

   public static void func_76690_a(ChunkLoader$AnvilConverterData var0, NBTTagCompound var1, WorldChunkManager var2) {
      var1.func_74768_a("xPos", var0.field_76701_k);
      var1.func_74768_a("zPos", var0.field_76699_l);
      var1.func_74772_a("LastUpdate", var0.field_76698_a);
      int[] var3 = new int[var0.field_76697_c.length];

      for(int var4 = 0; var4 < var0.field_76697_c.length; ++var4) {
         var3[var4] = var0.field_76697_c[var4];
      }

      var1.func_74783_a("HeightMap", var3);
      var1.func_74757_a("TerrainPopulated", var0.field_76696_b);
      NBTTagList var16 = new NBTTagList();

      for(int var5 = 0; var5 < 8; ++var5) {
         boolean var6 = true;

         for(int var7 = 0; var7 < 16 && var6; ++var7) {
            for(int var8 = 0; var8 < 16 && var6; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  int var10 = var7 << 11 | var9 << 7 | var8 + (var5 << 4);
                  byte var11 = var0.field_76693_g[var10];
                  if (var11 != 0) {
                     var6 = false;
                     break;
                  }
               }
            }
         }

         if (!var6) {
            byte[] var19 = new byte[4096];
            NibbleArray var21 = new NibbleArray(var19.length, 4);
            NibbleArray var22 = new NibbleArray(var19.length, 4);
            NibbleArray var23 = new NibbleArray(var19.length, 4);

            for(int var24 = 0; var24 < 16; ++var24) {
               for(int var12 = 0; var12 < 16; ++var12) {
                  for(int var13 = 0; var13 < 16; ++var13) {
                     int var14 = var24 << 11 | var13 << 7 | var12 + (var5 << 4);
                     byte var15 = var0.field_76693_g[var14];
                     var19[var12 << 8 | var13 << 4 | var24] = (byte)(var15 & 255);
                     var21.func_76581_a(var24, var12, var13, var0.field_76692_f.func_76686_a(var24, var12 + (var5 << 4), var13));
                     var22.func_76581_a(var24, var12, var13, var0.field_76695_e.func_76686_a(var24, var12 + (var5 << 4), var13));
                     var23.func_76581_a(var24, var12, var13, var0.field_76694_d.func_76686_a(var24, var12 + (var5 << 4), var13));
                  }
               }
            }

            NBTTagCompound var25 = new NBTTagCompound();
            var25.func_74774_a("Y", (byte)(var5 & 0xFF));
            var25.func_74773_a("Blocks", var19);
            var25.func_74773_a("Data", var21.field_76585_a);
            var25.func_74773_a("SkyLight", var22.field_76585_a);
            var25.func_74773_a("BlockLight", var23.field_76585_a);
            var16.func_74742_a(var25);
         }
      }

      var1.func_74782_a("Sections", var16);
      byte[] var17 = new byte[256];

      for(int var18 = 0; var18 < 16; ++var18) {
         for(int var20 = 0; var20 < 16; ++var20) {
            var17[var20 << 4 | var18] = (byte)(var2.func_76935_a(var0.field_76701_k << 4 | var18, var0.field_76699_l << 4 | var20).field_76756_M & 0xFF);
         }
      }

      var1.func_74773_a("Biomes", var17);
      var1.func_74782_a("Entities", var0.field_76702_h);
      var1.func_74782_a("TileEntities", var0.field_151564_i);
      if (var0.field_151563_j != null) {
         var1.func_74782_a("TileTicks", var0.field_151563_j);
      }
   }
}
