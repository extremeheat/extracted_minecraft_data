package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public class RegistrySetBuilder {
   private final List<RegistrySetBuilder.RegistryStub<?>> entries = new ArrayList<>();

   public RegistrySetBuilder() {
      super();
   }

   static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> var0) {
      return new RegistrySetBuilder.EmptyTagLookup<T>(var0) {
         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
            return var0.get(var1);
         }
      };
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, RegistrySetBuilder.RegistryBootstrap<T> var3) {
      this.entries.add(new RegistrySetBuilder.RegistryStub(var1, var2, var3));
      return this;
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> var1, RegistrySetBuilder.RegistryBootstrap<T> var2) {
      return this.add(var1, Lifecycle.stable(), var2);
   }

   private RegistrySetBuilder.BuildState createState(RegistryAccess var1) {
      RegistrySetBuilder.BuildState var2 = RegistrySetBuilder.BuildState.create(var1, this.entries.stream().map(RegistrySetBuilder.RegistryStub::key));
      this.entries.forEach(var1x -> var1x.apply(var2));
      return var2;
   }

   public HolderLookup.Provider build(RegistryAccess var1) {
      RegistrySetBuilder.BuildState var2 = this.createState(var1);
      Stream var3 = var1.registries().map(var0 -> var0.value().asLookup());
      Stream var4 = this.entries.stream().map(var1x -> var1x.collectChanges(var2).buildAsLookup());
      HolderLookup.Provider var5 = HolderLookup.Provider.create(Stream.concat(var3, var4.peek(var2::addOwner)));
      var2.reportRemainingUnreferencedValues();
      var2.throwOnError();
      return var5;
   }

   public HolderLookup.Provider buildPatch(RegistryAccess var1, HolderLookup.Provider var2) {
      RegistrySetBuilder.BuildState var3 = this.createState(var1);
      HashMap var4 = new HashMap();
      var3.collectReferencedRegistries().forEach(var1x -> var4.put(var1x.key, var1x));
      this.entries.stream().map(var1x -> var1x.collectChanges(var3)).forEach(var1x -> var4.put(var1x.key, var1x));
      Stream var5 = var1.registries().map(var0 -> var0.value().asLookup());
      HolderLookup.Provider var6 = HolderLookup.Provider.create(
         Stream.concat(var5, var4.values().stream().map(RegistrySetBuilder.RegistryContents::buildAsLookup).peek(var3::addOwner))
      );
      var3.fillMissingHolders(var2);
      var3.reportRemainingUnreferencedValues();
      var3.throwOnError();
      return var6;
   }

   static record BuildState(
      RegistrySetBuilder.CompositeOwner a,
      RegistrySetBuilder.UniversalLookup b,
      Map<ResourceLocation, HolderGetter<?>> c,
      Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> d,
      List<RuntimeException> e
   ) {
      private final RegistrySetBuilder.CompositeOwner owner;
      final RegistrySetBuilder.UniversalLookup lookup;
      final Map<ResourceLocation, HolderGetter<?>> registries;
      final Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues;
      final List<RuntimeException> errors;

      private BuildState(
         RegistrySetBuilder.CompositeOwner var1,
         RegistrySetBuilder.UniversalLookup var2,
         Map<ResourceLocation, HolderGetter<?>> var3,
         Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> var4,
         List<RuntimeException> var5
      ) {
         super();
         this.owner = var1;
         this.lookup = var2;
         this.registries = var3;
         this.registeredValues = var4;
         this.errors = var5;
      }

      public static RegistrySetBuilder.BuildState create(RegistryAccess var0, Stream<ResourceKey<? extends Registry<?>>> var1) {
         RegistrySetBuilder.CompositeOwner var2 = new RegistrySetBuilder.CompositeOwner();
         ArrayList var3 = new ArrayList();
         RegistrySetBuilder.UniversalLookup var4 = new RegistrySetBuilder.UniversalLookup(var2);
         Builder var5 = ImmutableMap.builder();
         var0.registries().forEach(var1x -> var5.put(var1x.key().location(), RegistrySetBuilder.wrapContextLookup(var1x.value().asLookup())));
         var1.forEach(var2x -> var5.put(var2x.location(), var4));
         return new RegistrySetBuilder.BuildState(var2, var4, var5.build(), new HashMap<>(), var3);
      }

      public <T> BootstapContext<T> bootstapContext() {
         return new BootstapContext<T>() {
            @Override
            public Holder.Reference<T> register(ResourceKey<T> var1, T var2, Lifecycle var3) {
               RegistrySetBuilder.RegisteredValue var4 = BuildState.this.registeredValues.put(var1, new RegistrySetBuilder.RegisteredValue<>(var2, var3));
               if (var4 != null) {
                  BuildState.this.errors.add(new IllegalStateException("Duplicate registration for " + var1 + ", new=" + var2 + ", old=" + var4.value));
               }

               return BuildState.this.lookup.getOrCreate(var1);
            }

            @Override
            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> var1) {
               return (HolderGetter<S>)BuildState.this.registries.getOrDefault(var1.location(), BuildState.this.lookup);
            }
         };
      }

      public void reportRemainingUnreferencedValues() {
         for(ResourceKey var2 : this.lookup.holders.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + var2));
         }

         this.registeredValues.forEach((var1, var2x) -> this.errors.add(new IllegalStateException("Orpaned value " + var2x.value + " for key " + var1)));
      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException var1 = new IllegalStateException("Errors during registry creation");

            for(RuntimeException var3 : this.errors) {
               var1.addSuppressed(var3);
            }

            throw var1;
         }
      }

      public void addOwner(HolderOwner<?> var1) {
         this.owner.add(var1);
      }

      public void fillMissingHolders(HolderLookup.Provider var1) {
         HashMap var2 = new HashMap();
         Iterator var3 = this.lookup.holders.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            ResourceKey var5 = (ResourceKey)var4.getKey();
            Holder.Reference var6 = (Holder.Reference)var4.getValue();
            var2.computeIfAbsent(var5.registry(), var1x -> var1.lookup(ResourceKey.createRegistryKey(var1x)))
               .flatMap(var1x -> var1x.get(var5))
               .ifPresent(var2x -> {
                  var6.bindValue(var2x.value());
                  var3.remove();
               });
         }
      }

      public Stream<RegistrySetBuilder.RegistryContents<?>> collectReferencedRegistries() {
         return this.lookup
            .holders
            .keySet()
            .stream()
            .map(ResourceKey::registry)
            .distinct()
            .map(var0 -> new RegistrySetBuilder.RegistryContents(ResourceKey.createRegistryKey(var0), Lifecycle.stable(), Map.of()));
      }
   }

   static class CompositeOwner implements HolderOwner<Object> {
      private final Set<HolderOwner<?>> owners = Sets.newIdentityHashSet();

      CompositeOwner() {
         super();
      }

      @Override
      public boolean canSerializeIn(HolderOwner<Object> var1) {
         return this.owners.contains(var1);
      }

      public void add(HolderOwner<?> var1) {
         this.owners.add(var1);
      }
   }

   abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
      protected final HolderOwner<T> owner;

      protected EmptyTagLookup(HolderOwner<T> var1) {
         super();
         this.owner = var1;
      }

      @Override
      public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
         return Optional.of(HolderSet.emptyNamed(this.owner, var1));
      }
   }

   static record RegisteredValue<T>(T a, Lifecycle b) {
      final T value;
      private final Lifecycle lifecycle;

      RegisteredValue(T var1, Lifecycle var2) {
         super();
         this.value = (T)var1;
         this.lifecycle = var2;
      }
   }

   @FunctionalInterface
   public interface RegistryBootstrap<T> {
      void run(BootstapContext<T> var1);
   }

   static record RegistryContents<T>(ResourceKey<? extends Registry<? extends T>> a, Lifecycle b, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> c) {
      final ResourceKey<? extends Registry<? extends T>> key;
      final Lifecycle lifecycle;
      final Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values;

      RegistryContents(ResourceKey<? extends Registry<? extends T>> var1, Lifecycle var2, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> var3) {
         super();
         this.key = var1;
         this.lifecycle = var2;
         this.values = var3;
      }

      public HolderLookup.RegistryLookup<T> buildAsLookup() {
         return new HolderLookup.RegistryLookup<T>() {
            private final Map<ResourceKey<T>, Holder.Reference<T>> entries = RegistryContents.this.values
               .entrySet()
               .stream()
               .collect(Collectors.toUnmodifiableMap(Entry::getKey, var1x -> {
                  RegistrySetBuilder.ValueAndHolder var2 = (RegistrySetBuilder.ValueAndHolder)var1x.getValue();
                  Holder.Reference var3 = var2.holder().orElseGet(() -> Holder.Reference.createStandAlone(this, (ResourceKey<T>)var1x.getKey()));
                  var3.bindValue((T)var2.value().value());
                  return var3;
               }));

            @Override
            public ResourceKey<? extends Registry<? extends T>> key() {
               return RegistryContents.this.key;
            }

            @Override
            public Lifecycle registryLifecycle() {
               return RegistryContents.this.lifecycle;
            }

            @Override
            public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
               return Optional.ofNullable(this.entries.get(var1));
            }

            @Override
            public Stream<Holder.Reference<T>> listElements() {
               return this.entries.values().stream();
            }

            @Override
            public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
               return Optional.empty();
            }

            @Override
            public Stream<HolderSet.Named<T>> listTags() {
               return Stream.empty();
            }
         };
      }
   }

   static record RegistryStub<T>(ResourceKey<? extends Registry<T>> a, Lifecycle b, RegistrySetBuilder.RegistryBootstrap<T> c) {
      private final ResourceKey<? extends Registry<T>> key;
      private final Lifecycle lifecycle;
      private final RegistrySetBuilder.RegistryBootstrap<T> bootstrap;

      RegistryStub(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, RegistrySetBuilder.RegistryBootstrap<T> var3) {
         super();
         this.key = var1;
         this.lifecycle = var2;
         this.bootstrap = var3;
      }

      void apply(RegistrySetBuilder.BuildState var1) {
         this.bootstrap.run(var1.bootstapContext());
      }

      public RegistrySetBuilder.RegistryContents<T> collectChanges(RegistrySetBuilder.BuildState var1) {
         HashMap var2 = new HashMap();
         Iterator var3 = var1.registeredValues.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            ResourceKey var5 = (ResourceKey)var4.getKey();
            if (var5.isFor(this.key)) {
               RegistrySetBuilder.RegisteredValue var7 = (RegistrySetBuilder.RegisteredValue)var4.getValue();
               Holder.Reference var8 = var1.lookup.holders.remove(var5);
               var2.put(var5, new RegistrySetBuilder.ValueAndHolder(var7, Optional.ofNullable(var8)));
               var3.remove();
            }
         }

         return new RegistrySetBuilder.RegistryContents<>(this.key, this.lifecycle, var2);
      }
   }

   static class UniversalLookup extends RegistrySetBuilder.EmptyTagLookup<Object> {
      final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap<>();

      public UniversalLookup(HolderOwner<Object> var1) {
         super(var1);
      }

      @Override
      public Optional<Holder.Reference<Object>> get(ResourceKey<Object> var1) {
         return Optional.of(this.getOrCreate(var1));
      }

      <T> Holder.Reference<T> getOrCreate(ResourceKey<T> var1) {
         return this.holders.computeIfAbsent(var1, var1x -> Holder.Reference.createStandAlone(this.owner, var1x));
      }
   }

   static record ValueAndHolder<T>(RegistrySetBuilder.RegisteredValue<T> a, Optional<Holder.Reference<T>> b) {
      private final RegistrySetBuilder.RegisteredValue<T> value;
      private final Optional<Holder.Reference<T>> holder;

      ValueAndHolder(RegistrySetBuilder.RegisteredValue<T> var1, Optional<Holder.Reference<T>> var2) {
         super();
         this.value = var1;
         this.holder = var2;
      }
   }
}
