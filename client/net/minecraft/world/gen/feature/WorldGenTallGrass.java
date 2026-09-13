package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class WorldGenTallGrass extends WorldGenerator {
   private Block field_150522_a;
   private int field_76534_b;

   public WorldGenTallGrass(Block var1, int var2) {
      super();
      this.field_150522_a = var1;
      this.field_76534_b = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      Block var6;
      while(
         ((var6 = var1.func_147439_a(var3, var4, var5)).func_149688_o() == Material.field_151579_a || var6.func_149688_o() == Material.field_151584_j)
            && var4 > 0
      ) {
         --var4;
      }

      for(int var7 = 0; var7 < 128; ++var7) {
         int var8 = var3 + var2.nextInt(8) - var2.nextInt(8);
         int var9 = var4 + var2.nextInt(4) - var2.nextInt(4);
         int var10 = var5 + var2.nextInt(8) - var2.nextInt(8);
         if (var1.func_147437_c(var8, var9, var10) && this.field_150522_a.func_149718_j(var1, var8, var9, var10)) {
            var1.func_147465_d(var8, var9, var10, this.field_150522_a, this.field_76534_b, 2);
         }
      }

      return true;
   }
}
