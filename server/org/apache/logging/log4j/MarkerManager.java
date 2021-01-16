package org.apache.logging.log4j;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.util.PerformanceSensitive;
import org.apache.logging.log4j.util.StringBuilderFormattable;

public final class MarkerManager {
   private static final ConcurrentMap<String, Marker> MARKERS = new ConcurrentHashMap();

   private MarkerManager() {
      super();
   }

   public static void clear() {
      MARKERS.clear();
   }

   public static boolean exists(String var0) {
      return MARKERS.containsKey(var0);
   }

   public static Marker getMarker(String var0) {
      Marker var1 = (Marker)MARKERS.get(var0);
      if (var1 == null) {
         MARKERS.putIfAbsent(var0, new MarkerManager.Log4jMarker(var0));
         var1 = (Marker)MARKERS.get(var0);
      }

      return var1;
   }

   /** @deprecated */
   @Deprecated
   public static Marker getMarker(String var0, String var1) {
      Marker var2 = (Marker)MARKERS.get(var1);
      if (var2 == null) {
         throw new IllegalArgumentException("Parent Marker " + var1 + " has not been defined");
      } else {
         return getMarker(var0, var2);
      }
   }

   /** @deprecated */
   @Deprecated
   public static Marker getMarker(String var0, Marker var1) {
      return getMarker(var0).addParents(var1);
   }

   private static void requireNonNull(Object var0, String var1) {
      if (var0 == null) {
         throw new IllegalArgumentException(var1);
      }
   }

   public static class Log4jMarker implements Marker, StringBuilderFormattable {
      private static final long serialVersionUID = 100L;
      private final String name;
      private volatile Marker[] parents;

      private Log4jMarker() {
         super();
         this.name = null;
         this.parents = null;
      }

      public Log4jMarker(String var1) {
         super();
         MarkerManager.requireNonNull(var1, "Marker name cannot be null.");
         this.name = var1;
         this.parents = null;
      }

      public synchronized Marker addParents(Marker... var1) {
         MarkerManager.requireNonNull(var1, "A parent marker must be specified");
         Marker[] var2 = this.parents;
         int var3 = 0;
         int var4 = var1.length;
         Marker[] var5;
         int var6;
         if (var2 != null) {
            var5 = var1;
            var6 = var1.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Marker var8 = var5[var7];
               if (!contains(var8, var2) && !var8.isInstanceOf((Marker)this)) {
                  ++var3;
               }
            }

            if (var3 == 0) {
               return this;
            }

            var4 = var2.length + var3;
         }

         var5 = new Marker[var4];
         if (var2 != null) {
            System.arraycopy(var2, 0, var5, 0, var2.length);
         }

         var6 = var2 == null ? 0 : var2.length;
         Marker[] var11 = var1;
         int var12 = var1.length;

         for(int var9 = 0; var9 < var12; ++var9) {
            Marker var10 = var11[var9];
            if (var2 == null || !contains(var10, var2) && !var10.isInstanceOf((Marker)this)) {
               var5[var6++] = var10;
            }
         }

         this.parents = var5;
         return this;
      }

      public synchronized boolean remove(Marker var1) {
         MarkerManager.requireNonNull(var1, "A parent marker must be specified");
         Marker[] var2 = this.parents;
         if (var2 == null) {
            return false;
         } else {
            int var3 = var2.length;
            if (var3 == 1) {
               if (var2[0].equals(var1)) {
                  this.parents = null;
                  return true;
               } else {
                  return false;
               }
            } else {
               int var4 = 0;
               Marker[] var5 = new Marker[var3 - 1];

               for(int var6 = 0; var6 < var3; ++var6) {
                  Marker var7 = var2[var6];
                  if (!var7.equals(var1)) {
                     if (var4 == var3 - 1) {
                        return false;
                     }

                     var5[var4++] = var7;
                  }
               }

               this.parents = var5;
               return true;
            }
         }
      }

      public Marker setParents(Marker... var1) {
         if (var1 != null && var1.length != 0) {
            Marker[] var2 = new Marker[var1.length];
            System.arraycopy(var1, 0, var2, 0, var1.length);
            this.parents = var2;
         } else {
            this.parents = null;
         }

         return this;
      }

      public String getName() {
         return this.name;
      }

