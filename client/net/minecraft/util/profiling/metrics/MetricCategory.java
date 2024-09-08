package net.minecraft.util.profiling.metrics;

public enum MetricCategory {
   PATH_FINDING("pathfinding"),
   EVENT_LOOPS("event-loops"),
   CONSECUTIVE_EXECUTORS("consecutive-executors"),
   TICK_LOOP("ticking"),
   JVM("jvm"),
   CHUNK_RENDERING("chunk rendering"),
   CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
   CPU("cpu"),
   GPU("gpu");

   private final String description;

   private MetricCategory(final String nullxx) {
      this.description = nullxx;
   }

   public String getDescription() {
      return this.description;
   }
}
