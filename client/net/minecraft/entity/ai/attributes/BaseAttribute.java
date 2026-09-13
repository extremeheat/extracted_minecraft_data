package net.minecraft.entity.ai.attributes;

public abstract class BaseAttribute implements IAttribute {
   private final String field_111115_a;
   private final double field_111113_b;
   private boolean field_111114_c;

   protected BaseAttribute(String var1, double var2) {
      super();
      this.field_111115_a = var1;
      this.field_111113_b = var2;
      if (var1 == null) {
         throw new IllegalArgumentException("Name cannot be null!");
      }
   }

   @Override
   public String func_111108_a() {
      return this.field_111115_a;
   }

   @Override
   public double func_111110_b() {
      return this.field_111113_b;
   }

   @Override
   public boolean func_111111_c() {
      return this.field_111114_c;
   }

   public BaseAttribute func_111112_a(boolean var1) {
      this.field_111114_c = var1;
      return this;
   }

   @Override
   public int hashCode() {
      return this.field_111115_a.hashCode();
   }
}
