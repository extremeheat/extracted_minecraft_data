package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterlilyBlock extends BushBlock {
   protected static final VoxelShape AABB = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   protected WaterlilyBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      super.entityInside(var1, var2, var3, var4);
      if (var2 instanceof ServerLevel && var4 instanceof Boat) {
         var2.destroyBlock(new BlockPos(var3), true, var4);
      }

   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABB;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      FluidState var4 = var2.getFluidState(var3);
      FluidState var5 = var2.getFluidState(var3.above());
      return (var4.getType() == Fluids.WATER || var1.getMaterial() == Material.ICE) && var5.getType() == Fluids.EMPTY;
   }
}
