package net.minecraft.world.level.block;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class ButtonBlock extends FaceAttachedHorizontalDirectionalBlock {
   public static final BooleanProperty POWERED;
   protected static final VoxelShape CEILING_AABB_X;
   protected static final VoxelShape CEILING_AABB_Z;
   protected static final VoxelShape FLOOR_AABB_X;
   protected static final VoxelShape FLOOR_AABB_Z;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape PRESSED_CEILING_AABB_X;
   protected static final VoxelShape PRESSED_CEILING_AABB_Z;
   protected static final VoxelShape PRESSED_FLOOR_AABB_X;
   protected static final VoxelShape PRESSED_FLOOR_AABB_Z;
   protected static final VoxelShape PRESSED_NORTH_AABB;
   protected static final VoxelShape PRESSED_SOUTH_AABB;
   protected static final VoxelShape PRESSED_WEST_AABB;
   protected static final VoxelShape PRESSED_EAST_AABB;
   private final boolean sensitive;

   protected ButtonBlock(boolean var1, Block.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(FACE, AttachFace.WALL));
      this.sensitive = var1;
   }

   public int getTickDelay(LevelReader var1) {
      return this.sensitive ? 30 : 20;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      Direction var5 = (Direction)var1.getValue(FACING);
      boolean var6 = (Boolean)var1.getValue(POWERED);
      switch((AttachFace)var1.getValue(FACE)) {
      case FLOOR:
         if (var5.getAxis() == Direction.Axis.X) {
            return var6 ? PRESSED_FLOOR_AABB_X : FLOOR_AABB_X;
         }

         return var6 ? PRESSED_FLOOR_AABB_Z : FLOOR_AABB_Z;
      case WALL:
         switch(var5) {
         case EAST:
            return var6 ? PRESSED_EAST_AABB : EAST_AABB;
         case WEST:
            return var6 ? PRESSED_WEST_AABB : WEST_AABB;
         case SOUTH:
            return var6 ? PRESSED_SOUTH_AABB : SOUTH_AABB;
         case NORTH:
         default:
            return var6 ? PRESSED_NORTH_AABB : NORTH_AABB;
         }
      case CEILING:
      default:
         if (var5.getAxis() == Direction.Axis.X) {
            return var6 ? PRESSED_CEILING_AABB_X : CEILING_AABB_X;
         } else {
            return var6 ? PRESSED_CEILING_AABB_Z : CEILING_AABB_Z;
         }
      }
   }

   public boolean use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if ((Boolean)var1.getValue(POWERED)) {
         return true;
      } else {
         var2.setBlock(var3, (BlockState)var1.setValue(POWERED, true), 3);
         this.playSound(var4, var2, var3, true);
         this.updateNeighbours(var1, var2, var3);
         var2.getBlockTicks().scheduleTick(var3, this, this.getTickDelay(var2));
         return true;
      }
   }

   protected void playSound(@Nullable Player var1, LevelAccessor var2, BlockPos var3, boolean var4) {
      var2.playSound(var4 ? var1 : null, var3, this.getSound(var4), SoundSource.BLOCKS, 0.3F, var4 ? 0.6F : 0.5F);
   }

   protected abstract SoundEvent getSound(boolean var1);

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && var1.getBlock() != var4.getBlock()) {
         if ((Boolean)var1.getValue(POWERED)) {
            this.updateNeighbours(var1, var2, var3);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) && getConnectedDirection(var1) == var4 ? 15 : 0;
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public void tick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      if (!var2.isClientSide && (Boolean)var1.getValue(POWERED)) {
         if (this.sensitive) {
            this.checkPressed(var1, var2, var3);
         } else {
            var2.setBlock(var3, (BlockState)var1.setValue(POWERED, false), 3);
            this.updateNeighbours(var1, var2, var3);
            this.playSound((Player)null, var2, var3, false);
         }

      }
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide && this.sensitive && !(Boolean)var1.getValue(POWERED)) {
         this.checkPressed(var1, var2, var3);
      }
   }

   private void checkPressed(BlockState var1, Level var2, BlockPos var3) {
      List var4 = var2.getEntitiesOfClass(AbstractArrow.class, var1.getShape(var2, var3).bounds().move(var3));
      boolean var5 = !var4.isEmpty();
      boolean var6 = (Boolean)var1.getValue(POWERED);
      if (var5 != var6) {
         var2.setBlock(var3, (BlockState)var1.setValue(POWERED, var5), 3);
         this.updateNeighbours(var1, var2, var3);
         this.playSound((Player)null, var2, var3, var5);
      }

      if (var5) {
         var2.getBlockTicks().scheduleTick(new BlockPos(var3), this, this.getTickDelay(var2));
      }

   }

   private void updateNeighbours(BlockState var1, Level var2, BlockPos var3) {
      var2.updateNeighborsAt(var3, this);
      var2.updateNeighborsAt(var3.relative(getConnectedDirection(var1).getOpposite()), this);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, FACE);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      CEILING_AABB_X = Block.box(6.0D, 14.0D, 5.0D, 10.0D, 16.0D, 11.0D);
      CEILING_AABB_Z = Block.box(5.0D, 14.0D, 6.0D, 11.0D, 16.0D, 10.0D);
      FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 2.0D, 11.0D);
      FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 2.0D, 10.0D);
      NORTH_AABB = Block.box(5.0D, 6.0D, 14.0D, 11.0D, 10.0D, 16.0D);
      SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 2.0D);
      WEST_AABB = Block.box(14.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 2.0D, 10.0D, 11.0D);
      PRESSED_CEILING_AABB_X = Block.box(6.0D, 15.0D, 5.0D, 10.0D, 16.0D, 11.0D);
      PRESSED_CEILING_AABB_Z = Block.box(5.0D, 15.0D, 6.0D, 11.0D, 16.0D, 10.0D);
      PRESSED_FLOOR_AABB_X = Block.box(6.0D, 0.0D, 5.0D, 10.0D, 1.0D, 11.0D);
      PRESSED_FLOOR_AABB_Z = Block.box(5.0D, 0.0D, 6.0D, 11.0D, 1.0D, 10.0D);
      PRESSED_NORTH_AABB = Block.box(5.0D, 6.0D, 15.0D, 11.0D, 10.0D, 16.0D);
      PRESSED_SOUTH_AABB = Block.box(5.0D, 6.0D, 0.0D, 11.0D, 10.0D, 1.0D);
      PRESSED_WEST_AABB = Block.box(15.0D, 6.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      PRESSED_EAST_AABB = Block.box(0.0D, 6.0D, 5.0D, 1.0D, 10.0D, 11.0D);
   }
}
