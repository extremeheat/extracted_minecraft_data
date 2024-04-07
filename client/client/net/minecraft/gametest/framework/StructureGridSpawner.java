package net.minecraft.gametest.framework;

import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public class StructureGridSpawner implements GameTestRunner.StructureSpawner {
   private static final int SPACE_BETWEEN_COLUMNS = 5;
   private static final int SPACE_BETWEEN_ROWS = 6;
   private final int testsPerRow;
   private int currentRowCount;
   private AABB rowBounds;
   private final BlockPos.MutableBlockPos nextTestNorthWestCorner;
   private final BlockPos firstTestNorthWestCorner;

   public StructureGridSpawner(BlockPos var1, int var2) {
      super();
      this.testsPerRow = var2;
      this.nextTestNorthWestCorner = var1.mutable();
      this.rowBounds = new AABB(this.nextTestNorthWestCorner);
      this.firstTestNorthWestCorner = var1;
   }

   @Override
   public Optional<GameTestInfo> spawnStructure(GameTestInfo var1) {
      BlockPos var2 = new BlockPos(this.nextTestNorthWestCorner);
      var1.setNorthWestCorner(var2);
      var1.prepareTestStructure();
      AABB var3 = StructureUtils.getStructureBounds(var1.getStructureBlockEntity());
      this.rowBounds = this.rowBounds.minmax(var3);
      this.nextTestNorthWestCorner.move((int)var3.getXsize() + 5, 0, 0);
      if (++this.currentRowCount >= this.testsPerRow) {
         this.currentRowCount = 0;
         this.nextTestNorthWestCorner.move(0, 0, (int)this.rowBounds.getZsize() + 6);
         this.nextTestNorthWestCorner.setX(this.firstTestNorthWestCorner.getX());
         this.rowBounds = new AABB(this.nextTestNorthWestCorner);
      }

      return Optional.of(var1);
   }
}
