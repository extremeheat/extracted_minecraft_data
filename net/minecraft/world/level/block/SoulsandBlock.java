package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulsandBlock extends Block {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D);

   public SoulsandBlock(Block.Properties var1) {
      super(var1);
   }

   public VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      BubbleColumnBlock.growColumn(var2, var3.above(), false);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2));
   }

   public boolean isRedstoneConductor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }

   public int getTickDelay(LevelReader var1) {
      return 20;
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2));
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   public boolean isValidSpawn(BlockState var1, BlockGetter var2, BlockPos var3, EntityType var4) {
      return true;
   }

   public boolean isViewBlocking(BlockState var1, BlockGetter var2, BlockPos var3) {
      return true;
   }
}
