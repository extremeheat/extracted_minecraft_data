package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrySimple implements IRegistry {
   private static final Logger field_148743_a = LogManager.getLogger();
   protected final Map field_82596_a = this.func_148740_a();

   public RegistrySimple() {
      super();
   }

   protected Map func_148740_a() {
      return Maps.newHashMap();
   }

   @Override
   public Object func_82594_a(Object var1) {
      return this.field_82596_a.get(var1);
   }

   @Override
   public void func_82595_a(Object var1, Object var2) {
      if (this.field_82596_a.containsKey(var1)) {
         field_148743_a.debug("Adding duplicate key '" + var1 + "' to registry");
      }

      this.field_82596_a.put(var1, var2);
   }

   public Set func_148742_b() {
      return Collections.unmodifiableSet(this.field_82596_a.keySet());
   }

   public boolean func_148741_d(Object var1) {
      return this.field_82596_a.containsKey(var1);
   }
}
