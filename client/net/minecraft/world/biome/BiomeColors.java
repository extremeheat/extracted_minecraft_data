package net.minecraft.world.biome;

import java.util.Iterator;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;

public class BiomeColors {
   private static final BiomeColors.ColorResolver field_180291_a = Biome::func_180627_b;
   private static final BiomeColors.ColorResolver field_180289_b = Biome::func_180625_c;
   private static final BiomeColors.ColorResolver field_180290_c = (var0, var1) -> {
      return var0.func_185361_o();
   };
   private static final BiomeColors.ColorResolver field_204277_d = (var0, var1) -> {
      return var0.func_204274_p();
   };

   private static int func_180285_a(IWorldReaderBase var0, BlockPos var1, BiomeColors.ColorResolver var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = Minecraft.func_71410_x().field_71474_y.field_205217_U;
      int var7 = (var6 * 2 + 1) * (var6 * 2 + 1);

      int var10;
      for(Iterator var8 = BlockPos.func_191531_b(var1.func_177958_n() - var6, var1.func_177956_o(), var1.func_177952_p() - var6, var1.func_177958_n() + var6, var1.func_177956_o(), var1.func_177952_p() + var6).iterator(); var8.hasNext(); var5 += var10 & 255) {
         BlockPos.MutableBlockPos var9 = (BlockPos.MutableBlockPos)var8.next();
         var10 = var2.getColor(var0.func_180494_b(var9), var9);
         var3 += (var10 & 16711680) >> 16;
         var4 += (var10 & '\uff00') >> 8;
      }

      return (var3 / var7 & 255) << 16 | (var4 / var7 & 255) << 8 | var5 / var7 & 255;
   }

   public static int func_180286_a(IWorldReaderBase var0, BlockPos var1) {
      return func_180285_a(var0, var1, field_180291_a);
   }

   public static int func_180287_b(IWorldReaderBase var0, BlockPos var1) {
      return func_180285_a(var0, var1, field_180289_b);
   }

   public static int func_180288_c(IWorldReaderBase var0, BlockPos var1) {
      return func_180285_a(var0, var1, field_180290_c);
   }

   interface ColorResolver {
      int getColor(Biome var1, BlockPos var2);
   }
}
