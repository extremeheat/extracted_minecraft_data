package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {
   protected boolean field_150628_aC;

   protected BiomeGenPlains(int var1) {
      super(var1);
      this.func_76732_a(0.8F, 0.4F);
      this.func_150570_a(field_150593_e);
      this.field_76762_K.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
      this.field_76760_I.field_76832_z = -999;
      this.field_76760_I.field_76802_A = 4;
      this.field_76760_I.field_76803_B = 10;
   }

   public BlockFlower.EnumFlowerType func_180623_a(Random var1, BlockPos var2) {
      double var3 = field_180281_af.func_151601_a((double)var2.func_177958_n() / 200.0D, (double)var2.func_177952_p() / 200.0D);
      int var5;
      if (var3 < -0.8D) {
         var5 = var1.nextInt(4);
         switch(var5) {
         case 0:
            return BlockFlower.EnumFlowerType.ORANGE_TULIP;
         case 1:
            return BlockFlower.EnumFlowerType.RED_TULIP;
         case 2:
            return BlockFlower.EnumFlowerType.PINK_TULIP;
         case 3:
         default:
            return BlockFlower.EnumFlowerType.WHITE_TULIP;
         }
      } else if (var1.nextInt(3) > 0) {
         var5 = var1.nextInt(3);
         if (var5 == 0) {
            return BlockFlower.EnumFlowerType.POPPY;
         } else {
            return var5 == 1 ? BlockFlower.EnumFlowerType.HOUSTONIA : BlockFlower.EnumFlowerType.OXEYE_DAISY;
         }
      } else {
         return BlockFlower.EnumFlowerType.DANDELION;
      }
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      double var4 = field_180281_af.func_151601_a((double)(var3.func_177958_n() + 8) / 200.0D, (double)(var3.func_177952_p() + 8) / 200.0D);
      int var6;
      int var7;
      int var8;
      int var9;
      if (var4 < -0.8D) {
         this.field_76760_I.field_76802_A = 15;
         this.field_76760_I.field_76803_B = 5;
      } else {
         this.field_76760_I.field_76802_A = 4;
         this.field_76760_I.field_76803_B = 10;
         field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.GRASS);

         for(var6 = 0; var6 < 7; ++var6) {
            var7 = var2.nextInt(16) + 8;
            var8 = var2.nextInt(16) + 8;
            var9 = var2.nextInt(var1.func_175645_m(var3.func_177982_a(var7, 0, var8)).func_177956_o() + 32);
            field_180280_ag.func_180709_b(var1, var2, var3.func_177982_a(var7, var9, var8));
         }
      }

      if (this.field_150628_aC) {
         field_180280_ag.func_180710_a(BlockDoublePlant.EnumPlantType.SUNFLOWER);

         for(var6 = 0; var6 < 10; ++var6) {
            var7 = var2.nextInt(16) + 8;
            var8 = var2.nextInt(16) + 8;
            var9 = var2.nextInt(var1.func_175645_m(var3.func_177982_a(var7, 0, var8)).func_177956_o() + 32);
            field_180280_ag.func_180709_b(var1, var2, var3.func_177982_a(var7, var9, var8));
         }
      }

      super.func_180624_a(var1, var2, var3);
   }

   protected BiomeGenBase func_180277_d(int var1) {
      BiomeGenPlains var2 = new BiomeGenPlains(var1);
      var2.func_76735_a("Sunflower Plains");
      var2.field_150628_aC = true;
      var2.func_76739_b(9286496);
      var2.field_150609_ah = 14273354;
      return var2;
   }
}
