package net.minecraft.world.level.block;

import com.google.common.base.Suppliers;
import com.mojang.serialization.MapCodec;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class StrongRootsBlock extends PipeBlock implements SimpleWaterloggedBlock {
   public static final SimpleWeightedRandomList<Direction> GROWTH_DIRECTION = SimpleWeightedRandomList.<Direction>builder()
      .add(Direction.DOWN, 10)
      .add(Direction.NORTH)
      .add(Direction.SOUTH)
      .add(Direction.EAST)
      .add(Direction.WEST)
      .build();
   public static final MapCodec<StrongRootsBlock> CODEC = simpleCodec(StrongRootsBlock::new);
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final Supplier<ItemStack> SILK_TOUCH_DROP_TOOL = Suppliers.memoize(() -> {
      ItemStack var0 = new ItemStack(Items.NETHERITE_PICKAXE);
      ItemEnchantments.Mutable var1 = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
      var1.set(Enchantments.SILK_TOUCH, 1);
      var0.set(DataComponents.ENCHANTMENTS, var1.toImmutable());
      return var0;
   });
   private static final Supplier<ItemStack> FORTUNE_DROP_TOOL = Suppliers.memoize(() -> {
      ItemStack var0 = new ItemStack(Items.NETHERITE_PICKAXE);
      ItemEnchantments.Mutable var1 = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
      var1.set(Enchantments.FORTUNE, 3);
      var0.set(DataComponents.ENCHANTMENTS, var1.toImmutable());
      return var0;
   });
   private static final Supplier<ItemStack> PLAIN_DROP_TOOL = Suppliers.memoize(() -> new ItemStack(Items.NETHERITE_PICKAXE));
   public static final SimpleWeightedRandomList<Supplier<ItemStack>> TOOLS = SimpleWeightedRandomList.<Supplier<ItemStack>>builder()
      .add(PLAIN_DROP_TOOL, 3)
      .add(SILK_TOUCH_DROP_TOOL)
      .add(FORTUNE_DROP_TOOL)
      .build();
   public static final StrongRootsBlock.TraceEntry UP_TRACE = new StrongRootsBlock.TraceEntry(Direction.UP, UP);
   private static final List<StrongRootsBlock.TraceEntry> HORIZONTAL_TRACES = List.of(
      new StrongRootsBlock.TraceEntry(Direction.NORTH, NORTH),
      new StrongRootsBlock.TraceEntry(Direction.SOUTH, SOUTH),
      new StrongRootsBlock.TraceEntry(Direction.EAST, EAST),
      new StrongRootsBlock.TraceEntry(Direction.WEST, WEST)
   );

   @Override
   public MapCodec<StrongRootsBlock> codec() {
      return CODEC;
   }

   protected StrongRootsBlock(BlockBehaviour.Properties var1) {
      super(0.3125F, var1);
      this.registerDefaultState(
         this.stateDefinition
            .any()
            .setValue(NORTH, Boolean.valueOf(false))
            .setValue(EAST, Boolean.valueOf(false))
            .setValue(SOUTH, Boolean.valueOf(false))
            .setValue(WEST, Boolean.valueOf(false))
            .setValue(UP, Boolean.valueOf(false))
            .setValue(DOWN, Boolean.valueOf(false))
            .setValue(WATERLOGGED, Boolean.valueOf(false))
      );
   }

   @Override
   public BlockState getStateForPlacement(BlockPlaceContext var1) {
      return getStateWithConnections(var1.getLevel(), var1.getClickedPos(), this.defaultBlockState());
   }

   public static BlockState getStateWithConnections(BlockGetter var0, BlockPos var1, BlockState var2) {
      BlockState var3 = var0.getBlockState(var1.below());
      BlockState var4 = var0.getBlockState(var1.above());
      BlockState var5 = var0.getBlockState(var1.north());
      BlockState var6 = var0.getBlockState(var1.east());
      BlockState var7 = var0.getBlockState(var1.south());
      BlockState var8 = var0.getBlockState(var1.west());
      Block var9 = var2.getBlock();
      return var2.setValue(WATERLOGGED, Boolean.valueOf(var0.getFluidState(var1).getType() == Fluids.WATER))
         .trySetValue(DOWN, Boolean.valueOf(var3.is(var9) || var3.is(Blocks.POWERFUL_POTATO)))
         .trySetValue(UP, Boolean.valueOf(var4.is(var9) || var4.is(Blocks.POWERFUL_POTATO)))
         .trySetValue(NORTH, Boolean.valueOf(var5.is(var9) || var5.is(Blocks.POWERFUL_POTATO)))
         .trySetValue(EAST, Boolean.valueOf(var6.is(var9) || var6.is(Blocks.POWERFUL_POTATO)))
         .trySetValue(SOUTH, Boolean.valueOf(var7.is(var9) || var7.is(Blocks.POWERFUL_POTATO)))
         .trySetValue(WEST, Boolean.valueOf(var8.is(var9) || var8.is(Blocks.POWERFUL_POTATO)));
   }

   private static Optional<BlockPos> traceToTater(ServerLevel var0, BlockPos var1, BlockState var2, RandomSource var3) {
      int var4 = 0;
      BlockPos.MutableBlockPos var5 = var1.mutable();
      BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();
      ArrayList var7 = new ArrayList(5);
      var7.add(UP_TRACE);
      var7.addAll(HORIZONTAL_TRACES);
      List var8 = var7.subList(1, 5);
      Util.shuffle(var8, var3);

      while(var4 < 512) {
         for(StrongRootsBlock.TraceEntry var10 : var7) {
            boolean var11 = var2.getValue(var10.property);
            if (var11) {
               var6.setWithOffset(var5, var10.direction);
               if (var0.isLoaded(var6)) {
                  BlockState var12 = var0.getBlockState(var6);
                  if (var12.is(Blocks.POWERFUL_POTATO)) {
                     return Optional.of(var6);
                  }

                  if (var12.is(Blocks.STRONG_ROOTS)) {
                     var5.set(var6);
                     var2 = var12;
                     ++var4;
                     Util.shuffle(var8, var3);
                     break;
                  }
               }
            }
         }
         break;
      }

      return Optional.empty();
   }

   @Nullable
   private static StrongRootsBlock.FoundPos checkReplacementPos(ServerLevel var0, BlockPos var1) {
      BlockState var2 = var0.getBlockState(var1);
      if (!canReplace(var2)) {
         return null;
      } else {
         BlockState var3 = Blocks.STRONG_ROOTS.defaultBlockState();
         boolean var4 = false;

         for(Direction var8 : Direction.values()) {
            BlockState var9 = var0.getBlockState(var1.relative(var8));
            boolean var10 = var9.is(Blocks.STRONG_ROOTS) || var9.is(Blocks.POWERFUL_POTATO);
            if (var10) {
               if (var4) {
                  return null;
               }

               var4 = true;
               var3 = var3.trySetValue(PROPERTY_BY_DIRECTION.get(var8), Boolean.valueOf(true));
            }
         }

         return var4 ? new StrongRootsBlock.FoundPos(var3, var2) : null;
      }
   }

   @Nullable
   public static List<ItemStack> tryPlace(ServerLevel var0, BlockPos var1, RandomSource var2) {
      StrongRootsBlock.FoundPos var3 = checkReplacementPos(var0, var1);
      return var3 != null ? var3.apply(var0, var1, var2) : null;
   }

   public static boolean canReplace(BlockState var0) {
      if (var0.is(Blocks.POWERFUL_POTATO)) {
         return false;
      } else if (var0.is(BlockTags.FEATURES_CANNOT_REPLACE)) {
         return false;
      } else {
         return !var0.is(Blocks.STRONG_ROOTS);
      }
   }

   @Override
   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getValue(WATERLOGGED)) {
         var4.scheduleTick(var5, Fluids.WATER, Fluids.WATER.getTickDelay(var4));
      }

      boolean var7 = var3.is(this) || var3.is(Blocks.POWERFUL_POTATO);
      return var1.setValue(PROPERTY_BY_DIRECTION.get(var2), Boolean.valueOf(var7));
   }

   @Override
   protected boolean isRandomlyTicking(BlockState var1) {
      int var2 = 0;

      for(Property var4 : PROPERTY_BY_DIRECTION.values()) {
         if (var1.<Boolean>getValue(var4)) {
            ++var2;
         }

         if (var2 > 3) {
            return false;
         }
      }

      return true;
   }

   @Override
   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      GROWTH_DIRECTION.getRandomValue(var4).ifPresent(var4x -> {
         BlockPos var5 = var3.relative(var4x);
         StrongRootsBlock.FoundPos var6 = checkReplacementPos(var2, var5);
         if (var6 != null) {
            traceToTater(var2, var3, var1, var4).ifPresent(var4xx -> var6.apply(var2, var5, var4).forEach(var2xxx -> popResource(var2, var4xx, var2xxx)));
         }
      });
   }

   @Override
   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
   }

   @Override
   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return false;
   }

   @Override
   protected FluidState getFluidState(BlockState var1) {
      return var1.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(var1);
   }

   public static record FoundPos(BlockState a, BlockState b) {
      private final BlockState newState;
      private final BlockState oldState;

      public FoundPos(BlockState var1, BlockState var2) {
         super();
         this.newState = var1;
         this.oldState = var2;
      }

      public List<ItemStack> apply(ServerLevel var1, BlockPos var2, RandomSource var3) {
         boolean var4 = this.oldState.getFluidState().getType() == Fluids.WATER;
         List var5 = Block.getDrops(
            this.oldState, var1, var2, null, null, StrongRootsBlock.TOOLS.getRandomValue(var3).map(Supplier::get).orElse(ItemStack.EMPTY)
         );
         var1.setBlock(var2, this.newState.setValue(StrongRootsBlock.WATERLOGGED, Boolean.valueOf(var4)), 2);
         return var5;
      }
   }

   static record TraceEntry(Direction a, Property<Boolean> b) {
      final Direction direction;
      final Property<Boolean> property;

      TraceEntry(Direction var1, Property<Boolean> var2) {
         super();
         this.direction = var1;
         this.property = var2;
      }
   }
}
