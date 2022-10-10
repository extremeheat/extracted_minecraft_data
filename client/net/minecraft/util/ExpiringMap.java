package net.minecraft.util;

import it.unimi.dsi.fastutil.longs.Long2LongLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap.Entry;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.Map;

public class ExpiringMap<T> extends Long2ObjectOpenHashMap<T> {
   private final int field_201843_a;
   private final Long2LongMap field_201844_b = new Long2LongLinkedOpenHashMap();

   public ExpiringMap(int var1, int var2) {
      super(var1);
      this.field_201843_a = var2;
   }

   private void func_201842_a(long var1) {
      long var3 = Util.func_211177_b();
      this.field_201844_b.put(var1, var3);
      ObjectIterator var5 = this.field_201844_b.long2LongEntrySet().iterator();

      while(var5.hasNext()) {
         Entry var6 = (Entry)var5.next();
         Object var7 = super.get(var6.getLongKey());
         if (var3 - var6.getLongValue() <= (long)this.field_201843_a) {
            break;
         }

         if (var7 != null && this.func_205609_a_(var7)) {
            super.remove(var6.getLongKey());
            var5.remove();
         }
      }

   }

   protected boolean func_205609_a_(T var1) {
      return true;
   }

   public T put(long var1, T var3) {
      this.func_201842_a(var1);
      return super.put(var1, var3);
   }

   public T put(Long var1, T var2) {
      this.func_201842_a(var1);
      return super.put(var1, var2);
   }

   public T get(long var1) {
      this.func_201842_a(var1);
      return super.get(var1);
   }

   public void putAll(Map<? extends Long, ? extends T> var1) {
      throw new RuntimeException("Not implemented");
   }

   public T remove(long var1) {
      throw new RuntimeException("Not implemented");
   }

   public T remove(Object var1) {
      throw new RuntimeException("Not implemented");
   }
}
