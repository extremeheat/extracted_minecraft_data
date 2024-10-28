package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MossyCarpetBlock extends Block implements BonemealableBlock {
   public static final MapCodec<MossyCarpetBlock> CODEC = simpleCodec(MossyCarpetBlock::new);
   public static final BooleanProperty BASE;
   private static final EnumProperty<WallSide> NORTH;
   private static final EnumProperty<WallSide> EAST;
   private static final EnumProperty<WallSide> SOUTH;
   private static final EnumProperty<WallSide> WEST;
   private static final Map<Direction, EnumProperty<WallSide>> PROPERTY_BY_DIRECTION;
   private static final float AABB_OFFSET = 1.0F;
   private static final VoxelShape DOWN_AABB;
   private static final VoxelShape WEST_AABB;
   private static final VoxelShape EAST_AABB;
   private static final VoxelShape NORTH_AABB;
   private static final VoxelShape SOUTH_AABB;
   private static final int SHORT_HEIGHT = 10;
   private static final VoxelShape WEST_SHORT_AABB;
   private static final VoxelShape EAST_SHORT_AABB;
   private static final VoxelShape NORTH_SHORT_AABB;
   private static final VoxelShape SOUTH_SHORT_AABB;
   private final Map<BlockState, VoxelShape> shapesCache;

   public MapCodec<MossyCarpetBlock> codec() {
      return CODEC;
   }

   public MossyCarpetBlock(BlockBehaviour.Properties var1) {
      super(var1);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(BASE, true)).setValue(NORTH, WallSide.NONE)).setValue(EAST, WallSide.NONE)).setValue(SOUTH, WallSide.NONE)).setValue(WEST, WallSide.NONE));
      this.shapesCache = ImmutableMap.copyOf((Map)this.stateDefinition.getPossibleStates().stream().collect(Collectors.toMap(Function.identity(), MossyCarpetBlock::calculateShape)));
   }

   protected VoxelShape getOcclusionShape(BlockState var1) {
      return Shapes.empty();
   }

   private static VoxelShape calculateShape(BlockState var0) {
      VoxelShape var1 = Shapes.empty();
      if ((Boolean)var0.getValue(BASE)) {
         var1 = DOWN_AABB;
      }

      VoxelShape var10000;
      switch ((WallSide)var0.getValue(NORTH)) {
         case NONE -> var10000 = var1;
         case LOW -> var10000 = Shapes.or(var1, NORTH_SHORT_AABB);
         case TALL -> var10000 = Shapes.or(var1, NORTH_AABB);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      var1 = var10000;
      switch ((WallSide)var0.getValue(SOUTH)) {
         case NONE -> var10000 = var1;
         case LOW -> var10000 = Shapes.or(var1, SOUTH_SHORT_AABB);
         case TALL -> var10000 = Shapes.or(var1, SOUTH_AABB);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      var1 = var10000;
      switch ((WallSide)var0.getValue(EAST)) {
         case NONE -> var10000 = var1;
         case LOW -> var10000 = Shapes.or(var1, EAST_SHORT_AABB);
         case TALL -> var10000 = Shapes.or(var1, EAST_AABB);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      var1 = var10000;
      switch ((WallSide)var0.getValue(WEST)) {
         case NONE -> var10000 = var1;
         case LOW -> var10000 = Shapes.or(var1, WEST_SHORT_AABB);
         case TALL -> var10000 = Shapes.or(var1, WEST_AABB);
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      var1 = var10000;
      return var1.isEmpty() ? Shapes.block() : var1;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (VoxelShape)this.shapesCache.get(var1);
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Boolean)var1.getValue(BASE) ? DOWN_AABB : Shapes.empty();
   }

   protected boolean propagatesSkylightDown(BlockState var1) {
      return true;
   }

   protected boolean canSurvive(BlockState var1, LevelReader var2, BlockPos var3) {
      BlockState var4 = var2.getBlockState(var3.below());
      if ((Boolean)var1.getValue(BASE)) {
         return !var4.isAir();
      } else {
         return var4.is(this) && (Boolean)var4.getValue(BASE);
      }
   }

   private static boolean hasFaces(BlockState var0) {
      if ((Boolean)var0.getValue(BASE)) {
         return true;
      } else {
         Iterator var1 = PROPERTY_BY_DIRECTION.values().iterator();

         EnumProperty var2;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            var2 = (EnumProperty)var1.next();
         } while(var0.getValue(var2) == WallSide.NONE);

         return true;
      }
   }

   private static boolean canSupportAtFace(BlockGetter var0, BlockPos var1, Direction var2) {
      if (var2 == Direction.UP) {
         return false;
      } else {
         BlockPos var3 = var1.relative(var2);
         return MultifaceBlock.canAttachTo(var0, var2, var3, var0.getBlockState(var3));
      }
   }

   private static BlockState getUpdatedState(BlockState var0, BlockGetter var1, BlockPos var2, boolean var3) {
      BlockState var4 = null;
      BlockState var5 = null;
      var3 |= (Boolean)var0.getValue(BASE);

      EnumProperty var8;
      WallSide var9;
      for(Iterator var6 = Direction.Plane.HORIZONTAL.iterator(); var6.hasNext(); var0 = (BlockState)var0.setValue(var8, var9)) {
         Direction var7 = (Direction)var6.next();
         var8 = getPropertyForFace(var7);
         var9 = canSupportAtFace(var1, var2, var7) ? (var3 ? WallSide.LOW : (WallSide)var0.getValue(var8)) : WallSide.NONE;
         if (var9 == WallSide.LOW) {
            if (var4 == null) {
               var4 = var1.getBlockState(var2.above());
            }

            if (var4.is(Blocks.PALE_MOSS_CARPET) && var4.getValue(var8) != WallSide.NONE && !(Boolean)var4.getValue(BASE)) {
               var9 = WallSide.TALL;
            }

            if (!(Boolean)var0.getValue(BASE)) {
               if (var5 == null) {
                  var5 = var1.getBlockState(var2.below());
               }

               if (var5.is(Blocks.PALE_MOSS_CARPET) && var5.getValue(var8) == WallSide.NONE) {
                  var9 = WallSide.NONE;
               }
            }
         }
      }

      return var0;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return getUpdatedState(this.defaultBlockState(), var1.getLevel(), var1.getClickedPos(), true);
   }

   public static void placeAt(LevelAccessor var0, BlockPos var1, RandomSource var2, int var3) {
      BlockState var4 = Blocks.PALE_MOSS_CARPET.defaultBlockState();
      BlockState var5 = getUpdatedState(var4, var0, var1, true);
      var0.setBlock(var1, var5, 3);
      Objects.requireNonNull(var2);
      BlockState var6 = createTopperWithSideChance(var0, var1, var2::nextBoolean);
      if (!var6.isAir()) {
         var0.setBlock(var1.above(), var6, var3);
      }

   }

   public void setPlacedBy(Level var1, BlockPos var2, BlockState var3, @Nullable LivingEntity var4, ItemStack var5) {
      if (!var1.isClientSide) {
         RandomSource var6 = var1.getRandom();
         Objects.requireNonNull(var6);
         BlockState var7 = createTopperWithSideChance(var1, var2, var6::nextBoolean);
         if (!var7.isAir()) {
            var1.setBlock(var2.above(), var7, 3);
         }

      }
   }

   private static BlockState createTopperWithSideChance(BlockGetter var0, BlockPos var1, BooleanSupplier var2) {
      BlockPos var3 = var1.above();
      BlockState var4 = var0.getBlockState(var3);
      boolean var5 = var4.is(Blocks.PALE_MOSS_CARPET);
      if ((!var5 || !(Boolean)var4.getValue(BASE)) && (var5 || var4.canBeReplaced())) {
         BlockState var6 = (BlockState)Blocks.PALE_MOSS_CARPET.defaultBlockState().setValue(BASE, false);
         BlockState var7 = getUpdatedState(var6, var0, var1.above(), true);
         Iterator var8 = Direction.Plane.HORIZONTAL.iterator();

         while(var8.hasNext()) {
            Direction var9 = (Direction)var8.next();
            EnumProperty var10 = getPropertyForFace(var9);
            if (var7.getValue(var10) != WallSide.NONE && !var2.getAsBoolean()) {
               var7 = (BlockState)var7.setValue(var10, WallSide.NONE);
            }
         }

         if (hasFaces(var7) && var7 != var4) {
            return var7;
         } else {
            return Blocks.AIR.defaultBlockState();
         }
      } else {
         return Blocks.AIR.defaultBlockState();
      }
   }

   protected BlockState updateShape(BlockState var1, LevelReader var2, ScheduledTickAccess var3, BlockPos var4, Direction var5, BlockPos var6, BlockState var7, RandomSource var8) {
      if (!var1.canSurvive(var2, var4)) {
         return Blocks.AIR.defaultBlockState();
      } else {
         BlockState var9 = getUpdatedState(var1, var2, var4, false);
         return !hasFaces(var9) ? Blocks.AIR.defaultBlockState() : var9;
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(BASE, NORTH, EAST, SOUTH, WEST);
   }

   protected BlockState rotate(BlockState var1, Rotation var2) {
      BlockState var10000;
      switch (var2) {
         case CLOCKWISE_180 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(SOUTH))).setValue(EAST, (WallSide)var1.getValue(WEST))).setValue(SOUTH, (WallSide)var1.getValue(NORTH))).setValue(WEST, (WallSide)var1.getValue(EAST));
         case COUNTERCLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(EAST))).setValue(EAST, (WallSide)var1.getValue(SOUTH))).setValue(SOUTH, (WallSide)var1.getValue(WEST))).setValue(WEST, (WallSide)var1.getValue(NORTH));
         case CLOCKWISE_90 -> var10000 = (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(WEST))).setValue(EAST, (WallSide)var1.getValue(NORTH))).setValue(SOUTH, (WallSide)var1.getValue(EAST))).setValue(WEST, (WallSide)var1.getValue(SOUTH));
         default -> var10000 = var1;
      }

      return var10000;
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      BlockState var10000;
      switch (var2) {
         case LEFT_RIGHT -> var10000 = (BlockState)((BlockState)var1.setValue(NORTH, (WallSide)var1.getValue(SOUTH))).setValue(SOUTH, (WallSide)var1.getValue(NORTH));
         case FRONT_BACK -> var10000 = (BlockState)((BlockState)var1.setValue(EAST, (WallSide)var1.getValue(WEST))).setValue(WEST, (WallSide)var1.getValue(EAST));
         default -> var10000 = super.mirror(var1, var2);
      }

      return var10000;
   }

   @Nullable
   public static EnumProperty<WallSide> getPropertyForFace(Direction var0) {
      return (EnumProperty)PROPERTY_BY_DIRECTION.get(var0);
   }

   public boolean isValidBonemealTarget(LevelReader var1, BlockPos var2, BlockState var3) {
      return (Boolean)var3.getValue(BASE);
   }

   public boolean isBonemealSuccess(Level var1, RandomSource var2, BlockPos var3, BlockState var4) {
      return !createTopperWithSideChance(var1, var3, () -> {
         return true;
      }).isAir();
   }

   public void performBonemeal(ServerLevel var1, RandomSource var2, BlockPos var3, BlockState var4) {
      BlockState var5 = createTopperWithSideChance(var1, var3, () -> {
         return true;
      });
      if (!var5.isAir()) {
         var1.setBlock(var3.above(), var5, 3);
      }

   }

   static {
      BASE = BlockStateProperties.BOTTOM;
      NORTH = BlockStateProperties.NORTH_WALL;
      EAST = BlockStateProperties.EAST_WALL;
      SOUTH = BlockStateProperties.SOUTH_WALL;
      WEST = BlockStateProperties.WEST_WALL;
      PROPERTY_BY_DIRECTION = ImmutableMap.copyOf((Map)Util.make(Maps.newEnumMap(Direction.class), (var0) -> {
         var0.put(Direction.NORTH, NORTH);
         var0.put(Direction.EAST, EAST);
         var0.put(Direction.SOUTH, SOUTH);
         var0.put(Direction.WEST, WEST);
      }));
      DOWN_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0);
      WEST_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0);
      EAST_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0);
      NORTH_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0);
      SOUTH_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0);
      WEST_SHORT_AABB = Block.box(0.0, 0.0, 0.0, 1.0, 10.0, 16.0);
      EAST_SHORT_AABB = Block.box(15.0, 0.0, 0.0, 16.0, 10.0, 16.0);
      NORTH_SHORT_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 1.0);
      SOUTH_SHORT_AABB = Block.box(0.0, 0.0, 15.0, 16.0, 10.0, 16.0);
   }
}
