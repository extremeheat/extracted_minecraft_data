package net.minecraft.world.biome;

import java.util.Iterator;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;

public class BiomeColorHelper {
   private static final BiomeColorHelper.ColorResolver field_180291_a = new BiomeColorHelper.ColorResolver() {
      public int func_180283_a(BiomeGenBase var1, BlockPos var2) {
         return var1.func_180627_b(var2);
      }
   };
   private static final BiomeColorHelper.ColorResolver field_180289_b = new BiomeColorHelper.ColorResolver() {
      public int func_180283_a(BiomeGenBase var1, BlockPos var2) {
         return var1.func_180625_c(var2);
      }
   };
   private static final BiomeColorHelper.ColorResolver field_180290_c = new BiomeColorHelper.ColorResolver() {
      public int func_180283_a(BiomeGenBase var1, BlockPos var2) {
         return var1.field_76759_H;
      }
   };

   private static int func_180285_a(IBlockAccess var0, BlockPos var1, BiomeColorHelper.ColorResolver var2) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;

      int var8;
      for(Iterator var6 = BlockPos.func_177975_b(var1.func_177982_a(-1, 0, -1), var1.func_177982_a(1, 0, 1)).iterator(); var6.hasNext(); var5 += var8 & 255) {
         BlockPos.MutableBlockPos var7 = (BlockPos.MutableBlockPos)var6.next();
         var8 = var2.func_180283_a(var0.func_180494_b(var7), var7);
         var3 += (var8 & 16711680) >> 16;
         var4 += (var8 & '\uff00') >> 8;
      }

      return (var3 / 9 & 255) << 16 | (var4 / 9 & 255) << 8 | var5 / 9 & 255;
   }

   public static int func_180286_a(IBlockAccess var0, BlockPos var1) {
      return func_180285_a(var0, var1, field_180291_a);
   }

   public static int func_180287_b(IBlockAccess var0, BlockPos var1) {
      return func_180285_a(var0, var1, field_180289_b);
   }

   public static int func_180288_c(IBlockAccess var0, BlockPos var1) {
      return func_180285_a(var0, var1, field_180290_c);
   }

   interface ColorResolver {
      int func_180283_a(BiomeGenBase var1, BlockPos var2);
   }
}
