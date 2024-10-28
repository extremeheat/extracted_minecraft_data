package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Iterator;
import java.util.Queue;
import net.minecraft.util.ArrayListDeque;

public class SuppressedExceptionCollector {
   private static final int LATEST_ENTRY_COUNT = 8;
   private final Queue<LongEntry> latestEntries = new ArrayListDeque();
   private final Object2IntLinkedOpenHashMap<ShortEntry> entryCounts = new Object2IntLinkedOpenHashMap();

   public SuppressedExceptionCollector() {
      super();
   }

   private static long currentTimeMs() {
      return System.currentTimeMillis();
   }

   public synchronized void addEntry(String var1, Throwable var2) {
      long var3 = currentTimeMs();
      String var5 = var2.getMessage();
      this.latestEntries.add(new LongEntry(var3, var1, var2.getClass(), var5));

      while(this.latestEntries.size() > 8) {
         this.latestEntries.remove();
      }

      ShortEntry var6 = new ShortEntry(var1, var2.getClass());
      int var7 = this.entryCounts.getInt(var6);
      this.entryCounts.putAndMoveToFirst(var6, var7 + 1);
   }

   public synchronized String dump() {
      long var1 = currentTimeMs();
      StringBuilder var3 = new StringBuilder();
      if (!this.latestEntries.isEmpty()) {
         var3.append("\n\t\tLatest entries:\n");
         Iterator var4 = this.latestEntries.iterator();

         while(var4.hasNext()) {
            LongEntry var5 = (LongEntry)var4.next();
            var3.append("\t\t\t").append(var5.location).append(":").append(var5.cls).append(": ").append(var5.message).append(" (").append(var1 - var5.timestampMs).append("ms ago)").append("\n");
         }
      }

      if (!this.entryCounts.isEmpty()) {
         if (var3.isEmpty()) {
            var3.append("\n");
         }

         var3.append("\t\tEntry counts:\n");
         ObjectIterator var6 = Object2IntMaps.fastIterable(this.entryCounts).iterator();

         while(var6.hasNext()) {
            Object2IntMap.Entry var7 = (Object2IntMap.Entry)var6.next();
            var3.append("\t\t\t").append(((ShortEntry)var7.getKey()).location).append(":").append(((ShortEntry)var7.getKey()).cls).append(" x ").append(var7.getIntValue()).append("\n");
         }
      }

      return var3.isEmpty() ? "~~NONE~~" : var3.toString();
   }

   private static record LongEntry(long timestampMs, String location, Class<? extends Throwable> cls, String message) {
      final long timestampMs;
      final String location;
      final Class<? extends Throwable> cls;
      final String message;

      LongEntry(long var1, String var3, Class<? extends Throwable> var4, String var5) {
         super();
         this.timestampMs = var1;
         this.location = var3;
         this.cls = var4;
         this.message = var5;
      }

      public long timestampMs() {
         return this.timestampMs;
      }

      public String location() {
         return this.location;
      }

      public Class<? extends Throwable> cls() {
         return this.cls;
      }

      public String message() {
         return this.message;
      }
   }

   static record ShortEntry(String location, Class<? extends Throwable> cls) {
      final String location;
      final Class<? extends Throwable> cls;

      ShortEntry(String var1, Class<? extends Throwable> var2) {
         super();
         this.location = var1;
         this.cls = var2;
      }

      public String location() {
         return this.location;
      }

      public Class<? extends Throwable> cls() {
         return this.cls;
      }
   }
}
