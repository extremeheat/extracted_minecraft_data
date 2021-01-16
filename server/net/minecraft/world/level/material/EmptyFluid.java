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

   public boolean canBeReplacedWith(FluidState var1, BlockGetter var2, BlockPos var3, Fluid var4, Direction var5) {
      return true;
   }

   public Vec3 getFlow(BlockGetter var1, BlockPos var2, FluidState var3) {
      return Vec3.ZERO;
   }

   public int getTickDelay(LevelReader var1) {
      return 0;
   }

   protected boolean isEmpty() {
      return true;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   public float getHeight(FluidState var1, BlockGetter var2, BlockPos var3) {
      return 0.0F;
   }

   public float getOwnHeight(FluidState var1) {
      return 0.0F;
   }

   protected BlockState createLegacyBlock(FluidState var1) {
      return Blocks.AIR.defaultBlockState();
   }

   public boolean isSource(FluidState var1) {
      return false;
   }

   public int getAmount(FluidState var1) {
      return 0;
   }

   public VoxelShape getShape(FluidState var1, BlockGetter var2, BlockPos var3) {
      return Shapes.empty();
   }
}
