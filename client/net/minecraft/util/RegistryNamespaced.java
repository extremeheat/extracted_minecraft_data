package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {
   protected final ObjectIntIdentityMap<V> field_148759_a = new ObjectIntIdentityMap();
   protected final Map<V, K> field_148758_b;

   public RegistryNamespaced() {
      super();
      this.field_148758_b = ((BiMap)this.field_82596_a).inverse();
   }

   public void func_177775_a(int var1, K var2, V var3) {
      this.field_148759_a.func_148746_a(var3, var1);
      this.func_82595_a(var2, var3);
   }

   protected Map<K, V> func_148740_a() {
      return HashBiMap.create();
   }

   public V func_82594_a(K var1) {
      return super.func_82594_a(var1);
   }

   public K func_177774_c(V var1) {
      return this.field_148758_b.get(var1);
   }

   public boolean func_148741_d(K var1) {
      return super.func_148741_d(var1);
   }

   public int func_148757_b(V var1) {
      return this.field_148759_a.func_148747_b(var1);
   }

   public V func_148754_a(int var1) {
      return this.field_148759_a.func_148745_a(var1);
   }

   public Iterator<V> iterator() {
      return this.field_148759_a.iterator();
   }
}
