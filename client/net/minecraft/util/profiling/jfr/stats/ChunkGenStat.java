package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public record ChunkGenStat(Duration a, ChunkPos b, ColumnPos c, ChunkStatus d, String e) implements TimedStat {
   private final Duration duration;
   private final ChunkPos chunkPos;
   private final ColumnPos worldPos;
   private final ChunkStatus status;
   private final String level;

   public ChunkGenStat(Duration var1, ChunkPos var2, ColumnPos var3, ChunkStatus var4, String var5) {
      super();
      this.duration = var1;
      this.chunkPos = var2;
      this.worldPos = var3;
      this.status = var4;
      this.level = var5;
   }

   public static ChunkGenStat from(RecordedEvent var0) {
      return new ChunkGenStat(
         var0.getDuration(),
         new ChunkPos(var0.getInt("chunkPosX"), var0.getInt("chunkPosX")),
         new ColumnPos(var0.getInt("worldPosX"), var0.getInt("worldPosZ")),
         ChunkStatus.byName(var0.getString("status")),
         var0.getString("level")
      );
   }
}
