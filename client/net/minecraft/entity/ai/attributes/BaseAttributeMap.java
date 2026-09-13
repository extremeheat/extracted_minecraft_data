package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.server.management.LowerStringMap;

public abstract class BaseAttributeMap {
   protected final Map field_111154_a = new HashMap();
   protected final Map field_111153_b = new LowerStringMap();

   public BaseAttributeMap() {
      super();
   }

   public IAttributeInstance func_111151_a(IAttribute var1) {
      return (IAttributeInstance)this.field_111154_a.get(var1);
   }

   public IAttributeInstance func_111152_a(String var1) {
      return (IAttributeInstance)this.field_111153_b.get(var1);
   }

   public abstract IAttributeInstance func_111150_b(IAttribute var1);

   public Collection func_111146_a() {
      return this.field_111153_b.values();
   }

   public void func_111149_a(ModifiableAttributeInstance var1) {
   }

   public void func_111148_a(Multimap var1) {
      for(Entry var3 : var1.entries()) {
         IAttributeInstance var4 = this.func_111152_a((String)var3.getKey());
         if (var4 != null) {
            var4.func_111124_b((AttributeModifier)var3.getValue());
         }
      }
   }

   public void func_111147_b(Multimap var1) {
      for(Entry var3 : var1.entries()) {
         IAttributeInstance var4 = this.func_111152_a((String)var3.getKey());
         if (var4 != null) {
            var4.func_111124_b((AttributeModifier)var3.getValue());
            var4.func_111121_a((AttributeModifier)var3.getValue());
         }
      }
   }
}
