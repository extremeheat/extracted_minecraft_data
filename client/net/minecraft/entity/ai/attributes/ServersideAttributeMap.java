package net.minecraft.entity.ai.attributes;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.server.management.LowerStringMap;

public class ServersideAttributeMap extends BaseAttributeMap {
   private final Set<IAttributeInstance> field_111162_d = Sets.newHashSet();
   protected final Map<String, IAttributeInstance> field_111163_c = new LowerStringMap();

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

   public IAttributeInstance func_111150_b(IAttribute var1) {
      IAttributeInstance var2 = super.func_111150_b(var1);
      if (var1 instanceof RangedAttribute && ((RangedAttribute)var1).func_111116_f() != null) {
         this.field_111163_c.put(((RangedAttribute)var1).func_111116_f(), var2);
      }

      return var2;
   }

   protected IAttributeInstance func_180376_c(IAttribute var1) {
      return new ModifiableAttributeInstance(this, var1);
   }

   public void func_180794_a(IAttributeInstance var1) {
      if (var1.func_111123_a().func_111111_c()) {
         this.field_111162_d.add(var1);
      }

      Iterator var2 = this.field_180377_c.get(var1.func_111123_a()).iterator();

      while(var2.hasNext()) {
         IAttribute var3 = (IAttribute)var2.next();
         ModifiableAttributeInstance var4 = this.func_111151_a(var3);
         if (var4 != null) {
            var4.func_111131_f();
         }
      }

   }

   public Set<IAttributeInstance> func_111161_b() {
      return this.field_111162_d;
   }

   public Collection<IAttributeInstance> func_111160_c() {
      HashSet var1 = Sets.newHashSet();
      Iterator var2 = this.func_111146_a().iterator();

      while(var2.hasNext()) {
         IAttributeInstance var3 = (IAttributeInstance)var2.next();
         if (var3.func_111123_a().func_111111_c()) {
            var1.add(var3);
         }
      }

      return var1;
   }

   // $FF: synthetic method
   public IAttributeInstance func_111152_a(String var1) {
      return this.func_111152_a(var1);
   }

   // $FF: synthetic method
   public IAttributeInstance func_111151_a(IAttribute var1) {
      return this.func_111151_a(var1);
   }
}
