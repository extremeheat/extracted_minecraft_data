package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {
   public BiomeGenDesert(int var1) {
      super(var1);
      this.field_76762_K.clear();
      this.field_76752_A = Blocks.field_150354_m;
      this.field_76753_B = Blocks.field_150354_m;
      this.field_76760_I.field_76832_z = -999;
      this.field_76760_I.field_76804_C = 2;
      this.field_76760_I.field_76799_E = 50;
      this.field_76760_I.field_76800_F = 10;
      this.field_76762_K.clear();
   }

   @Override
   public void func_76728_a(World var1, Random var2, int var3, int var4) {
      super.func_76728_a(var1, var2, var3, var4);
      if (var2.nextInt(1000) == 0) {
         int var5 = var3 + var2.nextInt(16) + 8;
         int var6 = var4 + var2.nextInt(16) + 8;
         WorldGenDesertWells var7 = new WorldGenDesertWells();
         var7.func_76484_a(var1, var2, var5, var1.func_72976_f(var5, var6) + 1, var6);
      }
   }
}
