package net.minecraft.crash;

class CrashReportCategory$Entry {
   private final String field_85092_a;
   private final String field_85091_b;

   public CrashReportCategory$Entry(String var1, Object var2) {
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
