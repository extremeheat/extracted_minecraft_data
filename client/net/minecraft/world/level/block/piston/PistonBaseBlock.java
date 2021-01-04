package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonBaseBlock extends DirectionalBlock {
   public static final BooleanProperty EXTENDED;
   protected static final VoxelShape EAST_AABB;
   protected static final VoxelShape WEST_AABB;
   protected static final VoxelShape SOUTH_AABB;
   protected static final VoxelShape NORTH_AABB;
   protected static final VoxelShape UP_AABB;
   protected static final VoxelShape DOWN_AABB;
   private final boolean isSticky;

   public PistonBaseBlock(boolean var1, Block.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH)).setValue(EXTENDED, false));
      this.isSticky = var1;
   }

   public boolean isViewBlocking(BlockState var1, BlockGetter var2, BlockPos var3) {
      return !(Boolean)var1.getValue(EXTENDED);
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if ((Boolean)var1.getValue(EXTENDED)) {
         switch((Direction)var1.getValue(FACING)) {
         case DOWN:
            return DOWN_AABB;
         case UP:
         default:
            return UP_AABB;
         case NORTH:
            return NORTH_AABB;
         case SOUTH:
            return SOUTH_AABB;
         case WEST:
            return WEST_AABB;
         case EAST:
            return EAST_AABB;
         }
      } else {
         return Shapes.block();
      }
   }

   public boolean isRedstoneConductor(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (!var1.isClientSide) {
         this.checkIfExtend(var1, var2, var3);
      }

   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         this.checkIfExtend(var2, var3, var1);
      }

   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (var4.getBlock() != var1.getBlock()) {
         if (!var2.isClientSide && var2.getBlockEntity(var3) == null) {
            this.checkIfExtend(var2, var3, var1);
         }

      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return (BlockState)((BlockState)this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite())).setValue(EXTENDED, false);
   }

   private void checkIfExtend(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = (Direction)var3.getValue(FACING);
      boolean var5 = this.getNeighborSignal(var1, var2, var4);
      if (var5 && !(Boolean)var3.getValue(EXTENDED)) {
         if ((new PistonStructureResolver(var1, var2, var4, true)).resolve()) {
            var1.blockEvent(var2, this, 0, var4.get3DDataValue());
         }
      } else if (!var5 && (Boolean)var3.getValue(EXTENDED)) {
         BlockPos var6 = var2.relative(var4, 2);
         BlockState var7 = var1.getBlockState(var6);
         byte var8 = 1;
         if (var7.getBlock() == Blocks.MOVING_PISTON && var7.getValue(FACING) == var4) {
            BlockEntity var9 = var1.getBlockEntity(var6);
            if (var9 instanceof PistonMovingBlockEntity) {
               PistonMovingBlockEntity var10 = (PistonMovingBlockEntity)var9;
               if (var10.isExtending() && (var10.getProgress(0.0F) < 0.5F || var1.getGameTime() == var10.getLastTicked() || ((ServerLevel)var1).isHandlingTick())) {
                  var8 = 2;
               }
            }
         }

         var1.blockEvent(var2, this, var8, var4.get3DDataValue());
      }

   }

   private boolean getNeighborSignal(Level var1, BlockPos var2, Direction var3) {
      Direction[] var4 = Direction.values();
      int var5 = var4.length;

      int var6;
      for(var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];
         if (var7 != var3 && var1.hasSignal(var2.relative(var7), var7)) {
            return true;
         }
      }

      if (var1.hasSignal(var2, Direction.DOWN)) {
         return true;
      } else {
         BlockPos var9 = var2.above();
         Direction[] var10 = Direction.values();
         var6 = var10.length;

         for(int var11 = 0; var11 < var6; ++var11) {
            Direction var8 = var10[var11];
            if (var8 != Direction.DOWN && var1.hasSignal(var9.relative(var8), var8)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      Direction var6 = (Direction)var1.getValue(FACING);
      if (!var2.isClientSide) {
         boolean var7 = this.getNeighborSignal(var2, var3, var6);
         if (var7 && (var4 == 1 || var4 == 2)) {
            var2.setBlock(var3, (BlockState)var1.setValue(EXTENDED, true), 2);
            return false;
         }

         if (!var7 && var4 == 0) {
            return false;
         }
      }

      if (var4 == 0) {
         if (!this.moveBlocks(var2, var3, var6, true)) {
            return false;
         }

         var2.setBlock(var3, (BlockState)var1.setValue(EXTENDED, true), 67);
         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, var2.random.nextFloat() * 0.25F + 0.6F);
      } else if (var4 == 1 || var4 == 2) {
         BlockEntity var14 = var2.getBlockEntity(var3.relative(var6));
         if (var14 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)var14).finalTick();
         }

         var2.setBlock(var3, (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, var6)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT), 3);
         var2.setBlockEntity(var3, MovingPistonBlock.newMovingBlockEntity((BlockState)this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(var5 & 7)), var6, false, true));
         if (this.isSticky) {
            BlockPos var8 = var3.offset(var6.getStepX() * 2, var6.getStepY() * 2, var6.getStepZ() * 2);
            BlockState var9 = var2.getBlockState(var8);
            Block var10 = var9.getBlock();
            boolean var11 = false;
            if (var10 == Blocks.MOVING_PISTON) {
               BlockEntity var12 = var2.getBlockEntity(var8);
               if (var12 instanceof PistonMovingBlockEntity) {
                  PistonMovingBlockEntity var13 = (PistonMovingBlockEntity)var12;
                  if (var13.getDirection() == var6 && var13.isExtending()) {
                     var13.finalTick();
                     var11 = true;
                  }
               }
            }

            if (!var11) {
               if (var4 != 1 || var9.isAir() || !isPushable(var9, var2, var8, var6.getOpposite(), false, var6) || var9.getPistonPushReaction() != PushReaction.NORMAL && var10 != Blocks.PISTON && var10 != Blocks.STICKY_PISTON) {
                  var2.removeBlock(var3.relative(var6), false);
               } else {
                  this.moveBlocks(var2, var3, var6, false);
               }
            }
         } else {
            var2.removeBlock(var3.relative(var6), false);
         }

         var2.playSound((Player)null, (BlockPos)var3, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, var2.random.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   public static boolean isPushable(BlockState var0, Level var1, BlockPos var2, Direction var3, boolean var4, Direction var5) {
      Block var6 = var0.getBlock();
      if (var6 == Blocks.OBSIDIAN) {
         return false;
      } else if (!var1.getWorldBorder().isWithinBounds(var2)) {
         return false;
      } else if (var2.getY() < 0 || var3 == Direction.DOWN && var2.getY() == 0) {
         return false;
      } else if (var2.getY() <= var1.getMaxBuildHeight() - 1 && (var3 != Direction.UP || var2.getY() != var1.getMaxBuildHeight() - 1)) {
         if (var6 != Blocks.PISTON && var6 != Blocks.STICKY_PISTON) {
            if (var0.getDestroySpeed(var1, var2) == -1.0F) {
               return false;
            }

            switch(var0.getPistonPushReaction()) {
            case BLOCK:
               return false;
            case DESTROY:
               return var4;
            case PUSH_ONLY:
               return var3 == var5;
            }
         } else if ((Boolean)var0.getValue(EXTENDED)) {
            return false;
         }

         return !var6.isEntityBlock();
      } else {
         return false;
      }
   }

   private boolean moveBlocks(Level var1, BlockPos var2, Direction var3, boolean var4) {
      BlockPos var5 = var2.relative(var3);
      if (!var4 && var1.getBlockState(var5).getBlock() == Blocks.PISTON_HEAD) {
         var1.setBlock(var5, Blocks.AIR.defaultBlockState(), 20);
      }

      PistonStructureResolver var6 = new PistonStructureResolver(var1, var2, var3, var4);
      if (!var6.resolve()) {
         return false;
      } else {
         List var7 = var6.getToPush();
         ArrayList var8 = Lists.newArrayList();

         for(int var9 = 0; var9 < var7.size(); ++var9) {
            BlockPos var10 = (BlockPos)var7.get(var9);
            var8.add(var1.getBlockState(var10));
         }

         List var18 = var6.getToDestroy();
         int var19 = var7.size() + var18.size();
         BlockState[] var11 = new BlockState[var19];
         Direction var12 = var4 ? var3 : var3.getOpposite();
         HashSet var13 = Sets.newHashSet(var7);

         int var14;
         BlockPos var15;
         BlockState var16;
         for(var14 = var18.size() - 1; var14 >= 0; --var14) {
            var15 = (BlockPos)var18.get(var14);
            var16 = var1.getBlockState(var15);
            BlockEntity var17 = var16.getBlock().isEntityBlock() ? var1.getBlockEntity(var15) : null;
            dropResources(var16, var1, var15, var17);
            var1.setBlock(var15, Blocks.AIR.defaultBlockState(), 18);
            --var19;
            var11[var19] = var16;
         }

         for(var14 = var7.size() - 1; var14 >= 0; --var14) {
            var15 = (BlockPos)var7.get(var14);
            var16 = var1.getBlockState(var15);
            var15 = var15.relative(var12);
            var13.remove(var15);
            var1.setBlock(var15, (BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, var3), 68);
            var1.setBlockEntity(var15, MovingPistonBlock.newMovingBlockEntity((BlockState)var8.get(var14), var3, var4, false));
            --var19;
            var11[var19] = var16;
         }

         BlockState var22;
         if (var4) {
            PistonType var20 = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            var22 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, var3)).setValue(PistonHeadBlock.TYPE, var20);
            var16 = (BlockState)((BlockState)Blocks.MOVING_PISTON.defaultBlockState().setValue(MovingPistonBlock.FACING, var3)).setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            var13.remove(var5);
            var1.setBlock(var5, var16, 68);
            var1.setBlockEntity(var5, MovingPistonBlock.newMovingBlockEntity(var22, var3, true, true));
         }

         Iterator var21 = var13.iterator();

         while(var21.hasNext()) {
            var15 = (BlockPos)var21.next();
            var1.setBlock(var15, Blocks.AIR.defaultBlockState(), 66);
         }

         for(var14 = var18.size() - 1; var14 >= 0; --var14) {
            var22 = var11[var19++];
            BlockPos var23 = (BlockPos)var18.get(var14);
            var22.updateIndirectNeighbourShapes(var1, var23, 2);
            var1.updateNeighborsAt(var23, var22.getBlock());
         }

         for(var14 = var7.size() - 1; var14 >= 0; --var14) {
            var1.updateNeighborsAt((BlockPos)var7.get(var14), var11[var19++].getBlock());
         }

         if (var4) {
            var1.updateNeighborsAt(var5, Blocks.PISTON_HEAD);
         }

         return true;
      }
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      return (BlockState)var1.setValue(FACING, var2.rotate((Direction)var1.getValue(FACING)));
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      return var1.rotate(var2.getRotation((Direction)var1.getValue(FACING)));
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(FACING, EXTENDED);
   }

   public boolean useShapeForLightOcclusion(BlockState var1) {
      return (Boolean)var1.getValue(EXTENDED);
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }

   static {
      EXTENDED = BlockStateProperties.EXTENDED;
      EAST_AABB = Block.box(0.0D, 0.0D, 0.0D, 12.0D, 16.0D, 16.0D);
      WEST_AABB = Block.box(4.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
      SOUTH_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 12.0D);
      NORTH_AABB = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 16.0D, 16.0D);
      UP_AABB = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D);
      DOWN_AABB = Block.box(0.0D, 4.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   }
}
