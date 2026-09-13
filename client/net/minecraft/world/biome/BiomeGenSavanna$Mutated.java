package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BiomeGenSavanna$Mutated extends BiomeGenMutated {
   public BiomeGenSavanna$Mutated(int var1, BiomeGenBase var2) {
      super(var1, var2);
      this.field_76760_I.field_76832_z = 2;
      this.field_76760_I.field_76802_A = 2;
      this.field_76760_I.field_76803_B = 5;
   }

   @Override
   public void func_150573_a(World var1, Random var2, Block[] var3, byte[] var4, int var5, int var6, double var7) {
      this.field_76752_A = Blocks.field_150349_c;
      this.field_150604_aj = 0;
      this.field_76753_B = Blocks.field_150346_d;
      if (var7 > 1.75) {
         this.field_76752_A = Blocks.field_150348_b;
         this.field_76753_B = Blocks.field_150348_b;
      } else if (var7 > -0.5) {
         this.field_76752_A = Blocks.field_150346_d;
         this.field_150604_aj = 1;
      }

      this.func_150560_b(var1, var2, var3, var4, var5, var6, var7);
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      this.field_76760_I.func_150512_a(var1, var2, this, var3, var4);
   }
}
