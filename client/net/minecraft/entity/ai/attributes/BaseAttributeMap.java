package net.minecraft.entity.ai.attributes;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {
   protected final Map<IAttribute, IAttributeInstance> field_111154_a = Maps.newHashMap();
   protected final Map<String, IAttributeInstance> field_111153_b = new LowerStringMap();
   protected final Multimap<IAttribute, IAttribute> field_180377_c = HashMultimap.create();

   public BaseAttributeMap() {
      super();
   }

   public IAttributeInstance func_111151_a(IAttribute var1) {
      return (IAttributeInstance)this.field_111154_a.get(var1);
   }

   public IAttributeInstance func_111152_a(String var1) {
      return (IAttributeInstance)this.field_111153_b.get(var1);
   }

   public IAttributeInstance func_111150_b(IAttribute var1) {
      if (this.field_111153_b.containsKey(var1.func_111108_a())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         IAttributeInstance var2 = this.func_180376_c(var1);
         this.field_111153_b.put(var1.func_111108_a(), var2);
         this.field_111154_a.put(var1, var2);

         for(IAttribute var3 = var1.func_180372_d(); var3 != null; var3 = var3.func_180372_d()) {
            this.field_180377_c.put(var3, var1);
         }

         return var2;
      }
   }

   protected abstract IAttributeInstance func_180376_c(IAttribute var1);

   public Collection<IAttributeInstance> func_111146_a() {
      return this.field_111153_b.values();
   }

   public void func_180794_a(IAttributeInstance var1) {
   }

   public void func_111148_a(Multimap<String, AttributeModifier> var1) {
      Iterator var2 = var1.entries().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         IAttributeInstance var4 = this.func_111152_a((String)var3.getKey());
         if (var4 != null) {
            var4.func_111124_b((AttributeModifier)var3.getValue());
         }
      }

   }

   public void func_111147_b(Multimap<String, AttributeModifier> var1) {
      Iterator var2 = var1.entries().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         IAttributeInstance var4 = this.func_111152_a((String)var3.getKey());
         if (var4 != null) {
            var4.func_111124_b((AttributeModifier)var3.getValue());
            var4.func_111121_a((AttributeModifier)var3.getValue());
         }
      }

   }
}
