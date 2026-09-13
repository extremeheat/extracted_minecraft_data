package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public class WorldGenFlowers extends WorldGenerator {
   private Block field_150552_a;
   private int field_150551_b;

   public WorldGenFlowers(Block var1) {
      super();
      this.field_150552_a = var1;
   }

   public void func_150550_a(Block var1, int var2) {
      this.field_150552_a = var1;
      this.field_150551_b = var2;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      for(int var6 = 0; var6 < 64; ++var6) {
         int var7 = var3 + var2.nextInt(8) - var2.nextInt(8);
         int var8 = var4 + var2.nextInt(4) - var2.nextInt(4);
         int var9 = var5 + var2.nextInt(8) - var2.nextInt(8);
         if (var1.func_147437_c(var7, var8, var9)
            && (!var1.field_73011_w.field_76576_e || var8 < 255)
            && this.field_150552_a.func_149718_j(var1, var7, var8, var9)) {
            var1.func_147465_d(var7, var8, var9, this.field_150552_a, this.field_150551_b, 2);
         }
      }

      return true;
   }
}
