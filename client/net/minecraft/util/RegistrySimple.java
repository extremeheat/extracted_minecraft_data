package net.minecraft.util;

import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegistrySimple<K, V> implements IRegistry<K, V> {
   private static final Logger field_148743_a = LogManager.getLogger();
   protected final Map<K, V> field_82596_a = this.func_148740_a();

   public RegistrySimple() {
      super();
   }

   protected Map<K, V> func_148740_a() {
      return Maps.newHashMap();
   }

   public V func_82594_a(K var1) {
      return this.field_82596_a.get(var1);
   }

   public void func_82595_a(K var1, V var2) {
      Validate.notNull(var1);
      Validate.notNull(var2);
      if (this.field_82596_a.containsKey(var1)) {
         field_148743_a.debug("Adding duplicate key '" + var1 + "' to registry");
      }

      this.field_82596_a.put(var1, var2);
   }

   public Set<K> func_148742_b() {
      return Collections.unmodifiableSet(this.field_82596_a.keySet());
   }

   public boolean func_148741_d(K var1) {
      return this.field_82596_a.containsKey(var1);
   }

   public Iterator<V> iterator() {
      return this.field_82596_a.values().iterator();
   }
}
