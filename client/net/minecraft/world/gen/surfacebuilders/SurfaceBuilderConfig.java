package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.state.IBlockState;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final IBlockState field_204111_a;
   private final IBlockState field_204112_b;
   private final IBlockState field_204113_c;

   public SurfaceBuilderConfig(IBlockState var1, IBlockState var2, IBlockState var3) {
      super();
      this.field_204111_a = var1;
      this.field_204112_b = var2;
      this.field_204113_c = var3;
   }

   public IBlockState func_204108_a() {
      return this.field_204111_a;
   }

   public IBlockState func_204109_b() {
      return this.field_204112_b;
   }

   public IBlockState func_204110_c() {
      return this.field_204113_c;
   }
}
