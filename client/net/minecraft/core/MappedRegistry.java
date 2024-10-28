package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;
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
   private final ObjectList<Holder.Reference<T>> byId;
   private final Reference2IntMap<T> toId;
   private final Map<ResourceLocation, Holder.Reference<T>> byLocation;
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey;
   private final Map<T, Holder.Reference<T>> byValue;
   private final Map<ResourceKey<T>, RegistrationInfo> registrationInfos;
   private Lifecycle registryLifecycle;
   private final Map<TagKey<T>, HolderSet.Named<T>> frozenTags;
   TagSet<T> allTags;
   private boolean frozen;
   @Nullable
   private Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

   public Stream<HolderSet.Named<T>> listTags() {
      return this.getTags();
   }

   public MappedRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2) {
      this(var1, var2, false);
   }

   public MappedRegistry(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, boolean var3) {
      super();
      this.byId = new ObjectArrayList(256);
      this.toId = (Reference2IntMap)Util.make(new Reference2IntOpenHashMap(), (var0) -> {
         var0.defaultReturnValue(-1);
      });
      this.byLocation = new HashMap();
      this.byKey = new HashMap();
      this.byValue = new IdentityHashMap();
      this.registrationInfos = new IdentityHashMap();
      this.frozenTags = new IdentityHashMap();
      this.allTags = MappedRegistry.TagSet.unbound();
      this.key = var1;
      this.registryLifecycle = var2;
      if (var3) {
         this.unregisteredIntrusiveHolders = new IdentityHashMap();
      }

   }

   public ResourceKey<? extends Registry<T>> key() {
      return this.key;
   }

   public String toString() {
      String var10000 = String.valueOf(this.key);
      return "Registry[" + var10000 + " (" + String.valueOf(this.registryLifecycle) + ")]";
   }

   private void validateWrite() {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen");
      }
   }

   private void validateWrite(ResourceKey<T> var1) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + String.valueOf(var1) + ")");
      }
   }

   public Holder.Reference<T> register(ResourceKey<T> var1, T var2, RegistrationInfo var3) {
      this.validateWrite(var1);
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      if (this.byLocation.containsKey(var1.location())) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + String.valueOf(var1) + "' to registry"));
      } else if (this.byValue.containsKey(var2)) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + String.valueOf(var2) + "' to registry"));
      } else {
         Holder.Reference var4;
         if (this.unregisteredIntrusiveHolders != null) {
            var4 = (Holder.Reference)this.unregisteredIntrusiveHolders.remove(var2);
            if (var4 == null) {
               String var10002 = String.valueOf(var1);
               throw new AssertionError("Missing intrusive holder for " + var10002 + ":" + String.valueOf(var2));
            }

            var4.bindKey(var1);
         } else {
            var4 = (Holder.Reference)this.byKey.computeIfAbsent(var1, (var1x) -> {
               return Holder.Reference.createStandAlone(this, var1x);
            });
         }

         this.byKey.put(var1, var4);
         this.byLocation.put(var1.location(), var4);
         this.byValue.put(var2, var4);
         int var5 = this.byId.size();
         this.byId.add(var4);
         this.toId.put(var2, var5);
         this.registrationInfos.put(var1, var3);
         this.registryLifecycle = this.registryLifecycle.add(var3.lifecycle());
         return var4;
      }
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
   public T getValue(@Nullable ResourceKey<T> var1) {
      return getValueFromNullable((Holder.Reference)this.byKey.get(var1));
   }

   @Nullable
   public T byId(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? ((Holder.Reference)this.byId.get(var1)).value() : null;
   }

   public Optional<Holder.Reference<T>> get(int var1) {
      return var1 >= 0 && var1 < this.byId.size() ? Optional.ofNullable((Holder.Reference)this.byId.get(var1)) : Optional.empty();
   }

   public Optional<Holder.Reference<T>> get(ResourceLocation var1) {
      return Optional.ofNullable((Holder.Reference)this.byLocation.get(var1));
   }

   public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
      return Optional.ofNullable((Holder.Reference)this.byKey.get(var1));
   }

   public Optional<Holder.Reference<T>> getAny() {
      return this.byId.isEmpty() ? Optional.empty() : Optional.of((Holder.Reference)this.byId.getFirst());
   }

   public Holder<T> wrapAsHolder(T var1) {
      Holder.Reference var2 = (Holder.Reference)this.byValue.get(var1);
      return (Holder)(var2 != null ? var2 : Holder.direct(var1));
   }

   Holder.Reference<T> getOrCreateHolderOrThrow(ResourceKey<T> var1) {
      return (Holder.Reference)this.byKey.computeIfAbsent(var1, (var1x) -> {
         if (this.unregisteredIntrusiveHolders != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite(var1x);
            return Holder.Reference.createStandAlone(this, var1x);
         }
      });
   }

   public int size() {
      return this.byKey.size();
   }

   public Optional<RegistrationInfo> registrationInfo(ResourceKey<T> var1) {
      return Optional.ofNullable((RegistrationInfo)this.registrationInfos.get(var1));
   }

   public Lifecycle registryLifecycle() {
      return this.registryLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.transform(this.byId.iterator(), Holder::value);
   }

   @Nullable
   public T getValue(@Nullable ResourceLocation var1) {
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

   public Stream<Holder.Reference<T>> listElements() {
      return this.byId.stream();
   }

   public Stream<HolderSet.Named<T>> getTags() {
      return this.allTags.getTags();
   }

   HolderSet.Named<T> getOrCreateTagForRegistration(TagKey<T> var1) {
      return (HolderSet.Named)this.frozenTags.computeIfAbsent(var1, this::createTag);
   }

   private HolderSet.Named<T> createTag(TagKey<T> var1) {
      return new HolderSet.Named(this, var1);
   }

   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   public Optional<Holder.Reference<T>> getRandom(RandomSource var1) {
      return Util.getRandomSafe(this.byId, var1);
   }

   public boolean containsKey(ResourceLocation var1) {
      return this.byLocation.containsKey(var1);
   }

   public boolean containsKey(ResourceKey<T> var1) {
      return this.byKey.containsKey(var1);
   }

   public Registry<T> freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         this.byValue.forEach((var0, var1x) -> {
            var1x.bindValue(var0);
         });
         List var1 = this.byKey.entrySet().stream().filter((var0) -> {
            return !((Holder.Reference)var0.getValue()).isBound();
         }).map((var0) -> {
            return ((ResourceKey)var0.getKey()).location();
         }).sorted().toList();
         String var10002;
         if (!var1.isEmpty()) {
            var10002 = String.valueOf(this.key());
            throw new IllegalStateException("Unbound values in registry " + var10002 + ": " + String.valueOf(var1));
         } else {
            if (this.unregisteredIntrusiveHolders != null) {
               if (!this.unregisteredIntrusiveHolders.isEmpty()) {
                  throw new IllegalStateException("Some intrusive holders were not registered: " + String.valueOf(this.unregisteredIntrusiveHolders.values()));
               }

               this.unregisteredIntrusiveHolders = null;
            }

            if (this.allTags.isBound()) {
               throw new IllegalStateException("Tags already present before freezing");
            } else {
               List var2 = this.frozenTags.entrySet().stream().filter((var0) -> {
                  return !((HolderSet.Named)var0.getValue()).isBound();
               }).map((var0) -> {
                  return ((TagKey)var0.getKey()).location();
               }).sorted().toList();
               if (!var2.isEmpty()) {
                  var10002 = String.valueOf(this.key());
                  throw new IllegalStateException("Unbound tags in registry " + var10002 + ": " + String.valueOf(var2));
               } else {
                  this.allTags = MappedRegistry.TagSet.fromMap(this.frozenTags);
                  this.refreshTagsInHolders();
                  return this;
               }
            }
         }
      }
   }

   public Holder.Reference<T> createIntrusiveHolder(T var1) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return (Holder.Reference)this.unregisteredIntrusiveHolders.computeIfAbsent(var1, (var1x) -> {
            return Holder.Reference.createIntrusive(this, var1x);
         });
      }
   }

   public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
      return this.allTags.get(var1);
   }

   private Holder.Reference<T> validateAndUnwrapTagElement(TagKey<T> var1, Holder<T> var2) {
      String var10002;
      if (!var2.canSerializeIn(this)) {
         var10002 = String.valueOf(var1);
         throw new IllegalStateException("Can't create named set " + var10002 + " containing value " + String.valueOf(var2) + " from outside registry " + String.valueOf(this));
      } else if (var2 instanceof Holder.Reference) {
         Holder.Reference var3 = (Holder.Reference)var2;
         return var3;
      } else {
         var10002 = String.valueOf(var2);
         throw new IllegalStateException("Found direct holder " + var10002 + " value in tag " + String.valueOf(var1));
      }
   }

   public void bindTag(TagKey<T> var1, List<Holder<T>> var2) {
      this.validateWrite();
      this.getOrCreateTagForRegistration(var1).bind(var2);
   }

   void refreshTagsInHolders() {
      IdentityHashMap var1 = new IdentityHashMap();
      this.byKey.values().forEach((var1x) -> {
         var1.put(var1x, new ArrayList());
      });
      this.allTags.forEach((var2, var3) -> {
         Iterator var4 = var3.iterator();

         while(var4.hasNext()) {
            Holder var5 = (Holder)var4.next();
            Holder.Reference var6 = this.validateAndUnwrapTagElement(var2, var5);
            ((List)var1.get(var6)).add(var2);
         }

      });
      var1.forEach(Holder.Reference::bindTags);
   }

   public void bindAllTagsToEmpty() {
      this.validateWrite();
      this.frozenTags.values().forEach((var0) -> {
         var0.bind(List.of());
      });
   }

   public HolderGetter<T> createRegistrationLookup() {
      this.validateWrite();
      return new HolderGetter<T>() {
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
            return Optional.of(this.getOrThrow(var1));
         }

         public Holder.Reference<T> getOrThrow(ResourceKey<T> var1) {
            return MappedRegistry.this.getOrCreateHolderOrThrow(var1);
         }

         public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
            return Optional.of(this.getOrThrow(var1));
         }

         public HolderSet.Named<T> getOrThrow(TagKey<T> var1) {
            return MappedRegistry.this.getOrCreateTagForRegistration(var1);
         }
      };
   }

   public Registry.PendingTags<T> prepareTagReload(TagLoader.LoadResult<T> var1) {
      if (!this.frozen) {
         throw new IllegalStateException("Invalid method used for tag loading");
      } else {
         ImmutableMap.Builder var2 = ImmutableMap.builder();
         final HashMap var3 = new HashMap();
         var1.tags().forEach((var3x, var4x) -> {
            HolderSet.Named var5 = (HolderSet.Named)this.frozenTags.get(var3x);
            if (var5 == null) {
               var5 = this.createTag(var3x);
            }

            var2.put(var3x, var5);
            var3.put(var3x, List.copyOf(var4x));
         });
         final ImmutableMap var4 = var2.build();
         final HolderLookup.RegistryLookup.Delegate var5 = new HolderLookup.RegistryLookup.Delegate<T>() {
            public HolderLookup.RegistryLookup<T> parent() {
               return MappedRegistry.this;
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               return Optional.ofNullable((HolderSet.Named)var4.get(var1));
            }

            public Stream<HolderSet.Named<T>> listTags() {
               return var4.values().stream();
            }
         };
         return new Registry.PendingTags<T>() {
            public ResourceKey<? extends Registry<? extends T>> key() {
               return MappedRegistry.this.key();
            }

            public int size() {
               return var3.size();
            }

            public HolderLookup.RegistryLookup<T> lookup() {
               return var5;
            }

            public void apply() {
               var4.forEach((var1, var2) -> {
                  List var3x = (List)var3.getOrDefault(var1, List.of());
                  var2.bind(var3x);
               });
               MappedRegistry.this.allTags = MappedRegistry.TagSet.fromMap(var4);
               MappedRegistry.this.refreshTagsInHolders();
            }
         };
      }
   }

   private interface TagSet<T> {
      static <T> TagSet<T> unbound() {
         return new TagSet<T>() {
            public boolean isBound() {
               return false;
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               throw new IllegalStateException("Tags not bound, trying to access " + String.valueOf(var1));
            }

            public void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> var1) {
               throw new IllegalStateException("Tags not bound");
            }

            public Stream<HolderSet.Named<T>> getTags() {
               throw new IllegalStateException("Tags not bound");
            }
         };
      }

      static <T> TagSet<T> fromMap(final Map<TagKey<T>, HolderSet.Named<T>> var0) {
         return new TagSet<T>() {
            public boolean isBound() {
               return true;
            }

            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               return Optional.ofNullable((HolderSet.Named)var0.get(var1));
            }

            public void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> var1) {
               var0.forEach(var1);
            }

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
