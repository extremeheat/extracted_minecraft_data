package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {
   private int field_150632_aF;
   protected static final WorldGenForest field_150629_aC = new WorldGenForest(false, true);
   protected static final WorldGenForest field_150630_aD = new WorldGenForest(false, false);
   protected static final WorldGenCanopyTree field_150631_aE = new WorldGenCanopyTree(false);

   public BiomeGenForest(int var1, int var2) {
      super(var1);
      this.field_150632_aF = var2;
      this.field_76760_I.field_76832_z = 10;
      this.field_76760_I.field_76803_B = 2;
      if (this.field_150632_aF == 1) {
         this.field_76760_I.field_76832_z = 6;
         this.field_76760_I.field_76802_A = 100;
         this.field_76760_I.field_76803_B = 1;
      }

      this.func_76733_a(5159473);
      this.func_76732_a(0.7F, 0.8F);
      if (this.field_150632_aF == 2) {
         this.field_150609_ah = 353825;
         this.field_76790_z = 3175492;
         this.func_76732_a(0.6F, 0.6F);
      }

      if (this.field_150632_aF == 0) {
         this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
      }

      if (this.field_150632_aF == 3) {
         this.field_76760_I.field_76832_z = -999;
      }

   }

   protected BiomeGenBase func_150557_a(int var1, boolean var2) {
      if (this.field_150632_aF == 2) {
         this.field_150609_ah = 353825;
         this.field_76790_z = var1;
         if (var2) {
            this.field_150609_ah = (this.field_150609_ah & 16711422) >> 1;
         }

         return this;
      } else {
         return super.func_150557_a(var1, var2);
      }
   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      if (this.field_150632_aF == 3 && var1.nextInt(3) > 0) {
         return field_150631_aE;
      } else {
         return (WorldGenAbstractTree)(this.field_150632_aF != 2 && var1.nextInt(5) != 0 ? this.field_76757_N : field_150630_aD);
      }
   }

   public BlockFlower.EnumFlowerType func_180623_a(Random var1, BlockPos var2) {
      if (this.field_150632_aF == 1) {
         double var3 = MathHelper.func_151237_a((1.0D + field_180281_af.func_151601_a((double)var2.func_177958_n() / 48.0D, (double)var2.func_177952_p() / 48.0D)) / 2.0D, 0.0D, 0.9999D);
         BlockFlower.EnumFlowerType var5 = BlockFlower.EnumFlowerType.values()[(int)(var3 * (double)BlockFlower.EnumFlowerType.values().length)];
         return var5 == BlockFlower.EnumFlowerType.BLUE_ORCHID ? BlockFlower.EnumFlowerType.POPPY : var5;
      } else {
         return super.func_180623_a(var1, var2);
      }
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      int var4;
      int var5;
      int var6;
      int var7;
      if (this.field_150632_aF == 3) {
         for(var4 = 0; var4 < 4; ++var4) {
            for(var5 = 0; var5 < 4; ++var5) {
               var6 = var4 * 4 + 1 + 8 + var2.nextInt(3);
               var7 = var5 * 4 + 1 + 8 + var2.nextInt(3);
               BlockPos var8 = var1.func_175645_m(var3.func_177982_a(var6, 0, var7));
               if (var2.nextInt(20) == 0) {
                  WorldGenBigMushroom var9 = new WorldGenBigMushroom();
                  var9.func_180709_b(var1, var2, var8);
               } else {
                  WorldGenAbstractTree var12 = this.func_150567_a(var2);
                  var12.func_175904_e();
                  if (var12.func_180709_b(var1, var2, var8)) {
                     var12.func_180711_a(var1, var2, var8);
                  }
               }
            }
         }
      }

      var4 = var2.nextInt(5) - 3;
      if (this.field_150632_aF == 1) {
         var4 += 2;
      }

      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var2.nextInt(3);
         if (var6 == 0) {
            field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.SYRINGA);
         } else if (var6 == 1) {
            field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.ROSE);
         } else if (var6 == 2) {
            field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.PAEONIA);
         }

         for(var7 = 0; var7 < 5; ++var7) {
            int var11 = var2.nextInt(16) + 8;
            int var13 = var2.nextInt(16) + 8;
            int var10 = var2.nextInt(var1.func_175645_m(var3.func_177982_a(var11, 0, var13)).func_177956_o() + 32);
            if (field_180280_ag.func_180709_b(var1, var2, new BlockPos(var3.func_177958_n() + var11, var10, var3.func_177952_p() + var13))) {
               break;
            }
         }
      }

      super.func_180624_a(var1, var2, var3);
   }

   public int func_180627_b(BlockPos var1) {
      int var2 = super.func_180627_b(var1);
      return this.field_150632_aF == 3 ? (var2 & 16711422) + 2634762 >> 1 : var2;
   }

   protected BiomeGenBase func_180277_d(int var1) {
      if (this.field_76756_M == BiomeGenBase.field_76767_f.field_76756_M) {
         BiomeGenForest var2 = new BiomeGenForest(var1, 1);
         var2.func_150570_a(new BiomeGenBase.Height(this.field_76748_D, this.field_76749_E + 0.2F));
         var2.func_76735_a("Flower Forest");
         var2.func_150557_a(6976549, true);
         var2.func_76733_a(8233509);
         return var2;
      } else {
         return this.field_76756_M != BiomeGenBase.field_150583_P.field_76756_M && this.field_76756_M != BiomeGenBase.field_150582_Q.field_76756_M ? new BiomeGenMutated(var1, this) {
            public void func_180624_a(World var1, Random var2, BlockPos var3) {
               this.field_150611_aD.func_180624_a(var1, var2, var3);
            }
         } : new BiomeGenMutated(var1, this) {
            public WorldGenAbstractTree func_150567_a(Random var1) {
               return var1.nextBoolean() ? BiomeGenForest.field_150629_aC : BiomeGenForest.field_150630_aD;
            }
         };
      }
   }
}
