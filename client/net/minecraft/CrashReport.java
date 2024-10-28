package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.util.MemoryReserve;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final DateTimeFormatter DATE_TIME_FORMATTER;
   private final String title;
   private final Throwable exception;
   private final List<CrashReportCategory> details = Lists.newArrayList();
   @Nullable
   private Path saveFile;
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
         this.uncategorizedStackTrace = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.details.get(0)).getStacktrace(), 0, 1);
      }

      if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
         var1.append("-- Head --\n");
         var1.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         var1.append("Stacktrace:\n");
         StackTraceElement[] var2 = this.uncategorizedStackTrace;
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            StackTraceElement var5 = var2[var4];
            var1.append("\t").append("at ").append(var5);
            var1.append("\n");
         }

         var1.append("\n");
      }

      Iterator var6 = this.details.iterator();

      while(var6.hasNext()) {
         CrashReportCategory var7 = (CrashReportCategory)var6.next();
         var7.getDetails(var1);
         var1.append("\n\n");
      }

      this.systemReport.appendToCrashReportString(var1);
   }

   public String getExceptionMessage() {
      StringWriter var1 = null;
      PrintWriter var2 = null;
      Object var3 = this.exception;
      if (((Throwable)var3).getMessage() == null) {
         if (var3 instanceof NullPointerException) {
            var3 = new NullPointerException(this.title);
         } else if (var3 instanceof StackOverflowError) {
            var3 = new StackOverflowError(this.title);
         } else if (var3 instanceof OutOfMemoryError) {
            var3 = new OutOfMemoryError(this.title);
         }

         ((Throwable)var3).setStackTrace(this.exception.getStackTrace());
      }

      String var4;
      try {
         var1 = new StringWriter();
         var2 = new PrintWriter(var1);
         ((Throwable)var3).printStackTrace(var2);
         var4 = var1.toString();
      } finally {
         IOUtils.closeQuietly(var1);
         IOUtils.closeQuietly(var2);
      }

      return var4;
   }

   public String getFriendlyReport(ReportType var1, List<String> var2) {
      StringBuilder var3 = new StringBuilder();
      var1.appendHeader(var3, var2);
      var3.append("Time: ");
      var3.append(DATE_TIME_FORMATTER.format(ZonedDateTime.now()));
      var3.append("\n");
      var3.append("Description: ");
      var3.append(this.title);
      var3.append("\n\n");
      var3.append(this.getExceptionMessage());
      var3.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int var4 = 0; var4 < 87; ++var4) {
         var3.append("-");
      }

      var3.append("\n\n");
      this.getDetails(var3);
      return var3.toString();
   }

   public String getFriendlyReport(ReportType var1) {
      return this.getFriendlyReport(var1, List.of());
   }

   @Nullable
   public Path getSaveFile() {
      return this.saveFile;
   }

   public boolean saveToFile(Path var1, ReportType var2, List<String> var3) {
      if (this.saveFile != null) {
         return false;
      } else {
         try {
            if (var1.getParent() != null) {
               FileUtil.createDirectoriesSafe(var1.getParent());
            }

            BufferedWriter var4 = Files.newBufferedWriter(var1, StandardCharsets.UTF_8);

            try {
               ((Writer)var4).write(this.getFriendlyReport(var2, var3));
            } catch (Throwable var8) {
               if (var4 != null) {
                  try {
                     ((Writer)var4).close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (var4 != null) {
               ((Writer)var4).close();
            }

            this.saveFile = var1;
            return true;
         } catch (Throwable var9) {
            LOGGER.error("Could not save crash report to {}", var1, var9);
            return false;
         }
      }
   }

   public boolean saveToFile(Path var1, ReportType var2) {
      return this.saveToFile(var1, var2, List.of());
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
            LOGGER.error("Negative index in crash report handler ({}/{})", var5.length, var4);
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

   public static CrashReport forThrowable(Throwable var0, String var1) {
      while(var0 instanceof CompletionException && var0.getCause() != null) {
         var0 = var0.getCause();
      }

      CrashReport var2;
      if (var0 instanceof ReportedException var3) {
         var2 = var3.getReport();
      } else {
         var2 = new CrashReport(var1, var0);
      }

      return var2;
   }

   public static void preload() {
      MemoryReserve.allocate();
      (new CrashReport("Don't panic!", new Throwable())).getFriendlyReport(ReportType.CRASH);
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
   }
}
