package net.minecraft.world.level.block;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlock extends Block implements BucketPickup {
   private static final Codec<FlowingFluid> FLOWING_FLUID;
   public static final MapCodec<LiquidBlock> CODEC;
   public static final IntegerProperty LEVEL;
   protected final FlowingFluid fluid;
   private final List<FluidState> stateCache;
   public static final VoxelShape STABLE_SHAPE;
   public static final ImmutableList<Direction> POSSIBLE_FLOW_DIRECTIONS;

   public MapCodec<LiquidBlock> codec() {
      return CODEC;
   }

   protected LiquidBlock(FlowingFluid var1, BlockBehaviour.Properties var2) {
      super(var2);
      this.fluid = var1;
      this.stateCache = Lists.newArrayList();
      this.stateCache.add(var1.getSource(false));

      for(int var3 = 1; var3 < 8; ++var3) {
         this.stateCache.add(var1.getFlowing(8 - var3, false));
      }

      this.stateCache.add(var1.getFlowing(8, true));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, 0));
   }

   protected VoxelShape getCollisionShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return var4.isAbove(STABLE_SHAPE, var3, true) && (Integer)var1.getValue(LEVEL) == 0 && var4.canStandOnFluid(var2.getFluidState(var3.above()), var1.getFluidState()) ? STABLE_SHAPE : Shapes.empty();
   }

   protected boolean isRandomlyTicking(BlockState var1) {
      return var1.getFluidState().isRandomlyTicking();
   }

   protected void randomTick(BlockState var1, ServerLevel var2, BlockPos var3, RandomSource var4) {
      var1.getFluidState().randomTick(var2, var3, var4);
   }

   protected boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   protected boolean isPathfindable(BlockState var1, PathComputationType var2) {
      return !this.fluid.is(FluidTags.LAVA);
   }

   protected FluidState getFluidState(BlockState var1) {
      int var2 = (Integer)var1.getValue(LEVEL);
      return (FluidState)this.stateCache.get(Math.min(var2, 8));
   }

   protected boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return var2.getFluidState().getType().isSame(this.fluid);
   }

   protected RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   protected List<ItemStack> getDrops(BlockState var1, LootParams.Builder var2) {
      return Collections.emptyList();
   }

   protected VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   protected void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (this.shouldSpreadLiquid(var2, var3, var1)) {
         var2.scheduleTick(var3, var1.getFluidState().getType(), this.fluid.getTickDelay(var2));
      }

   }

   protected BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getFluidState().isSource() || var3.getFluidState().isSource()) {
         var4.scheduleTick(var5, var1.getFluidState().getType(), this.fluid.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   protected void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (this.shouldSpreadLiquid(var2, var3, var1)) {
         var2.scheduleTick(var3, var1.getFluidState().getType(), this.fluid.getTickDelay(var2));
      }

   }

   private boolean shouldSpreadLiquid(Level var1, BlockPos var2, BlockState var3) {
      if (this.fluid.is(FluidTags.LAVA)) {
         boolean var4 = var1.getBlockState(var2.below()).is(Blocks.SOUL_SOIL);
         UnmodifiableIterator var5 = POSSIBLE_FLOW_DIRECTIONS.iterator();

         while(var5.hasNext()) {
            Direction var6 = (Direction)var5.next();
            BlockPos var7 = var2.relative(var6.getOpposite());
            if (var1.getFluidState(var7).is(FluidTags.WATER)) {
               Block var8 = var1.getFluidState(var2).isSource() ? Blocks.OBSIDIAN : Blocks.COBBLESTONE;
               var1.setBlockAndUpdate(var2, var8.defaultBlockState());
               this.fizz(var1, var2);
               return false;
            }

            if (var4 && var1.getBlockState(var7).is(Blocks.BLUE_ICE)) {
               var1.setBlockAndUpdate(var2, Blocks.BASALT.defaultBlockState());
               this.fizz(var1, var2);
               return false;
            }
         }
      }

      return true;
   }

   private void fizz(LevelAccessor var1, BlockPos var2) {
      var1.levelEvent(1501, var2, 0);
   }

   protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> var1) {
      var1.add(LEVEL);
   }

   public ItemStack pickupBlock(@Nullable Player var1, LevelAccessor var2, BlockPos var3, BlockState var4) {
      if ((Integer)var4.getValue(LEVEL) == 0) {
         var2.setBlock(var3, Blocks.AIR.defaultBlockState(), 11);
         return new ItemStack(this.fluid.getBucket());
      } else {
         return ItemStack.EMPTY;
      }
   }

   public Optional<SoundEvent> getPickupSound() {
      return this.fluid.getPickupSound();
   }

   static {
      FLOWING_FLUID = BuiltInRegistries.FLUID.byNameCodec().comapFlatMap((var0) -> {
         DataResult var10000;
         if (var0 instanceof FlowingFluid var1) {
            var10000 = DataResult.success(var1);
         } else {
            var10000 = DataResult.error(() -> {
               return "Not a flowing fluid: " + String.valueOf(var0);
            });
         }

         return var10000;
      }, (var0) -> {
         return var0;
      });
      CODEC = RecordCodecBuilder.mapCodec((var0) -> {
         return var0.group(FLOWING_FLUID.fieldOf("fluid").forGetter((var0x) -> {
            return var0x.fluid;
         }), propertiesCodec()).apply(var0, LiquidBlock::new);
      });
      LEVEL = BlockStateProperties.LEVEL;
      STABLE_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
      POSSIBLE_FLOW_DIRECTIONS = ImmutableList.of(Direction.DOWN, Direction.SOUTH, Direction.NORTH, Direction.EAST, Direction.WEST);
   }
}
