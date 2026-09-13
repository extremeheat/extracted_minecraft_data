package net.minecraft.crash;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.util.MathHelper;

public class CrashReportCategory {
   private final CrashReport field_85078_a;
   private final String field_85076_b;
   private final List field_85077_c = new ArrayList();
   private StackTraceElement[] field_85075_d = new StackTraceElement[0];

   public CrashReportCategory(CrashReport var1, String var2) {
      super();
      this.field_85078_a = var1;
      this.field_85076_b = var2;
   }

   public static String func_85074_a(double var0, double var2, double var4) {
      return String.format(
         "%.2f,%.2f,%.2f - %s", var0, var2, var4, func_85071_a(MathHelper.func_76128_c(var0), MathHelper.func_76128_c(var2), MathHelper.func_76128_c(var4))
      );
   }

   public static String func_85071_a(int var0, int var1, int var2) {
      StringBuilder var3 = new StringBuilder();

      try {
         var3.append(String.format("World: (%d,%d,%d)", var0, var1, var2));
      } catch (Throwable var16) {
         var3.append("(Error finding world loc)");
      }

      var3.append(", ");

      try {
         int var4 = var0 >> 4;
         int var5 = var2 >> 4;
         int var6 = var0 & 15;
         int var7 = var1 >> 4;
         int var8 = var2 & 15;
         int var9 = var4 << 4;
         int var10 = var5 << 4;
         int var11 = (var4 + 1 << 4) - 1;
         int var12 = (var5 + 1 << 4) - 1;
         var3.append(
            String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", var6, var7, var8, var4, var5, var9, var10, var11, var12)
         );
      } catch (Throwable var15) {
         var3.append("(Error finding chunk loc)");
      }

      var3.append(", ");

      try {
         int var17 = var0 >> 9;
         int var18 = var2 >> 9;
         int var19 = var17 << 5;
         int var20 = var18 << 5;
         int var21 = (var17 + 1 << 5) - 1;
         int var22 = (var18 + 1 << 5) - 1;
         int var23 = var17 << 9;
         int var24 = var18 << 9;
         int var25 = (var17 + 1 << 9) - 1;
         int var13 = (var18 + 1 << 9) - 1;
         var3.append(
            String.format(
               "Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)",
               var17,
               var18,
               var19,
               var20,
               var21,
               var22,
               var23,
               var24,
               var25,
               var13
            )
         );
      } catch (Throwable var14) {
         var3.append("(Error finding world loc)");
      }

      return var3.toString();
   }

   public void func_71500_a(String var1, Callable var2) {
      try {
         this.func_71507_a(var1, var2.call());
      } catch (Throwable var4) {
         this.func_71499_a(var1, var4);
      }
   }

   public void func_71507_a(String var1, Object var2) {
      this.field_85077_c.add(new CrashReportCategory$Entry(var1, var2));
   }

   public void func_71499_a(String var1, Throwable var2) {
      this.func_71507_a(var1, var2);
   }

   public int func_85073_a(int var1) {
      StackTraceElement[] var2 = Thread.currentThread().getStackTrace();
      if (var2.length <= 0) {
         return 0;
      } else {
         this.field_85075_d = new StackTraceElement[var2.length - 3 - var1];
         System.arraycopy(var2, 3 + var1, this.field_85075_d, 0, this.field_85075_d.length);
         return this.field_85075_d.length;
      }
   }

   public boolean func_85069_a(StackTraceElement var1, StackTraceElement var2) {
      if (this.field_85075_d.length != 0 && var1 != null) {
         StackTraceElement var3 = this.field_85075_d[0];
         if (var3.isNativeMethod() == var1.isNativeMethod()
            && var3.getClassName().equals(var1.getClassName())
            && var3.getFileName().equals(var1.getFileName())
            && var3.getMethodName().equals(var1.getMethodName())) {
            if (var2 != null != this.field_85075_d.length > 1) {
               return false;
            } else if (var2 != null && !this.field_85075_d[1].equals(var2)) {
               return false;
            } else {
               this.field_85075_d[0] = var1;
               return true;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void func_85070_b(int var1) {
      StackTraceElement[] var2 = new StackTraceElement[this.field_85075_d.length - var1];
      System.arraycopy(this.field_85075_d, 0, var2, 0, var2.length);
      this.field_85075_d = var2;
   }

   public void func_85072_a(StringBuilder var1) {
      var1.append("-- ").append(this.field_85076_b).append(" --\n");
      var1.append("Details:");

      for(CrashReportCategory$Entry var3 : this.field_85077_c) {
         var1.append("\n\t");
         var1.append(var3.func_85089_a());
         var1.append(": ");
         var1.append(var3.func_85090_b());
      }

      if (this.field_85075_d != null && this.field_85075_d.length > 0) {
         var1.append("\nStacktrace:");

         for(StackTraceElement var5 : this.field_85075_d) {
            var1.append("\n\tat ");
            var1.append(var5.toString());
         }
      }
   }

   public StackTraceElement[] func_147152_a() {
      return this.field_85075_d;
   }

   public static void func_147153_a(CrashReportCategory var0, int var1, int var2, int var3, Block var4, int var5) {
      int var6 = Block.func_149682_b(var4);
      var0.func_71500_a("Block type", new CrashReportCategory$1(var6, var4));
      var0.func_71500_a("Block data value", new CrashReportCategory$2(var5));
      var0.func_71500_a("Block location", new CrashReportCategory$3(var1, var2, var3));
   }
}
