package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class CrashReportCategory {
   private final CrashReport report;
   private final String title;
   private final List<CrashReportCategory.Entry> entries = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(CrashReport var1, String var2) {
      super();
      this.report = var1;
      this.title = var2;
   }

   public static String formatLocation(double var0, double var2, double var4) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", var0, var2, var4, formatLocation(new BlockPos(var0, var2, var4)));
   }

   public static String formatLocation(BlockPos var0) {
      return formatLocation(var0.getX(), var0.getY(), var0.getZ());
   }

   public static String formatLocation(int var0, int var1, int var2) {
      StringBuilder var3 = new StringBuilder();

      try {
         var3.append(String.format("World: (%d,%d,%d)", var0, var1, var2));
      } catch (Throwable var16) {
         var3.append("(Error finding world loc)");
      }

      var3.append(", ");

      int var4;
      int var5;
      int var6;
      int var7;
      int var8;
      int var9;
      int var10;
      int var11;
      int var12;
      try {
         var4 = var0 >> 4;
         var5 = var2 >> 4;
         var6 = var0 & 15;
         var7 = var1 >> 4;
         var8 = var2 & 15;
         var9 = var4 << 4;
         var10 = var5 << 4;
         var11 = (var4 + 1 << 4) - 1;
         var12 = (var5 + 1 << 4) - 1;
         var3.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", var6, var7, var8, var4, var5, var9, var10, var11, var12));
      } catch (Throwable var15) {
         var3.append("(Error finding chunk loc)");
      }

      var3.append(", ");

      try {
         var4 = var0 >> 9;
         var5 = var2 >> 9;
         var6 = var4 << 5;
         var7 = var5 << 5;
         var8 = (var4 + 1 << 5) - 1;
         var9 = (var5 + 1 << 5) - 1;
         var10 = var4 << 9;
         var11 = var5 << 9;
         var12 = (var4 + 1 << 9) - 1;
         int var13 = (var5 + 1 << 9) - 1;
         var3.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", var4, var5, var6, var7, var8, var9, var10, var11, var12, var13));
      } catch (Throwable var14) {
         var3.append("(Error finding world loc)");
      }

      return var3.toString();
   }

   public CrashReportCategory setDetail(String var1, CrashReportDetail<String> var2) {
      try {
         this.setDetail(var1, var2.call());
      } catch (Throwable var4) {
         this.setDetailError(var1, var4);
      }

      return this;
   }

   public CrashReportCategory setDetail(String var1, Object var2) {
      this.entries.add(new CrashReportCategory.Entry(var1, var2));
      return this;
   }

   public void setDetailError(String var1, Throwable var2) {
      this.setDetail(var1, (Object)var2);
   }

   public int fillInStackTrace(int var1) {
      StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
      if (var2.length <= 0) {
         return 0;
      } else {
         this.stackTrace = new StackTraceElement[var2.length - 3 - var1];
         System.arraycopy(var2, 3 + var1, this.stackTrace, 0, this.stackTrace.length);
         return this.stackTrace.length;
      }
   }

   public boolean validateStackTrace(StackTraceElement var1, StackTraceElement var2) {
      if (this.stackTrace.length != 0 && var1 != null) {
         StackTraceElement var3 = this.stackTrace[0];
         if (var3.isNativeMethod() == var1.isNativeMethod() && var3.getClassName().equals(var1.getClassName()) && var3.getFileName().equals(var1.getFileName()) && var3.getMethodName().equals(var1.getMethodName())) {
            if (var2 != null != this.stackTrace.length > 1) {
               return false;
            } else if (var2 != null && !this.stackTrace[1].equals(var2)) {
               return false;
            } else {
               this.stackTrace[0] = var1;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void trimStacktrace(int var1) {
      StackTraceElement[] var2 = new StackTraceElement[this.stackTrace.length - var1];
      System.arraycopy(this.stackTrace, 0, var2, 0, var2.length);
      this.stackTrace = var2;
   }

   public void getDetails(StringBuilder var1) {
      var1.append("-- ").append(this.title).append(" --\n");
      var1.append("Details:");
      Iterator var2 = this.entries.iterator();

      while(var2.hasNext()) {
         CrashReportCategory.Entry var3 = (CrashReportCategory.Entry)var2.next();
         var1.append("\n\t");
         var1.append(var3.getKey());
         var1.append(": ");
         var1.append(var3.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         var1.append("\nStacktrace:");
         StackTraceElement[] var6 = this.stackTrace;
         int var7 = var6.length;

         for(int var4 = 0; var4 < var7; ++var4) {
            StackTraceElement var5 = var6[var4];
            var1.append("\n\tat ");
            var1.append(var5);
         }
      }

   }

   public StackTraceElement[] getStacktrace() {
      return this.stackTrace;
   }

   public static void populateBlockDetails(CrashReportCategory var0, BlockPos var1, @Nullable BlockState var2) {
      if (var2 != null) {
         var0.setDetail("Block", var2::toString);
      }

      var0.setDetail("Block location", () -> {
         return formatLocation(var1);
      });
   }

   static class Entry {
      private final String key;
      private final String value;

      public Entry(String var1, Object var2) {
         super();
         this.key = var1;
         if (var2 == null) {
            this.value = "~~NULL~~";
         } else if (var2 instanceof Throwable) {
            Throwable var3 = (Throwable)var2;
            this.value = "~~ERROR~~ " + var3.getClass().getSimpleName() + ": " + var3.getMessage();
         } else {
            this.value = var2.toString();
         }

      }

      public String getKey() {
         return this.key;
      }

      public String getValue() {
         return this.value;
      }
   }
}
