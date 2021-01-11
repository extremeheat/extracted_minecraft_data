package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {
   private static final WorldGenTaiga1 field_150639_aC = new WorldGenTaiga1();
   private static final WorldGenTaiga2 field_150640_aD = new WorldGenTaiga2(false);
   private static final WorldGenMegaPineTree field_150641_aE = new WorldGenMegaPineTree(false, false);
   private static final WorldGenMegaPineTree field_150642_aF = new WorldGenMegaPineTree(false, true);
   private static final WorldGenBlockBlob field_150643_aG;
   private int field_150644_aH;

   public BiomeGenTaiga(int var1, int var2) {
      super(var1);
      this.field_150644_aH = var2;
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 8, 4, 4));
      this.field_76760_I.field_76832_z = 10;
      if (var2 != 1 && var2 != 2) {
         this.field_76760_I.field_76803_B = 1;
         this.field_76760_I.field_76798_D = 1;
      } else {
         this.field_76760_I.field_76803_B = 7;
         this.field_76760_I.field_76804_C = 1;
         this.field_76760_I.field_76798_D = 3;
      }

   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      if ((this.field_150644_aH == 1 || this.field_150644_aH == 2) && var1.nextInt(3) == 0) {
         return this.field_150644_aH != 2 && var1.nextInt(13) != 0 ? field_150641_aE : field_150642_aF;
      } else {
         return (WorldGenAbstractTree)(var1.nextInt(3) == 0 ? field_150639_aC : field_150640_aD);
      }
   }

   public WorldGenerator func_76730_b(Random var1) {
      return var1.nextInt(5) > 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      int var4;
      int var5;
      int var6;
      int var7;
      if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
         var4 = var2.nextInt(3);

         for(var5 = 0; var5 < var4; ++var5) {
            var6 = var2.nextInt(16) + 8;
            var7 = var2.nextInt(16) + 8;
            BlockPos var8 = var1.func_175645_m(var3.func_177982_a(var6, 0, var7));
            field_150643_aG.func_180709_b(var1, var2, var8);
         }
      }

      field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.FERN);

      for(var4 = 0; var4 < 7; ++var4) {
         var5 = var2.nextInt(16) + 8;
         var6 = var2.nextInt(16) + 8;
         var7 = var2.nextInt(var1.func_175645_m(var3.func_177982_a(var5, 0, var6)).func_177956_o() + 32);
         field_180280_ag.func_180709_b(var1, var2, var3.func_177982_a(var5, var7, var6));
      }

      super.func_180624_a(var1, var2, var3);
   }

   public void func_180622_a(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
         this.field_76752_A = Blocks.field_150349_c.func_176223_P();
         this.field_76753_B = Blocks.field_150346_d.func_176223_P();
         if (var6 > 1.75D) {
            this.field_76752_A = Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.COARSE_DIRT);
         } else if (var6 > -0.95D) {
            this.field_76752_A = Blocks.field_150346_d.func_176223_P().func_177226_a(BlockDirt.field_176386_a, BlockDirt.DirtType.PODZOL);
         }
      }

      this.func_180628_b(var1, var2, var3, var4, var5, var6);
   }

   protected BiomeGenBase func_180277_d(int var1) {
      return this.field_76756_M == BiomeGenBase.field_150578_U.field_76756_M ? (new BiomeGenTaiga(var1, 2)).func_150557_a(5858897, true).func_76735_a("Mega Spruce Taiga").func_76733_a(5159473).func_76732_a(0.25F, 0.8F).func_150570_a(new BiomeGenBase.Height(this.field_76748_D, this.field_76749_E)) : super.func_180277_d(var1);
   }

   static {
      field_150643_aG = new WorldGenBlockBlob(Blocks.field_150341_Y, 0);
   }
}
