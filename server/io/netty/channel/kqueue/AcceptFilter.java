package io.netty.channel.kqueue;

import io.netty.util.internal.ObjectUtil;

public final class AcceptFilter {
   static final AcceptFilter PLATFORM_UNSUPPORTED = new AcceptFilter("", "");
   private final String filterName;
   private final String filterArgs;

   public AcceptFilter(String var1, String var2) {
      super();
      this.filterName = (String)ObjectUtil.checkNotNull(var1, "filterName");
      this.filterArgs = (String)ObjectUtil.checkNotNull(var2, "filterArgs");
   }

   public String filterName() {
      return this.filterName;
   }

   public String filterArgs() {
      return this.filterArgs;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof AcceptFilter)) {
         return false;
      } else {
         AcceptFilter var2 = (AcceptFilter)var1;
         return this.filterName.equals(var2.filterName) && this.filterArgs.equals(var2.filterArgs);
      }
   }

   public int hashCode() {
      return 31 * (31 + this.filterName.hashCode()) + this.filterArgs.hashCode();
   }

   public String toString() {
      return this.filterName + ", " + this.filterArgs;
   }
}
