package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
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
   private static final WorldGenBlockBlob field_150643_aG = new WorldGenBlockBlob(Blocks.field_150341_Y, 0);
   private int field_150644_aH;

   public BiomeGenTaiga(int var1, int var2) {
      super(var1);
      this.field_150644_aH = var2;
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityWolf.class, 8, 4, 4));
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

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      if ((this.field_150644_aH == 1 || this.field_150644_aH == 2) && var1.nextInt(3) == 0) {
         return this.field_150644_aH != 2 && var1.nextInt(13) != 0 ? field_150641_aE : field_150642_aF;
      } else {
         return (WorldGenAbstractTree)(var1.nextInt(3) == 0 ? field_150639_aC : field_150640_aD);
      }
   }

   @Override
   public WorldGenerator func_76730_b(Random var1) {
      return var1.nextInt(5) > 0 ? new WorldGenTallGrass(Blocks.field_150329_H, 2) : new WorldGenTallGrass(Blocks.field_150329_H, 1);
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
         int var5 = var2.nextInt(3);

         for(int var6 = 0; var6 < var5; ++var6) {
            int var7 = var3 + var2.nextInt(16) + 8;
            int var8 = var4 + var2.nextInt(16) + 8;
            int var9 = var1.func_72976_f(var7, var8);
            field_150643_aG.func_76484_a(var1, var2, var7, var9, var8);
         }
      }

      field_150610_ae.func_150548_a(3);

      for(int var10 = 0; var10 < 7; ++var10) {
         int var11 = var3 + var2.nextInt(16) + 8;
         int var12 = var4 + var2.nextInt(16) + 8;
         int var13 = var2.nextInt(var1.func_72976_f(var11, var12) + 32);
         field_150610_ae.func_76484_a(var1, var2, var11, var13, var12);
      }

      super.func_76728_a(var1, var2, var3, var4);
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      if (this.field_150644_aH == 1 || this.field_150644_aH == 2) {
         this.field_76752_A = Blocks.field_150349_c;
         this.field_150604_aj = 0;
         this.field_76753_B = Blocks.field_150346_d;
         if (var7 > 1.75) {
            this.field_76752_A = Blocks.field_150346_d;
            this.field_150604_aj = 1;
         } else if (var7 > -0.95) {
            this.field_76752_A = Blocks.field_150346_d;
            this.field_150604_aj = 2;
         }
      }

      this.func_150560_b(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      return this.field_76756_M == BiomeGenBase.field_150578_U.field_76756_M
         ? new BiomeGenTaiga(this.field_76756_M + 128, 2)
            .func_150557_a(5858897, true)
            .func_76735_a("Mega Spruce Taiga")
            .func_76733_a(5159473)
            .func_76732_a(0.25F, 0.8F)
            .func_150570_a(new BiomeGenBase$Height(this.field_76748_D, this.field_76749_E))
         : super.func_150566_k();
   }
}
