package net.minecraft.server.packs.repository;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.server.packs.PackResources;
import net.minecraft.world.flag.FeatureFlagSet;

public class PackRepository {
   private final Set<RepositorySource> sources;
   private Map<String, Pack> available = ImmutableMap.of();
   private List<Pack> selected = ImmutableList.of();

   public PackRepository(RepositorySource... var1) {
      super();
      this.sources = ImmutableSet.copyOf(var1);
   }

   public void reload() {
      List var1 = this.selected.stream().map(Pack::getId).collect(ImmutableList.toImmutableList());
      this.available = this.discoverAvailable();
      this.selected = this.rebuildSelected(var1);
   }

   private Map<String, Pack> discoverAvailable() {
      TreeMap var1 = Maps.newTreeMap();

      for(RepositorySource var3 : this.sources) {
         var3.loadPacks(var1x -> var1.put(var1x.getId(), var1x));
      }

      return ImmutableMap.copyOf(var1);
   }

   public void setSelected(Collection<String> var1) {
      this.selected = this.rebuildSelected(var1);
   }

   public boolean addPack(String var1) {
      Pack var2 = this.available.get(var1);
      if (var2 != null && !this.selected.contains(var2)) {
         ArrayList var3 = Lists.newArrayList(this.selected);
         var3.add(var2);
         this.selected = var3;
         return true;
      } else {
         return false;
      }
   }

   public boolean removePack(String var1) {
      Pack var2 = this.available.get(var1);
      if (var2 != null && this.selected.contains(var2)) {
         ArrayList var3 = Lists.newArrayList(this.selected);
         var3.remove(var2);
         this.selected = var3;
         return true;
      } else {
         return false;
      }
   }

   private List<Pack> rebuildSelected(Collection<String> var1) {
      List var2 = this.getAvailablePacks(var1).collect(Collectors.toList());

      for(Pack var4 : this.available.values()) {
         if (var4.isRequired() && !var2.contains(var4)) {
            var4.getDefaultPosition().insert(var2, var4, Functions.identity(), false);
         }
      }

      return ImmutableList.copyOf(var2);
   }

   private Stream<Pack> getAvailablePacks(Collection<String> var1) {
      return var1.stream().map(this.available::get).filter(Objects::nonNull);
   }

   public Collection<String> getAvailableIds() {
      return this.available.keySet();
   }

   public Collection<Pack> getAvailablePacks() {
      return this.available.values();
   }

   public Collection<String> getSelectedIds() {
      return this.selected.stream().map(Pack::getId).collect(ImmutableSet.toImmutableSet());
   }

   public FeatureFlagSet getRequestedFeatureFlags() {
      return this.getSelectedPacks().stream().map(Pack::getRequestedFeatures).reduce(FeatureFlagSet::join).orElse(FeatureFlagSet.of());
   }

   public Collection<Pack> getSelectedPacks() {
      return this.selected;
   }

   @Nullable
   public Pack getPack(String var1) {
      return this.available.get(var1);
   }

   public boolean isAvailable(String var1) {
      return this.available.containsKey(var1);
   }

   public List<PackResources> openAllSelected() {
      return this.selected.stream().map(Pack::open).collect(ImmutableList.toImmutableList());
   }
}
