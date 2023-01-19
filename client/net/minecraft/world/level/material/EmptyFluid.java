package net.minecraft.world.level.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyFluid extends Fluid {
   public EmptyFluid() {
      super();
   }

   @Override
   public Item getBucket() {
      return Items.AIR;
   }

   @Override
   public boolean canBeReplacedWith(FluidState var1, BlockGetter var2, BlockPos var3, Fluid var4, Direction var5) {
      return true;
   }

   @Override
   public Vec3 getFlow(BlockGetter var1, BlockPos var2, FluidState var3) {
      return Vec3.ZERO;
   }

   @Override
   public int getTickDelay(LevelReader var1) {
      return 0;
   }

   @Override
   protected boolean isEmpty() {
      return true;
   }

   @Override
   protected float getExplosionResistance() {
      return 0.0F;
   }

   @Override
   public float getHeight(FluidState var1, BlockGetter var2, BlockPos var3) {
      return 0.0F;
   }

   @Override
   public float getOwnHeight(FluidState var1) {
      return 0.0F;
   }

   @Override
   protected BlockState createLegacyBlock(FluidState var1) {
      return Blocks.AIR.defaultBlockState();
   }

   @Override
   public boolean isSource(FluidState var1) {
      return false;
   }

   @Override
   public int getAmount(FluidState var1) {
      return 0;
   }

   @Override
   public VoxelShape getShape(FluidState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }
}
