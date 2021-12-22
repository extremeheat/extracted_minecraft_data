package net.minecraft.util.profiling.metrics;

public enum MetricCategory {
   PATH_FINDING("pathfinding"),
   EVENT_LOOPS("event-loops"),
   MAIL_BOXES("mailboxes"),
   TICK_LOOP("ticking"),
   JVM("jvm"),
   CHUNK_RENDERING("chunk rendering"),
   CHUNK_RENDERING_DISPATCHING("chunk rendering dispatching"),
   CPU("cpu");

   private final String description;

   private MetricCategory(String var3) {
      this.description = var3;
   }

   public String getDescription() {
      return this.description;
   }

   // $FF: synthetic method
   private static MetricCategory[] $values() {
      return new MetricCategory[]{PATH_FINDING, EVENT_LOOPS, MAIL_BOXES, TICK_LOOP, JVM, CHUNK_RENDERING, CHUNK_RENDERING_DISPATCHING, CPU};
   }
}
