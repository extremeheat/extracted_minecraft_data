package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;

public class PackRepository implements AutoCloseable {
   private final Set<RepositorySource> sources;
   private Map<String, Pack> available;
   private List<Pack> selected;
   private final Pack.PackConstructor constructor;

   public PackRepository(Pack.PackConstructor var1, RepositorySource... var2) {
      super();
      this.available = ImmutableMap.of();
      this.selected = ImmutableList.of();
      this.constructor = var1;
      this.sources = ImmutableSet.copyOf(var2);
   }

   public PackRepository(PackType var1, RepositorySource... var2) {
      this((var1x, var2x, var3, var4, var5, var6, var7) -> {
         return new Pack(var1x, var2x, var3, var4, var5, var1, var6, var7);
      }, var2);
   }

   public void reload() {
      List var1 = (List)this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
      this.close();
      this.available = this.discoverAvailable();
      this.selected = this.rebuildSelected(var1);
   }

   private Map<String, Pack> discoverAvailable() {
      TreeMap var1 = Maps.newTreeMap();
      Iterator var2 = this.sources.iterator();

      while(var2.hasNext()) {
         RepositorySource var3 = (RepositorySource)var2.next();
         var3.loadPacks((var1x) -> {
            Pack var10000 = (Pack)var1.put(var1x.getId(), var1x);
         }, this.constructor);
      }

      return ImmutableMap.copyOf(var1);
   }

   public void setSelected(Collection<String> var1) {
      this.selected = this.rebuildSelected(var1);
   }

   private List<Pack> rebuildSelected(Collection<String> var1) {
      List var2 = (List)this.getAvailablePacks(var1).collect(Collectors.toList());
      Iterator var3 = this.available.values().iterator();

      while(var3.hasNext()) {
         Pack var4 = (Pack)var3.next();
         if (var4.isRequired() && !var2.contains(var4)) {
            var4.getDefaultPosition().insert(var2, var4, Functions.identity(), false);
         }
      }

      return ImmutableList.copyOf(var2);
   }

   private Stream<Pack> getAvailablePacks(Collection<String> var1) {
      Stream var10000 = var1.stream();
      Map var10001 = this.available;
      var10001.getClass();
      return var10000.map(var10001::get).filter(Objects::nonNull);
   }

   public Collection<String> getAvailableIds() {
      return this.available.keySet();
   }

   public Collection<Pack> getAvailablePacks() {
      return this.available.values();
   }

   public Collection<String> getSelectedIds() {
      return (Collection)this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
   }

   public Collection<Pack> getSelectedPacks() {
      return this.selected;
   }

   @Nullable
   public Pack getPack(String var1) {
      return (Pack)this.available.get(var1);
   }

   public void close() {
      this.available.values().forEach(Pack::close);
   }

   public boolean isAvailable(String var1) {
      return this.available.containsKey(var1);
   }

   public List<PackResources> openAllSelected() {
      return (List)this.selected.stream().map(Pack::open).collect(ImmutableList.toImmutableList());
   }
}
