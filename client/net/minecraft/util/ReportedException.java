package net.minecraft.util;

import net.minecraft.crash.CrashReport;

public class ReportedException extends RuntimeException {
   private final CrashReport field_71576_a;

   public ReportedException(CrashReport var1) {
      super();
      this.field_71576_a = var1;
   }

   public CrashReport func_71575_a() {
      return this.field_71576_a;
   }

   @Override
   public Throwable getCause() {
      return this.field_71576_a.func_71505_b();
   }

   @Override
   public String getMessage() {
      return this.field_71576_a.func_71501_a();
   }
}
