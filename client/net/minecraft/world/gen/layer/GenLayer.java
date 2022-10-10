package net.minecraft.world.gen.layer;

import javax.annotation.Nullable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.area.AreaDimension;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

public class GenLayer {
   private final IAreaFactory<LazyArea> field_202834_a;

   public GenLayer(IAreaFactory<LazyArea> var1) {
      super();
      this.field_202834_a = var1;
   }

   public Biome[] func_202833_a(int var1, int var2, int var3, int var4, @Nullable Biome var5) {
      AreaDimension var6 = new AreaDimension(var1, var2, var3, var4);
      LazyArea var7 = (LazyArea)this.field_202834_a.make(var6);
      Biome[] var8 = new Biome[var3 * var4];

      for(int var9 = 0; var9 < var4; ++var9) {
         for(int var10 = 0; var10 < var3; ++var10) {
            var8[var10 + var9 * var3] = Biome.func_180276_a(var7.func_202678_a(var10, var9), var5);
         }
      }

      return var8;
   }
}
