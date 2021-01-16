package com.mojang.serialization;

public class Lifecycle {
   private static final Lifecycle STABLE = new Lifecycle() {
      public String toString() {
         return "Stable";
      }
   };
   private static final Lifecycle EXPERIMENTAL = new Lifecycle() {
      public String toString() {
         return "Experimental";
      }
   };

   private Lifecycle() {
      super();
   }

   public static Lifecycle experimental() {
      return EXPERIMENTAL;
   }

   public static Lifecycle stable() {
      return STABLE;
   }

   public static Lifecycle deprecated(int var0) {
      return new Lifecycle.Deprecated(var0);
   }

   public Lifecycle add(Lifecycle var1) {
      if (this != EXPERIMENTAL && var1 != EXPERIMENTAL) {
         if (this instanceof Lifecycle.Deprecated) {
            return var1 instanceof Lifecycle.Deprecated && ((Lifecycle.Deprecated)var1).since < ((Lifecycle.Deprecated)this).since ? var1 : this;
         } else {
            return var1 instanceof Lifecycle.Deprecated ? var1 : STABLE;
         }
      } else {
         return EXPERIMENTAL;
      }
   }

   // $FF: synthetic method
   Lifecycle(Object var1) {
      this();
   }

   public static final class Deprecated extends Lifecycle {
      private final int since;

      public Deprecated(int var1) {
         super(null);
         this.since = var1;
      }

      public int since() {
         return this.since;
      }
   }
}
