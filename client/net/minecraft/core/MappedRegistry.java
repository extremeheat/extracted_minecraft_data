package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;
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
import java.util.function.BiConsumer;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.RandomSource;

public class MappedRegistry<T> implements WritableRegistry<T> {
   private final ResourceKey<? extends Registry<T>> key;
   private final ObjectList<Holder.Reference<T>> byId = new ObjectArrayList(256);
   private final Reference2IntMap<T> toId = Util.make(new Reference2IntOpenHashMap(), var0 -> var0.defaultReturnValue(-1));
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation = new HashMap<>();
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey = new HashMap<>();
   private final Map<T, Holder.Reference<T>> byValue = new IdentityHashMap<>();
   private final Map<ResourceKey<T>, RegistrationInfo> registrationInfos = new IdentityHashMap<>();
   private Lifecycle registryLifecycle;
   private final Map<TagKey<T>, HolderSet.Named<T>> frozenTags = new IdentityHashMap<>();
   MappedRegistry.TagSet<T> allTags = MappedRegistry.TagSet.unbound();
   private boolean frozen;
   @Nullable
   private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

   @Override
   public Stream<HolderSet.Named<T>> listTags() {
      return this.getTags();
   }

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
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + var1 + "' to registry"));
      } else if (this.byValue.containsKey(var2)) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + var2 + "' to registry"));
      } else {
         Holder.Reference var4;
         if (this.unregisteredIntrusiveHolders != null) {
            var4 = this.unregisteredIntrusiveHolders.remove(var2);
            if (var4 == null) {
               throw new AssertionError("Missing intrusive holder for " + var1 + ":" + var2);
            }

            var4.bindKey(var1);
         } else {
            var4 = this.byKey.computeIfAbsent(var1, var1x -> Holder.Reference.createStandAlone(this, (ResourceKey<T>)var1x));
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
   public T getValue(@Nullable ResourceKey<T> var1) {
      return getValueFromNullable(this.byKey.get(var1));
   }

   @Nullable
   @Override
   public T byId(int var1) {
      return (T)(var1 >= 0 && var1 < this.byId.size() ? ((Holder.Reference)this.byId.get(var1)).value() : null);
   }

   @Override
   public Optional<Holder.Reference<T>> get(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? Optional.ofNullable((Holder.Reference<T>)this.byId.get(var1)) : Optional.empty();
   }

   @Override
   public Optional<Holder.Reference<T>> get(ResourceLocation var1) {
      return Optional.ofNullable(this.byLocation.get(var1));
   }

   @Override
   public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
      return Optional.ofNullable(this.byKey.get(var1));
   }

   @Override
   public Optional<Holder.Reference<T>> getAny() {
      return this.byId.isEmpty() ? Optional.empty() : Optional.of((Holder.Reference<T>)this.byId.getFirst());
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
            return Holder.Reference.createStandAlone(this, (ResourceKey<T>)var1x);
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
   public T getValue(@Nullable ResourceLocation var1) {
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
   public Stream<Holder.Reference<T>> listElements() {
      return this.byId.stream();
   }

   @Override
   public Stream<HolderSet.Named<T>> getTags() {
      return this.allTags.getTags();
   }

   HolderSet.Named<T> getOrCreateTagForRegistration(TagKey<T> var1) {
      return this.frozenTags.computeIfAbsent(var1, this::createTag);
   }

   private HolderSet.Named<T> createTag(TagKey<T> var1) {
      return new HolderSet.Named<>(this, (TagKey<T>)var1);
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

            if (this.allTags.isBound()) {
               throw new IllegalStateException("Tags already present before freezing");
            } else {
               List var2 = this.frozenTags
                  .entrySet()
                  .stream()
                  .filter(var0 -> !var0.getValue().isBound())
                  .map(var0 -> var0.getKey().location())
                  .sorted()
                  .toList();
               if (!var2.isEmpty()) {
                  throw new IllegalStateException("Unbound tags in registry " + this.key() + ": " + var2);
               } else {
                  this.allTags = MappedRegistry.TagSet.fromMap(this.frozenTags);
                  this.refreshTagsInHolders();
                  return this;
               }
            }
         }
      }
   }

   @Override
   public Holder.Reference<T> createIntrusiveHolder(T var1) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return this.unregisteredIntrusiveHolders.computeIfAbsent((T)var1, var1x -> Holder.Reference.createIntrusive(this, (T)var1x));
      }
   }

   @Override
   public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
      return this.allTags.get(var1);
   }

   private Holder.Reference<T> validateAndUnwrapTagElement(TagKey<T> var1, Holder<T> var2) {
      if (!var2.canSerializeIn(this)) {
         throw new IllegalStateException("Can't create named set " + var1 + " containing value " + var2 + " from outside registry " + this);
      } else if (var2 instanceof Holder.Reference) {
         return (Holder.Reference<T>)var2;
      } else {
         throw new IllegalStateException("Found direct holder " + var2 + " value in tag " + var1);
      }
   }

   @Override
   public void bindTag(TagKey<T> var1, List<Holder<T>> var2) {
      this.validateWrite();
      this.getOrCreateTagForRegistration(var1).bind(var2);
   }

   void refreshTagsInHolders() {
      IdentityHashMap var1 = new IdentityHashMap();
      this.byKey.values().forEach(var1x -> var1.put(var1x, new ArrayList()));
      this.allTags.forEach((var2, var3) -> {
         for (Holder var5 : var3) {
            Holder.Reference var6 = this.validateAndUnwrapTagElement((TagKey<T>)var2, var5);
            ((List)var1.get(var6)).add(var2);
         }
      });
      var1.forEach(Holder.Reference::bindTags);
   }

   public void bindAllTagsToEmpty() {
      this.validateWrite();
      this.frozenTags.values().forEach(var0 -> var0.bind(List.of()));
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
            return MappedRegistry.this.getOrCreateTagForRegistration(var1);
         }
      };
   }

   @Override
   public Registry.PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> var1) {
      if (!this.frozen) {
         throw new IllegalStateException("Invalid method used for tag loading");
      } else {
         Builder var2 = ImmutableMap.builder();
         final HashMap var3 = new HashMap();
         var1.tags().forEach((var3x, var4x) -> {
            HolderSet.Named var5x = this.frozenTags.get(var3x);
            if (var5x == null) {
               var5x = this.createTag((TagKey<T>)var3x);
            }

            var2.put(var3x, var5x);
            var3.put(var3x, List.copyOf(var4x));
         });
         final ImmutableMap var4 = var2.build();
         final HolderLookup.RegistryLookup.Delegate var5 = new HolderLookup.RegistryLookup.Delegate<T>() {
            @Override
            public HolderLookup.RegistryLookup<T> parent() {
               return MappedRegistry.this;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               return Optional.ofNullable((HolderSet.Named<T>)var4.get(var1));
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
               return var4.values().stream();
            }
         };
         return new Registry.PendingTags<T>() {
            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
               return MappedRegistry.this.key();
            }

            @Override
            public HolderLookup.RegistryLookup<T> lookup() {
               return var5;
            }

            @Override
            public void apply() {
               var4.forEach((var1, var2) -> {
                  List var3x = var3.getOrDefault(var1, List.of());
                  var2.bind(var3x);
               });
               MappedRegistry.this.allTags = MappedRegistry.TagSet.fromMap(var4);
               MappedRegistry.this.refreshTagsInHolders();
            }
         };
      }
   }

   interface TagSet<T> {
      static <T> MappedRegistry.TagSet<T> unbound() {
         return new MappedRegistry.TagSet<T>() {
            @Override
            public boolean isBound() {
               return false;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               throw new IllegalStateException("Tags not bound, trying to access " + var1);
            }

            @Override
            public void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> var1) {
               throw new IllegalStateException("Tags not bound");
            }

            @Override
            public Stream<HolderSet.Named<T>> getTags() {
               throw new IllegalStateException("Tags not bound");
            }
         };
      }

      static <T> MappedRegistry.TagSet<T> fromMap(final Map<TagKey<T>, HolderSet.Named<T>> var0) {
         return new MappedRegistry.TagSet<T>() {
            @Override
            public boolean isBound() {
               return true;
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               return Optional.ofNullable((HolderSet.Named<T>)var0.get(var1));
            }

            @Override
            public void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> var1) {
               var0.forEach(var1);
            }

            @Override
            public Stream<HolderSet.Named<T>> getTags() {
               return var0.values().stream();
            }
         };
      }

      boolean isBound();

      Optional<HolderSet.Named<T>> get(TagKey<T> var1);

      void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> var1);

      Stream<HolderSet.Named<T>> getTags();
   }
}
