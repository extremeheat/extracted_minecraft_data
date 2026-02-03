package net.minecraft;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionException;
import net.minecraft.util.FileUtil;
import net.minecraft.util.MemoryReserve;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class CrashReport {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final DateTimeFormatter DATE_TIME_FORMATTER;
   private final String title;
   private final Throwable exception;
   private final List<CrashReportCategory> details = Lists.newArrayList();
   private @Nullable Path saveFile;
   private boolean trackingStackTrace = true;
   private StackTraceElement[] uncategorizedStackTrace = new StackTraceElement[0];
   private final SystemReport systemReport = new SystemReport();

   public CrashReport(final String title, final Throwable t) {
      super();
      this.title = title;
      this.exception = t;
   }

   public String getTitle() {
      return this.title;
   }

   public Throwable getException() {
      return this.exception;
   }

   public String getDetails() {
      StringBuilder builder = new StringBuilder();
      this.getDetails(builder);
      return builder.toString();
   }

   public void getDetails(final StringBuilder builder) {
      if ((this.uncategorizedStackTrace == null || this.uncategorizedStackTrace.length <= 0) && !this.details.isEmpty()) {
         this.uncategorizedStackTrace = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.details.get(0)).getStacktrace(), 0, 1);
      }

      if (this.uncategorizedStackTrace != null && this.uncategorizedStackTrace.length > 0) {
         builder.append("-- Head --\n");
         builder.append("Thread: ").append(Thread.currentThread().getName()).append("\n");
         builder.append("Stacktrace:\n");

         for(StackTraceElement element : this.uncategorizedStackTrace) {
            builder.append("\t").append("at ").append(element);
            builder.append("\n");
         }

         builder.append("\n");
      }

      for(CrashReportCategory entry : this.details) {
         entry.getDetails(builder);
         builder.append("\n\n");
      }

      this.systemReport.appendToCrashReportString(builder);
   }

   public String getExceptionMessage() {
      StringWriter writer = null;
      PrintWriter printWriter = null;
      Throwable exception = this.exception;
      if (exception.getMessage() == null) {
         if (exception instanceof NullPointerException) {
            exception = new NullPointerException(this.title);
         } else if (exception instanceof StackOverflowError) {
            exception = new StackOverflowError(this.title);
         } else if (exception instanceof OutOfMemoryError) {
            exception = new OutOfMemoryError(this.title);
         }

         exception.setStackTrace(this.exception.getStackTrace());
      }

      String var4;
      try {
         writer = new StringWriter();
         printWriter = new PrintWriter(writer);
         exception.printStackTrace(printWriter);
         var4 = writer.toString();
      } finally {
         IOUtils.closeQuietly(writer);
         IOUtils.closeQuietly(printWriter);
      }

      return var4;
   }

   public String getFriendlyReport(final ReportType reportType, final List<String> extraComments) {
      StringBuilder builder = new StringBuilder();
      reportType.appendHeader(builder, extraComments);
      builder.append("Time: ");
      builder.append(DATE_TIME_FORMATTER.format(ZonedDateTime.now()));
      builder.append("\n");
      builder.append("Description: ");
      builder.append(this.title);
      builder.append("\n\n");
      builder.append(this.getExceptionMessage());
      builder.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

      for(int i = 0; i < 87; ++i) {
         builder.append("-");
      }

      builder.append("\n\n");
      this.getDetails(builder);
      return builder.toString();
   }

   public String getFriendlyReport(final ReportType reportType) {
      return this.getFriendlyReport(reportType, List.of());
   }

   public @Nullable Path getSaveFile() {
      return this.saveFile;
   }

   public boolean saveToFile(final Path saveFile, final ReportType reportType, final List<String> extraComments) {
      if (this.saveFile != null) {
         return false;
      } else {
         try {
            if (saveFile.getParent() != null) {
               FileUtil.createDirectoriesSafe(saveFile.getParent());
            }

            Writer writer = Files.newBufferedWriter(saveFile, StandardCharsets.UTF_8);

            try {
               writer.write(this.getFriendlyReport(reportType, extraComments));
            } catch (Throwable var8) {
               if (writer != null) {
                  try {
                     writer.close();
                  } catch (Throwable var7) {
                     var8.addSuppressed(var7);
                  }
               }

               throw var8;
            }

            if (writer != null) {
               writer.close();
            }

            this.saveFile = saveFile;
            return true;
         } catch (Throwable t) {
            LOGGER.error("Could not save crash report to {}", saveFile, t);
            return false;
         }
      }
   }

   public boolean saveToFile(final Path file, final ReportType reportType) {
      return this.saveToFile(file, reportType, List.of());
   }

   public SystemReport getSystemReport() {
      return this.systemReport;
   }

   public CrashReportCategory addCategory(final String name) {
      return this.addCategory(name, 1);
   }

   public CrashReportCategory addCategory(final String name, final int nestedOffset) {
      CrashReportCategory category = new CrashReportCategory(name);
      if (this.trackingStackTrace) {
         int size = category.fillInStackTrace(nestedOffset);
         StackTraceElement[] fullTrace = this.exception.getStackTrace();
         StackTraceElement source = null;
         StackTraceElement next = null;
         int traceIndex = fullTrace.length - size;
         if (traceIndex < 0) {
            LOGGER.error("Negative index in crash report handler ({}/{})", fullTrace.length, size);
         }

         if (fullTrace != null && 0 <= traceIndex && traceIndex < fullTrace.length) {
            source = fullTrace[traceIndex];
            if (fullTrace.length + 1 - size < fullTrace.length) {
               next = fullTrace[fullTrace.length + 1 - size];
            }
         }

         this.trackingStackTrace = category.validateStackTrace(source, next);
         if (fullTrace != null && fullTrace.length >= size && 0 <= traceIndex && traceIndex < fullTrace.length) {
            this.uncategorizedStackTrace = new StackTraceElement[traceIndex];
            System.arraycopy(fullTrace, 0, this.uncategorizedStackTrace, 0, this.uncategorizedStackTrace.length);
         } else {
            this.trackingStackTrace = false;
         }
      }

      this.details.add(category);
      return category;
   }

   public static CrashReport forThrowable(Throwable t, final String title) {
      while(t instanceof CompletionException && t.getCause() != null) {
         t = t.getCause();
      }

      CrashReport report;
      if (t instanceof ReportedException reportedException) {
         report = reportedException.getReport();
      } else {
         report = new CrashReport(title, t);
      }

      return report;
   }

   public static void preload() {
      MemoryReserve.allocate();
      (new CrashReport("Don't panic!", new Throwable())).getFriendlyReport(ReportType.CRASH);
   }

   static {
      DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ROOT);
   }
}
