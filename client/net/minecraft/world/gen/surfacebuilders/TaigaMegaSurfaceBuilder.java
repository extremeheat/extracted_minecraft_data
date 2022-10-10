package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class TaigaMegaSurfaceBuilder implements ISurfaceBuilder<SurfaceBuilderConfig> {
   public TaigaMegaSurfaceBuilder() {
      super();
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      if (var7 > 1.75D) {
         Biome.field_203955_aj.func_205610_a_(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, Biome.field_203948_ac);
      } else if (var7 > -0.95D) {
         Biome.field_203955_aj.func_205610_a_(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, Biome.field_203949_ad);
      } else {
         Biome.field_203955_aj.func_205610_a_(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, Biome.field_203961_Z);
      }

   }
}
