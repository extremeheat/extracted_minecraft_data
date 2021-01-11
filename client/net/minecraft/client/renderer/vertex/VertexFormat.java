package net.minecraft.client.renderer.vertex;

import com.google.common.collect.Lists;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VertexFormat {
   private static final Logger field_177357_a = LogManager.getLogger();
   private final List<VertexFormatElement> field_177355_b;
   private final List<Integer> field_177356_c;
   private int field_177353_d;
   private int field_177354_e;
   private List<Integer> field_177351_f;
   private int field_177352_g;

   public VertexFormat(VertexFormat var1) {
      this();

      for(int var2 = 0; var2 < var1.func_177345_h(); ++var2) {
         this.func_181721_a(var1.func_177348_c(var2));
      }

      this.field_177353_d = var1.func_177338_f();
   }

   public VertexFormat() {
      super();
      this.field_177355_b = Lists.newArrayList();
      this.field_177356_c = Lists.newArrayList();
      this.field_177353_d = 0;
      this.field_177354_e = -1;
      this.field_177351_f = Lists.newArrayList();
      this.field_177352_g = -1;
   }

   public void func_177339_a() {
      this.field_177355_b.clear();
      this.field_177356_c.clear();
      this.field_177354_e = -1;
      this.field_177351_f.clear();
      this.field_177352_g = -1;
      this.field_177353_d = 0;
   }

   public VertexFormat func_181721_a(VertexFormatElement var1) {
      if (var1.func_177374_g() && this.func_177341_i()) {
         field_177357_a.warn("VertexFormat error: Trying to add a position VertexFormatElement when one already exists, ignoring.");
         return this;
      } else {
         this.field_177355_b.add(var1);
         this.field_177356_c.add(this.field_177353_d);
         switch(var1.func_177375_c()) {
         case NORMAL:
            this.field_177352_g = this.field_177353_d;
            break;
         case COLOR:
            this.field_177354_e = this.field_177353_d;
            break;
         case UV:
            this.field_177351_f.add(var1.func_177369_e(), this.field_177353_d);
         }

         this.field_177353_d += var1.func_177368_f();
         return this;
      }
   }

   public boolean func_177350_b() {
      return this.field_177352_g >= 0;
   }

   public int func_177342_c() {
      return this.field_177352_g;
   }

   public boolean func_177346_d() {
      return this.field_177354_e >= 0;
   }

   public int func_177340_e() {
      return this.field_177354_e;
   }

   public boolean func_177347_a(int var1) {
      return this.field_177351_f.size() - 1 >= var1;
   }

   public int func_177344_b(int var1) {
      return (Integer)this.field_177351_f.get(var1);
   }

   public String toString() {
      String var1 = "format: " + this.field_177355_b.size() + " elements: ";

      for(int var2 = 0; var2 < this.field_177355_b.size(); ++var2) {
         var1 = var1 + ((VertexFormatElement)this.field_177355_b.get(var2)).toString();
         if (var2 != this.field_177355_b.size() - 1) {
            var1 = var1 + " ";
         }
      }

      return var1;
   }

   private boolean func_177341_i() {
      int var1 = 0;

      for(int var2 = this.field_177355_b.size(); var1 < var2; ++var1) {
         VertexFormatElement var3 = (VertexFormatElement)this.field_177355_b.get(var1);
         if (var3.func_177374_g()) {
            return true;
         }
      }

      return false;
   }

   public int func_181719_f() {
      return this.func_177338_f() / 4;
   }

   public int func_177338_f() {
      return this.field_177353_d;
   }

   public List<VertexFormatElement> func_177343_g() {
      return this.field_177355_b;
   }

   public int func_177345_h() {
      return this.field_177355_b.size();
   }

   public VertexFormatElement func_177348_c(int var1) {
      return (VertexFormatElement)this.field_177355_b.get(var1);
   }

   public int func_181720_d(int var1) {
      return (Integer)this.field_177356_c.get(var1);
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         VertexFormat var2 = (VertexFormat)var1;
         if (this.field_177353_d != var2.field_177353_d) {
            return false;
         } else if (!this.field_177355_b.equals(var2.field_177355_b)) {
            return false;
         } else {
            return this.field_177356_c.equals(var2.field_177356_c);
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.field_177355_b.hashCode();
      var1 = 31 * var1 + this.field_177356_c.hashCode();
      var1 = 31 * var1 + this.field_177353_d;
      return var1;
   }
}
