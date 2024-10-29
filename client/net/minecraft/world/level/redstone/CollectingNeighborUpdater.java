package net.minecraft.world.level.redstone;

import com.mojang.logging.LogUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;

public class CollectingNeighborUpdater implements NeighborUpdater {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final Level level;
   private final int maxChainedNeighborUpdates;
   private final ArrayDeque<NeighborUpdates> stack = new ArrayDeque();
   private final List<NeighborUpdates> addedThisLayer = new ArrayList();
   private int count = 0;

   public CollectingNeighborUpdater(Level var1, int var2) {
      super();
      this.level = var1;
      this.maxChainedNeighborUpdates = var2;
   }

   public void shapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6) {
      this.addAndRun(var3, new ShapeUpdate(var1, var2, var3.immutable(), var4.immutable(), var5, var6));
   }

   public void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3) {
      this.addAndRun(var1, new SimpleNeighborUpdate(var1, var2, var3));
   }

   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
      this.addAndRun(var2, new FullNeighborUpdate(var1, var2.immutable(), var3, var4, var5));
   }

   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, @Nullable Direction var3, @Nullable Orientation var4) {
      this.addAndRun(var1, new MultiNeighborUpdate(var1.immutable(), var2, var4, var3));
   }

   private void addAndRun(BlockPos var1, NeighborUpdates var2) {
      boolean var3 = this.count > 0;
      boolean var4 = this.maxChainedNeighborUpdates >= 0 && this.count >= this.maxChainedNeighborUpdates;
      ++this.count;
      if (!var4) {
         if (var3) {
            this.addedThisLayer.add(var2);
         } else {
            this.stack.push(var2);
         }
      } else if (this.count - 1 == this.maxChainedNeighborUpdates) {
         LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + var1.toShortString());
      }

      if (!var3) {
         this.runUpdates();
      }

   }

   private void runUpdates() {
      try {
         while(!this.stack.isEmpty() || !this.addedThisLayer.isEmpty()) {
            for(int var1 = this.addedThisLayer.size() - 1; var1 >= 0; --var1) {
               this.stack.push((NeighborUpdates)this.addedThisLayer.get(var1));
            }

            this.addedThisLayer.clear();
            NeighborUpdates var5 = (NeighborUpdates)this.stack.peek();

            while(this.addedThisLayer.isEmpty()) {
               if (!var5.runNext(this.level)) {
                  this.stack.pop();
                  break;
               }
            }
         }
      } finally {
         this.stack.clear();
         this.addedThisLayer.clear();
         this.count = 0;
      }

   }

   private static record ShapeUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int updateFlags, int updateLimit) implements NeighborUpdates {
      ShapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6) {
         super();
         this.direction = var1;
         this.neighborState = var2;
         this.pos = var3;
         this.neighborPos = var4;
         this.updateFlags = var5;
         this.updateLimit = var6;
      }

      public boolean runNext(Level var1) {
         NeighborUpdater.executeShapeUpdate(var1, this.direction, this.pos, this.neighborPos, this.neighborState, this.updateFlags, this.updateLimit);
         return false;
      }

      public Direction direction() {
         return this.direction;
      }

      public BlockState neighborState() {
         return this.neighborState;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public BlockPos neighborPos() {
         return this.neighborPos;
      }

      public int updateFlags() {
         return this.updateFlags;
      }

      public int updateLimit() {
         return this.updateLimit;
      }
   }

   private interface NeighborUpdates {
      boolean runNext(Level var1);
   }

   static record SimpleNeighborUpdate(BlockPos pos, Block block, @Nullable Orientation orientation) implements NeighborUpdates {
      SimpleNeighborUpdate(BlockPos var1, Block var2, @Nullable Orientation var3) {
         super();
         this.pos = var1;
         this.block = var2;
         this.orientation = var3;
      }

      public boolean runNext(Level var1) {
         BlockState var2 = var1.getBlockState(this.pos);
         NeighborUpdater.executeUpdate(var1, var2, this.pos, this.block, this.orientation, false);
         return false;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public Block block() {
         return this.block;
      }

      @Nullable
      public Orientation orientation() {
         return this.orientation;
      }
   }

   static record FullNeighborUpdate(BlockState state, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) implements NeighborUpdates {
      FullNeighborUpdate(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
         super();
         this.state = var1;
         this.pos = var2;
         this.block = var3;
         this.orientation = var4;
         this.movedByPiston = var5;
      }

      public boolean runNext(Level var1) {
         NeighborUpdater.executeUpdate(var1, this.state, this.pos, this.block, this.orientation, this.movedByPiston);
         return false;
      }

      public BlockState state() {
         return this.state;
      }

      public BlockPos pos() {
         return this.pos;
      }

      public Block block() {
         return this.block;
      }

      @Nullable
      public Orientation orientation() {
         return this.orientation;
      }

      public boolean movedByPiston() {
         return this.movedByPiston;
      }
   }

   static final class MultiNeighborUpdate implements NeighborUpdates {
      private final BlockPos sourcePos;
      private final Block sourceBlock;
      @Nullable
      private Orientation orientation;
      @Nullable
      private final Direction skipDirection;
      private int idx = 0;

      MultiNeighborUpdate(BlockPos var1, Block var2, @Nullable Orientation var3, @Nullable Direction var4) {
         super();
         this.sourcePos = var1;
         this.sourceBlock = var2;
         this.orientation = var3;
         this.skipDirection = var4;
         if (NeighborUpdater.UPDATE_ORDER[this.idx] == var4) {
            ++this.idx;
         }

      }

      public boolean runNext(Level var1) {
         Direction var2 = NeighborUpdater.UPDATE_ORDER[this.idx++];
         BlockPos var3 = this.sourcePos.relative(var2);
         BlockState var4 = var1.getBlockState(var3);
         Orientation var5 = null;
         if (var1.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
            if (this.orientation == null) {
               this.orientation = ExperimentalRedstoneUtils.initialOrientation(var1, this.skipDirection == null ? null : this.skipDirection.getOpposite(), (Direction)null);
            }

            var5 = this.orientation.withFront(var2);
         }

         NeighborUpdater.executeUpdate(var1, var4, var3, this.sourceBlock, var5, false);
         if (this.idx < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.idx] == this.skipDirection) {
            ++this.idx;
         }

         return this.idx < NeighborUpdater.UPDATE_ORDER.length;
      }
   }
}
