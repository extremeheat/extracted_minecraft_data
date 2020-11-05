package net.minecraft.world.level.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING;
   public static final BooleanProperty POWERED;
   public static final BooleanProperty ATTACHED;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape EAST_AABB;

   public TripWireHookBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(POWERED, false)).setValue(ATTACHED, false));
   }

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

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      Direction var4 = (Direction)var1.getValue(FACING);
      BlockPos var5 = var3.relative(var4.getOpposite());
      BlockState var6 = var2.getBlockState(var5);
      return var4.getAxis().isHorizontal() && var6.isFaceSturdy(var2, var5, var4);
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getOpposite() == var1.getValue(FACING) && !var1.canSurvive(var4, var5) ? Blocks.AIR.defaultBlockState() : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      BlockState var2 = (BlockState)((BlockState)this.defaultBlockState().setValue(POWERED, false)).setValue(ATTACHED, false);
      Level var3 = var1.getLevel();
      BlockPos var4 = var1.getClickedPos();
      Direction[] var5 = var1.getNearestLookingDirections();
      Direction[] var6 = var5;
      int var7 = var5.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         Direction var9 = var6[var8];
         if (var9.getAxis().isHorizontal()) {
            Direction var10 = var9.getOpposite();
            var2 = (BlockState)var2.setValue(FACING, var10);
            if (var2.canSurvive(var3, var4)) {
               return var2;
            }
         }
      }

      return null;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      this.calculateState(var1, var2, var3, false, false, -1, (BlockState)null);
   }

   public void calculateState(Level var1, BlockPos var2, BlockState var3, boolean var4, boolean var5, int var6, @Nullable BlockState var7) {
      Direction var8 = (Direction)var3.getValue(FACING);
      boolean var9 = (Boolean)var3.getValue(ATTACHED);
      boolean var10 = (Boolean)var3.getValue(POWERED);
      boolean var11 = !var4;
      boolean var12 = false;
      int var13 = 0;
      BlockState[] var14 = new BlockState[42];

      BlockPos var16;
      for(int var15 = 1; var15 < 42; ++var15) {
         var16 = var2.relative(var8, var15);
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

            boolean var18 = !(Boolean)var17.getValue(TripWireBlock.DISARMED);
            boolean var19 = (Boolean)var17.getValue(TripWireBlock.POWERED);
            var12 |= var18 && var19;
            var14[var15] = var17;
            if (var15 == var6) {
               var1.getBlockTicks().scheduleTick(var2, this, 10);
               var11 &= var18;
            }
         }
      }

      var11 &= var13 > 1;
      var12 &= var11;
      BlockState var20 = (BlockState)((BlockState)this.defaultBlockState().setValue(ATTACHED, var11)).setValue(POWERED, var12);
      if (var13 > 0) {
         var16 = var2.relative(var8, var13);
         Direction var22 = var8.getOpposite();
         var1.setBlock(var16, (BlockState)var20.setValue(FACING, var22), 3);
         this.notifyNeighbors(var1, var16, var22);
         this.playSound(var1, var16, var11, var12, var9, var10);
      }

      this.playSound(var1, var2, var11, var12, var9, var10);
      if (!var4) {
         var1.setBlock(var2, (BlockState)var20.setValue(FACING, var8), 3);
         if (var5) {
            this.notifyNeighbors(var1, var2, var8);
         }
      }

      if (var9 != var11) {
         for(int var21 = 1; var21 < var13; ++var21) {
            BlockPos var23 = var2.relative(var8, var21);
            BlockState var24 = var14[var21];
            if (var24 != null) {
               var1.setBlock(var23, (BlockState)var24.setValue(ATTACHED, var11), 3);
               if (!var1.getBlockState(var23).isAir()) {
               }
            }
         }
      }

   }

   public void tick(BlockState var1, ServerLevel var2, BlockPos var3, Random var4) {
      this.calculateState(var2, var3, var1, false, true, -1, (BlockState)null);
   }

   private void playSound(Level var1, BlockPos var2, boolean var3, boolean var4, boolean var5, boolean var6) {
      if (var4 && !var6) {
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.TRIPWIRE_CLICK_ON, SoundSource.BLOCKS, 0.4F, 0.6F);
      } else if (!var4 && var6) {
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.TRIPWIRE_CLICK_OFF, SoundSource.BLOCKS, 0.4F, 0.5F);
      } else if (var3 && !var5) {
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.TRIPWIRE_ATTACH, SoundSource.BLOCKS, 0.4F, 0.7F);
      } else if (!var3 && var5) {
         var1.playSound((Player)null, (BlockPos)var2, SoundEvents.TRIPWIRE_DETACH, SoundSource.BLOCKS, 0.4F, 1.2F / (var1.random.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighbors(Level var1, BlockPos var2, Direction var3) {
      var1.updateNeighborsAt(var2, this);
      var1.updateNeighborsAt(var2.relative(var3.getOpposite()), this);
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         boolean var6 = (Boolean)var1.getValue(ATTACHED);
         boolean var7 = (Boolean)var1.getValue(POWERED);
         if (var6 || var7) {
            this.calculateState(var2, var3, var1, true, false, -1, (BlockState)null);
         }

         if (var7) {
            var2.updateNeighborsAt(var3, this);
            var2.updateNeighborsAt(var3.relative(((Direction)var1.getValue(FACING)).getOpposite()), this);
         }

         super.onRemove(var1, var2, var3, var4, var5);
      }
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return (Boolean)var1.getValue(POWERED) ? 15 : 0;
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (!(Boolean)var1.getValue(POWERED)) {
         return 0;
      } else {
         return var1.getValue(FACING) == var4 ? 15 : 0;
      }
   }

   public boolean isSignalSource(BlockState var1) {
      return true;
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, POWERED, ATTACHED);
   }

   static {
      FACING = HorizontalDirectionalBlock.FACING;
      POWERED = BlockStateProperties.POWERED;
      ATTACHED = BlockStateProperties.ATTACHED;
      NORTH_AABB = Block.box(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
      SOUTH_AABB = Block.box(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
      WEST_AABB = Block.box(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
      EAST_AABB = Block.box(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);
   }
}
