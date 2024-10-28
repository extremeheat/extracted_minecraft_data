package net.minecraft;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CrashReportCategory {
   private final String title;
   private final List<Entry> entries = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(String var1) {
      super();
      this.title = var1;
   }

   public static String formatLocation(LevelHeightAccessor var0, double var1, double var3, double var5) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", var1, var3, var5, formatLocation(var0, BlockPos.containing(var1, var3, var5)));
   }

   public static String formatLocation(LevelHeightAccessor var0, BlockPos var1) {
      return formatLocation(var0, var1.getX(), var1.getY(), var1.getZ());
   }

   public static String formatLocation(LevelHeightAccessor var0, int var1, int var2, int var3) {
      StringBuilder var4 = new StringBuilder();

      try {
         var4.append(String.format(Locale.ROOT, "World: (%d,%d,%d)", var1, var2, var3));
      } catch (Throwable var19) {
         var4.append("(Error finding world loc)");
      }

      var4.append(", ");

      int var5;
      int var6;
      int var7;
      int var8;
      int var9;
      int var10;
      int var11;
      int var12;
      int var13;
      int var14;
      int var15;
      int var16;
      try {
         var5 = SectionPos.blockToSectionCoord(var1);
         var6 = SectionPos.blockToSectionCoord(var2);
         var7 = SectionPos.blockToSectionCoord(var3);
         var8 = var1 & 15;
         var9 = var2 & 15;
         var10 = var3 & 15;
         var11 = SectionPos.sectionToBlockCoord(var5);
         var12 = var0.getMinBuildHeight();
         var13 = SectionPos.sectionToBlockCoord(var7);
         var14 = SectionPos.sectionToBlockCoord(var5 + 1) - 1;
         var15 = var0.getMaxBuildHeight() - 1;
         var16 = SectionPos.sectionToBlockCoord(var7 + 1) - 1;
         var4.append(String.format(Locale.ROOT, "Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)", var8, var9, var10, var5, var6, var7, var11, var12, var13, var14, var15, var16));
      } catch (Throwable var18) {
         var4.append("(Error finding chunk loc)");
      }

      var4.append(", ");

      try {
         var5 = var1 >> 9;
         var6 = var3 >> 9;
         var7 = var5 << 5;
         var8 = var6 << 5;
         var9 = (var5 + 1 << 5) - 1;
         var10 = (var6 + 1 << 5) - 1;
         var11 = var5 << 9;
         var12 = var0.getMinBuildHeight();
         var13 = var6 << 9;
         var14 = (var5 + 1 << 9) - 1;
         var15 = var0.getMaxBuildHeight() - 1;
         var16 = (var6 + 1 << 9) - 1;
         var4.append(String.format(Locale.ROOT, "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)", var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16));
      } catch (Throwable var17) {
         var4.append("(Error finding world loc)");
      }

      return var4.toString();
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
      this.entries.add(new Entry(var1, var2));
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
         Entry var3 = (Entry)var2.next();
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

   public static void populateBlockDetails(CrashReportCategory var0, LevelHeightAccessor var1, BlockPos var2, @Nullable BlockState var3) {
      if (var3 != null) {
         Objects.requireNonNull(var3);
         var0.setDetail("Block", var3::toString);
      }

      var0.setDetail("Block location", () -> {
         return formatLocation(var1, var2);
      });
   }

   private static class Entry {
      private final String key;
      private final String value;

      public Entry(String var1, @Nullable Object var2) {
         super();
         this.key = var1;
         if (var2 == null) {
            this.value = "~~NULL~~";
         } else if (var2 instanceof Throwable) {
            Throwable var3 = (Throwable)var2;
            String var10001 = var3.getClass().getSimpleName();
            this.value = "~~ERROR~~ " + var10001 + ": " + var3.getMessage();
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
