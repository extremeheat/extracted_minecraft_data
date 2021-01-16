package org.apache.logging.log4j.core.filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LifeCycle2;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.ObjectArrayIterator;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.PerformanceSensitive;

@Plugin(
   name = "filters",
   category = "Core",
   printObject = true
)
@PerformanceSensitive({"allocation"})
public final class CompositeFilter extends AbstractLifeCycle implements Iterable<Filter>, Filter {
   private static final Filter[] EMPTY_FILTERS = new Filter[0];
   private final Filter[] filters;

   private CompositeFilter() {
      super();
      this.filters = EMPTY_FILTERS;
   }

   private CompositeFilter(Filter[] var1) {
      super();
      this.filters = var1 == null ? EMPTY_FILTERS : var1;
   }

   public CompositeFilter addFilter(Filter var1) {
      if (var1 == null) {
         return this;
      } else if (!(var1 instanceof CompositeFilter)) {
         Filter[] var9 = (Filter[])Arrays.copyOf(this.filters, this.filters.length + 1);
         var9[this.filters.length] = var1;
         return new CompositeFilter(var9);
      } else {
         int var2 = this.filters.length + ((CompositeFilter)var1).size();
         Filter[] var3 = (Filter[])Arrays.copyOf(this.filters, var2);
         int var4 = this.filters.length;
         Filter[] var5 = ((CompositeFilter)var1).filters;
         int var6 = var5.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Filter var8 = var5[var7];
            var3[var4] = var8;
         }

         return new CompositeFilter(var3);
      }
   }

   public CompositeFilter removeFilter(Filter var1) {
      if (var1 == null) {
         return this;
      } else {
         ArrayList var2 = new ArrayList(Arrays.asList(this.filters));
         if (var1 instanceof CompositeFilter) {
            Filter[] var3 = ((CompositeFilter)var1).filters;
            int var4 = var3.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               Filter var6 = var3[var5];
               var2.remove(var6);
            }
         } else {
            var2.remove(var1);
         }

         return new CompositeFilter((Filter[])var2.toArray(new Filter[this.filters.length - 1]));
      }
   }

   public Iterator<Filter> iterator() {
      return new ObjectArrayIterator(this.filters);
   }

   /** @deprecated */
   @Deprecated
   public List<Filter> getFilters() {
      return Arrays.asList(this.filters);
   }

   public Filter[] getFiltersArray() {
      return this.filters;
   }

   public boolean isEmpty() {
      return this.filters.length == 0;
   }

   public int size() {
      return this.filters.length;
   }

   public void start() {
      this.setStarting();
      Filter[] var1 = this.filters;
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Filter var4 = var1[var3];
         var4.start();
      }

      this.setStarted();
   }

   public boolean stop(long var1, TimeUnit var3) {
      this.setStopping();
      Filter[] var4 = this.filters;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         Filter var7 = var4[var6];
         if (var7 instanceof LifeCycle2) {
            ((LifeCycle2)var7).stop(var1, var3);
         } else {
            var7.stop();
         }
      }

      this.setStopped();
      return true;
   }

   public Filter.Result getOnMismatch() {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result getOnMatch() {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      Filter.Result var6 = Filter.Result.NEUTRAL;

      for(int var7 = 0; var7 < this.filters.length; ++var7) {
         var6 = this.filters[var7].filter(var1, var2, var3, var4, var5);
         if (var6 == Filter.Result.ACCEPT || var6 == Filter.Result.DENY) {
            return var6;
         }
      }

      return var6;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      Filter.Result var6 = Filter.Result.NEUTRAL;

      for(int var7 = 0; var7 < this.filters.length; ++var7) {
         var6 = this.filters[var7].filter(var1, var2, var3, var4, var5);
         if (var6 == Filter.Result.ACCEPT || var6 == Filter.Result.DENY) {
            return var6;
         }
      }

      return var6;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      Filter.Result var7 = Filter.Result.NEUTRAL;

      for(int var8 = 0; var8 < this.filters.length; ++var8) {
         var7 = this.filters[var8].filter(var1, var2, var3, var4, var5, var6);
         if (var7 == Filter.Result.ACCEPT || var7 == Filter.Result.DENY) {
            return var7;
         }
      }

      return var7;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      Filter.Result var8 = Filter.Result.NEUTRAL;

      for(int var9 = 0; var9 < this.filters.length; ++var9) {
         var8 = this.filters[var9].filter(var1, var2, var3, var4, var5, var6, var7);
         if (var8 == Filter.Result.ACCEPT || var8 == Filter.Result.DENY) {
            return var8;
         }
      }

      return var8;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      Filter.Result var9 = Filter.Result.NEUTRAL;

      for(int var10 = 0; var10 < this.filters.length; ++var10) {
         var9 = this.filters[var10].filter(var1, var2, var3, var4, var5, var6, var7, var8);
         if (var9 == Filter.Result.ACCEPT || var9 == Filter.Result.DENY) {
            return var9;
         }
      }

      return var9;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      Filter.Result var10 = Filter.Result.NEUTRAL;

      for(int var11 = 0; var11 < this.filters.length; ++var11) {
         var10 = this.filters[var11].filter(var1, var2, var3, var4, var5, var6, var7, var8, var9);
         if (var10 == Filter.Result.ACCEPT || var10 == Filter.Result.DENY) {
            return var10;
         }
      }

      return var10;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      Filter.Result var11 = Filter.Result.NEUTRAL;

      for(int var12 = 0; var12 < this.filters.length; ++var12) {
         var11 = this.filters[var12].filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         if (var11 == Filter.Result.ACCEPT || var11 == Filter.Result.DENY) {
            return var11;
         }
      }

      return var11;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      Filter.Result var12 = Filter.Result.NEUTRAL;

      for(int var13 = 0; var13 < this.filters.length; ++var13) {
         var12 = this.filters[var13].filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
         if (var12 == Filter.Result.ACCEPT || var12 == Filter.Result.DENY) {
            return var12;
         }
      }

      return var12;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      Filter.Result var13 = Filter.Result.NEUTRAL;

      for(int var14 = 0; var14 < this.filters.length; ++var14) {
         var13 = this.filters[var14].filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
         if (var13 == Filter.Result.ACCEPT || var13 == Filter.Result.DENY) {
            return var13;
         }
      }

      return var13;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      Filter.Result var14 = Filter.Result.NEUTRAL;

      for(int var15 = 0; var15 < this.filters.length; ++var15) {
         var14 = this.filters[var15].filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
         if (var14 == Filter.Result.ACCEPT || var14 == Filter.Result.DENY) {
            return var14;
         }
      }

      return var14;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      Filter.Result var15 = Filter.Result.NEUTRAL;

      for(int var16 = 0; var16 < this.filters.length; ++var16) {
         var15 = this.filters[var16].filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
         if (var15 == Filter.Result.ACCEPT || var15 == Filter.Result.DENY) {
            return var15;
         }
      }

      return var15;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      Filter.Result var6 = Filter.Result.NEUTRAL;

      for(int var7 = 0; var7 < this.filters.length; ++var7) {
         var6 = this.filters[var7].filter(var1, var2, var3, var4, var5);
         if (var6 == Filter.Result.ACCEPT || var6 == Filter.Result.DENY) {
            return var6;
         }
      }

      return var6;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      Filter.Result var6 = Filter.Result.NEUTRAL;

      for(int var7 = 0; var7 < this.filters.length; ++var7) {
         var6 = this.filters[var7].filter(var1, var2, var3, var4, var5);
         if (var6 == Filter.Result.ACCEPT || var6 == Filter.Result.DENY) {
            return var6;
         }
      }

      return var6;
   }

   public Filter.Result filter(LogEvent var1) {
      Filter.Result var2 = Filter.Result.NEUTRAL;

      for(int var3 = 0; var3 < this.filters.length; ++var3) {
         var2 = this.filters[var3].filter(var1);
         if (var2 == Filter.Result.ACCEPT || var2 == Filter.Result.DENY) {
            return var2;
         }
      }

      return var2;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();

      for(int var2 = 0; var2 < this.filters.length; ++var2) {
         if (var1.length() == 0) {
            var1.append('{');
         } else {
            var1.append(", ");
         }

         var1.append(this.filters[var2].toString());
      }

      if (var1.length() > 0) {
         var1.append('}');
      }

      return var1.toString();
   }

   @PluginFactory
   public static CompositeFilter createFilters(@PluginElement("Filters") Filter[] var0) {
      return new CompositeFilter(var0);
   }
}
