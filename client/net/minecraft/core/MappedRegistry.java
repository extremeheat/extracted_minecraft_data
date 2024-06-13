package net.minecraft.core;

import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Lifecycle;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import org.slf4j.Logger;

public class MappedRegistry<T> implements WritableRegistry<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   final ResourceKey<? extends Registry<T>> key;
   private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList(256);
   private final Reference2IntMap<T> toId = Util.make(new Reference2IntOpenHashMap(), var0 -> var0.defaultReturnValue(-1));
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap<>();
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap<>();
   private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap<>();
   private final Map<ResourceKey<T>, RegistrationInfo> registrationInfos = new IdentityHashMap<>();
   private Lifecycle registryLifecycle;
   private volatile Map<TagKey<T>, HolderSet.Named<T>> tags = new IdentityHashMap<>();
   private boolean frozen;
   @Nullable
   private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;
   private final HolderLookup.RegistryLookup<T> lookup = new HolderLookup.RegistryLookup<T>() {
      @Override
      public ResourceKey<? extends Registry<? extends T>> key() {
         return MappedRegistry.this.key;
      }

      @Override
      public Lifecycle registryLifecycle() {
         return MappedRegistry.this.registryLifecycle();
      }

      @Override
      public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
         return MappedRegistry.this.getHolder(var1);
      }

      @Override
      public Stream<Holder.Reference<T>> listElements() {
         return MappedRegistry.this.holders();
      }

      @Override
      public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
         return MappedRegistry.this.getTag(var1);
      }

      @Override
      public Stream<HolderSet.Named<T>> listTags() {
         return MappedRegistry.this.getTags().map(Pair::getSecond);
      }
   };
   private final Object tagAdditionLock = new Object();

   public MappedRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2) {
      this(var1, var2, false);
   }

   public MappedRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, boolean var3) {
      super();
      this.key = var1;
      this.registryLifecycle = var2;
      if (var3) {
         this.unregisteredIntrusiveHolders = new IdentityHashMap<>();
      }
   }

   @Override
   public ResourceKey<? extends Registry<T>> key() {
      return this.key;
   }

   @Override
   public String toString() {
      return "Registry[" + this.key + " (" + this.registryLifecycle + ")]";
   }

   private void validateWrite() {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   private void validateWrite(ResourceKey<T> var1) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + var1 + ")");
      }
   }

   @Override
   public Holder.Reference<T> register(ResourceKey<T> var1, T var2, RegistrationInfo var3) {
      this.validateWrite(var1);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      if (this.byLocation.containsKey(var1.location())) {
         Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + var1 + "' to registry"));
      }

      if (this.byValue.containsKey(var2)) {
         Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + var2 + "' to registry"));
      }

      Holder.Reference var4;
      if (this.unregisteredIntrusiveHolders != null) {
         var4 = this.unregisteredIntrusiveHolders.remove(var2);
         if (var4 == null) {
            throw new AssertionError("Missing intrusive holder for " + var1 + ":" + var2);
         }

         var4.bindKey(var1);
      } else {
         var4 = this.byKey.computeIfAbsent(var1, var1x -> Holder.Reference.createStandAlone(this.holderOwner(), (ResourceKey<T>)var1x));
      }

      this.byKey.put(var1, var4);
      this.byLocation.put(var1.location(), var4);
      this.byValue.put((T)var2, var4);
      int var5 = this.byId.size();
      this.byId.add(var4);
      this.toId.put(var2, var5);
      this.registrationInfos.put(var1, var3);
      this.registryLifecycle = this.registryLifecycle.add(var3.lifecycle());
      return var4;
   }

   @Nullable
   @Override
   public ResourceLocation getKey(T var1) {
      Holder.Reference var2 = this.byValue.get(var1);
      return var2 != null ? var2.key().location() : null;
   }

   @Override
   public Optional<ResourceKey<T>> getResourceKey(T var1) {
      return Optional.ofNullable(this.byValue.get(var1)).map(Holder.Reference::key);
   }

   @Override
   public int getId(@Nullable T var1) {
      return this.toId.getInt(var1);
   }

   @Nullable
   @Override
   public T get(@Nullable ResourceKey<T> var1) {
      return getValueFromNullable(this.byKey.get(var1));
   }

   @Nullable
   @Override
   public T byId(int var1) {
      return (T)(var1 >= 0 && var1 < this.byId.size() ? ((Holder.Reference)this.byId.get(var1)).value() : null);
   }

   @Override
   public Optional<Holder.Reference<T>> getHolder(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? Optional.ofNullable((Holder.Reference<T>)this.byId.get(var1)) : Optional.empty();
   }

   @Override
   public Optional<Holder.Reference<T>> getHolder(ResourceLocation var1) {
      return Optional.ofNullable(this.byLocation.get(var1));
   }

   @Override
   public Optional<Holder.Reference<T>> getHolder(ResourceKey<T> var1) {
      return Optional.ofNullable(this.byKey.get(var1));
   }

   @Override
   public Holder<T> wrapAsHolder(T var1) {
      Holder.Reference var2 = this.byValue.get(var1);
      return (Holder<T>)(var2 != null ? var2 : Holder.direct((T)var1));
   }

   Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> var1) {
      return this.byKey.computeIfAbsent(var1, var1x -> {
         if (this.unregisteredIntrusiveHolders != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite((ResourceKey<T>)var1x);
            return Holder.Reference.createStandAlone(this.holderOwner(), (ResourceKey<T>)var1x);
         }
      });
   }

   @Override
   public int size() {
      return this.byKey.size();
   }

   @Override
   public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> var1) {
      return Optional.ofNullable(this.registrationInfos.get(var1));
   }

   @Override
   public Lifecycle registryLifecycle() {
      return this.registryLifecycle;
   }

   @Override
   public Iterator<T> iterator() {
      return Iterators.transform(this.byId.iterator(), Holder::value);
   }

   @Nullable
   @Override
   public T get(@Nullable ResourceLocation var1) {
      Holder.Reference var2 = this.byLocation.get(var1);
      return getValueFromNullable(var2);
   }

   @Nullable
   private static <T> T getValueFromNullable(@Nullable Holder.Reference<T> var0) {
      return (T)(var0 != null ? var0.value() : null);
   }

   @Override
   public Set<ResourceLocation> keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   @Override
   public Set<ResourceKey<T>> registryKeySet() {
      return Collections.unmodifiableSet(this.byKey.keySet());
   }

   @Override
   public Set<Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableSet(Maps.transformValues(this.byKey, Holder::value).entrySet());
   }

   @Override
   public Stream<Holder.Reference<T>> holders() {
      return this.byId.stream();
   }

   @Override
   public Stream<Pair<TagKey<T>, HolderSet.Named<T>>> getTags() {
      return this.tags.entrySet().stream().map(var0 -> Pair.of(var0.getKey(), var0.getValue()));
   }

   @Override
   public HolderSet.Named<T> getOrCreateTag(TagKey<T> var1) {
      HolderSet.Named var2 = this.tags.get(var1);
      if (var2 != null) {
         return var2;
      } else {
         synchronized (this.tagAdditionLock) {
            var2 = this.tags.get(var1);
            if (var2 != null) {
               return var2;
            } else {
               var2 = this.createTag(var1);
               IdentityHashMap var4 = new IdentityHashMap<>(this.tags);
               var4.put(var1, var2);
               this.tags = var4;
               return var2;
            }
         }
      }
   }

   private HolderSet.Named<T> createTag(TagKey<T> var1) {
      return new HolderSet.Named<>(this.holderOwner(), var1);
   }

   @Override
   public Stream<TagKey<T>> getTagNames() {
      return this.tags.keySet().stream();
   }

   @Override
   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   @Override
   public Optional<Holder.Reference<T>> getRandom(RandomSource var1) {
      return Util.getRandomSafe(this.byId, var1);
   }

   @Override
   public boolean containsKey(ResourceLocation var1) {
      return this.byLocation.containsKey(var1);
   }

   @Override
   public boolean containsKey(ResourceKey<T> var1) {
      return this.byKey.containsKey(var1);
   }

   @Override
   public Registry<T> freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         this.byValue.forEach((var0, var1x) -> var1x.bindValue((T)var0));
         List var1 = this.byKey.entrySet().stream().filter(var0 -> !var0.getValue().isBound()).map(var0 -> var0.getKey().location()).sorted().toList();
         if (!var1.isEmpty()) {
            throw new IllegalStateException("Unbound values in registry " + this.key() + ": " + var1);
         } else {
            if (this.unregisteredIntrusiveHolders != null) {
               if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                  throw new IllegalStateException("Some intrusive holders were not registered: " + this.unregisteredIntrusiveHolders.values());
               }

               this.unregisteredIntrusiveHolders = null;
            }

            return this;
         }
      }
   }

   @Override
   public Holder.Reference<T> createIntrusiveHolder(T var1) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return this.unregisteredIntrusiveHolders.computeIfAbsent((T)var1, var1x -> Holder.Reference.createIntrusive(this.asLookup(), (T)var1x));
      }
   }

   @Override
   public Optional<HolderSet.Named<T>> getTag(TagKey<T> var1) {
      return Optional.ofNullable(this.tags.get(var1));
   }

   @Override
   public void bindTags(Map<TagKey<T>, List<Holder<T>>> var1) {
      IdentityHashMap var2 = new IdentityHashMap();
      this.byKey.values().forEach(var1x -> var2.put(var1x, new ArrayList()));
      var1.forEach((var2x, var3x) -> {
         for (Holder var5x : var3x) {
            if (!var5x.canSerializeIn(this.asLookup())) {
               throw new IllegalStateException("Can't create named set " + var2x + " containing value " + var5x + " from outside registry " + this);
            }

            if (!(var5x instanceof Holder.Reference var6)) {
               throw new IllegalStateException("Found direct holder " + var5x + " value in tag " + var2x);
            }

            ((List)var2.get(var6)).add(var2x);
         }
      });
      SetView var3 = Sets.difference(this.tags.keySet(), var1.keySet());
      if (!var3.isEmpty()) {
         LOGGER.warn(
            "Not all defined tags for registry {} are present in data pack: {}",
            this.key(),
            var3.stream().map(var0 -> var0.location().toString()).sorted().collect(Collectors.joining(", "))
         );
      }

      synchronized (this.tagAdditionLock) {
         IdentityHashMap var5 = new IdentityHashMap<>(this.tags);
         var1.forEach((var2x, var3x) -> var5.computeIfAbsent(var2x, this::createTag).bind((List<Holder<T>>)var3x));
         var2.forEach(Holder.Reference::bindTags);
         this.tags = var5;
      }
   }

   @Override
   public void resetTags() {
      this.tags.values().forEach(var0 -> var0.bind(List.of()));
      this.byKey.values().forEach(var0 -> var0.bindTags(Set.of()));
   }

   @Override
   public HolderGetter<T> createRegistrationLookup() {
      this.validateWrite();
      return new HolderGetter<T>() {
         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
            return Optional.of(this.getOrThrow(var1));
         }

         @Override
         public Holder.Reference<T> getOrThrow(ResourceKey<T> var1) {
            return MappedRegistry.this.getOrCreateHolderOrThrow(var1);
         }

         @Override
         public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
            return Optional.of(this.getOrThrow(var1));
         }

         @Override
         public HolderSet.Named<T> getOrThrow(TagKey<T> var1) {
            return MappedRegistry.this.getOrCreateTag(var1);
         }
      };
   }

   @Override
   public HolderOwner<T> holderOwner() {
      return this.lookup;
   }

   @Override
   public HolderLookup.RegistryLookup<T> asLookup() {
      return this.lookup;
   }
}