package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.NBTTagCompound;

@Immutable
public class LockCode {
   public static final LockCode field_180162_a = new LockCode("");
   private final String field_180161_b;

   public LockCode(String var1) {
      super();
      this.field_180161_b = var1;
   }

   public boolean func_180160_a() {
      return this.field_180161_b == null || this.field_180161_b.isEmpty();
   }

   public String func_180159_b() {
      return this.field_180161_b;
   }

   public void func_180157_a(NBTTagCompound var1) {
      var1.func_74778_a("Lock", this.field_180161_b);
   }

   public static LockCode func_180158_b(NBTTagCompound var0) {
      if (var0.func_150297_b("Lock", 8)) {
         String var1 = var0.func_74779_i("Lock");
         return new LockCode(var1);
      } else {
         return field_180162_a;
      }
   }
}
