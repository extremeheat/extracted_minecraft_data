package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterators;
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
import net.minecraft.core.component.DataComponentLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagLoader;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

public class MappedRegistry<T> implements WritableRegistry<T> {
   private final ResourceKey<? extends Registry<T>> key;
   private final ObjectList<Holder.Reference<T>> byId;
   private final Reference2IntMap<T> toId;
   private final Map<Identifier, Holder.Reference<T>> byLocation;
   private final Map<ResourceKey<T>, Holder.Reference<T>> byKey;
   private final Map<T, Holder.Reference<T>> byValue;
   private final Map<ResourceKey<T>, RegistrationInfo> registrationInfos;
   private Lifecycle registryLifecycle;
   private final Map<TagKey<T>, HolderSet.Named<T>> frozenTags;
   private TagSet<T> allTags;
   private @Nullable DataComponentLookup<T> componentLookup;
   private boolean frozen;
   private @Nullable Map<T, Holder.Reference<T>> unregisteredIntrusiveHolders;

   public Stream<HolderSet.Named<T>> listTags() {
      return this.getTags();
   }

   public MappedRegistry(final ResourceKey<? extends Registry<T>> key, final Lifecycle lifecycle) {
      this(key, lifecycle, false);
   }

   public MappedRegistry(final ResourceKey<? extends Registry<T>> key, final Lifecycle initialLifecycle, final boolean intrusiveHolders) {
      super();
      this.byId = new ObjectArrayList(256);
      this.toId = (Reference2IntMap)Util.make(new Reference2IntOpenHashMap(), (t) -> t.defaultReturnValue(-1));
      this.byLocation = new HashMap();
      this.byKey = new HashMap();
      this.byValue = new IdentityHashMap();
      this.registrationInfos = new IdentityHashMap();
      this.frozenTags = new IdentityHashMap();
      this.allTags = MappedRegistry.TagSet.<T>unbound();
      this.key = key;
      this.registryLifecycle = initialLifecycle;
      if (intrusiveHolders) {
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

   private void validateWrite(final ResourceKey<T> key) {
      if (this.frozen) {
         throw new IllegalStateException("Registry is already frozen (trying to add key " + String.valueOf(key) + ")");
      }
   }

   public Holder.Reference<T> register(final ResourceKey<T> key, final T value, final RegistrationInfo registrationInfo) {
      this.validateWrite(key);
      Objects.requireNonNull(key);
      Objects.requireNonNull(value);
      if (this.byLocation.containsKey(key.identifier())) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate key '" + String.valueOf(key) + "' to registry"));
      } else if (this.byValue.containsKey(value)) {
         throw (IllegalStateException)Util.pauseInIde(new IllegalStateException("Adding duplicate value '" + String.valueOf(value) + "' to registry"));
      } else {
         Holder.Reference<T> holder;
         if (this.unregisteredIntrusiveHolders != null) {
            holder = (Holder.Reference)this.unregisteredIntrusiveHolders.remove(value);
            if (holder == null) {
               String var10002 = String.valueOf(key);
               throw new AssertionError("Missing intrusive holder for " + var10002 + ":" + String.valueOf(value));
            }

            holder.bindKey(key);
         } else {
            holder = (Holder.Reference)this.byKey.computeIfAbsent(key, (k) -> Holder.Reference.createStandAlone(this, k));
         }

         this.byKey.put(key, holder);
         this.byLocation.put(key.identifier(), holder);
         this.byValue.put(value, holder);
         int newId = this.byId.size();
         this.byId.add(holder);
         this.toId.put(value, newId);
         this.registrationInfos.put(key, registrationInfo);
         this.registryLifecycle = this.registryLifecycle.add(registrationInfo.lifecycle());
         return holder;
      }
   }

   public @Nullable Identifier getKey(final T thing) {
      Holder.Reference<T> holder = (Holder.Reference)this.byValue.get(thing);
      return holder != null ? holder.key().identifier() : null;
   }

   public Optional<ResourceKey<T>> getResourceKey(final T thing) {
      return Optional.ofNullable((Holder.Reference)this.byValue.get(thing)).map(Holder.Reference::key);
   }

   public int getId(final @Nullable T thing) {
      return this.toId.getInt(thing);
   }

   public @Nullable T getValue(final @Nullable ResourceKey<T> key) {
      return (T)getValueFromNullable((Holder.Reference)this.byKey.get(key));
   }

   public @Nullable T byId(final int id) {
      return (T)(id >= 0 && id < this.byId.size() ? ((Holder.Reference)this.byId.get(id)).value() : null);
   }

   public Optional<Holder.Reference<T>> get(final int id) {
      return id >= 0 && id < this.byId.size() ? Optional.ofNullable((Holder.Reference)this.byId.get(id)) : Optional.empty();
   }

   public Optional<Holder.Reference<T>> get(final Identifier id) {
      return Optional.ofNullable((Holder.Reference)this.byLocation.get(id));
   }

   public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
      return Optional.ofNullable((Holder.Reference)this.byKey.get(id));
   }

