package net.minecraft.world.level.block.piston;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.PistonType;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PistonBaseBlock extends DirectionalBlock {
   public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;
   public static final int TRIGGER_EXTEND = 0;
   public static final int TRIGGER_CONTRACT = 1;
   public static final int TRIGGER_DROP = 2;
   public static final float PLATFORM_THICKNESS = 4.0F;
   protected static final VoxelShape EAST_AABB = Block.box(0.0, 0.0, 0.0, 12.0, 16.0, 16.0);
   protected static final VoxelShape WEST_AABB = Block.box(4.0, 0.0, 0.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape SOUTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 12.0);
   protected static final VoxelShape NORTH_AABB = Block.box(0.0, 0.0, 4.0, 16.0, 16.0, 16.0);
   protected static final VoxelShape UP_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0);
   protected static final VoxelShape DOWN_AABB = Block.box(0.0, 4.0, 0.0, 16.0, 16.0, 16.0);
   private final boolean isSticky;

   public PistonBaseBlock(boolean var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(EXTENDED, Boolean.valueOf(false)));
      this.isSticky = var1;
   }

   @Override
   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      if (var1.getValue(EXTENDED)) {
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

   @Override
   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, LivingEntity var4, ItemStack var5) {
      if (!var1.isClientSide) {
         this.checkIfExtend(var1, var2, var3);
      }
   }

   @Override
   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         this.checkIfExtend(var2, var3, var1);
      }
   }

   @Override
   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         if (!var2.isClientSide && var2.getBlockEntity(var3) == null) {
            this.checkIfExtend(var2, var3, var1);
         }
      }
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.defaultBlockState().setValue(FACING, var1.getNearestLookingDirection().getOpposite()).setValue(EXTENDED, Boolean.valueOf(false));
   }

   private void checkIfExtend(Level var1, BlockPos var2, BlockState var3) {
      Direction var4 = var3.getValue(FACING);
      boolean var5 = this.getNeighborSignal(var1, var2, var4);
      if (var5 && !var3.getValue(EXTENDED)) {
         if (new PistonStructureResolver(var1, var2, var4, true).resolve()) {
            var1.blockEvent(var2, this, 0, var4.get3DDataValue());
         }
      } else if (!var5 && var3.getValue(EXTENDED)) {
         BlockPos var6 = var2.relative(var4, 2);
         BlockState var7 = var1.getBlockState(var6);
         byte var8 = 1;
         if (var7.is(Blocks.MOVING_PISTON) && var7.getValue(FACING) == var4) {
            BlockEntity var9 = var1.getBlockEntity(var6);
            if (var9 instanceof PistonMovingBlockEntity var10
               && var10.isExtending()
               && (var10.getProgress(0.0F) < 0.5F || var1.getGameTime() == var10.getLastTicked() || ((ServerLevel)var1).isHandlingTick())) {
               var8 = 2;
            }
         }

         var1.blockEvent(var2, this, var8, var4.get3DDataValue());
      }
   }

   private boolean getNeighborSignal(Level var1, BlockPos var2, Direction var3) {
      for(Direction var7 : Direction.values()) {
         if (var7 != var3 && var1.hasSignal(var2.relative(var7), var7)) {
            return true;
         }
      }

      if (var1.hasSignal(var2, Direction.DOWN)) {
         return true;
      } else {
         BlockPos var9 = var2.above();

         for(Direction var8 : Direction.values()) {
            if (var8 != Direction.DOWN && var1.hasSignal(var9.relative(var8), var8)) {
               return true;
            }
         }

         return false;
      }
   }

   // $QF: Could not properly define all variable types!
   // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
   @Override
   public boolean triggerEvent(BlockState var1, Level var2, BlockPos var3, int var4, int var5) {
      Direction var6 = var1.getValue(FACING);
      if (!var2.isClientSide) {
         boolean var7 = this.getNeighborSignal(var2, var3, var6);
         if (var7 && (var4 == 1 || var4 == 2)) {
            var2.setBlock(var3, var1.setValue(EXTENDED, Boolean.valueOf(true)), 2);
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

         var2.setBlock(var3, var1.setValue(EXTENDED, Boolean.valueOf(true)), 67);
         var2.playSound(null, var3, SoundEvents.PISTON_EXTEND, SoundSource.BLOCKS, 0.5F, var2.random.nextFloat() * 0.25F + 0.6F);
         var2.gameEvent(null, GameEvent.PISTON_EXTEND, var3);
      } else if (var4 == 1 || var4 == 2) {
         BlockEntity var14 = var2.getBlockEntity(var3.relative(var6));
         if (var14 instanceof PistonMovingBlockEntity) {
            ((PistonMovingBlockEntity)var14).finalTick();
         }

         BlockState var8 = Blocks.MOVING_PISTON
            .defaultBlockState()
            .setValue(MovingPistonBlock.FACING, var6)
            .setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
         var2.setBlock(var3, var8, 20);
         var2.setBlockEntity(
            MovingPistonBlock.newMovingBlockEntity(
               var3, var8, this.defaultBlockState().setValue(FACING, Direction.from3DDataValue(var5 & 7)), var6, false, true
            )
         );
         var2.blockUpdated(var3, var8.getBlock());
         var8.updateNeighbourShapes(var2, var3, 2);
         if (this.isSticky) {
            BlockPos var9 = var3.offset(var6.getStepX() * 2, var6.getStepY() * 2, var6.getStepZ() * 2);
            BlockState var10 = var2.getBlockState(var9);
            boolean var11 = false;
            if (var10.is(Blocks.MOVING_PISTON)) {
               BlockEntity var12 = var2.getBlockEntity(var9);
               if (var12 instanceof PistonMovingBlockEntity var13 && var13.getDirection() == var6 && var13.isExtending()) {
                  var13.finalTick();
                  var11 = true;
               }
            }

            if (!var11) {
               if (var4 != 1
                  || var10.isAir()
                  || !isPushable(var10, var2, var9, var6.getOpposite(), false, var6)
                  || var10.getPistonPushReaction() != PushReaction.NORMAL && !var10.is(Blocks.PISTON) && !var10.is(Blocks.STICKY_PISTON)) {
                  var2.removeBlock(var3.relative(var6), false);
               } else {
                  this.moveBlocks(var2, var3, var6, false);
               }
            }
         } else {
            var2.removeBlock(var3.relative(var6), false);
         }

         var2.playSound(null, var3, SoundEvents.PISTON_CONTRACT, SoundSource.BLOCKS, 0.5F, var2.random.nextFloat() * 0.15F + 0.6F);
         var2.gameEvent(null, GameEvent.PISTON_CONTRACT, var3);
      }

      return true;
   }

   public static boolean isPushable(BlockState var0, Level var1, BlockPos var2, Direction var3, boolean var4, Direction var5) {
      if (var2.getY() < var1.getMinBuildHeight() || var2.getY() > var1.getMaxBuildHeight() - 1 || !var1.getWorldBorder().isWithinBounds(var2)) {
         return false;
      } else if (var0.isAir()) {
         return true;
      } else if (var0.is(Blocks.OBSIDIAN) || var0.is(Blocks.CRYING_OBSIDIAN) || var0.is(Blocks.RESPAWN_ANCHOR) || var0.is(Blocks.REINFORCED_DEEPSLATE)) {
         return false;
      } else if (var3 == Direction.DOWN && var2.getY() == var1.getMinBuildHeight()) {
         return false;
      } else if (var3 == Direction.UP && var2.getY() == var1.getMaxBuildHeight() - 1) {
         return false;
      } else {
         if (!var0.is(Blocks.PISTON) && !var0.is(Blocks.STICKY_PISTON)) {
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
         } else if (var0.getValue(EXTENDED)) {
            return false;
         }

         return !var0.hasBlockEntity();
      }
   }

   private boolean moveBlocks(Level var1, BlockPos var2, Direction var3, boolean var4) {
      BlockPos var5 = var2.relative(var3);
      if (!var4 && var1.getBlockState(var5).is(Blocks.PISTON_HEAD)) {
         var1.setBlock(var5, Blocks.AIR.defaultBlockState(), 20);
      }

      PistonStructureResolver var6 = new PistonStructureResolver(var1, var2, var3, var4);
      if (!var6.resolve()) {
         return false;
      } else {
         HashMap var7 = Maps.newHashMap();
         List var8 = var6.getToPush();
         ArrayList var9 = Lists.newArrayList();

         for(int var10 = 0; var10 < var8.size(); ++var10) {
            BlockPos var11 = (BlockPos)var8.get(var10);
            BlockState var12 = var1.getBlockState(var11);
            var9.add(var12);
            var7.put(var11, var12);
         }

         List var19 = var6.getToDestroy();
         BlockState[] var20 = new BlockState[var8.size() + var19.size()];
         Direction var21 = var4 ? var3 : var3.getOpposite();
         int var13 = 0;

         for(int var14 = var19.size() - 1; var14 >= 0; --var14) {
            BlockPos var15 = (BlockPos)var19.get(var14);
            BlockState var16 = var1.getBlockState(var15);
            BlockEntity var17 = var16.hasBlockEntity() ? var1.getBlockEntity(var15) : null;
            dropResources(var16, var1, var15, var17);
            var1.setBlock(var15, Blocks.AIR.defaultBlockState(), 18);
            var1.gameEvent(GameEvent.BLOCK_DESTROY, var15, GameEvent.Context.of(var16));
            if (!var16.is(BlockTags.FIRE)) {
               var1.addDestroyBlockEffect(var15, var16);
            }

            var20[var13++] = var16;
         }

         for(int var23 = var8.size() - 1; var23 >= 0; --var23) {
            BlockPos var26 = (BlockPos)var8.get(var23);
            BlockState var33 = var1.getBlockState(var26);
            var26 = var26.relative(var21);
            var7.remove(var26);
            BlockState var38 = Blocks.MOVING_PISTON.defaultBlockState().setValue(FACING, var3);
            var1.setBlock(var26, var38, 68);
            var1.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(var26, var38, (BlockState)var9.get(var23), var3, var4, false));
            var20[var13++] = var33;
         }

         if (var4) {
            PistonType var24 = this.isSticky ? PistonType.STICKY : PistonType.DEFAULT;
            BlockState var28 = Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.FACING, var3).setValue(PistonHeadBlock.TYPE, var24);
            BlockState var34 = Blocks.MOVING_PISTON
               .defaultBlockState()
               .setValue(MovingPistonBlock.FACING, var3)
               .setValue(MovingPistonBlock.TYPE, this.isSticky ? PistonType.STICKY : PistonType.DEFAULT);
            var7.remove(var5);
            var1.setBlock(var5, var34, 68);
            var1.setBlockEntity(MovingPistonBlock.newMovingBlockEntity(var5, var34, var28, var3, true, true));
         }

         BlockState var25 = Blocks.AIR.defaultBlockState();

         for(BlockPos var35 : var7.keySet()) {
            var1.setBlock(var35, var25, 82);
         }

         for(Entry var36 : var7.entrySet()) {
            BlockPos var39 = (BlockPos)var36.getKey();
            BlockState var18 = (BlockState)var36.getValue();
            var18.updateIndirectNeighbourShapes(var1, var39, 2);
            var25.updateNeighbourShapes(var1, var39, 2);
            var25.updateIndirectNeighbourShapes(var1, var39, 2);
         }

         var13 = 0;

         for(int var31 = var19.size() - 1; var31 >= 0; --var31) {
            BlockState var37 = var20[var13++];
            BlockPos var40 = (BlockPos)var19.get(var31);
            var37.updateIndirectNeighbourShapes(var1, var40, 2);
            var1.updateNeighborsAt(var40, var37.getBlock());
         }

         for(int var32 = var8.size() - 1; var32 >= 0; --var32) {
            var1.updateNeighborsAt((BlockPos)var8.get(var32), var20[var13++].getBlock());
         }

         if (var4) {
            var1.updateNeighborsAt(var5, Blocks.PISTON_HEAD);
         }

         return true;
      }
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
      var1.add(FACING, EXTENDED);
   }

   @Override
   public boolean useShapeForLightOcclusion(BlockState var1) {
      return var1.getValue(EXTENDED);
   }

   @Override
   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return false;
   }
}
