package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;

public class CrashReportCategory {
   private final CrashReport field_85078_a;
   private final String field_85076_b;
   private final List<CrashReportCategory.Entry> field_85077_c = Lists.newArrayList();
   private StackTraceElement[] field_85075_d = new StackTraceElement[0];

   public CrashReportCategory(CrashReport var1, String var2) {
      super();
      this.field_85078_a = var1;
      this.field_85076_b = var2;
   }

   public static String func_85074_a(double var0, double var2, double var4) {
      return String.format("%.2f,%.2f,%.2f - %s", var0, var2, var4, func_180522_a(new BlockPos(var0, var2, var4)));
   }

   public static String func_180522_a(BlockPos var0) {
      int var1 = var0.func_177958_n();
      int var2 = var0.func_177956_o();
      int var3 = var0.func_177952_p();
      StringBuilder var4 = new StringBuilder();

      try {
         var4.append(String.format("World: (%d,%d,%d)", var1, var2, var3));
      } catch (Throwable var17) {
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
      try {
         var5 = var1 >> 4;
         var6 = var3 >> 4;
         var7 = var1 & 15;
         var8 = var2 >> 4;
         var9 = var3 & 15;
         var10 = var5 << 4;
         var11 = var6 << 4;
         var12 = (var5 + 1 << 4) - 1;
         var13 = (var6 + 1 << 4) - 1;
         var4.append(String.format("Chunk: (at %d,%d,%d in %d,%d; contains blocks %d,0,%d to %d,255,%d)", var7, var8, var9, var5, var6, var10, var11, var12, var13));
      } catch (Throwable var16) {
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
         var12 = var6 << 9;
         var13 = (var5 + 1 << 9) - 1;
         int var14 = (var6 + 1 << 9) - 1;
         var4.append(String.format("Region: (%d,%d; contains chunks %d,%d to %d,%d, blocks %d,0,%d to %d,255,%d)", var5, var6, var7, var8, var9, var10, var11, var12, var13, var14));
      } catch (Throwable var15) {
         var4.append("(Error finding world loc)");
      }

      return var4.toString();
   }

   public void func_71500_a(String var1, Callable<String> var2) {
      try {
         this.func_71507_a(var1, var2.call());
      } catch (Throwable var4) {
         this.func_71499_a(var1, var4);
      }

   }

   public void func_71507_a(String var1, Object var2) {
      this.field_85077_c.add(new CrashReportCategory.Entry(var1, var2));
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
         if (var3.isNativeMethod() == var1.isNativeMethod() && var3.getClassName().equals(var1.getClassName()) && var3.getFileName().equals(var1.getFileName()) && var3.getMethodName().equals(var1.getMethodName())) {
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
      Iterator var2 = this.field_85077_c.iterator();

      while(var2.hasNext()) {
         CrashReportCategory.Entry var3 = (CrashReportCategory.Entry)var2.next();
         var1.append("\n\t");
         var1.append(var3.func_85089_a());
         var1.append(": ");
         var1.append(var3.func_85090_b());
      }

      if (this.field_85075_d != null && this.field_85075_d.length > 0) {
         var1.append("\nStacktrace:");
         StackTraceElement[] var6 = this.field_85075_d;
         int var7 = var6.length;

         for(int var4 = 0; var4 < var7; ++var4) {
            StackTraceElement var5 = var6[var4];
            var1.append("\n\tat ");
            var1.append(var5.toString());
         }
      }

   }

   public StackTraceElement[] func_147152_a() {
      return this.field_85075_d;
   }

   public static void func_180523_a(CrashReportCategory var0, final BlockPos var1, final Block var2, final int var3) {
      final int var4 = Block.func_149682_b(var2);
      var0.func_71500_a("Block type", new Callable<String>() {
         public String call() throws Exception {
            try {
               return String.format("ID #%d (%s // %s)", var4, var2.func_149739_a(), var2.getClass().getCanonicalName());
            } catch (Throwable var2x) {
               return "ID #" + var4;
            }
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var0.func_71500_a("Block data value", new Callable<String>() {
         public String call() throws Exception {
            if (var3 < 0) {
               return "Unknown? (Got " + var3 + ")";
            } else {
               String var1 = String.format("%4s", Integer.toBinaryString(var3)).replace(" ", "0");
               return String.format("%1$d / 0x%1$X / 0b%2$s", var3, var1);
            }
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var0.func_71500_a("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.func_180522_a(var1);
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
   }

   public static void func_175750_a(CrashReportCategory var0, final BlockPos var1, final IBlockState var2) {
      var0.func_71500_a("Block", new Callable<String>() {
         public String call() throws Exception {
            return var2.toString();
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
      var0.func_71500_a("Block location", new Callable<String>() {
         public String call() throws Exception {
            return CrashReportCategory.func_180522_a(var1);
         }

         // $FF: synthetic method
         public Object call() throws Exception {
            return this.call();
         }
      });
   }

   static class Entry {
      private final String field_85092_a;
      private final String field_85091_b;

      public Entry(String var1, Object var2) {
         super();
         this.field_85092_a = var1;
         if (var2 == null) {
            this.field_85091_b = "~~NULL~~";
         } else if (var2 instanceof Throwable) {
            Throwable var3 = (Throwable)var2;
            this.field_85091_b = "~~ERROR~~ " + var3.getClass().getSimpleName() + ": " + var3.getMessage();
         } else {
            this.field_85091_b = var2.toString();
         }

      }

      public String func_85089_a() {
         return this.field_85092_a;
      }

      public String func_85090_b() {
         return this.field_85091_b;
      }
   }
}
