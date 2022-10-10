package net.minecraft.client.renderer.vertex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormatElement {
   private static final Logger field_177381_a = LogManager.getLogger();
   private final VertexFormatElement.EnumType field_177379_b;
   private final VertexFormatElement.EnumUsage field_177380_c;
   private final int field_177377_d;
   private final int field_177378_e;

   public VertexFormatElement(int var1, VertexFormatElement.EnumType var2, VertexFormatElement.EnumUsage var3, int var4) {
      super();
      if (this.func_177372_a(var1, var3)) {
         this.field_177380_c = var3;
      } else {
         field_177381_a.warn("Multiple vertex elements of the same type other than UVs are not supported. Forcing type to UV.");
         this.field_177380_c = VertexFormatElement.EnumUsage.UV;
      }

      this.field_177379_b = var2;
      this.field_177377_d = var1;
      this.field_177378_e = var4;
   }

   private final boolean func_177372_a(int var1, VertexFormatElement.EnumUsage var2) {
      return var1 == 0 || var2 == VertexFormatElement.EnumUsage.UV;
   }

   public final VertexFormatElement.EnumType func_177367_b() {
      return this.field_177379_b;
   }

   public final VertexFormatElement.EnumUsage func_177375_c() {
      return this.field_177380_c;
   }

   public final int func_177370_d() {
      return this.field_177378_e;
   }

   public final int func_177369_e() {
      return this.field_177377_d;
   }

   public String toString() {
      return this.field_177378_e + "," + this.field_177380_c.func_177384_a() + "," + this.field_177379_b.func_177396_b();
   }

   public final int func_177368_f() {
      return this.field_177379_b.func_177395_a() * this.field_177378_e;
   }

   public final boolean func_177374_g() {
      return this.field_177380_c == VertexFormatElement.EnumUsage.POSITION;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormatElement var2 = (VertexFormatElement)var1;
         if (this.field_177378_e != var2.field_177378_e) {
            return false;
         } else if (this.field_177377_d != var2.field_177377_d) {
            return false;
         } else if (this.field_177379_b != var2.field_177379_b) {
            return false;
         } else {
            return this.field_177380_c == var2.field_177380_c;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.field_177379_b.hashCode();
      var1 = 31 * var1 + this.field_177380_c.hashCode();
      var1 = 31 * var1 + this.field_177377_d;
      var1 = 31 * var1 + this.field_177378_e;
      return var1;
   }

   public static enum EnumType {
      FLOAT(4, "Float", 5126),
      UBYTE(1, "Unsigned Byte", 5121),
      BYTE(1, "Byte", 5120),
      USHORT(2, "Unsigned Short", 5123),
      SHORT(2, "Short", 5122),
      UINT(4, "Unsigned Int", 5125),
      INT(4, "Int", 5124);

      private final int field_177407_h;
      private final String field_177408_i;
      private final int field_177405_j;

      private EnumType(int var3, String var4, int var5) {
         this.field_177407_h = var3;
         this.field_177408_i = var4;
         this.field_177405_j = var5;
      }

      public int func_177395_a() {
         return this.field_177407_h;
      }

      public String func_177396_b() {
         return this.field_177408_i;
      }

      public int func_177397_c() {
         return this.field_177405_j;
      }
   }

   public static enum EnumUsage {
      POSITION("Position"),
      NORMAL("Normal"),
      COLOR("Vertex Color"),
      UV("UV"),
      MATRIX("Bone Matrix"),
      BLEND_WEIGHT("Blend Weight"),
      PADDING("Padding");

      private final String field_177392_h;

      private EnumUsage(String var3) {
         this.field_177392_h = var3;
      }

      public String func_177384_a() {
         return this.field_177392_h;
      }
   }
}
