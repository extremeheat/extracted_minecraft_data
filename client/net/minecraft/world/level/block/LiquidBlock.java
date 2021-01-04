package net.minecraft.world.level.block;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class LiquidBlock extends Block implements BucketPickup {
   public static final IntegerProperty LEVEL;
   protected final FlowingFluid fluid;
   private final List<FluidState> stateCache;

   protected LiquidBlock(FlowingFluid var1, Block.Properties var2) {
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

   public void randomTick(BlockState var1, Level var2, BlockPos var3, Random var4) {
      var2.getFluidState(var3).randomTick(var2, var3, var4);
   }

   public boolean propagatesSkylightDown(BlockState var1, BlockGetter var2, BlockPos var3) {
      return false;
   }

   public boolean isPathfindable(BlockState var1, BlockGetter var2, BlockPos var3, PathComputationType var4) {
      return !this.fluid.is(FluidTags.LAVA);
   }

   public FluidState getFluidState(BlockState var1) {
      int var2 = (Integer)var1.getValue(LEVEL);
      return (FluidState)this.stateCache.get(Math.min(var2, 8));
   }

   public boolean skipRendering(BlockState var1, BlockState var2, Direction var3) {
      return var2.getFluidState().getType().isSame(this.fluid) ? true : super.canOcclude(var1);
   }

   public RenderShape getRenderShape(BlockState var1) {
      return RenderShape.INVISIBLE;
   }

   public List<ItemStack> getDrops(BlockState var1, LootContext.Builder var2) {
      return Collections.emptyList();
   }

   public VoxelShape getShape(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4) {
      return Shapes.empty();
   }

   public int getTickDelay(LevelReader var1) {
      return this.fluid.getTickDelay(var1);
   }

   public void onPlace(BlockState var1, Level var2, BlockPos var3, BlockState var4, boolean var5) {
      if (this.shouldSpreadLiquid(var2, var3, var1)) {
         var2.getLiquidTicks().scheduleTick(var3, var1.getFluidState().getType(), this.getTickDelay(var2));
      }

   }

   public BlockState updateShape(BlockState var1, Direction var2, BlockState var3, LevelAccessor var4, BlockPos var5, BlockPos var6) {
      if (var1.getFluidState().isSource() || var3.getFluidState().isSource()) {
         var4.getLiquidTicks().scheduleTick(var5, var1.getFluidState().getType(), this.getTickDelay(var4));
      }

      return super.updateShape(var1, var2, var3, var4, var5, var6);
   }

   public void neighborChanged(BlockState var1, Level var2, BlockPos var3, Block var4, BlockPos var5, boolean var6) {
      if (this.shouldSpreadLiquid(var2, var3, var1)) {
         var2.getLiquidTicks().scheduleTick(var3, var1.getFluidState().getType(), this.getTickDelay(var2));
      }

   }

   public boolean shouldSpreadLiquid(Level var1, BlockPos var2, BlockState var3) {
      if (this.fluid.is(FluidTags.LAVA)) {
         boolean var4 = false;
         Direction[] var5 = Direction.values();
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Direction var8 = var5[var7];
            if (var8 != Direction.DOWN && var1.getFluidState(var2.relative(var8)).is(FluidTags.WATER)) {
               var4 = true;
               break;
            }
         }

         if (var4) {
            FluidState var9 = var1.getFluidState(var2);
            if (var9.isSource()) {
               var1.setBlockAndUpdate(var2, Blocks.OBSIDIAN.defaultBlockState());
               this.fizz(var1, var2);
               return false;
            }

            if (var9.getHeight(var1, var2) >= 0.44444445F) {
               var1.setBlockAndUpdate(var2, Blocks.COBBLESTONE.defaultBlockState());
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

   public Fluid takeLiquid(LevelAccessor var1, BlockPos var2, BlockState var3) {
      if ((Integer)var3.getValue(LEVEL) == 0) {
         var1.setBlock(var2, Blocks.AIR.defaultBlockState(), 11);
         return this.fluid;
      } else {
         return Fluids.EMPTY;
      }
   }

   public void entityInside(BlockState var1, Level var2, BlockPos var3, Entity var4) {
      if (this.fluid.is(FluidTags.LAVA)) {
         var4.setInLava();
      }

   }

   static {
      LEVEL = BlockStateProperties.LEVEL;
   }
}
