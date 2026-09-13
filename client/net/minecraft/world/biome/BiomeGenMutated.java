package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
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
      this.field_76762_K = new ArrayList(var2.field_76762_K);
      this.field_76761_J = new ArrayList(var2.field_76761_J);
      this.field_82914_M = new ArrayList(var2.field_82914_M);
      this.field_76755_L = new ArrayList(var2.field_76755_L);
      this.field_76750_F = var2.field_76750_F;
      this.field_76751_G = var2.field_76751_G;
      this.field_76748_D = var2.field_76748_D + 0.1F;
      this.field_76749_E = var2.field_76749_E + 0.2F;
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      this.field_150611_aD.field_76760_I.func_150512_a(var1, var2, this, var3, var4);
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      this.field_150611_aD.func_150573_a(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public float func_76741_f() {
      return this.field_150611_aD.func_76741_f();
   }

   @Override
   public WorldGenAbstractTree func_150567_a(Random var1) {
      return this.field_150611_aD.func_150567_a(var1);
   }

   @Override
   public int func_150571_c(int var1, int var2, int var3) {
      return this.field_150611_aD.func_150571_c(var1, var2, var2);
   }

   @Override
   public int func_150558_b(int var1, int var2, int var3) {
      return this.field_150611_aD.func_150558_b(var1, var2, var2);
   }

   @Override
   public Class func_150562_l() {
      return this.field_150611_aD.func_150562_l();
   }

   @Override
   public boolean func_150569_a(BiomeGenBase var1) {
      return this.field_150611_aD.func_150569_a(var1);
   }

   @Override
   public BiomeGenBase$TempCategory func_150561_m() {
      return this.field_150611_aD.func_150561_m();
   }
}
