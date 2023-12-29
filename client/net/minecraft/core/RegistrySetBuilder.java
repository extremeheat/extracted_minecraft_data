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
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.apache.commons.lang3.mutable.MutableObject;

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

   static <T> HolderLookup.RegistryLookup<T> lookupFromMap(
      final ResourceKey<? extends Registry<? extends T>> var0, final Lifecycle var1, final Map<ResourceKey<T>, Holder.Reference<T>> var2
   ) {
      return new HolderLookup.RegistryLookup<T>() {
         @Override
         public ResourceKey<? extends Registry<? extends T>> key() {
            return var0;
         }

         @Override
         public Lifecycle registryLifecycle() {
            return var1;
         }

         @Override
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1x) {
            return Optional.ofNullable((Holder.Reference<T>)var2.get(var1x));
         }

         @Override
         public Stream<Holder.Reference<T>> listElements() {
            return var2.values().stream();
         }

         @Override
         public Optional<HolderSet.Named<T>> get(TagKey<T> var1x) {
            return Optional.empty();
         }

         @Override
         public Stream<HolderSet.Named<T>> listTags() {
            return Stream.empty();
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

   private static HolderLookup.Provider buildProviderWithContext(RegistryAccess var0, Stream<HolderLookup.RegistryLookup<?>> var1) {
      Stream var2 = var0.registries().map(var0x -> var0x.value().asLookup());
      return HolderLookup.Provider.create(Stream.concat(var2, var1));
   }

   public HolderLookup.Provider build(RegistryAccess var1) {
      RegistrySetBuilder.BuildState var2 = this.createState(var1);
      Stream var3 = this.entries.stream().map(var1x -> var1x.collectRegisteredValues(var2).buildAsLookup(var2.owner));
      HolderLookup.Provider var4 = buildProviderWithContext(var1, var3);
      var2.reportNotCollectedHolders();
      var2.reportUnclaimedRegisteredValues();
      var2.throwOnError();
      return var4;
   }

   private HolderLookup.Provider createLazyFullPatchedRegistries(
      RegistryAccess var1,
      HolderLookup.Provider var2,
      Cloner.Factory var3,
      Map<ResourceKey<? extends Registry<?>>, RegistrySetBuilder.RegistryContents<?>> var4,
      HolderLookup.Provider var5
   ) {
      RegistrySetBuilder.CompositeOwner var6 = new RegistrySetBuilder.CompositeOwner();
      MutableObject var7 = new MutableObject();
      List var8 = var4.keySet()
         .stream()
         .map(var6x -> this.createLazyFullPatchedRegistries(var6, var3, var6x, var5, var2, var7))
         .peek(var6::add)
         .collect(Collectors.toUnmodifiableList());
      HolderLookup.Provider var9 = buildProviderWithContext(var1, var8.stream());
      var7.setValue(var9);
      return var9;
   }

   private <T> HolderLookup.RegistryLookup<T> createLazyFullPatchedRegistries(
      HolderOwner<T> var1,
      Cloner.Factory var2,
      ResourceKey<? extends Registry<? extends T>> var3,
      HolderLookup.Provider var4,
      HolderLookup.Provider var5,
      MutableObject<HolderLookup.Provider> var6
   ) {
      Cloner var7 = var2.cloner(var3);
      if (var7 == null) {
         throw new NullPointerException("No cloner for " + var3.location());
      } else {
         HashMap var8 = new HashMap();
         HolderLookup.RegistryLookup var9 = var4.lookupOrThrow(var3);
         var9.listElements().forEach(var5x -> {
            ResourceKey var6x = var5x.key();
            RegistrySetBuilder.LazyHolder var7x = new RegistrySetBuilder.LazyHolder(var1, var6x);
            var7x.supplier = () -> (T)var7.clone(var5x.value(), var4, (HolderLookup.Provider)var6.getValue());
            var8.put(var6x, var7x);
         });
         HolderLookup.RegistryLookup var10 = var5.lookupOrThrow(var3);
         var10.listElements().forEach(var5x -> {
            ResourceKey var6x = var5x.key();
            var8.computeIfAbsent(var6x, var6xx -> {
               RegistrySetBuilder.LazyHolder var7x = new RegistrySetBuilder.LazyHolder(var1, var6x);
               var7x.supplier = () -> (T)var7.clone(var5x.value(), var5, (HolderLookup.Provider)var6.getValue());
               return var7x;
            });
         });
         Lifecycle var11 = var9.registryLifecycle().add(var10.registryLifecycle());
         return lookupFromMap(var3, var11, var8);
      }
   }

   public RegistrySetBuilder.PatchedRegistries buildPatch(RegistryAccess var1, HolderLookup.Provider var2, Cloner.Factory var3) {
      RegistrySetBuilder.BuildState var4 = this.createState(var1);
      HashMap var5 = new HashMap();
      this.entries.stream().map(var1x -> var1x.collectRegisteredValues(var4)).forEach(var1x -> var5.put(var1x.key, var1x));
      Set var6 = var1.listRegistries().collect(Collectors.toUnmodifiableSet());
      var2.listRegistries()
         .filter(var1x -> !var6.contains(var1x))
         .forEach(var1x -> var5.putIfAbsent(var1x, new RegistrySetBuilder.RegistryContents<>(var1x, Lifecycle.stable(), Map.of())));
      Stream var7 = var5.values().stream().map(var1x -> var1x.buildAsLookup(var4.owner));
      HolderLookup.Provider var8 = buildProviderWithContext(var1, var7);
      var4.reportUnclaimedRegisteredValues();
      var4.throwOnError();
      HolderLookup.Provider var9 = this.createLazyFullPatchedRegistries(var1, var2, var3, var5, var8);
      return new RegistrySetBuilder.PatchedRegistries(var9, var8);
   }

   static record BuildState(
      RegistrySetBuilder.CompositeOwner a,
      RegistrySetBuilder.UniversalLookup b,
      Map<ResourceLocation, HolderGetter<?>> c,
      Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> d,
      List<RuntimeException> e
   ) {
      final RegistrySetBuilder.CompositeOwner owner;
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

      public void reportUnclaimedRegisteredValues() {
         this.registeredValues.forEach((var1, var2) -> this.errors.add(new IllegalStateException("Orpaned value " + var2.value + " for key " + var1)));
      }

      public void reportNotCollectedHolders() {
         for(ResourceKey var2 : this.lookup.holders.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + var2));
         }
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

      public <T> HolderOwner<T> cast() {
         return this;
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

   static class LazyHolder<T> extends Holder.Reference<T> {
      @Nullable
      Supplier<T> supplier;

      protected LazyHolder(HolderOwner<T> var1, @Nullable ResourceKey<T> var2) {
         super(Holder.Reference.Type.STAND_ALONE, var1, var2, (T)null);
      }

      @Override
      protected void bindValue(T var1) {
         super.bindValue((T)var1);
         this.supplier = null;
      }

      @Override
      public T value() {
         if (this.supplier != null) {
            this.bindValue(this.supplier.get());
         }

         return super.value();
      }
   }

   public static record PatchedRegistries(HolderLookup.Provider a, HolderLookup.Provider b) {
      private final HolderLookup.Provider full;
      private final HolderLookup.Provider patches;

      public PatchedRegistries(HolderLookup.Provider var1, HolderLookup.Provider var2) {
         super();
         this.full = var1;
         this.patches = var2;
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
      private final Lifecycle lifecycle;
      private final Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values;

      RegistryContents(ResourceKey<? extends Registry<? extends T>> var1, Lifecycle var2, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> var3) {
         super();
         this.key = var1;
         this.lifecycle = var2;
         this.values = var3;
      }

      public HolderLookup.RegistryLookup<T> buildAsLookup(RegistrySetBuilder.CompositeOwner var1) {
         Map var2 = this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Entry::getKey, var1x -> {
            RegistrySetBuilder.ValueAndHolder var2x = (RegistrySetBuilder.ValueAndHolder)var1x.getValue();
            Holder.Reference var3x = var2x.holder().orElseGet(() -> Holder.Reference.createStandAlone(var1.cast(), (ResourceKey<T>)var1x.getKey()));
            var3x.bindValue((T)var2x.value().value());
            return var3x;
         }));
         HolderLookup.RegistryLookup var3 = RegistrySetBuilder.lookupFromMap(this.key, this.lifecycle, var2);
         var1.add(var3);
         return var3;
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

      public RegistrySetBuilder.RegistryContents<T> collectRegisteredValues(RegistrySetBuilder.BuildState var1) {
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
