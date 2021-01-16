package io.netty.util;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class ResourceLeakDetector<T> {
   private static final String PROP_LEVEL_OLD = "io.netty.leakDetectionLevel";
   private static final String PROP_LEVEL = "io.netty.leakDetection.level";
   private static final ResourceLeakDetector.Level DEFAULT_LEVEL;
   private static final String PROP_TARGET_RECORDS = "io.netty.leakDetection.targetRecords";
   private static final int DEFAULT_TARGET_RECORDS = 4;
   private static final int TARGET_RECORDS;
   private static ResourceLeakDetector.Level level;
   private static final InternalLogger logger;
   static final int DEFAULT_SAMPLING_INTERVAL = 128;
   private final ConcurrentMap<ResourceLeakDetector.DefaultResourceLeak<?>, ResourceLeakDetector.LeakEntry> allLeaks;
   private final ReferenceQueue<Object> refQueue;
   private final ConcurrentMap<String, Boolean> reportedLeaks;
   private final String resourceType;
   private final int samplingInterval;
   private static final AtomicReference<String[]> excludedMethods;

   /** @deprecated */
   @Deprecated
   public static void setEnabled(boolean var0) {
      setLevel(var0 ? ResourceLeakDetector.Level.SIMPLE : ResourceLeakDetector.Level.DISABLED);
   }

   public static boolean isEnabled() {
      return getLevel().ordinal() > ResourceLeakDetector.Level.DISABLED.ordinal();
   }

   public static void setLevel(ResourceLeakDetector.Level var0) {
      if (var0 == null) {
         throw new NullPointerException("level");
      } else {
         level = var0;
      }
   }

   public static ResourceLeakDetector.Level getLevel() {
      return level;
   }

   /** @deprecated */
   @Deprecated
   public ResourceLeakDetector(Class<?> var1) {
      this(StringUtil.simpleClassName(var1));
   }

   /** @deprecated */
   @Deprecated
   public ResourceLeakDetector(String var1) {
      this((String)var1, 128, 9223372036854775807L);
   }

   /** @deprecated */
   @Deprecated
   public ResourceLeakDetector(Class<?> var1, int var2, long var3) {
      this(var1, var2);
   }

   public ResourceLeakDetector(Class<?> var1, int var2) {
      this(StringUtil.simpleClassName(var1), var2, 9223372036854775807L);
   }

   /** @deprecated */
   @Deprecated
   public ResourceLeakDetector(String var1, int var2, long var3) {
      super();
      this.allLeaks = PlatformDependent.newConcurrentHashMap();
      this.refQueue = new ReferenceQueue();
      this.reportedLeaks = PlatformDependent.newConcurrentHashMap();
      if (var1 == null) {
         throw new NullPointerException("resourceType");
      } else {
         this.resourceType = var1;
         this.samplingInterval = var2;
      }
   }

   /** @deprecated */
   @Deprecated
   public final ResourceLeak open(T var1) {
      return this.track0(var1);
   }

   public final ResourceLeakTracker<T> track(T var1) {
      return this.track0(var1);
   }

   private ResourceLeakDetector.DefaultResourceLeak track0(T var1) {
      ResourceLeakDetector.Level var2 = level;
      if (var2 == ResourceLeakDetector.Level.DISABLED) {
         return null;
      } else if (var2.ordinal() < ResourceLeakDetector.Level.PARANOID.ordinal()) {
         if (PlatformDependent.threadLocalRandom().nextInt(this.samplingInterval) == 0) {
            this.reportLeak();
            return new ResourceLeakDetector.DefaultResourceLeak(var1, this.refQueue, this.allLeaks);
         } else {
            return null;
         }
      } else {
         this.reportLeak();
         return new ResourceLeakDetector.DefaultResourceLeak(var1, this.refQueue, this.allLeaks);
      }
   }

   private void clearRefQueue() {
      while(true) {
         ResourceLeakDetector.DefaultResourceLeak var1 = (ResourceLeakDetector.DefaultResourceLeak)this.refQueue.poll();
         if (var1 == null) {
            return;
         }

         var1.dispose();
      }
   }

   private void reportLeak() {
      if (!logger.isErrorEnabled()) {
         this.clearRefQueue();
      } else {
         while(true) {
            ResourceLeakDetector.DefaultResourceLeak var1 = (ResourceLeakDetector.DefaultResourceLeak)this.refQueue.poll();
            if (var1 == null) {
               return;
            }

            if (var1.dispose()) {
               String var2 = var1.toString();
               if (this.reportedLeaks.putIfAbsent(var2, Boolean.TRUE) == null) {
                  if (var2.isEmpty()) {
                     this.reportUntracedLeak(this.resourceType);
                  } else {
                     this.reportTracedLeak(this.resourceType, var2);
                  }
               }
            }
         }
      }
   }

   protected void reportTracedLeak(String var1, String var2) {
      logger.error("LEAK: {}.release() was not called before it's garbage-collected. See http://netty.io/wiki/reference-counted-objects.html for more information.{}", var1, var2);
   }

   protected void reportUntracedLeak(String var1) {
      logger.error("LEAK: {}.release() was not called before it's garbage-collected. Enable advanced leak reporting to find out where the leak occurred. To enable advanced leak reporting, specify the JVM option '-D{}={}' or call {}.setLevel() See http://netty.io/wiki/reference-counted-objects.html for more information.", var1, "io.netty.leakDetection.level", ResourceLeakDetector.Level.ADVANCED.name().toLowerCase(), StringUtil.simpleClassName((Object)this));
   }

   /** @deprecated */
   @Deprecated
   protected void reportInstancesLeak(String var1) {
   }

   public static void addExclusions(Class var0, String... var1) {
      HashSet var2 = new HashSet(Arrays.asList(var1));
      Method[] var3 = var0.getDeclaredMethods();
      int var4 = var3.length;

      int var5;
      for(var5 = 0; var5 < var4; ++var5) {
         Method var6 = var3[var5];
         if (var2.remove(var6.getName()) && var2.isEmpty()) {
            break;
         }
      }

      if (!var2.isEmpty()) {
         throw new IllegalArgumentException("Can't find '" + var2 + "' in " + var0.getName());
      } else {
         String[] var7;
         String[] var8;
         do {
            var7 = (String[])excludedMethods.get();
            var8 = (String[])Arrays.copyOf(var7, var7.length + 2 * var1.length);

            for(var5 = 0; var5 < var1.length; ++var5) {
               var8[var7.length + var5 * 2] = var0.getName();
               var8[var7.length + var5 * 2 + 1] = var1[var5];
            }
         } while(!excludedMethods.compareAndSet(var7, var8));

      }
   }

   static {
      DEFAULT_LEVEL = ResourceLeakDetector.Level.SIMPLE;
      logger = InternalLoggerFactory.getInstance(ResourceLeakDetector.class);
      boolean var0;
      if (SystemPropertyUtil.get("io.netty.noResourceLeakDetection") != null) {
         var0 = SystemPropertyUtil.getBoolean("io.netty.noResourceLeakDetection", false);
         logger.debug("-Dio.netty.noResourceLeakDetection: {}", (Object)var0);
         logger.warn("-Dio.netty.noResourceLeakDetection is deprecated. Use '-D{}={}' instead.", "io.netty.leakDetection.level", DEFAULT_LEVEL.name().toLowerCase());
      } else {
         var0 = false;
      }

      ResourceLeakDetector.Level var1 = var0 ? ResourceLeakDetector.Level.DISABLED : DEFAULT_LEVEL;
      String var2 = SystemPropertyUtil.get("io.netty.leakDetectionLevel", var1.name());
      var2 = SystemPropertyUtil.get("io.netty.leakDetection.level", var2);
      ResourceLeakDetector.Level var3 = ResourceLeakDetector.Level.parseLevel(var2);
      TARGET_RECORDS = SystemPropertyUtil.getInt("io.netty.leakDetection.targetRecords", 4);
      level = var3;
      if (logger.isDebugEnabled()) {
         logger.debug("-D{}: {}", "io.netty.leakDetection.level", var3.name().toLowerCase());
         logger.debug("-D{}: {}", "io.netty.leakDetection.targetRecords", TARGET_RECORDS);
      }

      excludedMethods = new AtomicReference(EmptyArrays.EMPTY_STRINGS);
   }

   private static final class LeakEntry {
      static final ResourceLeakDetector.LeakEntry INSTANCE = new ResourceLeakDetector.LeakEntry();
      private static final int HASH;

      private LeakEntry() {
         super();
      }

      public int hashCode() {
         return HASH;
      }

      public boolean equals(Object var1) {
         return var1 == this;
      }

      static {
         HASH = System.identityHashCode(INSTANCE);
      }
   }

   private static final class Record extends Throwable {
      private static final long serialVersionUID = 6065153674892850720L;
      private static final ResourceLeakDetector.Record BOTTOM = new ResourceLeakDetector.Record();
      private final String hintString;
      private final ResourceLeakDetector.Record next;
      private final int pos;

      Record(ResourceLeakDetector.Record var1, Object var2) {
         super();
         this.hintString = var2 instanceof ResourceLeakHint ? ((ResourceLeakHint)var2).toHintString() : var2.toString();
         this.next = var1;
         this.pos = var1.pos + 1;
      }

      Record(ResourceLeakDetector.Record var1) {
         super();
         this.hintString = null;
         this.next = var1;
         this.pos = var1.pos + 1;
      }

      private Record() {
         super();
         this.hintString = null;
         this.next = null;
         this.pos = -1;
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder(2048);
         if (this.hintString != null) {
            var1.append("\tHint: ").append(this.hintString).append(StringUtil.NEWLINE);
         }

         StackTraceElement[] var2 = this.getStackTrace();

         label30:
         for(int var3 = 3; var3 < var2.length; ++var3) {
            StackTraceElement var4 = var2[var3];
            String[] var5 = (String[])ResourceLeakDetector.excludedMethods.get();

            for(int var6 = 0; var6 < var5.length; var6 += 2) {
               if (var5[var6].equals(var4.getClassName()) && var5[var6 + 1].equals(var4.getMethodName())) {
                  continue label30;
               }
            }

            var1.append('\t');
            var1.append(var4.toString());
            var1.append(StringUtil.NEWLINE);
         }

         return var1.toString();
      }
   }

   private static final class DefaultResourceLeak<T> extends WeakReference<Object> implements ResourceLeakTracker<T>, ResourceLeak {
      private static final AtomicReferenceFieldUpdater<ResourceLeakDetector.DefaultResourceLeak<?>, ResourceLeakDetector.Record> headUpdater = AtomicReferenceFieldUpdater.newUpdater(ResourceLeakDetector.DefaultResourceLeak.class, ResourceLeakDetector.Record.class, "head");
      private static final AtomicIntegerFieldUpdater<ResourceLeakDetector.DefaultResourceLeak<?>> droppedRecordsUpdater = AtomicIntegerFieldUpdater.newUpdater(ResourceLeakDetector.DefaultResourceLeak.class, "droppedRecords");
      private volatile ResourceLeakDetector.Record head;
      private volatile int droppedRecords;
      private final ConcurrentMap<ResourceLeakDetector.DefaultResourceLeak<?>, ResourceLeakDetector.LeakEntry> allLeaks;
      private final int trackedHash;

      DefaultResourceLeak(Object var1, ReferenceQueue<Object> var2, ConcurrentMap<ResourceLeakDetector.DefaultResourceLeak<?>, ResourceLeakDetector.LeakEntry> var3) {
         super(var1, var2);

         assert var1 != null;

         this.trackedHash = System.identityHashCode(var1);
         var3.put(this, ResourceLeakDetector.LeakEntry.INSTANCE);
         headUpdater.set(this, new ResourceLeakDetector.Record(ResourceLeakDetector.Record.BOTTOM));
         this.allLeaks = var3;
      }

      public void record() {
         this.record0((Object)null);
      }

      public void record(Object var1) {
         this.record0(var1);
      }

      private void record0(Object var1) {
         if (ResourceLeakDetector.TARGET_RECORDS > 0) {
            ResourceLeakDetector.Record var2;
            ResourceLeakDetector.Record var3;
            while((var3 = var2 = (ResourceLeakDetector.Record)headUpdater.get(this)) != null) {
               int var6 = var2.pos + 1;
               boolean var5;
               if (var6 >= ResourceLeakDetector.TARGET_RECORDS) {
                  int var7 = Math.min(var6 - ResourceLeakDetector.TARGET_RECORDS, 30);
                  if (var5 = PlatformDependent.threadLocalRandom().nextInt(1 << var7) != 0) {
                     var3 = var2.next;
                  }
               } else {
                  var5 = false;
               }

               ResourceLeakDetector.Record var4 = var1 != null ? new ResourceLeakDetector.Record(var3, var1) : new ResourceLeakDetector.Record(var3);
               if (headUpdater.compareAndSet(this, var2, var4)) {
                  if (var5) {
                     droppedRecordsUpdater.incrementAndGet(this);
                  }

                  return;
               }
            }

         }
      }

      boolean dispose() {
         this.clear();
         return this.allLeaks.remove(this, ResourceLeakDetector.LeakEntry.INSTANCE);
      }

      public boolean close() {
         if (this.allLeaks.remove(this, ResourceLeakDetector.LeakEntry.INSTANCE)) {
            this.clear();
            headUpdater.set(this, (Object)null);
            return true;
         } else {
            return false;
         }
      }

      public boolean close(T var1) {
         assert this.trackedHash == System.identityHashCode(var1);

         return this.close() && var1 != null;
      }

      public String toString() {
         ResourceLeakDetector.Record var1 = (ResourceLeakDetector.Record)headUpdater.getAndSet(this, (Object)null);
         if (var1 == null) {
            return "";
         } else {
            int var2 = droppedRecordsUpdater.get(this);
            int var3 = 0;
            int var4 = var1.pos + 1;
            StringBuilder var5 = (new StringBuilder(var4 * 2048)).append(StringUtil.NEWLINE);
            var5.append("Recent access records: ").append(StringUtil.NEWLINE);
            int var6 = 1;

            for(HashSet var7 = new HashSet(var4); var1 != ResourceLeakDetector.Record.BOTTOM; var1 = var1.next) {
               String var8 = var1.toString();
               if (var7.add(var8)) {
                  if (var1.next == ResourceLeakDetector.Record.BOTTOM) {
                     var5.append("Created at:").append(StringUtil.NEWLINE).append(var8);
                  } else {
                     var5.append('#').append(var6++).append(':').append(StringUtil.NEWLINE).append(var8);
                  }
               } else {
                  ++var3;
               }
            }

            if (var3 > 0) {
               var5.append(": ").append(var2).append(" leak records were discarded because they were duplicates").append(StringUtil.NEWLINE);
            }

            if (var2 > 0) {
               var5.append(": ").append(var2).append(" leak records were discarded because the leak record count is targeted to ").append(ResourceLeakDetector.TARGET_RECORDS).append(". Use system property ").append("io.netty.leakDetection.targetRecords").append(" to increase the limit.").append(StringUtil.NEWLINE);
            }

            var5.setLength(var5.length() - StringUtil.NEWLINE.length());
            return var5.toString();
         }
      }
   }

   public static enum Level {
      DISABLED,
      SIMPLE,
      ADVANCED,
      PARANOID;

      private Level() {
      }

      static ResourceLeakDetector.Level parseLevel(String var0) {
         String var1 = var0.trim();
         ResourceLeakDetector.Level[] var2 = values();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ResourceLeakDetector.Level var5 = var2[var4];
            if (var1.equalsIgnoreCase(var5.name()) || var1.equals(String.valueOf(var5.ordinal()))) {
               return var5;
            }
         }

         return ResourceLeakDetector.DEFAULT_LEVEL;
      }
   }
}
