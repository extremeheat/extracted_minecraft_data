package net.minecraft.block.properties;

import com.google.common.base.Objects;

public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> field_177704_a;
   private final String field_177703_b;

   protected PropertyHelper(String var1, Class<T> var2) {
      super();
      this.field_177704_a = var2;
      this.field_177703_b = var1;
   }

   public String func_177701_a() {
      return this.field_177703_b;
   }

   public Class<T> func_177699_b() {
      return this.field_177704_a;
   }

   public String toString() {
      return Objects.toStringHelper(this).add("name", this.field_177703_b).add("clazz", this.field_177704_a).add("values", this.func_177700_c()).toString();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         PropertyHelper var2 = (PropertyHelper)var1;
         return this.field_177704_a.equals(var2.field_177704_a) && this.field_177703_b.equals(var2.field_177703_b);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return 31 * this.field_177704_a.hashCode() + this.field_177703_b.hashCode();
   }
}
