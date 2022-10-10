package net.minecraft.stats;

import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.util.registry.IRegistry;

public class StatType<T> implements Iterable<Stat<T>> {
   private final IRegistry<T> field_199082_a;
   private final Map<T, Stat<T>> field_199083_b = new IdentityHashMap();

   public StatType(IRegistry<T> var1) {
      super();
      this.field_199082_a = var1;
   }

   public boolean func_199079_a(T var1) {
      return this.field_199083_b.containsKey(var1);
   }

   public Stat<T> func_199077_a(T var1, IStatFormater var2) {
      return (Stat)this.field_199083_b.computeIfAbsent(var1, (var2x) -> {
         return new Stat(this, var2x, var2);
      });
   }

   public IRegistry<T> func_199080_a() {
      return this.field_199082_a;
   }

   public int func_199081_b() {
      return this.field_199083_b.size();
   }

   public Iterator<Stat<T>> iterator() {
      return this.field_199083_b.values().iterator();
   }

   public Stat<T> func_199076_b(T var1) {
      return this.func_199077_a(var1, IStatFormater.DEFAULT);
   }

   public String func_199078_c() {
      return "stat_type." + IRegistry.field_212634_w.func_177774_c(this).toString().replace(':', '.');
   }
}
