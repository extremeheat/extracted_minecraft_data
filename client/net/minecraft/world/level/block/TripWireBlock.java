package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Iterator;
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
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TripWireBlock extends Block {
   public static final MapCodec<TripWireBlock> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return var0.group(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("hook").forGetter((var0x) -> {
         return var0x.hook;
      }), propertiesCodec()).apply(var0, TripWireBlock::new);
   });
   public static final BooleanProperty POWERED;
   public static final BooleanProperty ATTACHED;
   public static final BooleanProperty DISARMED;
   public static final BooleanProperty NORTH;
   public static final BooleanProperty EAST;
   public static final BooleanProperty SOUTH;
   public static final BooleanProperty WEST;
   private static final Map<Direction, BooleanProperty> PROPERTY_BY_DIRECTION;
   protected static final VoxelShape AABB;
   protected static final VoxelShape NOT_ATTACHED_AABB;
   private static final int RECHECK_PERIOD = 10;
   private final Block hook;

   public MapCodec<TripWireBlock> codec() {
      return CODEC;
   }

   public TripWireBlock(Block var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.registerDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(POWERED, false)).setValue(ATTACHED, false)).setValue(DISARMED, false)).setValue(NORTH, false)).setValue(EAST, false)).setValue(SOUTH, false)).setValue(WEST, false));
      this.hook = var1;
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return (Boolean)var1.getValue(ATTACHED) ? AABB : NOT_ATTACHED_AABB;
   }

   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      Level var2 = var1.getLevel();
      BlockPos var3 = var1.getClickedPos();
      return (BlockState)((BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(NORTH, this.shouldConnectTo(var2.getBlockState(var3.north()), Direction.NORTH))).setValue(EAST, this.shouldConnectTo(var2.getBlockState(var3.east()), Direction.EAST))).setValue(SOUTH, this.shouldConnectTo(var2.getBlockState(var3.south()), Direction.SOUTH))).setValue(WEST, this.shouldConnectTo(var2.getBlockState(var3.west()), Direction.WEST));
   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      return var2.getAxis().isHorizontal() ? (BlockState)var1.setValue((Property)PROPERTY_BY_DIRECTION.get(var2), this.shouldConnectTo(var3, var2)) : super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var4.is(var1.getBlock())) {
         this.updateSource(var2, var3, var1);
      }
   }

   protected void onRemove(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (!var5 && !var1.is(var4.getBlock())) {
         this.updateSource(var2, var3, (BlockState)var1.setValue(POWERED, true));
      }
   }

   public BlockState playerWillDestroy(Level var1, BlockPos var2, BlockState var3, Player var4) {
      if (!var1.isClientSide && !var4.getMainHandItem().isEmpty() && var4.getMainHandItem().is(Items.SHEARS)) {
         var1.setBlock(var2, (BlockState)var3.setValue(DISARMED, true), 4);
         var1.gameEvent(var4, GameEvent.SHEAR, var2);
      }

      return super.playerWillDestroy(var1, var2, var3, var4);
   }

   private void updateSource(Level var1, BlockPos var2, BlockState var3) {
      Direction[] var4 = new Direction[]{Direction.SOUTH, Direction.WEST};
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Direction var7 = var4[var6];

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

   protected void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (!var2.isClientSide) {
         if (!(Boolean)var1.getValue(POWERED)) {
            this.checkPressed(var2, var3);
         }
      }
   }

   protected void tick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      if ((Boolean)var2.getBlockState(var3).getValue(POWERED)) {
         this.checkPressed(var2, var3);
      }
   }

   private void checkPressed(Level var1, BlockPos var2) {
      BlockState var3 = var1.getBlockState(var2);
      boolean var4 = (Boolean)var3.getValue(POWERED);
      boolean var5 = false;
      List var6 = var1.getEntities((Entity)null, var3.getShape(var1, var2).bounds().move(var2));
      if (!var6.isEmpty()) {
         Iterator var7 = var6.iterator();

         while(var7.hasNext()) {
            Entity var8 = (Entity)var7.next();
            if (!var8.isIgnoringBlockTriggers()) {
               var5 = true;
               break;
            }
         }
      }

      if (var5 != var4) {
         var3 = (BlockState)var3.setValue(POWERED, var5);
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

   protected BlockState rotate(BlockState var1, Rotation var2) {
      switch (var2) {
         case CLOCKWISE_180 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(SOUTH))).setValue(EAST, (Boolean)var1.getValue(WEST))).setValue(SOUTH, (Boolean)var1.getValue(NORTH))).setValue(WEST, (Boolean)var1.getValue(EAST));
         }
         case COUNTERCLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(EAST))).setValue(EAST, (Boolean)var1.getValue(SOUTH))).setValue(SOUTH, (Boolean)var1.getValue(WEST))).setValue(WEST, (Boolean)var1.getValue(NORTH));
         }
         case CLOCKWISE_90 -> {
            return (BlockState)((BlockState)((BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(WEST))).setValue(EAST, (Boolean)var1.getValue(NORTH))).setValue(SOUTH, (Boolean)var1.getValue(EAST))).setValue(WEST, (Boolean)var1.getValue(SOUTH));
         }
         default -> {
            return var1;
         }
      }
   }

   protected BlockState mirror(BlockState var1, Mirror var2) {
      switch (var2) {
         case LEFT_RIGHT -> {
            return (BlockState)((BlockState)var1.setValue(NORTH, (Boolean)var1.getValue(SOUTH))).setValue(SOUTH, (Boolean)var1.getValue(NORTH));
         }
         case FRONT_BACK -> {
            return (BlockState)((BlockState)var1.setValue(EAST, (Boolean)var1.getValue(WEST))).setValue(WEST, (Boolean)var1.getValue(EAST));
         }
         default -> {
            return super.mirror(var1, var2);
         }
      }
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }

   static {
      POWERED = BlockStateProperties.POWERED;
      ATTACHED = BlockStateProperties.ATTACHED;
      DISARMED = BlockStateProperties.DISARMED;
      NORTH = PipeBlock.NORTH;
      EAST = PipeBlock.EAST;
      SOUTH = PipeBlock.SOUTH;
      WEST = PipeBlock.WEST;
      PROPERTY_BY_DIRECTION = CrossCollisionBlock.PROPERTY_BY_DIRECTION;
      AABB = Block.box(0.0, 1.0, 0.0, 16.0, 2.5, 16.0);
      NOT_ATTACHED_AABB = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   }
}
