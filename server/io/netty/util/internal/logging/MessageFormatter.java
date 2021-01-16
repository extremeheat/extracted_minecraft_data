package io.netty.util.internal.logging;

import java.util.HashSet;
import java.util.Set;

final class MessageFormatter {
   private static final String DELIM_STR = "{}";
   private static final char ESCAPE_CHAR = '\\';

   static FormattingTuple format(String var0, Object var1) {
      return arrayFormat(var0, new Object[]{var1});
   }

   static FormattingTuple format(String var0, Object var1, Object var2) {
      return arrayFormat(var0, new Object[]{var1, var2});
   }

   static FormattingTuple arrayFormat(String var0, Object[] var1) {
      if (var1 != null && var1.length != 0) {
         int var2 = var1.length - 1;
         Object var3 = var1[var2];
         Throwable var4 = var3 instanceof Throwable ? (Throwable)var3 : null;
         if (var0 == null) {
            return new FormattingTuple((String)null, var4);
         } else {
            int var5 = var0.indexOf("{}");
            if (var5 == -1) {
               return new FormattingTuple(var0, var4);
            } else {
               StringBuilder var6 = new StringBuilder(var0.length() + 50);
               int var7 = 0;
               int var8 = 0;

               do {
                  boolean var9 = var5 == 0 || var0.charAt(var5 - 1) != '\\';
                  if (var9) {
                     var6.append(var0, var7, var5);
                  } else {
                     var6.append(var0, var7, var5 - 1);
                     var9 = var5 >= 2 && var0.charAt(var5 - 2) == '\\';
                  }

                  var7 = var5 + 2;
                  if (var9) {
                     deeplyAppendParameter(var6, var1[var8], (Set)null);
                     ++var8;
                     if (var8 > var2) {
                        break;
                     }
                  } else {
                     var6.append("{}");
                  }

                  var5 = var0.indexOf("{}", var7);
               } while(var5 != -1);

               var6.append(var0, var7, var0.length());
               return new FormattingTuple(var6.toString(), var8 <= var2 ? var4 : null);
            }
         }
      } else {
         return new FormattingTuple(var0, (Throwable)null);
      }
   }

   private static void deeplyAppendParameter(StringBuilder var0, Object var1, Set<Object[]> var2) {
      if (var1 == null) {
         var0.append("null");
      } else {
         Class var3 = var1.getClass();
         if (!var3.isArray()) {
            if (Number.class.isAssignableFrom(var3)) {
               if (var3 == Long.class) {
                  var0.append((Long)var1);
               } else if (var3 != Integer.class && var3 != Short.class && var3 != Byte.class) {
                  if (var3 == Double.class) {
                     var0.append((Double)var1);
                  } else if (var3 == Float.class) {
                     var0.append((Float)var1);
                  } else {
                     safeObjectAppend(var0, var1);
                  }
               } else {
                  var0.append(((Number)var1).intValue());
               }
            } else {
               safeObjectAppend(var0, var1);
            }
         } else {
            var0.append('[');
            if (var3 == boolean[].class) {
               booleanArrayAppend(var0, (boolean[])((boolean[])var1));
            } else if (var3 == byte[].class) {
               byteArrayAppend(var0, (byte[])((byte[])var1));
            } else if (var3 == char[].class) {
               charArrayAppend(var0, (char[])((char[])var1));
            } else if (var3 == short[].class) {
               shortArrayAppend(var0, (short[])((short[])var1));
            } else if (var3 == int[].class) {
               intArrayAppend(var0, (int[])((int[])var1));
            } else if (var3 == long[].class) {
               longArrayAppend(var0, (long[])((long[])var1));
            } else if (var3 == float[].class) {
               floatArrayAppend(var0, (float[])((float[])var1));
            } else if (var3 == double[].class) {
               doubleArrayAppend(var0, (double[])((double[])var1));
            } else {
               objectArrayAppend(var0, (Object[])((Object[])var1), var2);
            }

            var0.append(']');
         }

      }
   }

   private static void safeObjectAppend(StringBuilder var0, Object var1) {
      try {
         String var2 = var1.toString();
         var0.append(var2);
      } catch (Throwable var3) {
         System.err.println("SLF4J: Failed toString() invocation on an object of type [" + var1.getClass().getName() + ']');
         var3.printStackTrace();
         var0.append("[FAILED toString()]");
      }

   }

   private static void objectArrayAppend(StringBuilder var0, Object[] var1, Set<Object[]> var2) {
      if (var1.length != 0) {
         if (var2 == null) {
            var2 = new HashSet(var1.length);
         }

         if (((Set)var2).add(var1)) {
            deeplyAppendParameter(var0, var1[0], (Set)var2);

            for(int var3 = 1; var3 < var1.length; ++var3) {
               var0.append(", ");
               deeplyAppendParameter(var0, var1[var3], (Set)var2);
            }

            ((Set)var2).remove(var1);
         } else {
            var0.append("...");
         }

      }
   }

   private static void booleanArrayAppend(StringBuilder var0, boolean[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void byteArrayAppend(StringBuilder var0, byte[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void charArrayAppend(StringBuilder var0, char[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void shortArrayAppend(StringBuilder var0, short[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void intArrayAppend(StringBuilder var0, int[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void longArrayAppend(StringBuilder var0, long[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void floatArrayAppend(StringBuilder var0, float[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private static void doubleArrayAppend(StringBuilder var0, double[] var1) {
      if (var1.length != 0) {
         var0.append(var1[0]);

         for(int var2 = 1; var2 < var1.length; ++var2) {
            var0.append(", ");
            var0.append(var1[var2]);
         }

      }
   }

   private MessageFormatter() {
      super();
   }
}
