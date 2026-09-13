package net.minecraft.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;

public class ChunkProviderFlat implements IChunkProvider {
   private World field_73163_a;
   private Random field_73161_b;
   private final Block[] field_82700_c = new Block[256];
   private final byte[] field_82698_d = new byte[256];
   private final FlatGeneratorInfo field_82699_e;
   private final List field_82696_f = new ArrayList();
   private final boolean field_82697_g;
   private final boolean field_82702_h;
   private WorldGenLakes field_82703_i;
   private WorldGenLakes field_82701_j;

   public ChunkProviderFlat(World var1, long var2, boolean var4, String var5) {
      super();
      this.field_73163_a = var1;
      this.field_73161_b = new Random(var2);
      this.field_82699_e = FlatGeneratorInfo.func_82651_a(var5);
      if (var4) {
         Map var6 = this.field_82699_e.func_82644_b();
         if (var6.containsKey("village")) {
            Map var7 = (Map)var6.get("village");
            if (!var7.containsKey("size")) {
               var7.put("size", "1");
            }

            this.field_82696_f.add(new MapGenVillage(var7));
         }

         if (var6.containsKey("biome_1")) {
            this.field_82696_f.add(new MapGenScatteredFeature((Map)var6.get("biome_1")));
         }

         if (var6.containsKey("mineshaft")) {
            this.field_82696_f.add(new MapGenMineshaft((Map)var6.get("mineshaft")));
         }

         if (var6.containsKey("stronghold")) {
            this.field_82696_f.add(new MapGenStronghold((Map)var6.get("stronghold")));
         }
      }

      this.field_82697_g = this.field_82699_e.func_82644_b().containsKey("decoration");
      if (this.field_82699_e.func_82644_b().containsKey("lake")) {
         this.field_82703_i = new WorldGenLakes(Blocks.field_150355_j);
      }

      if (this.field_82699_e.func_82644_b().containsKey("lava_lake")) {
         this.field_82701_j = new WorldGenLakes(Blocks.field_150353_l);
      }

      this.field_82702_h = this.field_82699_e.func_82644_b().containsKey("dungeon");

      for(FlatLayerInfo var10 : this.field_82699_e.func_82650_c()) {
         for(int var8 = var10.func_82656_d(); var8 < var10.func_82656_d() + var10.func_82657_a(); ++var8) {
            this.field_82700_c[var8] = var10.func_151536_b();
            this.field_82698_d[var8] = (byte)var10.func_82658_c();
         }
      }
   }

   @Override
   public Chunk func_73158_c(int var1, int var2) {
      return this.func_73154_d(var1, var2);
   }

   @Override
   public Chunk func_73154_d(int var1, int var2) {
      Chunk var3 = new Chunk(this.field_73163_a, var1, var2);

      for(int var4 = 0; var4 < this.field_82700_c.length; ++var4) {
         Block var5 = this.field_82700_c[var4];
         if (var5 != null) {
            int var6 = var4 >> 4;
            ExtendedBlockStorage var7 = var3.func_76587_i()[var6];
            if (var7 == null) {
               var7 = new ExtendedBlockStorage(var4, !this.field_73163_a.field_73011_w.field_76576_e);
               var3.func_76587_i()[var6] = var7;
            }

            for(int var8 = 0; var8 < 16; ++var8) {
               for(int var9 = 0; var9 < 16; ++var9) {
                  var7.func_150818_a(var8, var4 & 15, var9, var5);
                  var7.func_76654_b(var8, var4 & 15, var9, this.field_82698_d[var4]);
               }
            }
         }
      }

      var3.func_76603_b();
      BiomeGenBase[] var10 = this.field_73163_a.func_72959_q().func_76933_b(null, var1 * 16, var2 * 16, 16, 16);
      byte[] var11 = var3.func_76605_m();

      for(int var12 = 0; var12 < var11.length; ++var12) {
         var11[var12] = (byte)var10[var12].field_76756_M;
      }

      for(MapGenBase var14 : this.field_82696_f) {
         var14.func_151539_a(this, this.field_73163_a, var1, var2, null);
      }

      var3.func_76603_b();
      return var3;
   }