   public Optional<Holder.Reference<T>> getAny() {
      return this.byId.isEmpty() ? Optional.empty() : Optional.of((Holder.Reference)this.byId.getFirst());
   }

   public Holder<T> wrapAsHolder(final T value) {
      Holder.Reference<T> existingHolder = (Holder.Reference)this.byValue.get(value);
      return (Holder<T>)(existingHolder != null ? existingHolder : Holder.direct(value));
   }

   private Holder.Reference<T> getOrCreateHolderOrThrow(final ResourceKey<T> key) {
      return (Holder.Reference)this.byKey.computeIfAbsent(key, (id) -> {
         if (this.unregisteredIntrusiveHolders != null) {
            throw new IllegalStateException("This registry can't create new holders without value");
         } else {
            this.validateWrite(id);
            return Holder.Reference.createStandAlone(this, id);
         }
      });
   }

   public int size() {
      return this.byKey.size();
   }

   public Optional<RegistrationInfo> registrationInfo(final ResourceKey<T> element) {
      return Optional.ofNullable((RegistrationInfo)this.registrationInfos.get(element));
   }

   public Lifecycle registryLifecycle() {
      return this.registryLifecycle;
   }

   public Iterator<T> iterator() {
      return Iterators.transform(this.byId.iterator(), Holder::value);
   }

   public @Nullable T getValue(final @Nullable Identifier key) {
      Holder.Reference<T> result = (Holder.Reference)this.byLocation.get(key);
      return (T)getValueFromNullable(result);
   }

   private static <T> @Nullable T getValueFromNullable(final Holder.@Nullable Reference<T> result) {
      return (T)(result != null ? result.value() : null);
   }

   public Set<Identifier> keySet() {
      return Collections.unmodifiableSet(this.byLocation.keySet());
   }

   public Set<ResourceKey<T>> registryKeySet() {
      return Collections.unmodifiableSet(this.byKey.keySet());
   }

   public Set<Map.Entry<ResourceKey<T>, T>> entrySet() {
      return Collections.unmodifiableSet(Util.mapValuesLazy(this.byKey, Holder::value).entrySet());
   }

   public Stream<Holder.Reference<T>> listElements() {
      return this.byId.stream();
   }

   public Stream<HolderSet.Named<T>> getTags() {
      return this.allTags.getTags();
   }

   private HolderSet.Named<T> getOrCreateTagForRegistration(final TagKey<T> tag) {
      return (HolderSet.Named)this.frozenTags.computeIfAbsent(tag, this::createTag);
   }

   private HolderSet.Named<T> createTag(final TagKey<T> tag) {
      return new HolderSet.Named<T>(this, tag);
   }

   public boolean isEmpty() {
      return this.byKey.isEmpty();
   }

