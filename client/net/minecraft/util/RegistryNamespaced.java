package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced extends RegistrySimple implements IObjectIntIterable {
   protected final ObjectIntIdentityMap field_148759_a = new ObjectIntIdentityMap();
   protected final Map field_148758_b = ((BiMap)this.field_82596_a).inverse();

   public RegistryNamespaced() {
      super();
   }

   public void func_148756_a(int var1, String var2, Object var3) {
      this.field_148759_a.func_148746_a(var3, var1);
      this.func_82595_a(func_148755_c(var2), var3);
   }

   @Override
   protected Map func_148740_a() {
      return HashBiMap.create();
   }

   public Object func_82594_a(String var1) {
      return super.func_82594_a(func_148755_c(var1));
   }

   public String func_148750_c(Object var1) {
      return (String)this.field_148758_b.get(var1);
   }

   public boolean func_148741_d(String var1) {
      return super.func_148741_d(func_148755_c(var1));
   }

   public int func_148757_b(Object var1) {
      return this.field_148759_a.func_148747_b(var1);
   }

   public Object func_148754_a(int var1) {
      return this.field_148759_a.func_148745_a(var1);
   }

   @Override
   public Iterator iterator() {
      return this.field_148759_a.iterator();
   }

   public boolean func_148753_b(int var1) {
      return this.field_148759_a.func_148744_b(var1);
   }

   private static String func_148755_c(String var0) {
      return var0.indexOf(58) == -1 ? "minecraft:" + var0 : var0;
   }
}
