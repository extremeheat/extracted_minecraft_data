package net.minecraft.world.level.chunk.storage;

import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.world.level.ChunkPos;

public interface ChunkIOErrorReporter {
   void reportChunkLoadFailure(Throwable throwable, RegionStorageInfo storageInfo, ChunkPos pos);

   void reportChunkSaveFailure(Throwable throwable, RegionStorageInfo storageInfo, ChunkPos pos);

   static ReportedException createMisplacedChunkReport(final ChunkPos storedPos, final ChunkPos requestedPos) {
      String var10002 = String.valueOf(storedPos);
      CrashReport report = CrashReport.forThrowable(new IllegalStateException("Retrieved chunk position " + var10002 + " does not match requested " + String.valueOf(requestedPos)), "Chunk found in invalid location");
      CrashReportCategory category = report.addCategory("Misplaced Chunk");
      Objects.requireNonNull(storedPos);
      category.setDetail("Stored Position", storedPos::toString);
      return new ReportedException(report);
   }

   default void reportMisplacedChunk(final ChunkPos storedPos, final ChunkPos requestedPos, final RegionStorageInfo storageInfo) {
      this.reportChunkLoadFailure(createMisplacedChunkReport(storedPos, requestedPos), storageInfo, requestedPos);
   }
}
