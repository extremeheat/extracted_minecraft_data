package org.apache.logging.log4j.core.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.AbstractLifeCycle;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.message.Message;

public abstract class AbstractFilter extends AbstractLifeCycle implements Filter {
   protected final Filter.Result onMatch;
   protected final Filter.Result onMismatch;

   protected AbstractFilter() {
      this((Filter.Result)null, (Filter.Result)null);
   }

   protected AbstractFilter(Filter.Result var1, Filter.Result var2) {
      super();
      this.onMatch = var1 == null ? Filter.Result.NEUTRAL : var1;
      this.onMismatch = var2 == null ? Filter.Result.DENY : var2;
   }

   protected boolean equalsImpl(Object var1) {
      if (this == var1) {
         return true;
      } else if (!super.equalsImpl(var1)) {
         return false;
      } else if (this.getClass() != var1.getClass()) {
         return false;
      } else {
         AbstractFilter var2 = (AbstractFilter)var1;
         if (this.onMatch != var2.onMatch) {
            return false;
         } else {
            return this.onMismatch == var2.onMismatch;
         }
      }
   }

   public Filter.Result filter(LogEvent var1) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Message var4, Throwable var5) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, Object var4, Throwable var5) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object... var5) {
      return Filter.Result.NEUTRAL;
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5) {
      return this.filter(var1, var2, var3, var4, var5);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6) {
      return this.filter(var1, var2, var3, var4, var5, var6);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
   }

   public Filter.Result filter(Logger var1, Level var2, Marker var3, String var4, Object var5, Object var6, Object var7, Object var8, Object var9, Object var10, Object var11, Object var12, Object var13, Object var14) {
      return this.filter(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   public final Filter.Result getOnMatch() {
      return this.onMatch;
   }

   public final Filter.Result getOnMismatch() {
      return this.onMismatch;
   }

   protected int hashCodeImpl() {
      boolean var1 = true;
      int var2 = super.hashCodeImpl();
      var2 = 31 * var2 + (this.onMatch == null ? 0 : this.onMatch.hashCode());
      var2 = 31 * var2 + (this.onMismatch == null ? 0 : this.onMismatch.hashCode());
      return var2;
   }

   public String toString() {
      return this.getClass().getSimpleName();
   }
}
