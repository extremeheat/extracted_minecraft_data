package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.serialization.DynamicOps;
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
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.RegistryOps;
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
      final ResourceKey<? extends Registry<? extends T>> var0, final Lifecycle var1, HolderOwner<T> var2, final Map<ResourceKey<T>, Holder.Reference<T>> var3
   ) {
      return new RegistrySetBuilder.EmptyTagRegistryLookup<T>(var2) {
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
            return Optional.ofNullable((Holder.Reference<T>)var3.get(var1x));
         }

         @Override
         public Stream<Holder.Reference<T>> listElements() {
            return var3.values().stream();
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

   private static HolderLookup.Provider buildProviderWithContext(
      RegistrySetBuilder.UniversalOwner var0, RegistryAccess var1, Stream<HolderLookup.RegistryLookup<?>> var2
   ) {
      final HashMap var3 = new HashMap();

      record 1Entry<T>(HolderLookup.RegistryLookup<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
         _Entry/* $VF was: 1Entry*/(HolderLookup.RegistryLookup<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
            super();
            this.lookup = lookup;
            this.opsInfo = opsInfo;
         }

         public static <T> 1Entry<T> createForContextRegistry(HolderLookup.RegistryLookup<T> var0) {
            return new 1Entry<>(new RegistrySetBuilder.EmptyTagLookupWrapper<>(var0, var0), RegistryOps.RegistryInfo.fromRegistryLookup(var0));
         }

         public static <T> 1Entry<T> createForNewRegistry(RegistrySetBuilder.UniversalOwner var0, HolderLookup.RegistryLookup<T> var1) {
            return new 1Entry<>(
               new RegistrySetBuilder.EmptyTagLookupWrapper<>(var0.cast(), var1), new RegistryOps.RegistryInfo<>(var0.cast(), var1, var1.registryLifecycle())
            );
         }
      }

      var1.registries().forEach(var1x -> var3.put(var1x.key(), 1Entry.createForContextRegistry(var1x.value().asLookup())));
      var2.forEach(var2x -> var3.put(var2x.key(), 1Entry.createForNewRegistry(var0, var2x)));
      return new HolderLookup.Provider() {
         @Override
         public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
            return var3.keySet().stream();
         }

         <T> Optional<1Entry<T>> getEntry(ResourceKey<? extends Registry<? extends T>> var1) {
            return Optional.ofNullable((1Entry<T>)var3.get(var1));
         }

         @Override
         public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
            return this.getEntry(var1).map(1Entry::lookup);
         }

         @Override
         public <V> RegistryOps<V> createSerializationContext(DynamicOps<V> var1) {
            return RegistryOps.create(var1, new RegistryOps.RegistryInfoLookup() {
               @Override
               public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
                  return getEntry(var1).map(1Entry::opsInfo);
               }
            });
         }
      };
   }

   public HolderLookup.Provider build(RegistryAccess var1) {
      RegistrySetBuilder.BuildState var2 = this.createState(var1);
      Stream var3 = this.entries.stream().map(var1x -> var1x.collectRegisteredValues(var2).buildAsLookup(var2.owner));
      HolderLookup.Provider var4 = buildProviderWithContext(var2.owner, var1, var3);
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
      RegistrySetBuilder.UniversalOwner var6 = new RegistrySetBuilder.UniversalOwner();
      MutableObject var7 = new MutableObject();
      List var8 = var4.keySet()
         .stream()
         .map(var6x -> this.createLazyFullPatchedRegistries(var6, var3, (ResourceKey<? extends Registry<? extends Object>>)var6x, var5, var2, var7))
         .collect(Collectors.toUnmodifiableList());
      HolderLookup.Provider var9 = buildProviderWithContext(var6, var1, var8.stream());
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
         return lookupFromMap(var3, var11, var1, var8);
      }
   }

   public RegistrySetBuilder.PatchedRegistries buildPatch(RegistryAccess var1, HolderLookup.Provider var2, Cloner.Factory var3) {
      RegistrySetBuilder.BuildState var4 = this.createState(var1);
      HashMap var5 = new HashMap();
      this.entries.stream().map(var1x -> var1x.collectRegisteredValues(var4)).forEach(var1x -> var5.put(var1x.key, var1x));
      Set var6 = var1.listRegistries().collect(Collectors.toUnmodifiableSet());
      var2.listRegistries()
         .filter(var1x -> !var6.contains(var1x))
         .forEach(
            var1x -> var5.putIfAbsent(var1x, new RegistrySetBuilder.RegistryContents<>((ResourceKey<? extends Registry<?>>)var1x, Lifecycle.stable(), Map.of()))
         );
      Stream var7 = var5.values().stream().map(var1x -> var1x.buildAsLookup(var4.owner));
      HolderLookup.Provider var8 = buildProviderWithContext(var4.owner, var1, var7);
      var4.reportUnclaimedRegisteredValues();
      var4.throwOnError();
      HolderLookup.Provider var9 = this.createLazyFullPatchedRegistries(var1, var2, var3, var5, var8);
      return new RegistrySetBuilder.PatchedRegistries(var9, var8);
   }

   static record BuildState(
      RegistrySetBuilder.UniversalOwner owner,
      RegistrySetBuilder.UniversalLookup lookup,
      Map<ResourceLocation, HolderGetter<?>> registries,
      Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues,
      List<RuntimeException> errors
   ) {

      private BuildState(
         RegistrySetBuilder.UniversalOwner owner,
         RegistrySetBuilder.UniversalLookup lookup,
         Map<ResourceLocation, HolderGetter<?>> registries,
         Map<ResourceKey<?>, RegistrySetBuilder.RegisteredValue<?>> registeredValues,
         List<RuntimeException> errors
      ) {
         super();
         this.owner = owner;
         this.lookup = lookup;
         this.registries = registries;
         this.registeredValues = registeredValues;
         this.errors = errors;
      }

      public static RegistrySetBuilder.BuildState create(RegistryAccess var0, Stream<ResourceKey<? extends Registry<?>>> var1) {
         RegistrySetBuilder.UniversalOwner var2 = new RegistrySetBuilder.UniversalOwner();
         ArrayList var3 = new ArrayList();
         RegistrySetBuilder.UniversalLookup var4 = new RegistrySetBuilder.UniversalLookup(var2);
         Builder var5 = ImmutableMap.builder();
         var0.registries().forEach(var1x -> var5.put(var1x.key().location(), RegistrySetBuilder.wrapContextLookup(var1x.value().asLookup())));
         var1.forEach(var2x -> var5.put(var2x.location(), var4));
         return new RegistrySetBuilder.BuildState(var2, var4, var5.build(), new HashMap<>(), var3);
      }

      public <T> BootstrapContext<T> bootstrapContext() {
         return new BootstrapContext<T>() {
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
         for (ResourceKey var2 : this.lookup.holders.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + var2));
         }
      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException var1 = new IllegalStateException("Errors during registry creation");

            for (RuntimeException var3 : this.errors) {
               var1.addSuppressed(var3);
            }

            throw var1;
         }
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

   static class EmptyTagLookupWrapper<T> extends RegistrySetBuilder.EmptyTagRegistryLookup<T> implements HolderLookup.RegistryLookup.Delegate<T> {
      private final HolderLookup.RegistryLookup<T> parent;

      EmptyTagLookupWrapper(HolderOwner<T> var1, HolderLookup.RegistryLookup<T> var2) {
         super(var1);
         this.parent = var2;
      }

      @Override
      public HolderLookup.RegistryLookup<T> parent() {
         return this.parent;
      }
   }

   abstract static class EmptyTagRegistryLookup<T> extends RegistrySetBuilder.EmptyTagLookup<T> implements HolderLookup.RegistryLookup<T> {
      protected EmptyTagRegistryLookup(HolderOwner<T> var1) {
         super(var1);
      }

      @Override
      public Stream<HolderSet.Named<T>> listTags() {
         throw new UnsupportedOperationException("Tags are not available in datagen");
      }
   }

   static class LazyHolder<T> extends Holder.Reference<T> {
      @Nullable
      Supplier<T> supplier;

      protected LazyHolder(HolderOwner<T> var1, @Nullable ResourceKey<T> var2) {
         super(Holder.Reference.Type.STAND_ALONE, var1, var2, null);
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

   public static record PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
      public PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
         super();
         this.full = full;
         this.patches = patches;
      }
   }

   static record RegisteredValue<T>(T value, Lifecycle lifecycle) {

      RegisteredValue(T value, Lifecycle lifecycle) {
         super();
         this.value = (T)value;
         this.lifecycle = lifecycle;
      }
   }

   @FunctionalInterface
   public interface RegistryBootstrap<T> {
      void run(BootstrapContext<T> var1);
   }

   static record RegistryContents<T>(
      ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values
   ) {

      RegistryContents(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, RegistrySetBuilder.ValueAndHolder<T>> values) {
         super();
         this.key = key;
         this.lifecycle = lifecycle;
         this.values = values;
      }

      public HolderLookup.RegistryLookup<T> buildAsLookup(RegistrySetBuilder.UniversalOwner var1) {
         Map var2 = this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Entry::getKey, var1x -> {
            RegistrySetBuilder.ValueAndHolder var2x = (RegistrySetBuilder.ValueAndHolder)var1x.getValue();
            Holder.Reference var3 = var2x.holder().orElseGet(() -> Holder.Reference.createStandAlone(var1.cast(), (ResourceKey<T>)var1x.getKey()));
            var3.bindValue(var2x.value().value());
            return var3;
         }));
         return RegistrySetBuilder.lookupFromMap(this.key, this.lifecycle, var1.cast(), var2);
      }
   }

   static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
      RegistryStub(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistrySetBuilder.RegistryBootstrap<T> bootstrap) {
         super();
         this.key = key;
         this.lifecycle = lifecycle;
         this.bootstrap = bootstrap;
      }

      void apply(RegistrySetBuilder.BuildState var1) {
         this.bootstrap.run(var1.bootstrapContext());
      }

      public RegistrySetBuilder.RegistryContents<T> collectRegisteredValues(RegistrySetBuilder.BuildState var1) {
         HashMap var2 = new HashMap();
         Iterator var3 = var1.registeredValues.entrySet().iterator();

         while (var3.hasNext()) {
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
         return (Holder.Reference<T>)this.holders.computeIfAbsent(var1, var1x -> Holder.Reference.createStandAlone(this.owner, (ResourceKey<Object>)var1x));
      }
   }

   static class UniversalOwner implements HolderOwner<Object> {
      UniversalOwner() {
         super();
      }

      public <T> HolderOwner<T> cast() {
         return this;
      }
   }

   static record ValueAndHolder<T>(RegistrySetBuilder.RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
      ValueAndHolder(RegistrySetBuilder.RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
         super();
         this.value = value;
         this.holder = holder;
      }
   }
}
