package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.InsideBlockEffectApplier;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WaterlilyBlock extends VegetationBlock {
   public static final MapCodec<WaterlilyBlock> CODEC = simpleCodec(WaterlilyBlock::new);
   private static final VoxelShape SHAPE = Block.column(14.0, 0.0, 1.5);

   public MapCodec<WaterlilyBlock> codec() {
      return CODEC;
   }

   protected WaterlilyBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4, InsideBlockEffectApplier var5) {
      super.entityInside(var1, var2, var3, var4, var5);
      if (var2 instanceof ServerLevel && var4 instanceof AbstractBoat) {
         var2.destroyBlock(new BlockPos(var3), true, var4);
      }

   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   protected boolean mayPlaceOn(BlockState var1, BlockGetter var2, BlockPos var3) {
      FluidState var4 = var2.getFluidState(var3);
      FluidState var5 = var2.getFluidState(var3.above());
      return (var4.getType() == Fluids.WATER || var1.getBlock() instanceof IceBlock) && var5.getType() == Fluids.EMPTY;
   }
}
