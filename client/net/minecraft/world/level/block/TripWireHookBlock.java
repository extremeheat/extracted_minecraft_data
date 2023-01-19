package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final int WIRE_DIST_MIN = 1;
   protected static final int WIRE_DIST_MAX = 42;
   private static final int RECHECK_PERIOD = 10;
   protected static final int AABB_OFFSET = 3;
   protected static final VoxelShape NORTH_AABB = Block.box(5.0, 0.0, 10.0, 11.0, 10.0, 16.0);
   protected static final VoxelShape SOUTH_AABB = Block.box(5.0, 0.0, 0.0, 11.0, 10.0, 6.0);
   protected static final VoxelShape WEST_AABB = Block.box(10.0, 0.0, 5.0, 16.0, 10.0, 11.0);
   protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 5.0, 6.0, 10.0, 11.0);

   public TripWireHookBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState(
         this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false))
      );
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      switch((Direction)var1.getValue(FACING)) {
         case EAST:
         default:
            return EAST_AABB;
         case WEST:
            return WEST_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case NORTH:
            return NORTH_AABB;
      }
   }

   @Override
   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = var1.getValue(FACING);
      BlockPos var5 = var3.relative(var4.getOpposite());
      BlockState var6 = var2.getBlockState(var5);
      return var4.getAxis().isHorizontal() && var6.isFaceSturdy(var2, var5, var4);
   }

   @Override
   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5)
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)).setValue(ATTACHED, Boolean.valueOf(false));
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();

      for(Direction var9 : var5) {
         if (var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            var2 = var2.setValue(FACING, var10);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      this.calculateState(var1, var2, var3, false, false, -1, null);
   }

   public void calculateState(Level var1, BlockPos var2, BlockState var3, boolean var4, boolean var5, int var6, @Nullable BlockState var7) {
      Direction var8 = var3.getValue(FACING);
      boolean var9 = var3.getValue(ATTACHED);
      boolean var10 = var3.getValue(POWERED);
      boolean var11 = !var4;
      boolean var12 = false;
      int var13 = 0;
      BlockState[] var14 = new BlockState[42];

      for(int var15 = 1; var15 < 42; ++var15) {
         BlockPos var16 = var2.relative(var8, var15);
         BlockState var17 = var1.getBlockState(var16);
         if (var17.is(Blocks.TRIPWIRE_HOOK)) {
            if (var17.getValue(FACING) == var8.getOpposite()) {
               var13 = var15;
            }
            break;
         }

         if (!var17.is(Blocks.TRIPWIRE) && var15 != var6) {
            var14[var15] = null;
            var11 = false;
         } else {
            if (var15 == var6) {
               var17 = (BlockState)MoreObjects.firstNonNull(var7, var17);
            }

            boolean var18 = !var17.getValue(TripWireBlock.DISARMED);
            boolean var19 = var17.getValue(TripWireBlock.POWERED);
            var12 |= var18 && var19;
            var14[var15] = var17;
            if (var15 == var6) {
               var1.scheduleTick(var2, this, 10);
               var11 &= var18;
            }
         }
      }

      var11 &= var13 > 1;
      var12 &= var11;
      BlockState var22 = this.defaultBlockState().setValue(ATTACHED, Boolean.valueOf(var11)).setValue(POWERED, Boolean.valueOf(var12));
      if (var13 > 0) {
         BlockPos var23 = var2.relative(var8, var13);
         Direction var25 = var8.getOpposite();
         var1.setBlock(var23, var22.setValue(FACING, var25), 3);
         this.notifyNeighbors(var1, var23, var25);
         this.emitState(var1, var23, var11, var12, var9, var10);
      }

      this.emitState(var1, var2, var11, var12, var9, var10);
      if (!var4) {
         var1.setBlock(var2, var22.setValue(FACING, var8), 3);
         if (var5) {
            this.notifyNeighbors(var1, var2, var8);
         }
      }

      if (var9 != var11) {
         for(int var24 = 1; var24 < var13; ++var24) {
            BlockPos var26 = var2.relative(var8, var24);
            BlockState var27 = var14[var24];
            if (var27 != null) {
               var1.setBlock(var26, var27.setValue(ATTACHED, Boolean.valueOf(var11)), 3);
               if (!var1.getBlockState(var26).isAir()) {
               }
            }
         }
      }
   }

   @Override
   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      this.calculateState(var2, var3, var1, false, true, -1, null);
   }

   private void emitState(Level var1, BlockPos var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var4 && !var6) {
         var1.playSound(null, var2, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4F, 0.6F);
         var1.gameEvent(null, GameEvent.BLOCK_ACTIVATE, var2);
      } else if (!var4 && var6) {
         var1.playSound(null, var2, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4F, 0.5F);
         var1.gameEvent(null, GameEvent.BLOCK_DEACTIVATE, var2);
      } else if (var3 && !var5) {
         var1.playSound(null, var2, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4F, 0.7F);
         var1.gameEvent(null, GameEvent.BLOCK_ATTACH, var2);
      } else if (!var3 && var5) {
         var1.playSound(null, var2, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4F, 1.2F / (var1.random.nextFloat() * 0.2F + 0.9F));
         var1.gameEvent(null, GameEvent.BLOCK_DETACH, var2);
      }
   }

   private void notifyNeighbors(Level var1, BlockPos var2, Direction var3) {
      var1.updateNeighborsAt(var2, this);
      var1.updateNeighborsAt(var2.relative(var3.getOpposite()), this);
   }

   @Override
   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         boolean var6 = var1.getValue(ATTACHED);
         boolean var7 = var1.getValue(POWERED);
         if (var6 || var7) {
            this.calculateState(var2, var3, var1, true, false, -1, null);
         }

         if (var7) {
            var2.updateNeighborsAt(var3, this);
            var2.updateNeighborsAt(var3.relative(var1.getValue(FACING).getOpposite()), this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   @Override
   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return var1.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!var1.getValue(POWERED)) {
         return 0;
      } else {
         return var1.getValue(FACING) == var4 ? 15 : 0;
      }
   }

   @Override
   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   @Override
   public BlockState rotate(BlockState var1, Rotation var2) {
      return var1.setValue(FACING, var2.rotate(var1.getValue(FACING)));
   }

   @Override
   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation(var1.getValue(FACING)));
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, ATTACHED);
   }
}
