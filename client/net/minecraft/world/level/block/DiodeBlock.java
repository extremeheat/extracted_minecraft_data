package net.minecraft.world.level.block;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.TickPriority;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class DiodeBlock extends HorizontalDirectionalBlock {
   protected static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
   public static final BooleanProperty POWERED;

   protected DiodeBlock(BlockBehaviour.Properties var1) {
      super(var1);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return SHAPE;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      return canSupportRigidBlock(var2, var3.below());
   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      if (!this.isLocked(var2, var3, var1)) {
         boolean var5 = (Boolean)var1.getValue(POWERED);
         boolean var6 = this.shouldTurnOn(var2, var3, var1);
         if (var5 && !var6) {
            var2.setBlock(var3, (BlockState)var1.setValue(POWERED, false), 2);
         } else if (!var5) {
            var2.setBlock(var3, (BlockState)var1.setValue(POWERED, true), 2);
            if (!var6) {
               var2.getBlockTicks().scheduleTick(var3, this, this.getDelay(var1), TickPriority.VERY_HIGH);
            }
         }

      }
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getSignal(var2, var3, var4);
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!(Boolean)var1.getValue(POWERED)) {
         return 0;
      } else {
         return var1.getValue(FACING) == var4 ? this.getOutputSignal(var2, var3, var1) : 0;
      }
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (var1.canSurvive(var2, var3)) {
         this.checkTickOnNeighbor(var2, var3, var1);
      } else {
         BlockEntity var7 = var1.hasBlockEntity() ? var2.getBlockEntity(var3) : null;
         dropResources(var1, var2, var3, var7);
         var2.removeBlock(var3, false);
         Direction[] var8 = Direction.values();
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            Direction var11 = var8[var10];
            var2.updateNeighborsAt(var3.relative(var11), this);
         }

      }
   }

   protected void checkTickOnNeighbor(Level var1, BlockPos var2, BlockState var3) {
      if (!this.isLocked(var1, var2, var3)) {
         boolean var4 = (Boolean)var3.getValue(POWERED);
         boolean var5 = this.shouldTurnOn(var1, var2, var3);
         if (var4 != var5 && !var1.getBlockTicks().willTickThisTick(var2, this)) {
            TickPriority var6 = TickPriority.HIGH;
            if (this.shouldPrioritize(var1, var2, var3)) {
               var6 = TickPriority.EXTREMELY_HIGH;
            } else if (var4) {
               var6 = TickPriority.VERY_HIGH;
            }

            var1.getBlockTicks().scheduleTick(var2, this, this.getDelay(var3), var6);
         }

      }
   }

   public boolean isLocked(LevelReader var1, BlockPos var2, BlockState var3) {
      return false;
   }

   protected boolean shouldTurnOn(Level var1, BlockPos var2, BlockState var3) {
      return this.getInputSignal(var1, var2, var3) > 0;
   }

   protected int getInputSignal(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = (Direction)var3.getValue(FACING);
      BlockPos var5 = var2.relative(var4);
      int var6 = var1.getSignal(var5, var4);
      if (var6 >= 15) {
         return var6;
      } else {
         BlockState var7 = var1.getBlockState(var5);
         return Math.max(var6, var7.is(Blocks.REDSTONE_WIRE) ? (Integer)var7.getValue(RedStoneWireBlock.POWER) : 0);
      }
   }

   protected int getAlternateSignal(LevelReader var1, BlockPos var2, BlockState var3) {
      Direction var4 = (Direction)var3.getValue(FACING);
      Direction var5 = var4.getClockWise();
      Direction var6 = var4.getCounterClockWise();
      return Math.max(this.getAlternateSignalAt(var1, var2.relative(var5), var5), this.getAlternateSignalAt(var1, var2.relative(var6), var6));
   }

   protected int getAlternateSignalAt(LevelReader var1, BlockPos var2, Direction var3) {
      BlockState var4 = var1.getBlockState(var2);
      if (this.isAlternateInput(var4)) {
         if (var4.is(Blocks.REDSTONE_BLOCK)) {
            return 15;
         } else {
            return var4.is(Blocks.REDSTONE_WIRE) ? (Integer)var4.getValue(RedStoneWireBlock.POWER) : var1.getDirectSignal(var2, var3);
         }
      } else {
         return 0;
      }
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)this.defaultBlockState().setValue(FACING, var1.getHorizontalDirection().getOpposite());
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (this.shouldTurnOn(var1, var2, var3)) {
         var1.getBlockTicks().scheduleTick(var2, this, 1);
      }

   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      this.updateNeighborsInFront(var2, var3, var1);
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         super.onRemove(var1, var2, var3, var4, var5);
         this.updateNeighborsInFront(var2, var3, var1);
      }
   }

   protected void updateNeighborsInFront(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = (Direction)var3.getValue(FACING);
      BlockPos var5 = var2.relative(var4.getOpposite());
      var1.neighborChanged(var5, this, var2);
      var1.updateNeighborsAtExceptFromFacing(var5, this, var4);
   }

   protected boolean isAlternateInput(BlockState var1) {
      return var1.isSignalSource();
   }

   protected int getOutputSignal(BlockGetter var1, BlockPos var2, BlockState var3) {
      return 15;
   }

   public static boolean isDiode(BlockState var0) {
      return var0.getBlock() instanceof DiodeBlock;
   }

   public boolean shouldPrioritize(BlockGetter var1, BlockPos var2, BlockState var3) {
      Direction var4 = ((Direction)var3.getValue(FACING)).getOpposite();
      BlockState var5 = var1.getBlockState(var2.relative(var4));
      return isDiode(var5) && var5.getValue(FACING) != var4;
   }

   protected abstract int getDelay(BlockState var1);

   static {
      POWERED = BlockStateProperties.POWERED;
   }
}
