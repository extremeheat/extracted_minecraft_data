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
   private final ArrayDeque<CollectingNeighborUpdater.NeighborUpdates> stack = new ArrayDeque<>();
   private final List<CollectingNeighborUpdater.NeighborUpdates> addedThisLayer = new ArrayList<>();
   private int count = 0;

   public CollectingNeighborUpdater(Level var1, int var2) {
      super();
      this.level = var1;
      this.maxChainedNeighborUpdates = var2;
   }

   @Override
   public void shapeUpdate(Direction var1, BlockState var2, BlockPos var3, BlockPos var4, int var5, int var6) {
      this.addAndRun(var3, new CollectingNeighborUpdater.ShapeUpdate(var1, var2, var3.immutable(), var4.immutable(), var5, var6));
   }

   @Override
   public void neighborChanged(BlockPos var1, Block var2, @Nullable Orientation var3) {
      this.addAndRun(var1, new CollectingNeighborUpdater.SimpleNeighborUpdate(var1, var2, var3));
   }

   @Override
   public void neighborChanged(BlockState var1, BlockPos var2, Block var3, @Nullable Orientation var4, boolean var5) {
      this.addAndRun(var2, new CollectingNeighborUpdater.FullNeighborUpdate(var1, var2.immutable(), var3, var4, var5));
   }

   @Override
   public void updateNeighborsAtExceptFromFacing(BlockPos var1, Block var2, @Nullable Direction var3, @Nullable Orientation var4) {
      this.addAndRun(var1, new CollectingNeighborUpdater.MultiNeighborUpdate(var1.immutable(), var2, var4, var3));
   }

   private void addAndRun(BlockPos var1, CollectingNeighborUpdater.NeighborUpdates var2) {
      boolean var3 = this.count > 0;
      boolean var4 = this.maxChainedNeighborUpdates >= 0 && this.count >= this.maxChainedNeighborUpdates;
      this.count++;
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
         while (!this.stack.isEmpty() || !this.addedThisLayer.isEmpty()) {
            for (int var1 = this.addedThisLayer.size() - 1; var1 >= 0; var1--) {
               this.stack.push(this.addedThisLayer.get(var1));
            }

            this.addedThisLayer.clear();
            CollectingNeighborUpdater.NeighborUpdates var5 = this.stack.peek();

            while (this.addedThisLayer.isEmpty()) {
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

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

   static final class MultiNeighborUpdate implements CollectingNeighborUpdater.NeighborUpdates {
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
            this.idx++;
         }
      }

      @Override
      public boolean runNext(Level var1) {
         Direction var2 = NeighborUpdater.UPDATE_ORDER[this.idx++];
         BlockPos var3 = this.sourcePos.relative(var2);
         BlockState var4 = var1.getBlockState(var3);
         Orientation var5 = null;
         if (var1.enabledFeatures().contains(FeatureFlags.REDSTONE_EXPERIMENTS)) {
            if (this.orientation == null) {
               this.orientation = ExperimentalRedstoneUtils.randomOrientation(var1, this.skipDirection == null ? null : this.skipDirection.getOpposite(), null);
            }

            var5 = this.orientation.withFront(var2);
         }

         NeighborUpdater.executeUpdate(var1, var4, var3, this.sourceBlock, var5, false);
         if (this.idx < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.idx] == this.skipDirection) {
            this.idx++;
         }

         return this.idx < NeighborUpdater.UPDATE_ORDER.length;
      }
   }

   interface NeighborUpdates {
      boolean runNext(Level var1);
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
