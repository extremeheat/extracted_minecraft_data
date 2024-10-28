package net.minecraft.gametest.framework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;

public class StructureGridSpawner implements GameTestRunner.StructureSpawner {
   private static final int SPACE_BETWEEN_COLUMNS = 5;
   private static final int SPACE_BETWEEN_ROWS = 6;
   private final int testsPerRow;
   private int currentRowCount;
   private AABB rowBounds;
   private final BlockPos.MutableBlockPos nextTestNorthWestCorner;
   private final BlockPos firstTestNorthWestCorner;
   private final boolean clearOnBatch;
   private float maxX = -1.0F;
   private final Collection<GameTestInfo> testInLastBatch = new ArrayList();

   public StructureGridSpawner(BlockPos var1, int var2, boolean var3) {
      super();
      this.testsPerRow = var2;
      this.nextTestNorthWestCorner = var1.mutable();
      this.rowBounds = new AABB(this.nextTestNorthWestCorner);
      this.firstTestNorthWestCorner = var1;
      this.clearOnBatch = var3;
   }

   public void onBatchStart(ServerLevel var1) {
      if (this.clearOnBatch) {
         this.testInLastBatch.forEach((var1x) -> {
            BoundingBox var2 = StructureUtils.getStructureBoundingBox(var1x.getStructureBlockEntity());
            StructureUtils.clearSpaceForStructure(var2, var1);
         });
         this.testInLastBatch.clear();
         this.rowBounds = new AABB(this.firstTestNorthWestCorner);
         this.nextTestNorthWestCorner.set(this.firstTestNorthWestCorner);
      }

   }

   public Optional<GameTestInfo> spawnStructure(GameTestInfo var1) {
      BlockPos var2 = new BlockPos(this.nextTestNorthWestCorner);
      var1.setNorthWestCorner(var2);
      var1.prepareTestStructure();
      AABB var3 = StructureUtils.getStructureBounds(var1.getStructureBlockEntity());
      this.rowBounds = this.rowBounds.minmax(var3);
      this.nextTestNorthWestCorner.move((int)var3.getXsize() + 5, 0, 0);
      if ((float)this.nextTestNorthWestCorner.getX() > this.maxX) {
         this.maxX = (float)this.nextTestNorthWestCorner.getX();
      }

      if (++this.currentRowCount >= this.testsPerRow) {
         this.currentRowCount = 0;
         this.nextTestNorthWestCorner.move(0, 0, (int)this.rowBounds.getZsize() + 6);
         this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
         this.rowBounds = new AABB(this.nextTestNorthWestCorner);
      }

      this.testInLastBatch.add(var1);
      return Optional.of(var1);
   }
}
