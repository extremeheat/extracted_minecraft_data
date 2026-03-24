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

   public Item getBucket() {
      return Items.AIR;
   }

   public boolean canBeReplacedWith(final FluidState state, final BlockGetter level, final BlockPos pos, final Fluid other, final Direction direction) {
      return true;
   }

   public Vec3 getFlow(final BlockGetter level, final BlockPos pos, final FluidState fluidState) {
      return Vec3.ZERO;
   }

   public int getTickDelay(final LevelReader level) {
      return 0;
   }

   protected boolean isEmpty() {
      return true;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   public float getHeight(final FluidState fluidState, final BlockGetter level, final BlockPos pos) {
      return 0.0F;
   }

   public float getOwnHeight(final FluidState fluidState) {
      return 0.0F;
   }

   protected BlockState createLegacyBlock(final FluidState fluidState) {
      return Blocks.AIR.defaultBlockState();
   }

   public boolean isSource(final FluidState fluidState) {
      return false;
   }

   public int getAmount(final FluidState fluidState) {
      return 0;
   }

   public VoxelShape getShape(final FluidState state, final BlockGetter level, final BlockPos pos) {
      return Shapes.empty();
   }
}
