package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Queue;
import net.minecraft.util.ArrayListDeque;

public class SuppressedExceptionCollector {
   private static final int LATEST_ENTRY_COUNT = 8;
   private final Queue<LongEntry> latestEntries = new ArrayListDeque<LongEntry>();
   private final Object2IntLinkedOpenHashMap<ShortEntry> entryCounts = new Object2IntLinkedOpenHashMap();

   public SuppressedExceptionCollector() {
      super();
   }

   private static long currentTimeMs() {
      return System.currentTimeMillis();
   }

   public synchronized void addEntry(final String location, final Throwable throwable) {
      long now = currentTimeMs();
      String message = throwable.getMessage();
      this.latestEntries.add(new LongEntry(now, location, throwable.getClass(), message));

      while(this.latestEntries.size() > 8) {
         this.latestEntries.remove();
      }

      ShortEntry key = new ShortEntry(location, throwable.getClass());
      int currentValue = this.entryCounts.getInt(key);
      this.entryCounts.putAndMoveToFirst(key, currentValue + 1);
   }

   public synchronized String dump() {
      long current = currentTimeMs();
      StringBuilder result = new StringBuilder();
      if (!this.latestEntries.isEmpty()) {
         result.append("\n\t\tLatest entries:\n");

         for(LongEntry e : this.latestEntries) {
            result.append("\t\t\t").append(e.location).append(":").append(e.cls).append(": ").append(e.message).append(" (").append(current - e.timestampMs).append("ms ago)").append("\n");
         }
      }

      if (!this.entryCounts.isEmpty()) {
         if (result.isEmpty()) {
            result.append("\n");
         }

         result.append("\t\tEntry counts:\n");
         ObjectIterator var6 = Object2IntMaps.fastIterable(this.entryCounts).iterator();

         while(var6.hasNext()) {
            Object2IntMap.Entry<ShortEntry> e = (Object2IntMap.Entry)var6.next();
            result.append("\t\t\t").append(((ShortEntry)e.getKey()).location).append(":").append(((ShortEntry)e.getKey()).cls).append(" x ").append(e.getIntValue()).append("\n");
         }
      }

      return result.isEmpty() ? "~~NONE~~" : result.toString();
   }

   private static record LongEntry(long timestampMs, String location, Class<? extends Throwable> cls, String message) {
      private LongEntry {
         super();
      }
   }

   private static record ShortEntry(String location, Class<? extends Throwable> cls) {
      private ShortEntry {
         super();
      }
   }
}
