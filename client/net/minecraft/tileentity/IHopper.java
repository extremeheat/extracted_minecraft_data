package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public interface IHopper extends IInventory {
   VoxelShape field_200101_a = Block.func_208617_a(2.0D, 11.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   VoxelShape field_200102_b = Block.func_208617_a(0.0D, 16.0D, 0.0D, 16.0D, 32.0D, 16.0D);
   VoxelShape field_200103_c = VoxelShapes.func_197872_a(field_200101_a, field_200102_b);

   default VoxelShape func_200100_i() {
      return field_200103_c;
   }

   @Nullable
   World func_145831_w();

   double func_96107_aA();

   double func_96109_aB();

   double func_96108_aC();
}
