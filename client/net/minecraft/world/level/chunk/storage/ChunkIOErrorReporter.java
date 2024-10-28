package net.minecraft.world.level.chunk.storage;

import java.util.Objects;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.world.level.ChunkPos;

public interface ChunkIOErrorReporter {
   void reportChunkLoadFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3);

   void reportChunkSaveFailure(Throwable var1, RegionStorageInfo var2, ChunkPos var3);

   static ReportedException createMisplacedChunkReport(ChunkPos var0, ChunkPos var1) {
      String var10002 = String.valueOf(var0);
      CrashReport var2 = CrashReport.forThrowable(new IllegalStateException("Retrieved chunk position " + var10002 + " does not match requested " + String.valueOf(var1)), "Chunk found in invalid location");
      CrashReportCategory var3 = var2.addCategory("Misplaced Chunk");
      Objects.requireNonNull(var0);
      var3.setDetail("Stored Position", var0::toString);
      return new ReportedException(var2);
   }

   default void reportMisplacedChunk(ChunkPos var1, ChunkPos var2, RegionStorageInfo var3) {
      this.reportChunkLoadFailure(createMisplacedChunkReport(var1, var2), var3, var2);
   }
}
