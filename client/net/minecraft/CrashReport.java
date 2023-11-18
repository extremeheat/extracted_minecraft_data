package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import net.minecraft.util.MemoryReserve;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
   private final String title;
   private final Throwable exception;
   private final List<CrashReportCategory> details = Lists.newArrayList();
   private File saveFile;
   private boolean trackingStackTrace = true;
   private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];
   private final SystemReport systemReport = new SystemReport();

   public CrashReport(String var1, Throwable var2) {
      super();
      this.title = var1;
      this.exception = var2;
   }

   public String getTitle() {
      return this.title;
   }

   public Throwable getException() {
      return this.exception;
   }

   public String getDetails() {
      StringBuilder var1 = new StringBuilder();
      this.getDetails(var1);
      return var1.toString();
   }

   public void getDetails(StringBuilder var1) {
      if ((this.uncategorizedStackTrace == null || this.uncategorizedStackTrace.length <= 0) && !this.details.isEmpty()) {
         this.uncategorizedStackTrace = (StackTraceElement[])ArrayUtils.subarray(this.details.get(0).getStacktrace(), 0, 1);
      }

      if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
         var1.append("-- Head --\n");
         var1.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         var1.append("Stacktrace:\n");

         for(StackTraceElement var5 : this.uncategorizedStackTrace) {
            var1.append("\t").append("at ").append(var5);
            var1.append("\n");
         }

         var1.append("\n");
      }

      for(CrashReportCategory var7 : this.details) {
         var7.getDetails(var1);
         var1.append("\n\n");
      }

      this.systemReport.appendToCrashReportString(var1);
   }

   public String getExceptionMessage() {
      StringWriter var1 = null;
      PrintWriter var2 = null;
      Object var3 = this.exception;
      if (var3.getMessage() == null) {
         if (var3 instanceof NullPointerException) {
            var3 = new NullPointerException(this.title);
         } else if (var3 instanceof StackOverflowError) {
            var3 = new StackOverflowError(this.title);
         } else if (var3 instanceof OutOfMemoryError) {
            var3 = new OutOfMemoryError(this.title);
         }

         var3.setStackTrace(this.exception.getStackTrace());
      }

      String var4;
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

   public String getFriendlyReport() {
      StringBuilder var1 = new StringBuilder();
      var1.append("---- Minecraft Crash Report ----\n");
      var1.append("// ");
      var1.append(getErrorComment());
      var1.append("\n\n");
      var1.append("Time: ");
      var1.append(DATE_TIME_FORMATTER.format(ZonedDateTime.now()));
      var1.append("\n");
      var1.append("Description: ");
      var1.append(this.title);
      var1.append("\n\n");
      var1.append(this.getExceptionMessage());
      var1.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int var2 = 0; var2 < 87; ++var2) {
         var1.append("-");
      }

      var1.append("\n\n");
      this.getDetails(var1);
      return var1.toString();
   }

   public File getSaveFile() {
      return this.saveFile;
   }

   public boolean saveToFile(File var1) {
      if (this.saveFile != null) {
         return false;
      } else {
         if (var1.getParentFile() != null) {
            var1.getParentFile().mkdirs();
         }

         OutputStreamWriter var2 = null;

         boolean var4;
         try {
            var2 = new OutputStreamWriter(new FileOutputStream(var1), StandardCharsets.UTF_8);
            var2.write(this.getFriendlyReport());
            this.saveFile = var1;
            return true;
         } catch (Throwable var8) {
            LOGGER.error("Could not save crash report to {}", var1, var8);
            var4 = false;
         } finally {
            IOUtils.closeQuietly(var2);
         }

         return var4;
      }
   }

   public SystemReport getSystemReport() {
      return this.systemReport;
   }

   public CrashReportCategory addCategory(String var1) {
      return this.addCategory(var1, 1);
   }

   public CrashReportCategory addCategory(String var1, int var2) {
      CrashReportCategory var3 = new CrashReportCategory(var1);
      if (this.trackingStackTrace) {
         int var4 = var3.fillInStackTrace(var2);
         StackTraceElement[] var5 = this.exception.getStackTrace();
         StackTraceElement var6 = null;
         StackTraceElement var7 = null;
         int var8 = var5.length - var4;
         if (var8 < 0) {
            LOGGER.error("Negative index in crash report handler ({}/{})", new Object[]{var5.length, var4, ")"});
         }

         if (var5 != null && 0 <= var8 && var8 < var5.length) {
            var6 = var5[var8];
            if (var5.length + 1 - var4 < var5.length) {
               var7 = var5[var5.length + 1 - var4];
            }
         }

         this.trackingStackTrace = var3.validateStackTrace(var6, var7);
         if (var5 != null && var5.length >= var4 && 0 <= var8 && var8 < var5.length) {
            this.uncategorizedStackTrace = new StackTraceElement[var8];
            System.arraycopy(var5, 0, this.uncategorizedStackTrace, 0, this.uncategorizedStackTrace.length);
         } else {
            this.trackingStackTrace = false;
         }
      }

      this.details.add(var3);
      return var3;
   }

   private static String getErrorComment() {
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
         return var0[(int)(Util.getNanos() % (long)var0.length)];
      } catch (Throwable var2) {
         return "Witty comment unavailable :(";
      }
   }

   public static CrashReport forThrowable(Throwable var0, String var1) {
      while(var0 instanceof CompletionException && var0.getCause() != null) {
         var0 = var0.getCause();
      }

      CrashReport var2;
      if (var0 instanceof ReportedException) {
         var2 = ((ReportedException)var0).getReport();
      } else {
         var2 = new CrashReport(var1, var0);
      }

      return var2;
   }

   public static void preload() {
      MemoryReserve.allocate();
      new CrashReport("Don't panic!", new Throwable()).getFriendlyReport();
   }
}
