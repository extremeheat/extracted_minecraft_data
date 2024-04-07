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

   @Override
   public Throwable getCause() {
      return this.report.getException();
   }

   @Override
   public String getMessage() {
      return this.report.getTitle();
   }
}
