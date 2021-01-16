package org.apache.logging.log4j.core.filter;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.message.Message;

@Plugin(
   name = "BurstFilter",
   category = "Core",
   elementType = "filter",
   printObject = true
)
public final class BurstFilter extends AbstractFilter {
   private static final long NANOS_IN_SECONDS = 1000000000L;
   private static final int DEFAULT_RATE = 10;
   private static final int DEFAULT_RATE_MULTIPLE = 100;
   private static final int HASH_SHIFT = 32;
   private final Level level;
   private final long burstInterval;
   private final DelayQueue<BurstFilter.LogDelay> history;
   private final Queue<BurstFilter.LogDelay> available;

   static BurstFilter.LogDelay createLogDelay(long var0) {
      return new BurstFilter.LogDelay(var0);
   }

   private BurstFilter(Level var1, float var2, long var3, Filter.Result var5, Filter.Result var6) {
      super(var5, var6);
      this.history = new DelayQueue();
      this.available = new ConcurrentLinkedQueue();
      this.level = var1;
      this.burstInterval = (long)(1.0E9F * ((float)var3 / var2));

      for(int var7 = 0; (long)var7 < var3; ++var7) {
         this.available.add(createLogDelay(0L));
      }

   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(LogEvent var1) {
      return this.filter(var1.getLevel());
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.filter(var2);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      return this.filter(var2);
   }

   private Filter.Result filter(Level var1) {
      if (!this.level.isMoreSpecificThan(var1)) {
         return this.onMatch;
      } else {
         BurstFilter.LogDelay var2;
         for(var2 = (BurstFilter.LogDelay)this.history.poll(); var2 != null; var2 = (BurstFilter.LogDelay)this.history.poll()) {
            this.available.add(var2);
         }

         var2 = (BurstFilter.LogDelay)this.available.poll();
         if (var2 != null) {
            var2.setDelay(this.burstInterval);
            this.history.add(var2);
            return this.onMatch;
         } else {
            return this.onMismatch;
         }
      }
   }

   public int getAvailable() {
      return this.available.size();
   }

   public void clear() {
      Iterator var1 = this.history.iterator();

      while(var1.hasNext()) {
         BurstFilter.LogDelay var2 = (BurstFilter.LogDelay)var1.next();
         this.history.remove(var2);
         this.available.add(var2);
      }

   }

   public String toString() {
      return "level=" + this.level.toString() + ", interval=" + this.burstInterval + ", max=" + this.history.size();
   }

   @PluginBuilderFactory
   public static BurstFilter.Builder newBuilder() {
      return new BurstFilter.Builder();
   }

   // $FF: synthetic method
   BurstFilter(Level var1, float var2, long var3, Filter.Result var5, Filter.Result var6, Object var7) {
      this(var1, var2, var3, var5, var6);
   }

   public static class Builder implements org.apache.logging.log4j.core.util.Builder<BurstFilter> {
      @PluginBuilderAttribute
      private Level level;
      @PluginBuilderAttribute
      private float rate;
      @PluginBuilderAttribute
      private long maxBurst;
      @PluginBuilderAttribute
      private Filter.Result onMatch;
      @PluginBuilderAttribute
      private Filter.Result onMismatch;

      public Builder() {
         super();
         this.level = Level.WARN;
         this.rate = 10.0F;
         this.onMatch = Filter.Result.NEUTRAL;
         this.onMismatch = Filter.Result.DENY;
      }

      public BurstFilter.Builder setLevel(Level var1) {
         this.level = var1;
         return this;
      }

      public BurstFilter.Builder setRate(float var1) {
         this.rate = var1;
         return this;
      }

      public BurstFilter.Builder setMaxBurst(long var1) {
         this.maxBurst = var1;
         return this;
      }

      public BurstFilter.Builder setOnMatch(Filter.Result var1) {
         this.onMatch = var1;
         return this;
      }

      public BurstFilter.Builder setOnMismatch(Filter.Result var1) {
         this.onMismatch = var1;
         return this;
      }

      public BurstFilter build() {
         if (this.rate <= 0.0F) {
            this.rate = 10.0F;
         }

         if (this.maxBurst <= 0L) {
            this.maxBurst = (long)(this.rate * 100.0F);
         }

         return new BurstFilter(this.level, this.rate, this.maxBurst, this.onMatch, this.onMismatch);
      }
   }

   private static class LogDelay implements Delayed {
      private long expireTime;

      LogDelay(long var1) {
         super();
         this.expireTime = var1;
      }

      public void setDelay(long var1) {
         this.expireTime = var1 + System.nanoTime();
      }

      public long getDelay(TimeUnit var1) {
         return var1.convert(this.expireTime - System.nanoTime(), TimeUnit.NANOSECONDS);
      }

      public int compareTo(Delayed var1) {
         long var2 = this.expireTime - ((BurstFilter.LogDelay)var1).expireTime;
         return Long.signum(var2);
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && this.getClass() == var1.getClass()) {
            BurstFilter.LogDelay var2 = (BurstFilter.LogDelay)var1;
            return this.expireTime == var2.expireTime;
         } else {
            return false;
         }
      }

      public int hashCode() {
         return (int)(this.expireTime ^ this.expireTime >>> 32);
      }
   }
}
