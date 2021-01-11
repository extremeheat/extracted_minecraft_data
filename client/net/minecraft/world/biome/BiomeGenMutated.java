package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.Random;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMutated extends BiomeGenBase {
   protected BiomeGenBase field_150611_aD;

   public BiomeGenMutated(int var1, BiomeGenBase var2) {
      super(var1);
      this.field_150611_aD = var2;
      this.func_150557_a(var2.field_76790_z, true);
      this.field_76791_y = var2.field_76791_y + " M";
      this.field_76752_A = var2.field_76752_A;
      this.field_76753_B = var2.field_76753_B;
      this.field_76754_C = var2.field_76754_C;
      this.field_76748_D = var2.field_76748_D;
      this.field_76749_E = var2.field_76749_E;
      this.field_76750_F = var2.field_76750_F;
      this.field_76751_G = var2.field_76751_G;
      this.field_76759_H = var2.field_76759_H;
      this.field_76766_R = var2.field_76766_R;
      this.field_76765_S = var2.field_76765_S;
      this.field_76762_K = Lists.newArrayList(var2.field_76762_K);
      this.field_76761_J = Lists.newArrayList(var2.field_76761_J);
      this.field_82914_M = Lists.newArrayList(var2.field_82914_M);
      this.field_76755_L = Lists.newArrayList(var2.field_76755_L);
      this.field_76750_F = var2.field_76750_F;
      this.field_76751_G = var2.field_76751_G;
      this.field_76748_D = var2.field_76748_D + 0.1F;
      this.field_76749_E = var2.field_76749_E + 0.2F;
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      this.field_150611_aD.field_76760_I.func_180292_a(var1, var2, this, var3);
   }

   public void func_180622_a(World var1, Random var2, ChunkPrimer var3, int var4, int var5, double var6) {
      this.field_150611_aD.func_180622_a(var1, var2, var3, var4, var5, var6);
   }

   public float func_76741_f() {
      return this.field_150611_aD.func_76741_f();
   }

   public WorldGenAbstractTree func_150567_a(Random var1) {
      return this.field_150611_aD.func_150567_a(var1);
   }

   public int func_180625_c(BlockPos var1) {
      return this.field_150611_aD.func_180625_c(var1);
   }

   public int func_180627_b(BlockPos var1) {
      return this.field_150611_aD.func_180627_b(var1);
   }

   public Class<? extends BiomeGenBase> func_150562_l() {
      return this.field_150611_aD.func_150562_l();
   }

   public boolean func_150569_a(BiomeGenBase var1) {
      return this.field_150611_aD.func_150569_a(var1);
   }

   public BiomeGenBase.TempCategory func_150561_m() {
      return this.field_150611_aD.func_150561_m();
   }
}
