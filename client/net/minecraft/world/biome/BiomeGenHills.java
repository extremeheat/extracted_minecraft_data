package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
   private WorldGenerator field_82915_S = new WorldGenMinable(Blocks.field_150418_aU, 8);
   private WorldGenTaiga2 field_150634_aD = new WorldGenTaiga2(false);
   private int field_150635_aE = 0;
   private int field_150636_aF = 1;
   private int field_150637_aG = 2;
   private int field_150638_aH = this.field_150635_aE;

   protected BiomeGenHills(int var1, boolean var2) {
      super(var1);
      if (var2) {
         this.field_76760_I.field_76832_z = 3;
         this.field_150638_aH = this.field_150636_aF;
      }
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      return (WorldGenAbstractTree)(var1.nextInt(3) > 0 ? this.field_150634_aD : super.func_150567_a(var1));
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      super.func_76728_a(var1, var2, var3, var4);
      int var5 = 3 + var2.nextInt(6);

      for(int var6 = 0; var6 < var5; ++var6) {
         int var7 = var3 + var2.nextInt(16);
         int var8 = var2.nextInt(28) + 4;
         int var9 = var4 + var2.nextInt(16);
         if (var1.func_147439_a(var7, var8, var9) == Blocks.field_150348_b) {
            var1.func_147465_d(var7, var8, var9, Blocks.field_150412_bA, 0, 2);
         }
      }

      for(int var10 = 0; var10 < 7; ++var10) {
         int var11 = var3 + var2.nextInt(16);
         int var12 = var2.nextInt(64);
         int var13 = var4 + var2.nextInt(16);
         this.field_82915_S.func_76484_a(var1, var2, var11, var12, var13);
      }
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      this.field_76752_A = Blocks.field_150349_c;
      this.field_150604_aj = 0;
      this.field_76753_B = Blocks.field_150346_d;
      if ((var7 < -1.0 || var7 > 2.0) && this.field_150638_aH == this.field_150637_aG) {
         this.field_76752_A = Blocks.field_150351_n;
         this.field_76753_B = Blocks.field_150351_n;
      } else if (var7 > 1.0 && this.field_150638_aH != this.field_150636_aF) {
         this.field_76752_A = Blocks.field_150348_b;
         this.field_76753_B = Blocks.field_150348_b;
      }

      this.func_150560_b(var1, var2, var3, var4, var5, var6, var7);
   }

   private BiomeGenHills func_150633_b(BiomeGenBase var1) {
      this.field_150638_aH = this.field_150637_aG;
      this.func_150557_a(var1.field_76790_z, true);
      this.func_76735_a(var1.field_76791_y + " M");
      this.func_150570_a(new BiomeGenBase$Height(var1.field_76748_D, var1.field_76749_E));
      this.func_76732_a(var1.field_76750_F, var1.field_76751_G);
      return this;
   }

   @Override
   protected BiomeGenBase func_150566_k() {
      return new BiomeGenHills(this.field_76756_M + 128, false).func_150633_b(this);
   }
}
