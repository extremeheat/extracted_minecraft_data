package net.minecraft.util;

class LongHashMap$Entry {
   final long field_76150_a;
   Object field_76148_b;
   LongHashMap$Entry field_76149_c;
   final int field_76147_d;

   LongHashMap$Entry(int var1, long var2, Object var4, LongHashMap$Entry var5) {
      super();
      this.field_76148_b = var4;
      this.field_76149_c = var5;
      this.field_76150_a = var2;
      this.field_76147_d = var1;
   }

   public final long func_76146_a() {
      return this.field_76150_a;
   }

   public final Object func_76145_b() {
      return this.field_76148_b;
   }

   @Override
   public final boolean equals(Object var1) {
      if (!(var1 instanceof LongHashMap$Entry)) {
         return false;
      } else {
         LongHashMap$Entry var2 = (LongHashMap$Entry)var1;
         Long var3 = this.func_76146_a();
         Long var4 = var2.func_76146_a();
         if (var3 == var4 || var3 != null && var3.equals(var4)) {
            Object var5 = this.func_76145_b();
            Object var6 = var2.func_76145_b();
            if (var5 == var6 || var5 != null && var5.equals(var6)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public final int hashCode() {
      return LongHashMap.access$000(this.field_76150_a);
   }

   @Override
   public final String toString() {
      return this.func_76146_a() + "=" + this.func_76145_b();
   }
}
