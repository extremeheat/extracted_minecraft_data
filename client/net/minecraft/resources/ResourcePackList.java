package net.minecraft.resources;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class ResourcePackList<T extends ResourcePackInfo> {
   private final Set<IPackFinder> field_198987_a = Sets.newHashSet();
   private final Map<String, T> field_198988_b = Maps.newLinkedHashMap();
   private final List<T> field_198989_c = Lists.newLinkedList();
   private final ResourcePackInfo.IFactory<T> field_198990_d;

   public ResourcePackList(ResourcePackInfo.IFactory<T> var1) {
      super();
      this.field_198990_d = var1;
   }

   public void func_198983_a() {
      Set var1 = (Set)this.field_198989_c.stream().map(ResourcePackInfo::func_195790_f).collect(Collectors.toCollection(LinkedHashSet::new));
      this.field_198988_b.clear();
      this.field_198989_c.clear();
      Iterator var2 = this.field_198987_a.iterator();

      while(var2.hasNext()) {
         IPackFinder var3 = (IPackFinder)var2.next();
         var3.func_195730_a(this.field_198988_b, this.field_198990_d);
      }

      this.func_198986_e();
      List var10000 = this.field_198989_c;
      Stream var10001 = var1.stream();
      Map var10002 = this.field_198988_b;
      var10002.getClass();
      var10000.addAll((Collection)var10001.map(var10002::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
      var2 = this.field_198988_b.values().iterator();

      while(var2.hasNext()) {
         ResourcePackInfo var4 = (ResourcePackInfo)var2.next();
         if (var4.func_195797_g() && !this.field_198989_c.contains(var4)) {
            var4.func_195792_i().func_198993_a(this.field_198989_c, var4, Functions.identity(), false);
         }
      }

   }

   private void func_198986_e() {
      ArrayList var1 = Lists.newArrayList(this.field_198988_b.entrySet());
      this.field_198988_b.clear();
      var1.stream().sorted(Entry.comparingByKey()).forEachOrdered((var1x) -> {
         ResourcePackInfo var10000 = (ResourcePackInfo)this.field_198988_b.put(var1x.getKey(), var1x.getValue());
      });
   }

   public void func_198985_a(Collection<T> var1) {
      this.field_198989_c.clear();
      this.field_198989_c.addAll(var1);
      Iterator var2 = this.field_198988_b.values().iterator();

      while(var2.hasNext()) {
         ResourcePackInfo var3 = (ResourcePackInfo)var2.next();
         if (var3.func_195797_g() && !this.field_198989_c.contains(var3)) {
            var3.func_195792_i().func_198993_a(this.field_198989_c, var3, Functions.identity(), false);
         }
      }

   }

   public Collection<T> func_198978_b() {
      return this.field_198988_b.values();
   }

   public Collection<T> func_198979_c() {
      ArrayList var1 = Lists.newArrayList(this.field_198988_b.values());
      var1.removeAll(this.field_198989_c);
      return var1;
   }

   public Collection<T> func_198980_d() {
      return this.field_198989_c;
   }

   @Nullable
   public T func_198981_a(String var1) {
      return (ResourcePackInfo)this.field_198988_b.get(var1);
   }

   public void func_198982_a(IPackFinder var1) {
      this.field_198987_a.add(var1);
   }
}
