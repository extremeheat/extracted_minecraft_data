package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {
   private final Set field_111162_d = Sets.newHashSet();
   protected final Map field_111163_c = new LowerStringMap();

   public ServersideAttributeMap() {
      super();
   }

   public ModifiableAttributeInstance func_111151_a(IAttribute var1) {
      return (ModifiableAttributeInstance)super.func_111151_a(var1);
   }

   public ModifiableAttributeInstance func_111152_a(String var1) {
      IAttributeInstance var2 = super.func_111152_a(var1);
      if (var2 == null) {
         var2 = (IAttributeInstance)this.field_111163_c.get(var1);
      }

      return (ModifiableAttributeInstance)var2;
   }

   @Override
   public IAttributeInstance func_111150_b(IAttribute var1) {
      if (this.field_111153_b.containsKey(var1.func_111108_a())) {
         throw new IllegalArgumentException("Attribute is already registered!");
      } else {
         ModifiableAttributeInstance var2 = new ModifiableAttributeInstance(this, var1);
         this.field_111153_b.put(var1.func_111108_a(), var2);
         if (var1 instanceof RangedAttribute && ((RangedAttribute)var1).func_111116_f() != null) {
            this.field_111163_c.put(((RangedAttribute)var1).func_111116_f(), var2);
         }

         this.field_111154_a.put(var1, var2);
         return var2;
      }
   }

   @Override
   public void func_111149_a(ModifiableAttributeInstance var1) {
      if (var1.func_111123_a().func_111111_c()) {
         this.field_111162_d.add(var1);
      }
   }

   public Set func_111161_b() {
      return this.field_111162_d;
   }

   public Collection func_111160_c() {
      HashSet var1 = Sets.newHashSet();

      for(IAttributeInstance var3 : this.func_111146_a()) {
         if (var3.func_111123_a().func_111111_c()) {
            var1.add(var3);
         }
      }

      return var1;
   }
}
