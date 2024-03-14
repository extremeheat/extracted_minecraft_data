package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock extends Block {
   public static final MapCodec<TripWireBlock> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("hook").forGetter(var0x -> var0x.hook), propertiesCodec())
            .apply(var0, TripWireBlock::new)
   );
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
   public static final BooleanProperty NORTH = PipeBlock.NORTH;
   public static final BooleanProperty EAST = PipeBlock.EAST;
   public static final BooleanProperty SOUTH = PipeBlock.SOUTH;
   public static final BooleanProperty WEST = PipeBlock.WEST;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
   protected static final VoxelShape AABB = Block.box(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
   protected static final VoxelShape NOT_ATTACHED_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final int RECHECK_PERIOD = 10;
   private final Block hook;

   @Override
   public MapCodec<TripWireBlock> codec() {
      return CODEC;
   }

   public TripWireBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(POWERED, Boolean.valueOf(false))
            .setValue(ATTACHED, Boolean.valueOf(false))
            .setValue(DISARMED, Boolean.valueOf(false))
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
      );
      this.hook = var1;
   }

   @Override
   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var1.getValue(ATTACHED) ? AABB : NOT_ATTACHED_AABB;
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return this.defaultBlockState()
         .setValue(NORTH, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.north()), Direction.NORTH)))
         .setValue(EAST, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.east()), Direction.EAST)))
         .setValue(SOUTH, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.south()), Direction.SOUTH)))
         .setValue(WEST, Boolean.valueOf(this.shouldConnectTo(var2.getBlockState(var3.west()), Direction.WEST)));
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getAxis().isHorizontal()
         ? var1.setValue(PROPERTY_BY_DIRECTION.get(var2), Boolean.valueOf(this.shouldConnectTo(var3, var2)))
         : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   @Override
   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.updateSource(var2, var3, var1);
      }
   }

   @Override
   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         this.updateSource(var2, var3, var1.setValue(POWERED, Boolean.valueOf(true)));
      }
   }

   @Override
   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && !var4.getMainHandItem().isEmpty() && var4.getMainHandItem().is(Items.SHEARS)) {
         var1.setBlock(var2, var3.setValue(DISARMED, Boolean.valueOf(true)), 4);
         var1.gameEvent(var4, GameEvent.SHEAR, var2);
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   private void updateSource(Level var1, BlockPos var2, BlockState var3) {
      for(Direction var7 : new Direction[]{Direction.SOUTH, Direction.WEST}) {
         for(int var8 = 1; var8 < 42; ++var8) {
            BlockPos var9 = var2.relative(var7, var8);
            BlockState var10 = var1.getBlockState(var9);
            if (var10.is(this.hook)) {
               if (var10.getValue(TripWireHookBlock.FACING) == var7.getOpposite()) {
                  TripWireHookBlock.calculateState(var1, var9, var10, false, true, var8, var3);
               }
               break;
            }

            if (!var10.is(this)) {
               break;
            }
         }
      }
   }

   @Override
   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         if (!var1.getValue(POWERED)) {
            this.checkPressed(var2, var3);
         }
      }
   }

   @Override
   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if (var2.getBlockState(var3).getValue(POWERED)) {
         this.checkPressed(var2, var3);
      }
   }

   private void checkPressed(Level var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      boolean var4 = var3.getValue(POWERED);
      boolean var5 = false;
      List var6 = var1.getEntities(null, var3.getShape(var1, var2).bounds().move(var2));
      if (!var6.isEmpty()) {
         for(Entity var8 : var6) {
            if (!var8.isIgnoringBlockTriggers()) {
               var5 = true;
               break;
            }
         }
      }

      if (var5 != var4) {
         var3 = var3.setValue(POWERED, Boolean.valueOf(var5));
         var1.setBlock(var2, var3, 3);
         this.updateSource(var1, var2, var3);
      }

      if (var5) {
         var1.scheduleTick(new BlockPos(var2), this, 10);
      }
   }

   public boolean shouldConnectTo(BlockState var1, Direction var2) {
      if (var1.is(this.hook)) {
         return var1.getValue(TripWireHookBlock.FACING) == var2.getOpposite();
      } else {
         return var1.is(this);
      }
   }

   @Override
   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch(var2) {
         case CLOCKWISE_180:
            return var1.setValue(NORTH, var1.getValue(SOUTH))
               .setValue(EAST, var1.getValue(WEST))
               .setValue(SOUTH, var1.getValue(NORTH))
               .setValue(WEST, var1.getValue(EAST));
         case COUNTERCLOCKWISE_90:
            return var1.setValue(NORTH, var1.getValue(EAST))
               .setValue(EAST, var1.getValue(SOUTH))
               .setValue(SOUTH, var1.getValue(WEST))
               .setValue(WEST, var1.getValue(NORTH));
         case CLOCKWISE_90:
            return var1.setValue(NORTH, var1.getValue(WEST))
               .setValue(EAST, var1.getValue(NORTH))
               .setValue(SOUTH, var1.getValue(EAST))
               .setValue(WEST, var1.getValue(SOUTH));
         default:
            return var1;
      }
   }

   @Override
   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch(var2) {
         case LEFT_RIGHT:
            return var1.setValue(NORTH, var1.getValue(SOUTH)).setValue(SOUTH, var1.getValue(NORTH));
         case FRONT_BACK:
            return var1.setValue(EAST, var1.getValue(WEST)).setValue(WEST, var1.getValue(EAST));
         default:
            return super.mirror(var1, var2);
      }
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
