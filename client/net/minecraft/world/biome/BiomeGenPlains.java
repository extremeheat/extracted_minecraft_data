package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {
   protected boolean field_150628_aC;

   protected BiomeGenPlains(int var1) {
      super(var1);
      this.func_76732_a(0.8F, 0.4F);
      this.func_150570_a(field_150593_e);
      this.field_76762_K.add(new BiomeGenBase$SpawnListEntry(EntityHorse.class, 5, 2, 6));
      this.field_76760_I.field_76832_z = -999;
      this.field_76760_I.field_76802_A = 4;
      this.field_76760_I.field_76803_B = 10;
   }

   @Override
   public String func_150572_a(Random var1, int var2, int var3, int var4) {
      double var5 = field_150606_ad.func_151601_a((double)var2 / 200.0, (double)var4 / 200.0);
      if (var5 < -0.8) {
         int var8 = var1.nextInt(4);
         return BlockFlower.field_149859_a[4 + var8];
      } else if (var1.nextInt(3) > 0) {
         int var7 = var1.nextInt(3);
         if (var7 == 0) {
            return BlockFlower.field_149859_a[0];
         } else {
            return var7 == 1 ? BlockFlower.field_149859_a[3] : BlockFlower.field_149859_a[8];
         }
      } else {
         return BlockFlower.field_149858_b[0];
      }
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      double var5 = field_150606_ad.func_151601_a((double)(var3 + 8) / 200.0, (double)(var4 + 8) / 200.0);
      if (var5 < -0.8) {
         this.field_76760_I.field_76802_A = 15;
         this.field_76760_I.field_76803_B = 5;
      } else {
         this.field_76760_I.field_76802_A = 4;
         this.field_76760_I.field_76803_B = 10;
         field_150610_ae.func_150548_a(2);

         for(int var7 = 0; var7 < 7; ++var7) {
            int var8 = var3 + var2.nextInt(16) + 8;
            int var9 = var4 + var2.nextInt(16) + 8;
            int var10 = var2.nextInt(var1.func_72976_f(var8, var9) + 32);
            field_150610_ae.func_76484_a(var1, var2, var8, var10, var9);
         }
      }

      if (this.field_150628_aC) {
         field_150610_ae.func_150548_a(0);

         for(int var11 = 0; var11 < 10; ++var11) {
            int var12 = var3 + var2.nextInt(16) + 8;
            int var13 = var4 + var2.nextInt(16) + 8;
            int var14 = var2.nextInt(var1.func_72976_f(var12, var13) + 32);
            field_150610_ae.func_76484_a(var1, var2, var12, var14, var13);
         }
      }

      super.func_76728_a(var1, var2, var3, var4);
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      BiomeGenPlains var1 = new BiomeGenPlains(this.field_76756_M + 128);
      var1.func_76735_a("Sunflower Plains");
      var1.field_150628_aC = true;
      var1.func_76739_b(9286496);
      var1.field_150609_ah = 14273354;
      return var1;
   }
}
