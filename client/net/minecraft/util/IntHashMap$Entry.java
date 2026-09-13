package net.minecraft.util;

class IntHashMap$Entry {
   final int field_76035_a;
   Object field_76033_b;
   IntHashMap$Entry field_76034_c;
   final int field_76032_d;

   IntHashMap$Entry(int var1, int var2, Object var3, IntHashMap$Entry var4) {
      super();
      this.field_76033_b = var3;
      this.field_76034_c = var4;
      this.field_76035_a = var2;
      this.field_76032_d = var1;
   }

   public final int func_76031_a() {
      return this.field_76035_a;
   }

   public final Object func_76030_b() {
      return this.field_76033_b;
   }

   @Override
   public final boolean equals(Object var1) {
      if (!(var1 instanceof IntHashMap$Entry)) {
         return false;
      } else {
         IntHashMap$Entry var2 = (IntHashMap$Entry)var1;
         Integer var3 = this.func_76031_a();
         Integer var4 = var2.func_76031_a();
         if (var3 == var4 || var3 != null && var3.equals(var4)) {
            Object var5 = this.func_76030_b();
            Object var6 = var2.func_76030_b();
            if (var5 == var6 || var5 != null && var5.equals(var6)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final int hashCode() {
      return IntHashMap.access$000(this.field_76035_a);
   }

   @Override
   public final String toString() {
      return this.func_76031_a() + "=" + this.func_76030_b();
   }
}
