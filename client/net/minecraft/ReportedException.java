package net.minecraft;

public class ReportedException extends RuntimeException {
   private final CrashReport report;

   public ReportedException(CrashReport var1) {
      super();
      this.report = var1;
   }

   public CrashReport getReport() {
      return this.report;
   }

   public Throwable getCause() {
      return this.report.getException();
   }

   public String getMessage() {
      return this.report.getTitle();
   }
}
