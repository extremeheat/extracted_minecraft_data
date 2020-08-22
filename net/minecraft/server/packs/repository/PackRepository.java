package net.minecraft.server.packs.repository;

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

public class PackRepository implements AutoCloseable {
   private final Set sources = Sets.newHashSet();
   private final Map available = Maps.newLinkedHashMap();
   private final List selected = Lists.newLinkedList();
   private final UnopenedPack.UnopenedPackConstructor constructor;

   public PackRepository(UnopenedPack.UnopenedPackConstructor var1) {
      this.constructor = var1;
   }

   public void reload() {
      this.close();
      Set var1 = (Set)this.selected.stream().map(UnopenedPack::getId).collect(Collectors.toCollection(LinkedHashSet::new));
      this.available.clear();
      this.selected.clear();
      Iterator var2 = this.sources.iterator();

      while(var2.hasNext()) {
         RepositorySource var3 = (RepositorySource)var2.next();
         var3.loadPacks(this.available, this.constructor);
      }

      this.sortAvailable();
      List var10000 = this.selected;
      Stream var10001 = var1.stream();
      Map var10002 = this.available;
      var10002.getClass();
      var10000.addAll((Collection)var10001.map(var10002::get).filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new)));
      var2 = this.available.values().iterator();

      while(var2.hasNext()) {
         UnopenedPack var4 = (UnopenedPack)var2.next();
         if (var4.isRequired() && !this.selected.contains(var4)) {
            var4.getDefaultPosition().insert(this.selected, var4, Functions.identity(), false);
         }
      }

   }

   private void sortAvailable() {
      ArrayList var1 = Lists.newArrayList(this.available.entrySet());
      this.available.clear();
      var1.stream().sorted(Entry.comparingByKey()).forEachOrdered((var1x) -> {
         UnopenedPack var10000 = (UnopenedPack)this.available.put(var1x.getKey(), var1x.getValue());
      });
   }

   public void setSelected(Collection var1) {
      this.selected.clear();
      this.selected.addAll(var1);
      Iterator var2 = this.available.values().iterator();

      while(var2.hasNext()) {
         UnopenedPack var3 = (UnopenedPack)var2.next();
         if (var3.isRequired() && !this.selected.contains(var3)) {
            var3.getDefaultPosition().insert(this.selected, var3, Functions.identity(), false);
         }
      }

   }

   public Collection getAvailable() {
      return this.available.values();
   }

   public Collection getUnselected() {
      ArrayList var1 = Lists.newArrayList(this.available.values());
      var1.removeAll(this.selected);
      return var1;
   }

   public Collection getSelected() {
      return this.selected;
   }

   @Nullable
   public UnopenedPack getPack(String var1) {
      return (UnopenedPack)this.available.get(var1);
   }

   public void addSource(RepositorySource var1) {
      this.sources.add(var1);
   }

   public void close() {
      this.available.values().forEach(UnopenedPack::close);
   }
}
