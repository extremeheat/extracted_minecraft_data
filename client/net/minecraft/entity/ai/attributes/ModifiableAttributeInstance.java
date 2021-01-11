package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ModifiableAttributeInstance implements IAttributeInstance {
   private final BaseAttributeMap field_111138_a;
   private final IAttribute field_111136_b;
   private final Map<Integer, Set<AttributeModifier>> field_111137_c = Maps.newHashMap();
   private final Map<String, Set<AttributeModifier>> field_111134_d = Maps.newHashMap();
   private final Map<UUID, AttributeModifier> field_111135_e = Maps.newHashMap();
   private double field_111132_f;
   private boolean field_111133_g = true;
   private double field_111139_h;

   public ModifiableAttributeInstance(BaseAttributeMap var1, IAttribute var2) {
      super();
      this.field_111138_a = var1;
      this.field_111136_b = var2;
      this.field_111132_f = var2.func_111110_b();

      for(int var3 = 0; var3 < 3; ++var3) {
         this.field_111137_c.put(var3, Sets.newHashSet());
      }

   }

   public IAttribute func_111123_a() {
      return this.field_111136_b;
   }

   public double func_111125_b() {
      return this.field_111132_f;
   }

   public void func_111128_a(double var1) {
      if (var1 != this.func_111125_b()) {
         this.field_111132_f = var1;
         this.func_111131_f();
      }
   }

   public Collection<AttributeModifier> func_111130_a(int var1) {
      return (Collection)this.field_111137_c.get(var1);
   }

   public Collection<AttributeModifier> func_111122_c() {
      HashSet var1 = Sets.newHashSet();

      for(int var2 = 0; var2 < 3; ++var2) {
         var1.addAll(this.func_111130_a(var2));
      }

      return var1;
   }

   public AttributeModifier func_111127_a(UUID var1) {
      return (AttributeModifier)this.field_111135_e.get(var1);
   }

   public boolean func_180374_a(AttributeModifier var1) {
      return this.field_111135_e.get(var1.func_111167_a()) != null;
   }

   public void func_111121_a(AttributeModifier var1) {
      if (this.func_111127_a(var1.func_111167_a()) != null) {
         throw new IllegalArgumentException("Modifier is already applied on this attribute!");
      } else {
         Object var2 = (Set)this.field_111134_d.get(var1.func_111166_b());
         if (var2 == null) {
            var2 = Sets.newHashSet();
            this.field_111134_d.put(var1.func_111166_b(), var2);
         }

         ((Set)this.field_111137_c.get(var1.func_111169_c())).add(var1);
         ((Set)var2).add(var1);
         this.field_111135_e.put(var1.func_111167_a(), var1);
         this.func_111131_f();
      }
   }

   protected void func_111131_f() {
      this.field_111133_g = true;
      this.field_111138_a.func_180794_a(this);
   }

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

   public void func_142049_d() {
      Collection var1 = this.func_111122_c();
      if (var1 != null) {
         ArrayList var4 = Lists.newArrayList(var1);
         Iterator var2 = var4.iterator();

         while(var2.hasNext()) {
            AttributeModifier var3 = (AttributeModifier)var2.next();
            this.func_111124_b(var3);
         }

      }
   }

   public double func_111126_e() {
      if (this.field_111133_g) {
         this.field_111139_h = this.func_111129_g();
         this.field_111133_g = false;
      }

      return this.field_111139_h;
   }

   private double func_111129_g() {
      double var1 = this.func_111125_b();

      AttributeModifier var4;
      for(Iterator var3 = this.func_180375_b(0).iterator(); var3.hasNext(); var1 += var4.func_111164_d()) {
         var4 = (AttributeModifier)var3.next();
      }

      double var7 = var1;

      Iterator var5;
      AttributeModifier var6;
      for(var5 = this.func_180375_b(1).iterator(); var5.hasNext(); var7 += var1 * var6.func_111164_d()) {
         var6 = (AttributeModifier)var5.next();
      }

      for(var5 = this.func_180375_b(2).iterator(); var5.hasNext(); var7 *= 1.0D + var6.func_111164_d()) {
         var6 = (AttributeModifier)var5.next();
      }

      return this.field_111136_b.func_111109_a(var7);
   }

   private Collection<AttributeModifier> func_180375_b(int var1) {
      HashSet var2 = Sets.newHashSet(this.func_111130_a(var1));

      for(IAttribute var3 = this.field_111136_b.func_180372_d(); var3 != null; var3 = var3.func_180372_d()) {
         IAttributeInstance var4 = this.field_111138_a.func_111151_a(var3);
         if (var4 != null) {
            var2.addAll(var4.func_111130_a(var1));
         }
      }

      return var2;
   }
}
