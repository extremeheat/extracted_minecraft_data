package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityWolf;
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
         this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityWolf.class, 5, 4, 4));
      }

      if (this.field_150632_aF == 3) {
         this.field_76760_I.field_76832_z = -999;
      }
   }

   @Override
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

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      if (this.field_150632_aF == 3 && var1.nextInt(3) > 0) {
         return field_150631_aE;
      } else {
         return (WorldGenAbstractTree)(this.field_150632_aF != 2 && var1.nextInt(5) != 0 ? this.field_76757_N : field_150630_aD);
      }
   }

   @Override
   public String func_150572_a(Random var1, int var2, int var3, int var4) {
      if (this.field_150632_aF == 1) {
         double var5 = MathHelper.func_151237_a((1.0 + field_150606_ad.func_151601_a((double)var2 / 48.0, (double)var4 / 48.0)) / 2.0, 0.0, 0.9999);
         int var7 = (int)(var5 * (double)BlockFlower.field_149859_a.length);
         if (var7 == 1) {
            var7 = 0;
         }

         return BlockFlower.field_149859_a[var7];
      } else {
         return super.func_150572_a(var1, var2, var3, var4);
      }
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      if (this.field_150632_aF == 3) {
         for(int var5 = 0; var5 < 4; ++var5) {
            for(int var6 = 0; var6 < 4; ++var6) {
               int var7 = var3 + var5 * 4 + 1 + 8 + var2.nextInt(3);
               int var8 = var4 + var6 * 4 + 1 + 8 + var2.nextInt(3);
               int var9 = var1.func_72976_f(var7, var8);
               if (var2.nextInt(20) == 0) {
                  WorldGenBigMushroom var10 = new WorldGenBigMushroom();
                  var10.func_76484_a(var1, var2, var7, var9, var8);
               } else {
                  WorldGenAbstractTree var17 = this.func_150567_a(var2);
                  var17.func_76487_a(1.0, 1.0, 1.0);
                  if (var17.func_76484_a(var1, var2, var7, var9, var8)) {
                     var17.func_150524_b(var1, var2, var7, var9, var8);
                  }
               }
            }
         }
      }

      int var12 = var2.nextInt(5) - 3;
      if (this.field_150632_aF == 1) {
         var12 += 2;
      }

      for(int var13 = 0; var13 < var12; ++var13) {
         int var14 = var2.nextInt(3);
         if (var14 == 0) {
            field_150610_ae.func_150548_a(1);
         } else if (var14 == 1) {
            field_150610_ae.func_150548_a(4);
         } else if (var14 == 2) {
            field_150610_ae.func_150548_a(5);
         }

         for(int var15 = 0; var15 < 5; ++var15) {
            int var16 = var3 + var2.nextInt(16) + 8;
            int var18 = var4 + var2.nextInt(16) + 8;
            int var11 = var2.nextInt(var1.func_72976_f(var16, var18) + 32);
            if (field_150610_ae.func_76484_a(var1, var2, var16, var11, var18)) {
               break;
            }
         }
      }

      super.func_76728_a(var1, var2, var3, var4);
   }

   @Override
   public int func_150558_b(int var1, int var2, int var3) {
      int var4 = super.func_150558_b(var1, var2, var3);
      return this.field_150632_aF == 3 ? (var4 & 16711422) + 2634762 >> 1 : var4;
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      if (this.field_76756_M == BiomeGenBase.field_76767_f.field_76756_M) {
         BiomeGenForest var1 = new BiomeGenForest(this.field_76756_M + 128, 1);
         var1.func_150570_a(new BiomeGenBase$Height(this.field_76748_D, this.field_76749_E + 0.2F));
         var1.func_76735_a("Flower Forest");
         var1.func_150557_a(6976549, true);
         var1.func_76733_a(8233509);
         return var1;
      } else {
         return (BiomeGenBase)(this.field_76756_M != BiomeGenBase.field_150583_P.field_76756_M
               && this.field_76756_M != BiomeGenBase.field_150582_Q.field_76756_M
            ? new BiomeGenForest$2(this, this.field_76756_M + 128, this)
            : new BiomeGenForest$1(this, this.field_76756_M + 128, this));
      }
   }
}
