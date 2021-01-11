package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.util.EnumFacing;

public abstract class BlockDirectional extends Block {
   public static final PropertyDirection field_176387_N;

   protected BlockDirectional(Material var1) {
      super(var1);
   }

   protected BlockDirectional(Material var1, MapColor var2) {
      super(var1, var2);
   }

   static {
      field_176387_N = PropertyDirection.func_177712_a("facing", EnumFacing.Plane.HORIZONTAL);
   }
}
