package net.minecraft.world.gen.surfacebuilders;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;

public class CompositeSurfaceBuilder<C extends ISurfaceBuilderConfig> implements ISurfaceBuilder<SurfaceBuilderConfig> {
   private final ISurfaceBuilder<C> field_205550_a;
   private final C field_205551_b;

   public CompositeSurfaceBuilder(ISurfaceBuilder<C> var1, C var2) {
      super();
      this.field_205550_a = var1;
      this.field_205551_b = var2;
   }

   public void func_205610_a_(Random var1, IChunk var2, Biome var3, int var4, int var5, int var6, double var7, IBlockState var9, IBlockState var10, int var11, long var12, SurfaceBuilderConfig var14) {
      this.field_205550_a.func_205610_a_(var1, var2, var3, var4, var5, var6, var7, var9, var10, var11, var12, this.field_205551_b);
   }

   public void func_205548_a(long var1) {
      this.field_205550_a.func_205548_a(var1);
   }

   public C func_205549_a() {
      return this.field_205551_b;
   }
}
