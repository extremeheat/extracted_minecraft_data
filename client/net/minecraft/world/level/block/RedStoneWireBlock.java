package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.math.Vector3f;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class RedStoneWireBlock extends Block {
   public static final EnumProperty<RedstoneSide> NORTH;
   public static final EnumProperty<RedstoneSide> EAST;
   public static final EnumProperty<RedstoneSide> SOUTH;
   public static final EnumProperty<RedstoneSide> WEST;
   public static final IntegerProperty POWER;
   public static final Map<Direction, EnumProperty<RedstoneSide>> PROPERTY_BY_DIRECTION;
   // $FF: renamed from: H int
   protected static final int field_56 = 1;
   // $FF: renamed from: W int
   protected static final int field_57 = 3;
   // $FF: renamed from: E int
   protected static final int field_58 = 13;
   // $FF: renamed from: N int
   protected static final int field_59 = 3;
   // $FF: renamed from: S int
   protected static final int field_60 = 13;
   private static final VoxelShape SHAPE_DOT;
   private static final Map<Direction, VoxelShape> SHAPES_FLOOR;
   private static final Map<Direction, VoxelShape> SHAPES_UP;
   private static final Map<BlockState, VoxelShape> SHAPES_CACHE;
   private static final Vec3[] COLORS;
   private static final float PARTICLE_DENSITY = 0.2F;
   private final BlockState crossState;
   private boolean shouldSignal = true;

   public RedStoneWireBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(NORTH, RedstoneSide.NONE)).setValue(EAST, RedstoneSide.NONE)).setValue(SOUTH, RedstoneSide.NONE)).setValue(WEST, RedstoneSide.NONE)).setValue(POWER, 0));
      this.crossState = (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, RedstoneSide.SIDE)).setValue(EAST, RedstoneSide.SIDE)).setValue(SOUTH, RedstoneSide.SIDE)).setValue(WEST, RedstoneSide.SIDE);
      UnmodifiableIterator var2 = this.getStateDefinition().getPossibleStates().iterator();

      while(var2.hasNext()) {
         BlockState var3 = (BlockState)var2.next();
         if ((Integer)var3.getValue(POWER) == 0) {
            SHAPES_CACHE.put(var3, this.calculateShape(var3));
         }
      }

   }

   private VoxelShape calculateShape(BlockState var1) {
      VoxelShape var2 = SHAPE_DOT;
      Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

      while(var3.hasNext()) {
         Direction var4 = (Direction)var3.next();
         RedstoneSide var5 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var4));
         if (var5 == RedstoneSide.SIDE) {
            var2 = Shapes.method_31(var2, (VoxelShape)SHAPES_FLOOR.get(var4));
         } else if (var5 == RedstoneSide.field_318) {
            var2 = Shapes.method_31(var2, (VoxelShape)SHAPES_UP.get(var4));
         }
      }

      return var2;
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)SHAPES_CACHE.get(var1.setValue(POWER, 0));
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return this.getConnectionState(var1.getLevel(), this.crossState, var1.getClickedPos());
   }

   private BlockState getConnectionState(BlockGetter var1, BlockState var2, BlockPos var3) {
      boolean var4 = isDot(var2);
      var2 = this.getMissingConnections(var1, (BlockState)this.defaultBlockState().setValue(POWER, (Integer)var2.getValue(POWER)), var3);
      if (var4 && isDot(var2)) {
         return var2;
      } else {
         boolean var5 = ((RedstoneSide)var2.getValue(NORTH)).isConnected();
         boolean var6 = ((RedstoneSide)var2.getValue(SOUTH)).isConnected();
         boolean var7 = ((RedstoneSide)var2.getValue(EAST)).isConnected();
         boolean var8 = ((RedstoneSide)var2.getValue(WEST)).isConnected();
         boolean var9 = !var5 && !var6;
         boolean var10 = !var7 && !var8;
         if (!var8 && var9) {
            var2 = (BlockState)var2.setValue(WEST, RedstoneSide.SIDE);
         }

         if (!var7 && var9) {
            var2 = (BlockState)var2.setValue(EAST, RedstoneSide.SIDE);
         }

         if (!var5 && var10) {
            var2 = (BlockState)var2.setValue(NORTH, RedstoneSide.SIDE);
         }

         if (!var6 && var10) {
            var2 = (BlockState)var2.setValue(SOUTH, RedstoneSide.SIDE);
         }

         return var2;
      }
   }

   private BlockState getMissingConnections(BlockGetter var1, BlockState var2, BlockPos var3) {
      boolean var4 = !var1.getBlockState(var3.above()).isRedstoneConductor(var1, var3);
      Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction var6 = (Direction)var5.next();
         if (!((RedstoneSide)var2.getValue((Property)PROPERTY_BY_DIRECTION.get(var6))).isConnected()) {
            RedstoneSide var7 = this.getConnectingSide(var1, var3, var6, var4);
            var2 = (BlockState)var2.setValue((Property)PROPERTY_BY_DIRECTION.get(var6), var7);
         }
      }

      return var2;
   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var2 == Direction.DOWN) {
         return var1;
      } else if (var2 == Direction.field_526) {
         return this.getConnectionState(var4, var1, var5);
      } else {
         RedstoneSide var7 = this.getConnectingSide(var4, var5, var2);
         return var7.isConnected() == ((RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var2))).isConnected() && !isCross(var1) ? (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), var7) : this.getConnectionState(var4, (BlockState)((BlockState)this.crossState.setValue(POWER, (Integer)var1.getValue(POWER))).setValue((Property)PROPERTY_BY_DIRECTION.get(var2), var7), var5);
      }
   }

   private static boolean isCross(BlockState var0) {
      return ((RedstoneSide)var0.getValue(NORTH)).isConnected() && ((RedstoneSide)var0.getValue(SOUTH)).isConnected() && ((RedstoneSide)var0.getValue(EAST)).isConnected() && ((RedstoneSide)var0.getValue(WEST)).isConnected();
   }

   private static boolean isDot(BlockState var0) {
      return !((RedstoneSide)var0.getValue(NORTH)).isConnected() && !((RedstoneSide)var0.getValue(SOUTH)).isConnected() && !((RedstoneSide)var0.getValue(EAST)).isConnected() && !((RedstoneSide)var0.getValue(WEST)).isConnected();
   }

   public void updateIndirectNeighbourShapes(BlockState var1, LevelAccessor var2, BlockPos var3, int var4, int var5) {
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      Iterator var7 = Direction.Plane.HORIZONTAL.iterator();

      while(var7.hasNext()) {
         Direction var8 = (Direction)var7.next();
         RedstoneSide var9 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var8));
         if (var9 != RedstoneSide.NONE && !var2.getBlockState(var6.setWithOffset(var3, (Direction)var8)).is(this)) {
            var6.move(Direction.DOWN);
            BlockState var10 = var2.getBlockState(var6);
            if (!var10.is(Blocks.OBSERVER)) {
               BlockPos var11 = var6.relative(var8.getOpposite());
               BlockState var12 = var10.updateShape(var8.getOpposite(), var2.getBlockState(var11), var2, var6, var11);
               updateOrDestroy(var10, var12, var2, var6, var4, var5);
            }

            var6.setWithOffset(var3, (Direction)var8).move(Direction.field_526);
            BlockState var14 = var2.getBlockState(var6);
            if (!var14.is(Blocks.OBSERVER)) {
               BlockPos var15 = var6.relative(var8.getOpposite());
               BlockState var13 = var14.updateShape(var8.getOpposite(), var2.getBlockState(var15), var2, var6, var15);
               updateOrDestroy(var14, var13, var2, var6, var4, var5);
            }
         }
      }

   }

   private RedstoneSide getConnectingSide(BlockGetter var1, BlockPos var2, Direction var3) {
      return this.getConnectingSide(var1, var2, var3, !var1.getBlockState(var2.above()).isRedstoneConductor(var1, var2));
   }

   private RedstoneSide getConnectingSide(BlockGetter var1, BlockPos var2, Direction var3, boolean var4) {
      BlockPos var5 = var2.relative(var3);
      BlockState var6 = var1.getBlockState(var5);
      if (var4) {
         boolean var7 = this.canSurviveOn(var1, var5, var6);
         if (var7 && shouldConnectTo(var1.getBlockState(var5.above()))) {
            if (var6.isFaceSturdy(var1, var5, var3.getOpposite())) {
               return RedstoneSide.field_318;
            }

            return RedstoneSide.SIDE;
         }
      }

      return !shouldConnectTo(var6, var3) && (var6.isRedstoneConductor(var1, var5) || !shouldConnectTo(var1.getBlockState(var5.below()))) ? RedstoneSide.NONE : RedstoneSide.SIDE;
   }

   public boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockPos var4 = var3.below();
      BlockState var5 = var2.getBlockState(var4);
      return this.canSurviveOn(var2, var4, var5);
   }

   private boolean canSurviveOn(BlockGetter var1, BlockPos var2, BlockState var3) {
      return var3.isFaceSturdy(var1, var2, Direction.field_526) || var3.is(Blocks.HOPPER);
   }

   private void updatePowerStrength(Level var1, BlockPos var2, BlockState var3) {
      int var4 = this.calculateTargetStrength(var1, var2);
      if ((Integer)var3.getValue(POWER) != var4) {
         if (var1.getBlockState(var2) == var3) {
            var1.setBlock(var2, (BlockState)var3.setValue(POWER, var4), 2);
         }

         HashSet var5 = Sets.newHashSet();
         var5.add(var2);
         Direction[] var6 = Direction.values();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Direction var9 = var6[var8];
            var5.add(var2.relative(var9));
         }

         Iterator var10 = var5.iterator();

         while(var10.hasNext()) {
            BlockPos var11 = (BlockPos)var10.next();
            var1.updateNeighborsAt(var11, this);
         }
      }

   }

   private int calculateTargetStrength(Level var1, BlockPos var2) {
      this.shouldSignal = false;
      int var3 = var1.getBestNeighborSignal(var2);
      this.shouldSignal = true;
      int var4 = 0;
      if (var3 < 15) {
         Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

         while(true) {
            while(var5.hasNext()) {
               Direction var6 = (Direction)var5.next();
               BlockPos var7 = var2.relative(var6);
               BlockState var8 = var1.getBlockState(var7);
               var4 = Math.max(var4, this.getWireSignal(var8));
               BlockPos var9 = var2.above();
               if (var8.isRedstoneConductor(var1, var7) && !var1.getBlockState(var9).isRedstoneConductor(var1, var9)) {
                  var4 = Math.max(var4, this.getWireSignal(var1.getBlockState(var7.above())));
               } else if (!var8.isRedstoneConductor(var1, var7)) {
                  var4 = Math.max(var4, this.getWireSignal(var1.getBlockState(var7.below())));
               }
            }

            return Math.max(var3, var4 - 1);
         }
      } else {
         return Math.max(var3, var4 - 1);
      }
   }

   private int getWireSignal(BlockState var1) {
      return var1.is(this) ? (Integer)var1.getValue(POWER) : 0;
   }

   private void checkCornerChangeAt(Level var1, BlockPos var2) {
      if (var1.getBlockState(var2).is(this)) {
         var1.updateNeighborsAt(var2, this);
         Direction[] var3 = Direction.values();
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Direction var6 = var3[var5];
            var1.updateNeighborsAt(var2.relative(var6), this);
         }

      }
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock()) && !var2.isClientSide) {
         this.updatePowerStrength(var2, var3, var1);
         Iterator var6 = Direction.Plane.VERTICAL.iterator();

         while(var6.hasNext()) {
            Direction var7 = (Direction)var6.next();
            var2.updateNeighborsAt(var3.relative(var7), this);
         }

         this.updateNeighborsOfNeighboringWires(var2, var3);
      }
   }

   public void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         super.onRemove(var1, var2, var3, var4, var5);
         if (!var2.isClientSide) {
            Direction[] var6 = Direction.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Direction var9 = var6[var8];
               var2.updateNeighborsAt(var3.relative(var9), this);
            }

            this.updatePowerStrength(var2, var3, var1);
            this.updateNeighborsOfNeighboringWires(var2, var3);
         }
      }
   }

   private void updateNeighborsOfNeighboringWires(Level var1, BlockPos var2) {
      Iterator var3 = Direction.Plane.HORIZONTAL.iterator();

      Direction var4;
      while(var3.hasNext()) {
         var4 = (Direction)var3.next();
         this.checkCornerChangeAt(var1, var2.relative(var4));
      }

      var3 = Direction.Plane.HORIZONTAL.iterator();

      while(var3.hasNext()) {
         var4 = (Direction)var3.next();
         BlockPos var5 = var2.relative(var4);
         if (var1.getBlockState(var5).isRedstoneConductor(var1, var5)) {
            this.checkCornerChangeAt(var1, var5.above());
         } else {
            this.checkCornerChangeAt(var1, var5.below());
         }
      }

   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (!var2.isClientSide) {
         if (var1.canSurvive(var2, var3)) {
            this.updatePowerStrength(var2, var3, var1);
         } else {
            dropResources(var1, var2, var3);
            var2.removeBlock(var3, false);
         }

      }
   }

   public int getDirectSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      return !this.shouldSignal ? 0 : var1.getSignal(var2, var3, var4);
   }

   public int getSignal(BlockState var1, BlockGetter var2, BlockPos var3, Direction var4) {
      if (this.shouldSignal && var4 != Direction.DOWN) {
         int var5 = (Integer)var1.getValue(POWER);
         if (var5 == 0) {
            return 0;
         } else {
            return var4 != Direction.field_526 && !((RedstoneSide)this.getConnectionState(var2, var1, var3).getValue((Property)PROPERTY_BY_DIRECTION.get(var4.getOpposite()))).isConnected() ? 0 : var5;
         }
      } else {
         return 0;
      }
   }

   protected static boolean shouldConnectTo(BlockState var0) {
      return shouldConnectTo(var0, (Direction)null);
   }

   protected static boolean shouldConnectTo(BlockState var0, @Nullable Direction var1) {
      if (var0.is(Blocks.REDSTONE_WIRE)) {
         return true;
      } else if (var0.is(Blocks.REPEATER)) {
         Direction var2 = (Direction)var0.getValue(RepeaterBlock.FACING);
         return var2 == var1 || var2.getOpposite() == var1;
      } else if (var0.is(Blocks.OBSERVER)) {
         return var1 == var0.getValue(ObserverBlock.FACING);
      } else {
         return var0.isSignalSource() && var1 != null;
      }
   }

   public boolean isSignalSource(BlockState var1) {
      return this.shouldSignal;
   }

   public static int getColorForPower(int var0) {
      Vec3 var1 = COLORS[var0];
      return Mth.color((float)var1.method_2(), (float)var1.method_3(), (float)var1.method_4());
   }

   private void spawnParticlesAlongLine(Level var1, Random var2, BlockPos var3, Vec3 var4, Direction var5, Direction var6, float var7, float var8) {
      float var9 = var8 - var7;
      if (!(var2.nextFloat() >= 0.2F * var9)) {
         float var10 = 0.4375F;
         float var11 = var7 + var9 * var2.nextFloat();
         double var12 = 0.5D + (double)(0.4375F * (float)var5.getStepX()) + (double)(var11 * (float)var6.getStepX());
         double var14 = 0.5D + (double)(0.4375F * (float)var5.getStepY()) + (double)(var11 * (float)var6.getStepY());
         double var16 = 0.5D + (double)(0.4375F * (float)var5.getStepZ()) + (double)(var11 * (float)var6.getStepZ());
         var1.addParticle(new DustParticleOptions(new Vector3f(var4), 1.0F), (double)var3.getX() + var12, (double)var3.getY() + var14, (double)var3.getZ() + var16, 0.0D, 0.0D, 0.0D);
      }
   }

   public void animateTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      int var5 = (Integer)var1.getValue(POWER);
      if (var5 != 0) {
         Iterator var6 = Direction.Plane.HORIZONTAL.iterator();

         while(var6.hasNext()) {
            Direction var7 = (Direction)var6.next();
            RedstoneSide var8 = (RedstoneSide)var1.getValue((Property)PROPERTY_BY_DIRECTION.get(var7));
            switch(var8) {
            case field_318:
               this.spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], var7, Direction.field_526, -0.5F, 0.5F);
            case SIDE:
               this.spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], Direction.DOWN, var7, 0.0F, 0.5F);
               break;
            case NONE:
            default:
               this.spawnParticlesAlongLine(var2, var4, var3, COLORS[var5], Direction.DOWN, var7, 0.0F, 0.3F);
            }
         }

      }
   }

   public BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
      case CLOCKWISE_180:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(SOUTH))).setValue(EAST, (RedstoneSide)var1.getValue(WEST))).setValue(SOUTH, (RedstoneSide)var1.getValue(NORTH))).setValue(WEST, (RedstoneSide)var1.getValue(EAST));
      case COUNTERCLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(EAST))).setValue(EAST, (RedstoneSide)var1.getValue(SOUTH))).setValue(SOUTH, (RedstoneSide)var1.getValue(WEST))).setValue(WEST, (RedstoneSide)var1.getValue(NORTH));
      case CLOCKWISE_90:
         return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(WEST))).setValue(EAST, (RedstoneSide)var1.getValue(NORTH))).setValue(SOUTH, (RedstoneSide)var1.getValue(EAST))).setValue(WEST, (RedstoneSide)var1.getValue(SOUTH));
      default:
         return var1;
      }
   }

   public BlockState mirror(BlockState var1, Mirror var2) {
      switch(var2) {
      case LEFT_RIGHT:
         return (BlockState)((BlockState)var1.setValue(NORTH, (RedstoneSide)var1.getValue(SOUTH))).setValue(SOUTH, (RedstoneSide)var1.getValue(NORTH));
      case FRONT_BACK:
         return (BlockState)((BlockState)var1.setValue(EAST, (RedstoneSide)var1.getValue(WEST))).setValue(WEST, (RedstoneSide)var1.getValue(EAST));
      default:
         return super.mirror(var1, var2);
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, POWER);
   }

   public InteractionResult use(BlockState var1, Level var2, BlockPos var3, Player var4, InteractionHand var5, BlockHitResult var6) {
      if (!var4.getAbilities().mayBuild) {
         return InteractionResult.PASS;
      } else {
         if (isCross(var1) || isDot(var1)) {
            BlockState var7 = isCross(var1) ? this.defaultBlockState() : this.crossState;
            var7 = (BlockState)var7.setValue(POWER, (Integer)var1.getValue(POWER));
            var7 = this.getConnectionState(var2, var7, var3);
            if (var7 != var1) {
               var2.setBlock(var3, var7, 3);
               this.updatesOnShapeChange(var2, var3, var1, var7);
               return InteractionResult.SUCCESS;
            }
         }

         return InteractionResult.PASS;
      }
   }

   private void updatesOnShapeChange(Level var1, BlockPos var2, BlockState var3, BlockState var4) {
      Iterator var5 = Direction.Plane.HORIZONTAL.iterator();

      while(var5.hasNext()) {
         Direction var6 = (Direction)var5.next();
         BlockPos var7 = var2.relative(var6);
         if (((RedstoneSide)var3.getValue((Property)PROPERTY_BY_DIRECTION.get(var6))).isConnected() != ((RedstoneSide)var4.getValue((Property)PROPERTY_BY_DIRECTION.get(var6))).isConnected() && var1.getBlockState(var7).isRedstoneConductor(var1, var7)) {
            var1.updateNeighborsAtExceptFromFacing(var7, var4.getBlock(), var6.getOpposite());
         }
      }

   }

   static {
      NORTH = BlockStateProperties.NORTH_REDSTONE;
      EAST = BlockStateProperties.EAST_REDSTONE;
      SOUTH = BlockStateProperties.SOUTH_REDSTONE;
      WEST = BlockStateProperties.WEST_REDSTONE;
      POWER = BlockStateProperties.POWER;
      PROPERTY_BY_DIRECTION = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST));
      SHAPE_DOT = Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D);
      SHAPES_FLOOR = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D), Direction.SOUTH, Block.box(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D), Direction.EAST, Block.box(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D), Direction.WEST, Block.box(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D)));
      SHAPES_UP = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Shapes.method_31((VoxelShape)SHAPES_FLOOR.get(Direction.NORTH), Block.box(3.0D, 0.0D, 0.0D, 13.0D, 16.0D, 1.0D)), Direction.SOUTH, Shapes.method_31((VoxelShape)SHAPES_FLOOR.get(Direction.SOUTH), Block.box(3.0D, 0.0D, 15.0D, 13.0D, 16.0D, 16.0D)), Direction.EAST, Shapes.method_31((VoxelShape)SHAPES_FLOOR.get(Direction.EAST), Block.box(15.0D, 0.0D, 3.0D, 16.0D, 16.0D, 13.0D)), Direction.WEST, Shapes.method_31((VoxelShape)SHAPES_FLOOR.get(Direction.WEST), Block.box(0.0D, 0.0D, 3.0D, 1.0D, 16.0D, 13.0D))));
      SHAPES_CACHE = Maps.newHashMap();
      COLORS = (Vec3[])Util.make(new Vec3[16], (var0) -> {
         for(int var1 = 0; var1 <= 15; ++var1) {
            float var2 = (float)var1 / 15.0F;
            float var3 = var2 * 0.6F + (var2 > 0.0F ? 0.4F : 0.3F);
            float var4 = Mth.clamp(var2 * var2 * 0.7F - 0.5F, 0.0F, 1.0F);
            float var5 = Mth.clamp(var2 * var2 * 0.6F - 0.7F, 0.0F, 1.0F);
            var0[var1] = new Vec3((double)var3, (double)var4, (double)var5);
         }

      });
   }
}
