package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
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
      this.field_76761_J.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      return this.field_76763_Q;
   }

   public int func_180627_b(BlockPos var1) {
      double var2 = field_180281_af.func_151601_a((double)var1.func_177958_n() * 0.0225D, (double)var1.func_177952_p() * 0.0225D);
      return var2 < -0.1D ? 5011004 : 6975545;
   }

   public int func_180625_c(BlockPos var1) {
      return 6975545;
   }

   public BlockFlower.EnumFlowerType func_180623_a(Random var1, BlockPos var2) {
      return BlockFlower.EnumFlowerType.BLUE_ORCHID;
   }

   public void func_180622_a(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      double var8 = field_180281_af.func_151601_a((double)var4 * 0.25D, (double)var5 * 0.25D);
      if (var8 > 0.0D) {
         int var10 = var4 & 15;
         int var11 = var5 & 15;

         for(int var12 = 255; var12 >= 0; --var12) {
            if (var3.func_177856_a(var11, var12, var10).func_177230_c().func_149688_o() != Material.field_151579_a) {
               if (var12 == 62 && var3.func_177856_a(var11, var12, var10).func_177230_c() != Blocks.field_150355_j) {
                  var3.func_177855_a(var11, var12, var10, Blocks.field_150355_j.func_176223_P());
                  if (var8 < 0.12D) {
                     var3.func_177855_a(var11, var12 + 1, var10, Blocks.field_150392_bi.func_176223_P());
                  }
               }
               break;
            }
         }
      }

      this.func_180628_b(var1, var2, var3, var4, var5, var6);
   }
}
