package net.minecraft.gametest.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class StructureGridSpawner implements GameTestRunner.StructureSpawner {
   private static final int SPACE_BETWEEN_COLUMNS = 5;
   private static final int SPACE_BETWEEN_ROWS = 6;
   private final int testsPerRow;
   private final Function<ResourceKey<Level>, BlockPos> firstTestNorthWestCorner;
   private final boolean clearOnBatch;
   private final Map<ResourceKey<Level>, DimensionGridState> grids = new HashMap();

   public StructureGridSpawner(final Function<ResourceKey<Level>, BlockPos> firstTestNorthWestCorner, final int testsPerRow, final boolean clearOnBatch) {
      super();
      this.testsPerRow = testsPerRow;
      this.firstTestNorthWestCorner = firstTestNorthWestCorner;
      this.clearOnBatch = clearOnBatch;
   }

   private DimensionGridState gridFor(final GameTestInfo testInfo) {
      return (DimensionGridState)this.grids.computeIfAbsent(testInfo.getTest().info().dimension(), (dimension) -> new DimensionGridState((BlockPos)this.firstTestNorthWestCorner.apply(dimension)));
   }

   public void onBatchStart(final MinecraftServer server) {
      if (this.clearOnBatch) {
         for(DimensionGridState grid : this.grids.values()) {
            grid.testsInLastBatch.forEach((info) -> {
               BoundingBox boundingBox = info.getTestInstanceBlockEntity().getTestBoundingBox();
               StructureUtils.clearSpaceForStructure(boundingBox, info.getLevel());
            });
            grid.testsInLastBatch.clear();
            grid.nextCorner.set(grid.firstTestNorthWestCorner);
            grid.rowBounds = new AABB(grid.firstTestNorthWestCorner);
            grid.currentRowCount = 0;
         }

      }
   }

   public Optional<GameTestInfo> spawnStructure(final GameTestInfo testInfo) {
      DimensionGridState grid = this.gridFor(testInfo);
      BlockPos northWestCorner = grid.nextCorner.immutable();
      testInfo.setTestBlockPos(northWestCorner);
      GameTestInfo infoWithStructure = testInfo.prepareTestStructure();
      if (infoWithStructure == null) {
         return Optional.empty();
      } else {
         infoWithStructure.startExecution(1);
         AABB structureBounds = testInfo.getTestInstanceBlockEntity().getTestBounds();
         grid.rowBounds = grid.rowBounds.minmax(structureBounds);
         grid.nextCorner.move((int)structureBounds.getXsize() + 5, 0, 0);
         if (++grid.currentRowCount >= this.testsPerRow) {
            grid.currentRowCount = 0;
            grid.nextCorner.move(0, 0, (int)grid.rowBounds.getZsize() + 6);
            grid.nextCorner.setX(grid.firstTestNorthWestCorner.getX());
            grid.rowBounds = new AABB(grid.nextCorner);
         }

         grid.testsInLastBatch.add(testInfo);
         return Optional.of(testInfo);
      }
   }

   private static final class DimensionGridState {
      private final BlockPos firstTestNorthWestCorner;
      private final BlockPos.MutableBlockPos nextCorner;
      private AABB rowBounds;
      private int currentRowCount;
      private final Collection<GameTestInfo> testsInLastBatch = new ArrayList();

      private DimensionGridState(final BlockPos start) {
         super();
         this.firstTestNorthWestCorner = start;
         this.nextCorner = start.mutable();
         this.rowBounds = new AABB(start);
         this.currentRowCount = 0;
      }
   }
}
