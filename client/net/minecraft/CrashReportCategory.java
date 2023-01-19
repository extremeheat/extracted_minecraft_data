package net.minecraft;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.block.state.BlockState;

public class CrashReportCategory {
   private final String title;
   private final List<CrashReportCategory.Entry> entries = Lists.newArrayList();
   private StackTraceElement[] stackTrace = new StackTraceElement[0];

   public CrashReportCategory(String var1) {
      super();
      this.title = var1;
   }

   public static String formatLocation(LevelHeightAccessor var0, double var1, double var3, double var5) {
      return String.format(Locale.ROOT, "%.2f,%.2f,%.2f - %s", var1, var3, var5, formatLocation(var0, new BlockPos(var1, var3, var5)));
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

      try {
         int var5 = SectionPos.blockToSectionCoord(var1);
         int var6 = SectionPos.blockToSectionCoord(var2);
         int var7 = SectionPos.blockToSectionCoord(var3);
         int var8 = var1 & 15;
         int var9 = var2 & 15;
         int var10 = var3 & 15;
         int var11 = SectionPos.sectionToBlockCoord(var5);
         int var12 = var0.getMinBuildHeight();
         int var13 = SectionPos.sectionToBlockCoord(var7);
         int var14 = SectionPos.sectionToBlockCoord(var5 + 1) - 1;
         int var15 = var0.getMaxBuildHeight() - 1;
         int var16 = SectionPos.sectionToBlockCoord(var7 + 1) - 1;
         var4.append(
            String.format(
               Locale.ROOT,
               "Section: (at %d,%d,%d in %d,%d,%d; chunk contains blocks %d,%d,%d to %d,%d,%d)",
               var8,
               var9,
               var10,
               var5,
               var6,
               var7,
               var11,
               var12,
               var13,
               var14,
               var15,
               var16
            )
         );
      } catch (Throwable var18) {
         var4.append("(Error finding chunk loc)");
      }

      var4.append(", ");

      try {
         int var20 = var1 >> 9;
         int var21 = var3 >> 9;
         int var22 = var20 << 5;
         int var23 = var21 << 5;
         int var24 = (var20 + 1 << 5) - 1;
         int var25 = (var21 + 1 << 5) - 1;
         int var26 = var20 << 9;
         int var27 = var0.getMinBuildHeight();
         int var28 = var21 << 9;
         int var29 = (var20 + 1 << 9) - 1;
         int var30 = var0.getMaxBuildHeight() - 1;
         int var31 = (var21 + 1 << 9) - 1;
         var4.append(
            String.format(
               Locale.ROOT,
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,%d,%d to %d,%d,%d)",
               var20,
               var21,
               var22,
               var23,
               var24,
               var25,
               var26,
               var27,
               var28,
               var29,
               var30,
               var31
            )
         );
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
      this.entries.add(new CrashReportCategory.Entry(var1, var2));
      return this;
   }

   public void setDetailError(String var1, Throwable var2) {
      this.setDetail(var1, var2);
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
         if (var3.isNativeMethod() == var1.isNativeMethod()
            && var3.getClassName().equals(var1.getClassName())
            && var3.getFileName().equals(var1.getFileName())
            && var3.getMethodName().equals(var1.getMethodName())) {
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

      for(CrashReportCategory.Entry var3 : this.entries) {
         var1.append("\n\t");
         var1.append(var3.getKey());
         var1.append(": ");
         var1.append(var3.getValue());
      }

      if (this.stackTrace != null && this.stackTrace.length > 0) {
         var1.append("\nStacktrace:");

         for(StackTraceElement var5 : this.stackTrace) {
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
         var0.setDetail("Block", var3::toString);
      }

      var0.setDetail("Block location", () -> formatLocation(var1, var2));
   }

   static class Entry {
      private final String key;
      private final String value;

      // $QF: Could not properly define all variable types!
      // Please report this to the Quiltflower issue tracker, at https://github.com/QuiltMC/quiltflower/issues with a copy of the class file (if you have the rights to distribute it!)
      public Entry(String var1, @Nullable Object var2) {
         super();
         this.key = var1;
         if (var2 == null) {
            this.value = "~~NULL~~";
         } else if (var2 instanceof Throwable var3) {
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