   @Override
   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   @Override
   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BiomeGenBase var6 = this.field_73163_a.func_72807_a(var4 + 16, var5 + 16);
      boolean var7 = false;
      this.field_73161_b.setSeed(this.field_73163_a.func_72905_C());
      long var8 = this.field_73161_b.nextLong() / 2L * 2L + 1L;
      long var10 = this.field_73161_b.nextLong() / 2L * 2L + 1L;
      this.field_73161_b.setSeed((long)var2 * var8 + (long)var3 * var10 ^ this.field_73163_a.func_72905_C());

      for(MapGenStructure var13 : this.field_82696_f) {
         boolean var14 = var13.func_75051_a(this.field_73163_a, this.field_73161_b, var2, var3);
         if (var13 instanceof MapGenVillage) {
            var7 |= var14;
         }
      }

      if (this.field_82703_i != null && !var7 && this.field_73161_b.nextInt(4) == 0) {
         int var16 = var4 + this.field_73161_b.nextInt(16) + 8;
         int var19 = this.field_73161_b.nextInt(256);
         int var22 = var5 + this.field_73161_b.nextInt(16) + 8;
         this.field_82703_i.func_76484_a(this.field_73163_a, this.field_73161_b, var16, var19, var22);
      }

      if (this.field_82701_j != null && !var7 && this.field_73161_b.nextInt(8) == 0) {
         int var17 = var4 + this.field_73161_b.nextInt(16) + 8;
         int var20 = this.field_73161_b.nextInt(this.field_73161_b.nextInt(248) + 8);
         int var23 = var5 + this.field_73161_b.nextInt(16) + 8;
         if (var20 < 63 || this.field_73161_b.nextInt(10) == 0) {
            this.field_82701_j.func_76484_a(this.field_73163_a, this.field_73161_b, var17, var20, var23);
         }
      }

      if (this.field_82702_h) {
         for(int var18 = 0; var18 < 8; ++var18) {
            int var21 = var4 + this.field_73161_b.nextInt(16) + 8;
            int var24 = this.field_73161_b.nextInt(256);
            int var15 = var5 + this.field_73161_b.nextInt(16) + 8;
            new WorldGenDungeons().func_76484_a(this.field_73163_a, this.field_73161_b, var21, var24, var15);
         }
      }

      if (this.field_82697_g) {
         var6.func_76728_a(this.field_73163_a, this.field_73161_b, var4, var5);
      }
   }

   @Override
   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   @Override
   public void func_104112_b() {
   }

   @Override
   public boolean func_73156_b() {
      return false;
   }

   @Override
   public boolean func_73157_c() {
      return true;
   }

   @Override
   public String func_73148_d() {
      return "FlatLevelSource";
   }

   @Override
   public List func_73155_a(EnumCreatureType var1, int var2, int var3, int var4) {
      BiomeGenBase var5 = this.field_73163_a.func_72807_a(var2, var4);
      return var5.func_76747_a(var1);
   }

   @Override
   public ChunkPosition func_147416_a(World var1, String var2, int var3, int var4, int var5) {
      if ("Stronghold".equals(var2)) {
         for(MapGenStructure var7 : this.field_82696_f) {
            if (var7 instanceof MapGenStronghold) {
               return var7.func_151545_a(var1, var3, var4, var5);
            }
         }
      }

      return null;
   }

   @Override
   public int func_73152_e() {
      return 0;
   }

   @Override
   public void func_82695_e(int var1, int var2) {
      for(MapGenStructure var4 : this.field_82696_f) {
         var4.func_151539_a(this, this.field_73163_a, var1, var2, null);
      }
   }
}
