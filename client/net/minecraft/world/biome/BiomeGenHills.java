package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
   private WorldGenerator field_82915_S;
   private WorldGenTaiga2 field_150634_aD;
   private int field_150635_aE;
   private int field_150636_aF;
   private int field_150637_aG;
   private int field_150638_aH;

   protected BiomeGenHills(int var1, boolean var2) {
      super(var1);
      this.field_82915_S = new WorldGenMinable(Blocks.field_150418_aU.func_176223_P().func_177226_a(BlockSilverfish.field_176378_a, BlockSilverfish.EnumType.STONE), 9);
      this.field_150634_aD = new WorldGenTaiga2(false);
      this.field_150635_aE = 0;
      this.field_150636_aF = 1;
      this.field_150637_aG = 2;
      this.field_150638_aH = this.field_150635_aE;
      if (var2) {
         this.field_76760_I.field_76832_z = 3;
         this.field_150638_aH = this.field_150636_aF;
      }

   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      return (WorldGenAbstractTree)(var1.nextInt(3) > 0 ? this.field_150634_aD : super.func_150567_a(var1));
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      super.func_180624_a(var1, var2, var3);
      int var4 = 3 + var2.nextInt(6);

      int var5;
      int var6;
      int var7;
      for(var5 = 0; var5 < var4; ++var5) {
         var6 = var2.nextInt(16);
         var7 = var2.nextInt(28) + 4;
         int var8 = var2.nextInt(16);
         BlockPos var9 = var3.func_177982_a(var6, var7, var8);
         if (var1.func_180495_p(var9).func_177230_c() == Blocks.field_150348_b) {
            var1.func_180501_a(var9, Blocks.field_150412_bA.func_176223_P(), 2);
         }
      }

      for(var4 = 0; var4 < 7; ++var4) {
         var5 = var2.nextInt(16);
         var6 = var2.nextInt(64);
         var7 = var2.nextInt(16);
         this.field_82915_S.func_180709_b(var1, var2, var3.func_177982_a(var5, var6, var7));
      }

   }

   public void func_180622_a(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      this.field_76752_A = Blocks.field_150349_c.func_176223_P();
      this.field_76753_B = Blocks.field_150346_d.func_176223_P();
      if ((var6 < -1.0D || var6 > 2.0D) && this.field_150638_aH == this.field_150637_aG) {
         this.field_76752_A = Blocks.field_150351_n.func_176223_P();
         this.field_76753_B = Blocks.field_150351_n.func_176223_P();
      } else if (var6 > 1.0D && this.field_150638_aH != this.field_150636_aF) {
         this.field_76752_A = Blocks.field_150348_b.func_176223_P();
         this.field_76753_B = Blocks.field_150348_b.func_176223_P();
      }

      this.func_180628_b(var1, var2, var3, var4, var5, var6);
   }

   private BiomeGenHills func_150633_b(BiomeGenBase var1) {
      this.field_150638_aH = this.field_150637_aG;
      this.func_150557_a(var1.field_76790_z, true);
      this.func_76735_a(var1.field_76791_y + " M");
      this.func_150570_a(new BiomeGenBase.Height(var1.field_76748_D, var1.field_76749_E));
      this.func_76732_a(var1.field_76750_F, var1.field_76751_G);
      return this;
   }

   protected BiomeGenBase func_180277_d(int var1) {
      return (new BiomeGenHills(var1, false)).func_150633_b(this);
   }
}
