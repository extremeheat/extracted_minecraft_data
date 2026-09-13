package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {
   protected BiomeGenSwamp(int var1) {
      super(var1);
      this.field_76760_I.field_76832_z = 2;
      this.field_76760_I.field_76802_A = 1;
      this.field_76760_I.field_76804_C = 1;
      this.field_76760_I.field_76798_D = 8;
      this.field_76760_I.field_76799_E = 10;
      this.field_76760_I.field_76806_I = 1;
      this.field_76760_I.field_76833_y = 4;
      this.field_76760_I.field_76805_H = 0;
      this.field_76760_I.field_76801_G = 0;
      this.field_76760_I.field_76803_B = 5;
      this.field_76759_H = 14745518;
      this.field_76761_J.add(new BiomeGenBase$SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      return this.field_76763_Q;
   }

   @Override
   public int func_150558_b(int var1, int var2, int var3) {
      double var4 = field_150606_ad.func_151601_a((double)var1 * 0.0225, (double)var3 * 0.0225);
      return var4 < -0.1 ? 5011004 : 6975545;
   }

   @Override
   public int func_150571_c(int var1, int var2, int var3) {
      return 6975545;
   }

   @Override
   public String func_150572_a(Random var1, int var2, int var3, int var4) {
      return BlockFlower.field_149859_a[1];
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      double var9 = field_150606_ad.func_151601_a((double)var5 * 0.25, (double)var6 * 0.25);
      if (var9 > 0.0) {
         int var11 = var5 & 15;
         int var12 = var6 & 15;
         int var13 = var3.length / 256;

         for(int var14 = 255; var14 >= 0; --var14) {
            int var15 = (var12 * 16 + var11) * var13 + var14;
            if (var3[var15] == null || var3[var15].func_149688_o() != Material.field_151579_a) {
               if (var14 == 62 && var3[var15] != Blocks.field_150355_j) {
                  var3[var15] = Blocks.field_150355_j;
                  if (var9 < 0.12) {
                     var3[var15 + 1] = Blocks.field_150392_bi;
                  }
               }
               break;
            }
         }
      }

      this.func_150560_b(var1, var2, var3, var4, var5, var6, var7);
   }
}
