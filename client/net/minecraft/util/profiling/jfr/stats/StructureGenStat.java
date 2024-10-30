package net.minecraft.util.profiling.jfr.stats;

import java.time.Duration;
import jdk.jfr.consumer.RecordedEvent;
import net.minecraft.world.level.ChunkPos;

public record StructureGenStat(Duration duration, ChunkPos chunkPos, String structureName, String level, boolean success) implements TimedStat {
   public StructureGenStat(Duration var1, ChunkPos var2, String var3, String var4, boolean var5) {
      super();
      this.duration = var1;
      this.chunkPos = var2;
      this.structureName = var3;
      this.level = var4;
      this.success = var5;
   }

   public static StructureGenStat from(RecordedEvent var0) {
      return new StructureGenStat(var0.getDuration(), new ChunkPos(var0.getInt("chunkPosX"), var0.getInt("chunkPosX")), var0.getString("structure"), var0.getString("level"), var0.getBoolean("success"));
   }

   public Duration duration() {
      return this.duration;
   }

   public ChunkPos chunkPos() {
      return this.chunkPos;
   }

   public String structureName() {
      return this.structureName;
   }

   public String level() {
      return this.level;
   }

   public boolean success() {
      return this.success;
   }
}
