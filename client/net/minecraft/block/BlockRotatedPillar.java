package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.util.EnumFacing;

public abstract class BlockRotatedPillar extends Block {
   public static final PropertyEnum<EnumFacing.Axis> field_176298_M = PropertyEnum.func_177709_a("axis", EnumFacing.Axis.class);

   protected BlockRotatedPillar(Material var1) {
      super(var1, var1.func_151565_r());
   }

   protected BlockRotatedPillar(Material var1, MapColor var2) {
      super(var1, var2);
   }
}
