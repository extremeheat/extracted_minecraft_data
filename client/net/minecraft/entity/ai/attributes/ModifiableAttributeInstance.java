package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final BaseAttributeMap field_111138_a;
   private final IAttribute field_111136_b;
   private final Map field_111137_c = Maps.newHashMap();
   private final Map field_111134_d = Maps.newHashMap();
   private final Map field_111135_e = Maps.newHashMap();
   private double field_111132_f;
   private boolean field_111133_g = true;
   private double field_111139_h;

   public ModifiableAttributeInstance(BaseAttributeMap var1, IAttribute var2) {
      super();
      this.field_111138_a = var1;
      this.field_111136_b = var2;
      this.field_111132_f = var2.func_111110_b();

      for(int var3 = 0; var3 < 3; ++var3) {
         this.field_111137_c.put(var3, new HashSet());
      }
   }

   @Override
   public IAttribute func_111123_a() {
      return this.field_111136_b;
   }

   @Override
   public double func_111125_b() {
      return this.field_111132_f;
   }

   @Override
   public void func_111128_a(double var1) {
      if (var1 != this.func_111125_b()) {
         this.field_111132_f = var1;
         this.func_111131_f();
      }
   }

   public Collection func_111130_a(int var1) {
      return (Collection)this.field_111137_c.get(var1);
   }

   @Override
   public Collection func_111122_c() {
      HashSet var1 = new HashSet();

      for(int var2 = 0; var2 < 3; ++var2) {
         var1.addAll(this.func_111130_a(var2));
      }

      return var1;
   }

   @Override
   public AttributeModifier func_111127_a(UUID var1) {
      return (AttributeModifier)this.field_111135_e.get(var1);
   }

   @Override
   public void func_111121_a(AttributeModifier var1) {
      if (this.func_111127_a(var1.func_111167_a()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Object var2 = (Set)this.field_111134_d.get(var1.func_111166_b());
         if (var2 == null) {
            var2 = new HashSet();
            this.field_111134_d.put(var1.func_111166_b(), var2);
         }

         ((Set)this.field_111137_c.get(var1.func_111169_c())).add(var1);
         var2.add(var1);
         this.field_111135_e.put(var1.func_111167_a(), var1);
         this.func_111131_f();
      }
   }

   private void func_111131_f() {
      this.field_111133_g = true;
      this.field_111138_a.func_111149_a(this);
   }

   @Override
   public void func_111124_b(AttributeModifier var1) {
      for(int var2 = 0; var2 < 3; ++var2) {
         Set var3 = (Set)this.field_111137_c.get(var2);
         var3.remove(var1);
      }

      Set var4 = (Set)this.field_111134_d.get(var1.func_111166_b());
      if (var4 != null) {
         var4.remove(var1);
         if (var4.isEmpty()) {
            this.field_111134_d.remove(var1.func_111166_b());
         }
      }

      this.field_111135_e.remove(var1.func_111167_a());
      this.func_111131_f();
   }

   @Override
   public void func_142049_d() {
      Collection var1 = this.func_111122_c();
      if (var1 != null) {
         for(AttributeModifier var3 : new ArrayList(var1)) {
            this.func_111124_b(var3);
         }
      }
   }

   @Override
   public double func_111126_e() {
      if (this.field_111133_g) {
         this.field_111139_h = this.func_111129_g();
         this.field_111133_g = false;
      }

      return this.field_111139_h;
   }

   private double func_111129_g() {
      double var1 = this.func_111125_b();

      for(AttributeModifier var4 : this.func_111130_a(0)) {
         var1 += var4.func_111164_d();
      }

      double var7 = var1;

      for(AttributeModifier var6 : this.func_111130_a(1)) {
         var7 += var1 * var6.func_111164_d();
      }

      for(AttributeModifier var9 : this.func_111130_a(2)) {
         var7 *= 1.0 + var9.func_111164_d();
      }

      return this.field_111136_b.func_111109_a(var7);
   }
}
