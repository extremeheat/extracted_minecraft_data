package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterlilyBlock extends BushBlock {
   public static final MapCodec<WaterlilyBlock> CODEC = simpleCodec(WaterlilyBlock::new);
   protected static final VoxelShape AABB = Block.box(1.0, 0.0, 1.0, 15.0, 1.5, 15.0);

   @Override
   public MapCodec<WaterlilyBlock> codec() {
      return CODEC;
   }

   protected WaterlilyBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      super.entityInside(var1, var2, var3, var4);
      if (var2 instanceof ServerLevel && var4 instanceof Boat) {
         var2.destroyBlock(new BlockPos(var3), true, var4);
      }
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return AABB;
   }

   @Override
   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      FluidState var4 = var2.getFluidState(var3);
      FluidState var5 = var2.getFluidState(var3.above());
      return (var4.getType() == Fluids.WATER || var1.getBlock() instanceof IceBlock) && var5.getType() == Fluids.EMPTY;
   }
}
