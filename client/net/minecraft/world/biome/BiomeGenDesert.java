package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenDesertWells;

public class BiomeGenDesert extends BiomeGenBase {
   public BiomeGenDesert(int var1) {
      super(var1);
      this.field_76762_K.clear();
      this.field_76752_A = Blocks.field_150354_m.func_176223_P();
      this.field_76753_B = Blocks.field_150354_m.func_176223_P();
      this.field_76760_I.field_76832_z = -999;
      this.field_76760_I.field_76804_C = 2;
      this.field_76760_I.field_76799_E = 50;
      this.field_76760_I.field_76800_F = 10;
      this.field_76762_K.clear();
   }

   public void func_180624_a(World var1, Random var2, BlockPos var3) {
      super.func_180624_a(var1, var2, var3);
      if (var2.nextInt(1000) == 0) {
         int var4 = var2.nextInt(16) + 8;
         int var5 = var2.nextInt(16) + 8;
         BlockPos var6 = var1.func_175645_m(var3.func_177982_a(var4, 0, var5)).func_177984_a();
         (new WorldGenDesertWells()).func_180709_b(var1, var2, var6);
      }

   }
}
