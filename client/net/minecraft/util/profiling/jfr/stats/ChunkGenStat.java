package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.server.level.ColumnPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public record ChunkGenStat(Duration duration, ChunkPos chunkPos, ColumnPos worldPos, ChunkStatus status, String level) implements TimedStat {
   public ChunkGenStat(Duration duration, ChunkPos chunkPos, ColumnPos worldPos, ChunkStatus status, String level) {
      super();
      this.duration = duration;
      this.chunkPos = chunkPos;
      this.worldPos = worldPos;
      this.status = status;
      this.level = level;
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
