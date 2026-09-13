package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;

public abstract class WorldGenerator {
   private final boolean field_76488_a;

   public WorldGenerator() {
      super();
      this.field_76488_a = false;
   }

   public WorldGenerator(boolean var1) {
      super();
      this.field_76488_a = var1;
   }

   public abstract boolean func_76484_a(World var1, Random var2, int var3, int var4, int var5);

   public void func_76487_a(double var1, double var3, double var5) {
   }

   protected void func_150515_a(World var1, int var2, int var3, int var4, Block var5) {
      this.func_150516_a(var1, var2, var3, var4, var5, 0);
   }

   protected void func_150516_a(World var1, int var2, int var3, int var4, Block var5, int var6) {
      if (this.field_76488_a) {
         var1.func_147465_d(var2, var3, var4, var5, var6, 3);
      } else {
         var1.func_147465_d(var2, var3, var4, var5, var6, 2);
      }
   }
}
