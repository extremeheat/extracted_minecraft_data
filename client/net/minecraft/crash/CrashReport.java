package net.minecraft.crash;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.minecraft.util.ReportedException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   private static final Logger field_147150_a = LogManager.getLogger();
   private final String field_71513_a;
   private final Throwable field_71511_b;
   private final CrashReportCategory field_85061_c = new CrashReportCategory(this, "System Details");
   private final List field_71512_c = new ArrayList();
   private File field_71510_d;
   private boolean field_85059_f = true;
   private StackTraceElement[] field_85060_g = new StackTraceElement[0];

   public CrashReport(String var1, Throwable var2) {
      super();
      this.field_71513_a = var1;
      this.field_71511_b = var2;
      this.func_71504_g();
   }

   private void func_71504_g() {
      this.field_85061_c.func_71500_a("Minecraft Version", new CrashReport$1(this));
      this.field_85061_c.func_71500_a("Operating System", new CrashReport$2(this));
      this.field_85061_c.func_71500_a("Java Version", new CrashReport$3(this));
      this.field_85061_c.func_71500_a("Java VM Version", new CrashReport$4(this));
      this.field_85061_c.func_71500_a("Memory", new CrashReport$5(this));
      this.field_85061_c.func_71500_a("JVM Flags", new CrashReport$6(this));
      this.field_85061_c.func_71500_a("AABB Pool Size", new CrashReport$7(this));
      this.field_85061_c.func_71500_a("IntCache", new CrashReport$8(this));
   }

   public String func_71501_a() {
      return this.field_71513_a;
   }

   public Throwable func_71505_b() {
      return this.field_71511_b;
   }

   public void func_71506_a(StringBuilder var1) {
      if ((this.field_85060_g == null || this.field_85060_g.length <= 0) && this.field_71512_c.size() > 0) {
         this.field_85060_g = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.field_71512_c.get(0)).func_147152_a(), 0, 1);
      }

      if (this.field_85060_g != null && this.field_85060_g.length > 0) {
         var1.append("-- Head --\n");
         var1.append("Stacktrace:\n");

         for(StackTraceElement var5 : this.field_85060_g) {
            var1.append("\t").append("at ").append(var5.toString());
            var1.append("\n");
         }

         var1.append("\n");
      }

      for(CrashReportCategory var7 : this.field_71512_c) {
         var7.func_85072_a(var1);
         var1.append("\n\n");
      }

      this.field_85061_c.func_85072_a(var1);
   }

   public String func_71498_d() {
      StringWriter var1 = null;
      PrintWriter var2 = null;
      Object var3 = this.field_71511_b;
      if (var3.getMessage() == null) {
         if (var3 instanceof NullPointerException) {
            var3 = new NullPointerException(this.field_71513_a);
         } else if (var3 instanceof StackOverflowError) {
            var3 = new StackOverflowError(this.field_71513_a);
         } else if (var3 instanceof OutOfMemoryError) {
            var3 = new OutOfMemoryError(this.field_71513_a);
         }

         var3.setStackTrace(this.field_71511_b.getStackTrace());
      }

      String var4 = var3.toString();

      try {
         var1 = new StringWriter();
         var2 = new PrintWriter(var1);
         var3.printStackTrace(var2);
         var4 = var1.toString();
      } finally {
         IOUtils.closeQuietly(var1);
         IOUtils.closeQuietly(var2);
      }

      return var4;
   }

   public String func_71502_e() {
      StringBuilder var1 = new StringBuilder();
      var1.append("---- Minecraft Crash Report ----\n");
      var1.append("// ");
      var1.append(func_71503_h());
      var1.append("\n\n");
      var1.append("Time: ");
      var1.append(new SimpleDateFormat().format(new Date()));
      var1.append("\n");
      var1.append("Description: ");
      var1.append(this.field_71513_a);
      var1.append("\n\n");
      var1.append(this.func_71498_d());
      var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int var2 = 0; var2 < 87; ++var2) {
         var1.append("-");
      }

      var1.append("\n\n");
      this.func_71506_a(var1);
      return var1.toString();
   }

   public File func_71497_f() {
      return this.field_71510_d;
   }

   public boolean func_147149_a(File var1) {
      if (this.field_71510_d != null) {
         return false;
      } else {
         if (var1.getParentFile() != null) {
            var1.getParentFile().mkdirs();
         }

         try {
            FileWriter var2 = new FileWriter(var1);
            var2.write(this.func_71502_e());
            var2.close();
            this.field_71510_d = var1;
            return true;
         } catch (Throwable var3) {
            field_147150_a.error("Could not save crash report to " + var1, var3);
            return false;
         }
      }
   }

   public CrashReportCategory func_85056_g() {
      return this.field_85061_c;
   }

   public CrashReportCategory func_85058_a(String var1) {
      return this.func_85057_a(var1, 1);
   }

   public CrashReportCategory func_85057_a(String var1, int var2) {
      CrashReportCategory var3 = new CrashReportCategory(this, var1);
      if (this.field_85059_f) {
         int var4 = var3.func_85073_a(var2);
         StackTraceElement[] var5 = this.field_71511_b.getStackTrace();
         StackTraceElement var6 = null;
         StackTraceElement var7 = null;
         int var8 = var5.length - var4;
         if (var8 < 0) {
            System.out.println("Negative index in crash report handler (" + var5.length + "/" + var4 + ")");
         }

         if (var5 != null && 0 <= var8 && var8 < var5.length) {
            var6 = var5[var8];
            if (var5.length + 1 - var4 < var5.length) {
               var7 = var5[var5.length + 1 - var4];
            }
         }

         this.field_85059_f = var3.func_85069_a(var6, var7);
         if (var4 > 0 && !this.field_71512_c.isEmpty()) {
            CrashReportCategory var9 = (CrashReportCategory)this.field_71512_c.get(this.field_71512_c.size() - 1);
            var9.func_85070_b(var4);
         } else if (var5 != null && var5.length >= var4 && 0 <= var8 && var8 < var5.length) {
            this.field_85060_g = new StackTraceElement[var8];
            System.arraycopy(var5, 0, this.field_85060_g, 0, this.field_85060_g.length);
         } else {
            this.field_85059_f = false;
         }
      }

      this.field_71512_c.add(var3);
      return var3;
   }

   private static String func_71503_h() {
      String[] var0 = new String[]{
         "Who set us up the TNT?",
         "Everything's going to plan. No, really, that was supposed to happen.",
         "Uh... Did I do that?",
         "Oops.",
         "Why did you do that?",
         "I feel sad now :(",
         "My bad.",
         "I'm sorry, Dave.",
         "I let you down. Sorry :(",
         "On the bright side, I bought you a teddy bear!",
         "Daisy, daisy...",
         "Oh - I know what I did wrong!",
         "Hey, that tickles! Hehehe!",
         "I blame Dinnerbone.",
         "You should try our sister game, Minceraft!",
         "Don't be sad. I'll do better next time, I promise!",
         "Don't be sad, have a hug! <3",
         "I just don't know what went wrong :(",
         "Shall we play a game?",
         "Quite honestly, I wouldn't worry myself about that.",
         "I bet Cylons wouldn't have this problem.",
         "Sorry :(",
         "Surprise! Haha. Well, this is awkward.",
         "Would you like a cupcake?",
         "Hi. I'm Minecraft, and I'm a crashaholic.",
         "Ooh. Shiny.",
         "This doesn't make any sense!",
         "Why is it breaking :(",
         "Don't do that.",
         "Ouch. That hurt :(",
         "You're mean.",
         "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
         "There are four lights!",
         "But it works on my machine."
      };

      try {
         return var0[(int)(System.nanoTime() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport func_85055_a(Throwable var0, String var1) {
      CrashReport var2;
      if (var0 instanceof ReportedException) {
         var2 = ((ReportedException)var0).func_71575_a();
      } else {
         var2 = new CrashReport(var1, var0);
      }

      return var2;
   }
}