      public Marker[] getParents() {
         return this.parents == null ? null : (Marker[])Arrays.copyOf(this.parents, this.parents.length);
      }

      public boolean hasParents() {
         return this.parents != null;
      }

      @PerformanceSensitive({"allocation", "unrolled"})
      public boolean isInstanceOf(Marker var1) {
         MarkerManager.requireNonNull(var1, "A marker parameter is required");
         if (this == var1) {
            return true;
         } else {
            Marker[] var2 = this.parents;
            if (var2 != null) {
               int var3 = var2.length;
               if (var3 == 1) {
                  return checkParent(var2[0], var1);
               }

               if (var3 == 2) {
                  return checkParent(var2[0], var1) || checkParent(var2[1], var1);
               }

               for(int var4 = 0; var4 < var3; ++var4) {
                  Marker var5 = var2[var4];
                  if (checkParent(var5, var1)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      @PerformanceSensitive({"allocation", "unrolled"})
      public boolean isInstanceOf(String var1) {
         MarkerManager.requireNonNull(var1, "A marker name is required");
         if (var1.equals(this.getName())) {
            return true;
         } else {
            Marker var2 = (Marker)MarkerManager.MARKERS.get(var1);
            if (var2 == null) {
               return false;
            } else {
               Marker[] var3 = this.parents;
               if (var3 != null) {
                  int var4 = var3.length;
                  if (var4 == 1) {
                     return checkParent(var3[0], var2);
                  }

                  if (var4 == 2) {
                     return checkParent(var3[0], var2) || checkParent(var3[1], var2);
                  }

                  for(int var5 = 0; var5 < var4; ++var5) {
                     Marker var6 = var3[var5];
                     if (checkParent(var6, var2)) {
                        return true;
                     }
                  }
               }

               return false;
            }
         }
      }

      @PerformanceSensitive({"allocation", "unrolled"})
      private static boolean checkParent(Marker var0, Marker var1) {
         if (var0 == var1) {
            return true;
         } else {
            Marker[] var2 = var0 instanceof MarkerManager.Log4jMarker ? ((MarkerManager.Log4jMarker)var0).parents : var0.getParents();
            if (var2 != null) {
               int var3 = var2.length;
               if (var3 == 1) {
                  return checkParent(var2[0], var1);
               }

               if (var3 == 2) {
                  return checkParent(var2[0], var1) || checkParent(var2[1], var1);
               }

               for(int var4 = 0; var4 < var3; ++var4) {
                  Marker var5 = var2[var4];
                  if (checkParent(var5, var1)) {
                     return true;
                  }
               }
            }

            return false;
         }
      }

      @PerformanceSensitive({"allocation"})
      private static boolean contains(Marker var0, Marker... var1) {
         int var2 = 0;

         for(int var3 = var1.length; var2 < var3; ++var2) {
            Marker var4 = var1[var2];
            if (var4 == var0) {
               return true;
            }
         }

         return false;
      }

      public boolean equals(Object var1) {
         if (this == var1) {
            return true;
         } else if (var1 != null && var1 instanceof Marker) {
            Marker var2 = (Marker)var1;
            return this.name.equals(var2.getName());
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.name.hashCode();
      }

      public String toString() {
         StringBuilder var1 = new StringBuilder();
         this.formatTo(var1);
         return var1.toString();
      }

      public void formatTo(StringBuilder var1) {
         var1.append(this.name);
         Marker[] var2 = this.parents;
         if (var2 != null) {
            addParentInfo(var1, var2);
         }

      }

      @PerformanceSensitive({"allocation"})
      private static void addParentInfo(StringBuilder var0, Marker... var1) {
         var0.append("[ ");
         boolean var2 = true;
         int var3 = 0;

         for(int var4 = var1.length; var3 < var4; ++var3) {
            Marker var5 = var1[var3];
            if (!var2) {
               var0.append(", ");
            }

            var2 = false;
            var0.append(var5.getName());
            Marker[] var6 = var5 instanceof MarkerManager.Log4jMarker ? ((MarkerManager.Log4jMarker)var5).parents : var5.getParents();
            if (var6 != null) {
               addParentInfo(var0, var6);
            }
         }

         var0.append(" ]");
      }
   }
}
