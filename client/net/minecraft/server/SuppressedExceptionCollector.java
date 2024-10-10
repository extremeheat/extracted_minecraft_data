package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Queue;
import net.minecraft.util.ArrayListDeque;

public class SuppressedExceptionCollector {
   private static final int LATEST_ENTRY_COUNT = 8;
   private final Queue<SuppressedExceptionCollector.LongEntry> latestEntries = new ArrayListDeque<>();
   private final Object2IntLinkedOpenHashMap<SuppressedExceptionCollector.ShortEntry> entryCounts = new Object2IntLinkedOpenHashMap();

   public SuppressedExceptionCollector() {
      super();
   }

   private static long currentTimeMs() {
      return System.currentTimeMillis();
   }

   public synchronized void addEntry(String var1, Throwable var2) {
      long var3 = currentTimeMs();
      String var5 = var2.getMessage();
      this.latestEntries.add(new SuppressedExceptionCollector.LongEntry(var3, var1, (Class<? extends Throwable>)var2.getClass(), var5));

      while (this.latestEntries.size() > 8) {
         this.latestEntries.remove();
      }

      SuppressedExceptionCollector.ShortEntry var6 = new SuppressedExceptionCollector.ShortEntry(var1, (Class<? extends Throwable>)var2.getClass());
      int var7 = this.entryCounts.getInt(var6);
      this.entryCounts.putAndMoveToFirst(var6, var7 + 1);
   }

   public synchronized String dump() {
      long var1 = currentTimeMs();
      StringBuilder var3 = new StringBuilder();
      if (!this.latestEntries.isEmpty()) {
         var3.append("\n\t\tLatest entries:\n");

         for (SuppressedExceptionCollector.LongEntry var5 : this.latestEntries) {
            var3.append("\t\t\t")
               .append(var5.location)
               .append(":")
               .append(var5.cls)
               .append(": ")
               .append(var5.message)
               .append(" (")
               .append(var1 - var5.timestampMs)
               .append("ms ago)")
               .append("\n");
         }
      }

      if (!this.entryCounts.isEmpty()) {
         if (var3.isEmpty()) {
            var3.append("\n");
         }

         var3.append("\t\tEntry counts:\n");
         ObjectIterator var6 = Object2IntMaps.fastIterable(this.entryCounts).iterator();

         while (var6.hasNext()) {
            Entry var7 = (Entry)var6.next();
            var3.append("\t\t\t")
               .append(((SuppressedExceptionCollector.ShortEntry)var7.getKey()).location)
               .append(":")
               .append(((SuppressedExceptionCollector.ShortEntry)var7.getKey()).cls)
               .append(" x ")
               .append(var7.getIntValue())
               .append("\n");
         }
      }

      return var3.isEmpty() ? "~~NONE~~" : var3.toString();
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException: Cannot invoke "String.equals(Object)" because "varName" is null
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
