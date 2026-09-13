package net.minecraft.entity.ai.attributes;

import java.util.UUID;
import org.apache.commons.lang3.Validate;

public class AttributeModifier {
   private final double field_111174_a;
   private final int field_111172_b;
   private final String field_111173_c;
   private final UUID field_111170_d;
   private boolean field_111171_e = true;

   public AttributeModifier(String var1, double var2, int var4) {
      this(UUID.randomUUID(), var1, var2, var4);
   }

   public AttributeModifier(UUID var1, String var2, double var3, int var5) {
      super();
      this.field_111170_d = var1;
      this.field_111173_c = var2;
      this.field_111174_a = var3;
      this.field_111172_b = var5;
      Validate.notEmpty(var2, "Modifier name cannot be empty", new Object[0]);
      Validate.inclusiveBetween(0, 2, var5, "Invalid operation", new Object[0]);
   }

   public UUID func_111167_a() {
      return this.field_111170_d;
   }

   public String func_111166_b() {
      return this.field_111173_c;
   }

   public int func_111169_c() {
      return this.field_111172_b;
   }

   public double func_111164_d() {
      return this.field_111174_a;
   }

   public boolean func_111165_e() {
      return this.field_111171_e;
   }

   public AttributeModifier func_111168_a(boolean var1) {
      this.field_111171_e = var1;
      return this;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         AttributeModifier var2 = (AttributeModifier)var1;
         return this.field_111170_d != null ? this.field_111170_d.equals(var2.field_111170_d) : var2.field_111170_d == null;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return this.field_111170_d != null ? this.field_111170_d.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "AttributeModifier{amount="
         + this.field_111174_a
         + ", operation="
         + this.field_111172_b
         + ", name='"
         + this.field_111173_c
         + '\''
         + ", id="
         + this.field_111170_d
         + ", serialize="
         + this.field_111171_e
         + '}';
   }
}