   public Optional<Holder.Reference<T>> getRandom(final RandomSource random) {
      return Util.<Holder.Reference<T>>getRandomSafe(this.byId, random);
   }

   public boolean containsKey(final Identifier key) {
      return this.byLocation.containsKey(key);
   }

   public boolean containsKey(final ResourceKey<T> key) {
      return this.byKey.containsKey(key);
   }

   public DataComponentLookup<T> componentLookup() {
      return (DataComponentLookup)Objects.requireNonNull(this.componentLookup, "Registry not frozen yet");
   }

   public Registry<T> freeze() {
      if (this.frozen) {
         return this;
      } else {
         this.frozen = true;
         this.byValue.forEach((value, holder) -> holder.bindValue(value));
         List<Identifier> unboundEntries = this.byKey.entrySet().stream().filter((e) -> !((Holder.Reference)e.getValue()).isBound()).map((e) -> ((ResourceKey)e.getKey()).identifier()).sorted().toList();
         if (!unboundEntries.isEmpty()) {
            String var3 = String.valueOf(this.key());
            throw new IllegalStateException("Unbound values in registry " + var3 + ": " + String.valueOf(unboundEntries));
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
               List<Identifier> unboundTags = this.frozenTags.entrySet().stream().filter((e) -> !((HolderSet.Named)e.getValue()).isBound()).map((e) -> ((TagKey)e.getKey()).location()).sorted().toList();
               if (!unboundTags.isEmpty()) {
                  String var10002 = String.valueOf(this.key());
                  throw new IllegalStateException("Unbound tags in registry " + var10002 + ": " + String.valueOf(unboundTags));
               } else {
                  this.componentLookup = new DataComponentLookup<T>(this.byId);
                  this.allTags = MappedRegistry.TagSet.<T>fromMap(this.frozenTags);
                  this.refreshTagsInHolders();
                  return this;
               }
            }
         }
      }
   }

   public Holder.Reference<T> createIntrusiveHolder(final T value) {
      if (this.unregisteredIntrusiveHolders == null) {
         throw new IllegalStateException("This registry can't create intrusive holders");
      } else {
         this.validateWrite();
         return (Holder.Reference)this.unregisteredIntrusiveHolders.computeIfAbsent(value, (v) -> Holder.Reference.createIntrusive(this, v));
      }
   }

   public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
      return this.allTags.get(id);
   }

   private Holder.Reference<T> validateAndUnwrapTagElement(final TagKey<T> id, final Holder<T> value) {
      if (!value.canSerializeIn(this)) {
         String var4 = String.valueOf(id);
         throw new IllegalStateException("Can't create named set " + var4 + " containing value " + String.valueOf(value) + " from outside registry " + String.valueOf(this));
      } else if (value instanceof Holder.Reference) {
         Holder.Reference<T> reference = (Holder.Reference)value;
         return reference;
      } else {
         String var10002 = String.valueOf(value);
         throw new IllegalStateException("Found direct holder " + var10002 + " value in tag " + String.valueOf(id));
      }
   }

   public void bindTags(final Map<TagKey<T>, List<Holder<T>>> pendingTags) {
      this.validateWrite();
      pendingTags.forEach((id, values) -> this.getOrCreateTagForRegistration(id).bind(values));
   }

   private void refreshTagsInHolders() {
      Map<Holder.Reference<T>, List<TagKey<T>>> tagsForElement = new IdentityHashMap();
      this.byKey.values().forEach((h) -> tagsForElement.put(h, new ArrayList()));
      this.allTags.forEach((id, values) -> {
         for(Holder<T> value : values) {
            Holder.Reference<T> reference = this.validateAndUnwrapTagElement(id, value);
            ((List)tagsForElement.get(reference)).add(id);
         }

      });
      tagsForElement.forEach(Holder.Reference::bindTags);
   }

   public void bindAllTagsToEmpty() {
      this.validateWrite();
      this.frozenTags.values().forEach((e) -> e.bind(List.of()));
   }

   public HolderGetter<T> createRegistrationLookup() {
      this.validateWrite();
      return new HolderGetter<T>() {
         {
            Objects.requireNonNull(MappedRegistry.this);
         }

         public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
            return Optional.of(this.getOrThrow(id));
         }

         public Holder.Reference<T> getOrThrow(final ResourceKey<T> id) {
            return MappedRegistry.this.getOrCreateHolderOrThrow(id);
         }

         public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
            return Optional.of(this.getOrThrow(id));
         }

         public HolderSet.Named<T> getOrThrow(final TagKey<T> id) {
            return MappedRegistry.this.getOrCreateTagForRegistration(id);
         }
      };
   }

   public Registry.PendingTags<T> prepareTagReload(final TagLoader.LoadResult<T> tags) {
      if (!this.frozen) {
         throw new IllegalStateException("Invalid method used for tag loading");
      } else {
         ImmutableMap.Builder<TagKey<T>, HolderSet.Named<T>> pendingTagsBuilder = ImmutableMap.builder();
         final Map<TagKey<T>, List<Holder<T>>> pendingContents = new HashMap();
         tags.tags().forEach((id, contents) -> {
            HolderSet.Named<T> tagToAdd = (HolderSet.Named)this.frozenTags.get(id);
            if (tagToAdd == null) {
               tagToAdd = this.createTag(id);
            }

            pendingTagsBuilder.put(id, tagToAdd);
            pendingContents.put(id, List.copyOf(contents));
         });
         final ImmutableMap<TagKey<T>, HolderSet.Named<T>> pendingTags = pendingTagsBuilder.build();
         final HolderLookup.RegistryLookup<T> patchedHolder = new HolderLookup.RegistryLookup.Delegate<T>() {
            {
               Objects.requireNonNull(MappedRegistry.this);
            }

            public HolderLookup.RegistryLookup<T> parent() {
               return MappedRegistry.this;
            }

            public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
               return Optional.ofNullable((HolderSet.Named)pendingTags.get(id));
            }

            public Stream<HolderSet.Named<T>> listTags() {
               return pendingTags.values().stream();
            }
         };
         return new Registry.PendingTags<T>() {
            {
               Objects.requireNonNull(MappedRegistry.this);
            }

            public ResourceKey<? extends Registry<? extends T>> key() {
               return MappedRegistry.this.key();
            }

            public int size() {
               return pendingContents.size();
            }

            public HolderLookup.RegistryLookup<T> lookup() {
               return patchedHolder;
            }

            public void apply() {
               pendingTags.forEach((id, tag) -> {
                  List<Holder<T>> values = (List)pendingContents.getOrDefault(id, List.of());
                  tag.bind(values);
               });
               MappedRegistry.this.allTags = MappedRegistry.TagSet.<T>fromMap(pendingTags);
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

            public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
               throw new IllegalStateException("Tags not bound, trying to access " + String.valueOf(id));
            }

            public void forEach(final BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> action) {
               throw new IllegalStateException("Tags not bound");
            }

            public Stream<HolderSet.Named<T>> getTags() {
               throw new IllegalStateException("Tags not bound");
            }
         };
      }

      static <T> TagSet<T> fromMap(final Map<TagKey<T>, HolderSet.Named<T>> tags) {
         return new TagSet<T>() {
            public boolean isBound() {
               return true;
            }

            public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
               return Optional.ofNullable((HolderSet.Named)tags.get(id));
            }

            public void forEach(final BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> action) {
               tags.forEach(action);
            }

            public Stream<HolderSet.Named<T>> getTags() {
               return tags.values().stream();
            }
         };
      }

      boolean isBound();

      Optional<HolderSet.Named<T>> get(TagKey<T> id);

      void forEach(BiConsumer<? super TagKey<T>, ? super HolderSet.Named<T>> action);

      Stream<HolderSet.Named<T>> getTags();
   }
}
