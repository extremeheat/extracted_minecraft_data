package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;

public class MappedRegistry<T> extends WritableRegistry<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList(256);
   private final Object2IntMap<T> toId = (Object2IntMap)Util.make(new Object2IntOpenCustomHashMap(Util.identityStrategy()), (var0) -> {
      var0.defaultReturnValue(-1);
   });
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap();
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap();
   private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap();
   private final Map<T, Lifecycle> lifecycles = new IdentityHashMap();
   private Lifecycle elementsLifecycle;
   private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap();
   private boolean frozen;
   @Nullable
   private final Function<T, Holder.Reference<T>> customHolderProvider;
   @Nullable
   private Map<T, Holder.Reference<T>> intrusiveHolderCache;
   @Nullable
   private List<Holder.Reference<T>> holdersInOrder;
   private int nextId;

   public MappedRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, @Nullable Function<T, Holder.Reference<T>> var3) {
      super(var1, var2);
      this.elementsLifecycle = var2;
      this.customHolderProvider = var3;
      if (var3 != null) {
         this.intrusiveHolderCache = new IdentityHashMap();
      }

   }

   private List<Holder.Reference<T>> holdersInOrder() {
      if (this.holdersInOrder == null) {
         this.holdersInOrder = this.byId.stream().filter(Objects::nonNull).toList();
      }

      return this.holdersInOrder;
   }

   private void validateWrite(ResourceKey<T> var1) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + var1 + ")");
      }
   }

   public Holder<T> registerMapping(int var1, ResourceKey<T> var2, T var3, Lifecycle var4) {
      return this.registerMapping(var1, var2, var3, var4, true);
   }

   private Holder<T> registerMapping(int var1, ResourceKey<T> var2, T var3, Lifecycle var4, boolean var5) {
      this.validateWrite(var2);
      Validate.notNull(var2);
      Validate.notNull(var3);
      this.byId.size(Math.max(this.byId.size(), var1 + 1));
      this.toId.put(var3, var1);
      this.holdersInOrder = null;
      if (var5 && this.byKey.containsKey(var2)) {
         Util.logAndPauseIfInIde("Adding duplicate key '" + var2 + "' to registry");
      }

      if (this.byValue.containsKey(var3)) {
         Util.logAndPauseIfInIde("Adding duplicate value '" + var3 + "' to registry");
      }

      this.lifecycles.put(var3, var4);
      this.elementsLifecycle = this.elementsLifecycle.add(var4);
      if (this.nextId <= var1) {
         this.nextId = var1 + 1;
      }

      Holder.Reference var6;
      if (this.customHolderProvider != null) {
         var6 = (Holder.Reference)this.customHolderProvider.apply(var3);
         Holder.Reference var7 = (Holder.Reference)this.byKey.put(var2, var6);
         if (var7 != null && var7 != var6) {
            throw new IllegalStateException("Invalid holder present for key " + var2);
         }
      } else {
         var6 = (Holder.Reference)this.byKey.computeIfAbsent(var2, (var1x) -> {
            return Holder.Reference.createStandAlone(this, var1x);
         });
      }

      this.byLocation.put(var2.location(), var6);
      this.byValue.put(var3, var6);
      var6.bind(var2, var3);
      this.byId.set(var1, var6);
      return var6;
   }

   public Holder<T> register(ResourceKey<T> var1, T var2, Lifecycle var3) {
      return this.registerMapping(this.nextId, var1, var2, var3);
   }

   public Holder<T> registerOrOverride(OptionalInt var1, ResourceKey<T> var2, T var3, Lifecycle var4) {
      this.validateWrite(var2);
      Validate.notNull(var2);
      Validate.notNull(var3);
      Holder var5 = (Holder)this.byKey.get(var2);
      Object var6 = var5 != null && var5.isBound() ? var5.value() : null;
      int var7;
      if (var6 == null) {
         var7 = var1.orElse(this.nextId);
      } else {
         var7 = this.toId.getInt(var6);
         if (var1.isPresent() && var1.getAsInt() != var7) {
            throw new IllegalStateException("ID mismatch");
         }

         this.lifecycles.remove(var6);
         this.toId.removeInt(var6);
         this.byValue.remove(var6);
      }

      return this.registerMapping(var7, var2, var3, var4, false);
   }

   @Nullable
   public ResourceLocation getKey(T var1) {
      Holder.Reference var2 = (Holder.Reference)this.byValue.get(var1);
      return var2 != null ? var2.key().location() : null;
   }

   public Optional<ResourceKey<T>> getResourceKey(T var1) {
      return Optional.ofNullable((Holder.Reference)this.byValue.get(var1)).map(Holder.Reference::key);
   }

   public int getId(@Nullable T var1) {
      return this.toId.getInt(var1);
   }

   @Nullable
   public T get(@Nullable ResourceKey<T> var1) {
      return getValueFromNullable((Holder.Reference)this.byKey.get(var1));
   }

   @Nullable
   public T byId(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? getValueFromNullable((Holder.Reference)this.byId.get(var1)) : null;
   }

   public Optional<Holder<T>> getHolder(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? Optional.ofNullable((Holder)this.byId.get(var1)) : Optional.empty();
   }

   public Optional<Holder<T>> getHolder(ResourceKey<T> var1) {
      return Optional.ofNullable((Holder)this.byKey.get(var1));
   }

   public Holder<T> getOrCreateHolderOrThrow(ResourceKey<T> var1) {
      return (Holder)this.byKey.computeIfAbsent(var1, (var1x) -> {
         if (this.customHolderProvider != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite(var1x);
            return Holder.Reference.createStandAlone(this, var1x);
         }
      });
   }

   public DataResult<Holder<T>> getOrCreateHolder(ResourceKey<T> var1) {
      Holder.Reference var2 = (Holder.Reference)this.byKey.get(var1);
      if (var2 == null) {
         if (this.customHolderProvider != null) {
            return DataResult.error("This registry can't create new holders without value (requested key: " + var1 + ")");
         }

         if (this.frozen) {
            return DataResult.error("Registry is already frozen (requested key: " + var1 + ")");
         }

         var2 = Holder.Reference.createStandAlone(this, var1);
         this.byKey.put(var1, var2);
      }

      return DataResult.success(var2);
   }

   public int size() {
      return this.byKey.size();
   }

   public Lifecycle lifecycle(T var1) {
      return (Lifecycle)this.lifecycles.get(var1);
   }

   public Lifecycle elementsLifecycle() {
      return this.elementsLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.transform(this.holdersInOrder().iterator(), Holder::value);
   }

   @Nullable
   public T get(@Nullable ResourceLocation var1) {
      Holder.Reference var2 = (Holder.Reference)this.byLocation.get(var1);
      return getValueFromNullable(var2);
   }

   @Nullable
   private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> var0) {
      return var0 != null ? var0.value() : null;
   }

   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   public Set<ResourceKey<T>> registryKeySet() {
      return Collections.unmodifiableSet(this.byKey.keySet());
   }

   public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Holder::value).entrySet());
   }

   public Stream<Holder.Reference<T>> holders() {
      return this.holdersInOrder().stream();
   }

   public boolean isKnownTagName(TagKey<T> var1) {
      return this.tags.containsKey(var1);
   }

   public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
      return this.tags.entrySet().stream().map((var0) -> {
         return Pair.of((TagKey)var0.getKey(), (HolderSet.Named)var0.getValue());
      });
   }

   public HolderSet.Named<T> getOrCreateTag(TagKey<T> var1) {
      HolderSet.Named var2 = (HolderSet.Named)this.tags.get(var1);
      if (var2 == null) {
         var2 = this.createTag(var1);
         IdentityHashMap var3 = new IdentityHashMap(this.tags);
         var3.put(var1, var2);
         this.tags = var3;
      }

      return var2;
   }

   private HolderSet.Named<T> createTag(TagKey<T> var1) {
      return new HolderSet.Named(this, var1);
   }

   public Stream<TagKey<T>> getTagNames() {
      return this.tags.keySet().stream();
   }

   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   public Optional<Holder<T>> getRandom(RandomSource var1) {
      return Util.getRandomSafe(this.holdersInOrder(), var1).map(Holder::hackyErase);
   }

   public boolean containsKey(ResourceLocation var1) {
      return this.byLocation.containsKey(var1);
   }

   public boolean containsKey(ResourceKey<T> var1) {
      return this.byKey.containsKey(var1);
   }

   public Registry<T> freeze() {
      this.frozen = true;
      List var1 = this.byKey.entrySet().stream().filter((var0) -> {
         return !((Holder.Reference)var0.getValue()).isBound();
      }).map((var0) -> {
         return ((ResourceKey)var0.getKey()).location();
      }).sorted().toList();
      if (!var1.isEmpty()) {
         ResourceKey var10002 = this.key();
         throw new IllegalStateException("Unbound values in registry " + var10002 + ": " + var1);
      } else {
         if (this.intrusiveHolderCache != null) {
            List var2 = this.intrusiveHolderCache.values().stream().filter((var0) -> {
               return !var0.isBound();
            }).toList();
            if (!var2.isEmpty()) {
               throw new IllegalStateException("Some intrusive holders were not added to registry: " + var2);
            }

            this.intrusiveHolderCache = null;
         }

         return this;
      }
   }

   public Holder.Reference<T> createIntrusiveHolder(T var1) {
      if (this.customHolderProvider == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else if (!this.frozen && this.intrusiveHolderCache != null) {
         return (Holder.Reference)this.intrusiveHolderCache.computeIfAbsent(var1, (var1x) -> {
            return Holder.Reference.createIntrusive(this, var1x);
         });
      } else {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   public Optional<HolderSet.Named<T>> getTag(TagKey<T> var1) {
      return Optional.ofNullable((HolderSet.Named)this.tags.get(var1));
   }

   public void bindTags(Map<TagKey<T>, List<Holder<T>>> var1) {
      IdentityHashMap var2 = new IdentityHashMap();
      this.byKey.values().forEach((var1x) -> {
         var2.put(var1x, new ArrayList());
      });
      var1.forEach((var2x, var3x) -> {
         Iterator var4 = var3x.iterator();

         while(var4.hasNext()) {
            Holder var5 = (Holder)var4.next();
            if (!var5.isValidInRegistry(this)) {
               throw new IllegalStateException("Can't create named set " + var2x + " containing value " + var5 + " from outside registry " + this);
            }

            if (!(var5 instanceof Holder.Reference)) {
               throw new IllegalStateException("Found direct holder " + var5 + " value in tag " + var2x);
            }

            Holder.Reference var6 = (Holder.Reference)var5;
            ((List)var2.get(var6)).add(var2x);
         }

      });
      Sets.SetView var3 = Sets.difference(this.tags.keySet(), var1.keySet());
      if (!var3.isEmpty()) {
         LOGGER.warn("Not all defined tags for registry {} are present in data pack: {}", this.key(), var3.stream().map((var0) -> {
            return var0.location().toString();
         }).sorted().collect(Collectors.joining(", ")));
      }

      IdentityHashMap var4 = new IdentityHashMap(this.tags);
      var1.forEach((var2x, var3x) -> {
         ((HolderSet.Named)var4.computeIfAbsent(var2x, this::createTag)).bind(var3x);
      });
      var2.forEach(Holder.Reference::bindTags);
      this.tags = var4;
   }

   public void resetTags() {
      this.tags.values().forEach((var0) -> {
         var0.bind(List.of());
      });
      this.byKey.values().forEach((var0) -> {
         var0.bindTags(Set.of());
      });
   }
}
