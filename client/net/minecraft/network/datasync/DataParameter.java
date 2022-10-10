package net.minecraft.network.datasync;

public class DataParameter<T> {
   private final int field_187157_a;
   private final DataSerializer<T> field_187158_b;

   public DataParameter(int var1, DataSerializer<T> var2) {
      super();
      this.field_187157_a = var1;
      this.field_187158_b = var2;
   }

   public int func_187155_a() {
      return this.field_187157_a;
   }

   public DataSerializer<T> func_187156_b() {
      return this.field_187158_b;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         DataParameter var2 = (DataParameter)var1;
         return this.field_187157_a == var2.field_187157_a;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.field_187157_a;
   }
}
