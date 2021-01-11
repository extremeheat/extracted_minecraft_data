package net.minecraft.world.gen.layer;

public class GenLayerIsland extends GenLayer {
   public GenLayerIsland(long var1) {
      super(var1);
   }

   public int[] func_75904_a(int var1, int var2, int var3, int var4) {
      int[] var5 = IntCache.func_76445_a(var3 * var4);

      for(int var6 = 0; var6 < var4; ++var6) {
         for(int var7 = 0; var7 < var3; ++var7) {
            this.func_75903_a((long)(var1 + var7), (long)(var2 + var6));
            var5[var7 + var6 * var3] = this.func_75902_a(10) == 0 ? 1 : 0;
         }
      }

      if (var1 > -var3 && var1 <= 0 && var2 > -var4 && var2 <= 0) {
         var5[-var1 + -var2 * var3] = 1;
      }

      return var5;
   }
}
