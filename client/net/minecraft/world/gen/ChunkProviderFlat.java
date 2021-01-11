package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderFlat implements IChunkProvider {
   private World field_73163_a;
   private Random field_73161_b;
   private final IBlockState[] field_82700_c = new IBlockState[256];
   private final FlatGeneratorInfo field_82699_e;
   private final List<MapGenStructure> field_82696_f = Lists.newArrayList();
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

         if (var6.containsKey("oceanmonument")) {
            this.field_82696_f.add(new StructureOceanMonument((Map)var6.get("oceanmonument")));
         }
      }

      if (this.field_82699_e.func_82644_b().containsKey("lake")) {
         this.field_82703_i = new WorldGenLakes(Blocks.field_150355_j);
      }

      if (this.field_82699_e.func_82644_b().containsKey("lava_lake")) {
         this.field_82701_j = new WorldGenLakes(Blocks.field_150353_l);
      }

      this.field_82702_h = this.field_82699_e.func_82644_b().containsKey("dungeon");
      int var13 = 0;
      int var14 = 0;
      boolean var8 = true;
      Iterator var9 = this.field_82699_e.func_82650_c().iterator();

      while(var9.hasNext()) {
         FlatLayerInfo var10 = (FlatLayerInfo)var9.next();

         for(int var11 = var10.func_82656_d(); var11 < var10.func_82656_d() + var10.func_82657_a(); ++var11) {
            IBlockState var12 = var10.func_175900_c();
            if (var12.func_177230_c() != Blocks.field_150350_a) {
               var8 = false;
               this.field_82700_c[var11] = var12;
            }
         }

         if (var10.func_175900_c().func_177230_c() == Blocks.field_150350_a) {
            var14 += var10.func_82657_a();
         } else {
            var13 += var10.func_82657_a() + var14;
            var14 = 0;
         }
      }

      var1.func_181544_b(var13);
      this.field_82697_g = var8 ? false : this.field_82699_e.func_82644_b().containsKey("decoration");
   }

   public Chunk func_73154_d(int var1, int var2) {
      ChunkPrimer var3 = new ChunkPrimer();

      int var7;
      for(int var4 = 0; var4 < this.field_82700_c.length; ++var4) {
         IBlockState var5 = this.field_82700_c[var4];
         if (var5 != null) {
            for(int var6 = 0; var6 < 16; ++var6) {
               for(var7 = 0; var7 < 16; ++var7) {
                  var3.func_177855_a(var6, var4, var7, var5);
               }
            }
         }
      }

      Iterator var8 = this.field_82696_f.iterator();

      while(var8.hasNext()) {
         MapGenBase var10 = (MapGenBase)var8.next();
         var10.func_175792_a(this, this.field_73163_a, var1, var2, var3);
      }

      Chunk var9 = new Chunk(this.field_73163_a, var3, var1, var2);
      BiomeGenBase[] var11 = this.field_73163_a.func_72959_q().func_76933_b((BiomeGenBase[])null, var1 * 16, var2 * 16, 16, 16);
      byte[] var12 = var9.func_76605_m();

      for(var7 = 0; var7 < var12.length; ++var7) {
         var12[var7] = (byte)var11[var7].field_76756_M;
      }

      var9.func_76603_b();
      return var9;
   }

   public boolean func_73149_a(int var1, int var2) {
      return true;
   }

   public void func_73153_a(IChunkProvider var1, int var2, int var3) {
      int var4 = var2 * 16;
      int var5 = var3 * 16;
      BlockPos var6 = new BlockPos(var4, 0, var5);
      BiomeGenBase var7 = this.field_73163_a.func_180494_b(new BlockPos(var4 + 16, 0, var5 + 16));
      boolean var8 = false;
      this.field_73161_b.setSeed(this.field_73163_a.func_72905_C());
      long var9 = this.field_73161_b.nextLong() / 2L * 2L + 1L;
      long var11 = this.field_73161_b.nextLong() / 2L * 2L + 1L;
      this.field_73161_b.setSeed((long)var2 * var9 + (long)var3 * var11 ^ this.field_73163_a.func_72905_C());
      ChunkCoordIntPair var13 = new ChunkCoordIntPair(var2, var3);
      Iterator var14 = this.field_82696_f.iterator();

      while(var14.hasNext()) {
         MapGenStructure var15 = (MapGenStructure)var14.next();
         boolean var16 = var15.func_175794_a(this.field_73163_a, this.field_73161_b, var13);
         if (var15 instanceof MapGenVillage) {
            var8 |= var16;
         }
      }

      if (this.field_82703_i != null && !var8 && this.field_73161_b.nextInt(4) == 0) {
         this.field_82703_i.func_180709_b(this.field_73163_a, this.field_73161_b, var6.func_177982_a(this.field_73161_b.nextInt(16) + 8, this.field_73161_b.nextInt(256), this.field_73161_b.nextInt(16) + 8));
      }

      if (this.field_82701_j != null && !var8 && this.field_73161_b.nextInt(8) == 0) {
         BlockPos var17 = var6.func_177982_a(this.field_73161_b.nextInt(16) + 8, this.field_73161_b.nextInt(this.field_73161_b.nextInt(248) + 8), this.field_73161_b.nextInt(16) + 8);
         if (var17.func_177956_o() < this.field_73163_a.func_181545_F() || this.field_73161_b.nextInt(10) == 0) {
            this.field_82701_j.func_180709_b(this.field_73163_a, this.field_73161_b, var17);
         }
      }

      if (this.field_82702_h) {
         for(int var18 = 0; var18 < 8; ++var18) {
            (new WorldGenDungeons()).func_180709_b(this.field_73163_a, this.field_73161_b, var6.func_177982_a(this.field_73161_b.nextInt(16) + 8, this.field_73161_b.nextInt(256), this.field_73161_b.nextInt(16) + 8));
         }
      }

      if (this.field_82697_g) {
         var7.func_180624_a(this.field_73163_a, this.field_73161_b, var6);
      }

   }

   public boolean func_177460_a(IChunkProvider var1, Chunk var2, int var3, int var4) {
      return false;
   }

   public boolean func_73151_a(boolean var1, IProgressUpdate var2) {
      return true;
   }

   public void func_104112_b() {
   }

   public boolean func_73156_b() {
      return false;
   }

   public boolean func_73157_c() {
      return true;
   }

   public String func_73148_d() {
      return "FlatLevelSource";
   }

   public List<BiomeGenBase.SpawnListEntry> func_177458_a(EnumCreatureType var1, BlockPos var2) {
      BiomeGenBase var3 = this.field_73163_a.func_180494_b(var2);
      return var3.func_76747_a(var1);
   }

   public BlockPos func_180513_a(World var1, String var2, BlockPos var3) {
      if ("Stronghold".equals(var2)) {
         Iterator var4 = this.field_82696_f.iterator();

         while(var4.hasNext()) {
            MapGenStructure var5 = (MapGenStructure)var4.next();
            if (var5 instanceof MapGenStronghold) {
               return var5.func_180706_b(var1, var3);
            }
         }
      }

      return null;
   }

   public int func_73152_e() {
      return 0;
   }

   public void func_180514_a(Chunk var1, int var2, int var3) {
      Iterator var4 = this.field_82696_f.iterator();

      while(var4.hasNext()) {
         MapGenStructure var5 = (MapGenStructure)var4.next();
         var5.func_175792_a(this, this.field_73163_a, var2, var3, (ChunkPrimer)null);
      }

   }

   public Chunk func_177459_a(BlockPos var1) {
      return this.func_73154_d(var1.func_177958_n() >> 4, var1.func_177952_p() >> 4);
   }
}
