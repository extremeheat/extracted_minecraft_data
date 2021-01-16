package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import javax.annotation.Nullable;

@GwtCompatible
public final class Preconditions {
   private Preconditions() {
      super();
   }

   public static void checkArgument(boolean var0) {
      if (!var0) {
         throw new IllegalArgumentException();
      }
   }

   public static void checkArgument(boolean var0, @Nullable Object var1) {
      if (!var0) {
         throw new IllegalArgumentException(String.valueOf(var1));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object... var2) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, char var2) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, int var2) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, long var2) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, char var2, char var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, char var2, int var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, char var2, long var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, char var2, @Nullable Object var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, int var2, char var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, int var2, int var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, int var2, long var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, int var2, @Nullable Object var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, long var2, char var4) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, long var2, int var4) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, long var2, long var4) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, long var2, @Nullable Object var4) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var4));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2, char var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2, int var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2, long var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3, @Nullable Object var4) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3, var4));
      }
   }

   public static void checkArgument(boolean var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3, @Nullable Object var4, @Nullable Object var5) {
      if (!var0) {
         throw new IllegalArgumentException(format(var1, var2, var3, var4, var5));
      }
   }

   public static void checkState(boolean var0) {
      if (!var0) {
         throw new IllegalStateException();
      }
   }

   public static void checkState(boolean var0, @Nullable Object var1) {
      if (!var0) {
         throw new IllegalStateException(String.valueOf(var1));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object... var2) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, char var2) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, int var2) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, long var2) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, char var2, char var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, char var2, int var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, char var2, long var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, char var2, @Nullable Object var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, int var2, char var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, int var2, int var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, int var2, long var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, int var2, @Nullable Object var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, long var2, char var4) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, long var2, int var4) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, long var2, long var4) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, long var2, @Nullable Object var4) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var4));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2, char var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2, int var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2, long var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3, @Nullable Object var4) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3, var4));
      }
   }

   public static void checkState(boolean var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3, @Nullable Object var4, @Nullable Object var5) {
      if (!var0) {
         throw new IllegalStateException(format(var1, var2, var3, var4, var5));
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable Object var1) {
      if (var0 == null) {
         throw new NullPointerException(String.valueOf(var1));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object... var2) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, char var2) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, int var2) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, long var2) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, char var2, char var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, char var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, char var2, long var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, char var2, @Nullable Object var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, int var2, char var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, int var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, int var2, long var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, int var2, @Nullable Object var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, long var2, char var4) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var4));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, long var2, int var4) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var4));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, long var2, long var4) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var4));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, long var2, @Nullable Object var4) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var4));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2, char var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2, int var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2, long var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3, @Nullable Object var4) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3, var4));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static <T> T checkNotNull(T var0, @Nullable String var1, @Nullable Object var2, @Nullable Object var3, @Nullable Object var4, @Nullable Object var5) {
      if (var0 == null) {
         throw new NullPointerException(format(var1, var2, var3, var4, var5));
      } else {
         return var0;
      }
   }

   @CanIgnoreReturnValue
   public static int checkElementIndex(int var0, int var1) {
      return checkElementIndex(var0, var1, "index");
   }

   @CanIgnoreReturnValue
   public static int checkElementIndex(int var0, int var1, @Nullable String var2) {
      if (var0 >= 0 && var0 < var1) {
         return var0;
      } else {
         throw new IndexOutOfBoundsException(badElementIndex(var0, var1, var2));
      }
   }

   private static String badElementIndex(int var0, int var1, String var2) {
      if (var0 < 0) {
         return format("%s (%s) must not be negative", var2, var0);
      } else if (var1 < 0) {
         throw new IllegalArgumentException("negative size: " + var1);
      } else {
         return format("%s (%s) must be less than size (%s)", var2, var0, var1);
      }
   }

   @CanIgnoreReturnValue
   public static int checkPositionIndex(int var0, int var1) {
      return checkPositionIndex(var0, var1, "index");
   }

   @CanIgnoreReturnValue
   public static int checkPositionIndex(int var0, int var1, @Nullable String var2) {
      if (var0 >= 0 && var0 <= var1) {
         return var0;
      } else {
         throw new IndexOutOfBoundsException(badPositionIndex(var0, var1, var2));
      }
   }

   private static String badPositionIndex(int var0, int var1, String var2) {
      if (var0 < 0) {
         return format("%s (%s) must not be negative", var2, var0);
      } else if (var1 < 0) {
         throw new IllegalArgumentException("negative size: " + var1);
      } else {
         return format("%s (%s) must not be greater than size (%s)", var2, var0, var1);
      }
   }

   public static void checkPositionIndexes(int var0, int var1, int var2) {
      if (var0 < 0 || var1 < var0 || var1 > var2) {
         throw new IndexOutOfBoundsException(badPositionIndexes(var0, var1, var2));
      }
   }

   private static String badPositionIndexes(int var0, int var1, int var2) {
      if (var0 >= 0 && var0 <= var2) {
         return var1 >= 0 && var1 <= var2 ? format("end index (%s) must not be less than start index (%s)", var1, var0) : badPositionIndex(var1, var2, "end index");
      } else {
         return badPositionIndex(var0, var2, "start index");
      }
   }

   static String format(String var0, @Nullable Object... var1) {
      var0 = String.valueOf(var0);
      StringBuilder var2 = new StringBuilder(var0.length() + 16 * var1.length);
      int var3 = 0;

      int var4;
      int var5;
      for(var4 = 0; var4 < var1.length; var3 = var5 + 2) {
         var5 = var0.indexOf("%s", var3);
         if (var5 == -1) {
            break;
         }

         var2.append(var0, var3, var5);
         var2.append(var1[var4++]);
      }

      var2.append(var0, var3, var0.length());
      if (var4 < var1.length) {
         var2.append(" [");
         var2.append(var1[var4++]);

         while(var4 < var1.length) {
            var2.append(", ");
            var2.append(var1[var4++]);
         }

         var2.append(']');
      }

      return var2.toString();
   }
}
