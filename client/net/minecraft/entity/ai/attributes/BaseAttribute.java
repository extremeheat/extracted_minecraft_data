package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {
   private final IAttribute field_180373_a;
   private final String field_111115_a;
   private final double field_111113_b;
   private boolean field_111114_c;

   protected BaseAttribute(IAttribute var1, String var2, double var3) {
      super();
      this.field_180373_a = var1;
      this.field_111115_a = var2;
      this.field_111113_b = var3;
      if (var2 == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   public String func_111108_a() {
      return this.field_111115_a;
   }

   public double func_111110_b() {
      return this.field_111113_b;
   }

   public boolean func_111111_c() {
      return this.field_111114_c;
   }

   public BaseAttribute func_111112_a(boolean var1) {
      this.field_111114_c = var1;
      return this;
   }

   public IAttribute func_180372_d() {
      return this.field_180373_a;
   }

   public int hashCode() {
      return this.field_111115_a.hashCode();
   }

   public boolean equals(Object var1) {
      return var1 instanceof IAttribute && this.field_111115_a.equals(((IAttribute)var1).func_111108_a());
   }
}
