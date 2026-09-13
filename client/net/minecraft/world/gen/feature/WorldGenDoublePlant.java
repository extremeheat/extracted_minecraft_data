package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenDoublePlant extends WorldGenerator {
   private int field_150549_a;

   public WorldGenDoublePlant() {
      super();
   }

   public void func_150548_a(int var1) {
      this.field_150549_a = var1;
   }

   @Override
   public boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5) {
      boolean var6 = false;

      for(int var7 = 0; var7 < 64; ++var7) {
         int var8 = var3 + var2.nextInt(8) - var2.nextInt(8);
         int var9 = var4 + var2.nextInt(4) - var2.nextInt(4);
         int var10 = var5 + var2.nextInt(8) - var2.nextInt(8);
         if (var1.func_147437_c(var8, var9, var10)
            && (!var1.field_73011_w.field_76576_e || var9 < 254)
            && Blocks.field_150398_cm.func_149742_c(var1, var8, var9, var10)) {
            Blocks.field_150398_cm.func_149889_c(var1, var8, var9, var10, this.field_150549_a, 2);
            var6 = true;
         }
      }

      return var6;
   }
}
