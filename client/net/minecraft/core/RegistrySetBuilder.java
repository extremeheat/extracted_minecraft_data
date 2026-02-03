package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;

public class RegistrySetBuilder {
   private final List<RegistryStub<?>> entries = new ArrayList();

   public RegistrySetBuilder() {
      super();
   }

   private static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> original) {
      return new EmptyTagLookup<T>(original) {
         public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
            return original.get(id);
         }
      };
   }

   private static <T> HolderLookup.RegistryLookup<T> lookupFromMap(final ResourceKey<? extends Registry<? extends T>> key, final Lifecycle lifecycle, final HolderOwner<T> owner, final Map<ResourceKey<T>, Holder.Reference<T>> entries) {
      return new EmptyTagRegistryLookup<T>(owner) {
         public ResourceKey<? extends Registry<? extends T>> key() {
            return key;
         }

         public Lifecycle registryLifecycle() {
            return lifecycle;
         }

         public Optional<Holder.Reference<T>> get(final ResourceKey<T> id) {
            return Optional.ofNullable((Holder.Reference)entries.get(id));
         }

         public Stream<Holder.Reference<T>> listElements() {
            return entries.values().stream();
         }
      };
   }

   public <T> RegistrySetBuilder add(final ResourceKey<? extends Registry<T>> key, final Lifecycle lifecycle, final RegistryBootstrap<T> bootstrap) {
      this.entries.add(new RegistryStub(key, lifecycle, bootstrap));
      return this;
   }

   public <T> RegistrySetBuilder add(final ResourceKey<? extends Registry<T>> key, final RegistryBootstrap<T> bootstrap) {
      return this.add(key, Lifecycle.stable(), bootstrap);
   }

   private BuildState createState(final RegistryAccess context) {
      BuildState state = RegistrySetBuilder.BuildState.create(context, this.entries.stream().map(RegistryStub::key));
      this.entries.forEach((e) -> e.apply(state));
      return state;
   }

   private static HolderLookup.Provider buildProviderWithContext(final UniversalOwner owner, final RegistryAccess context, final Stream<HolderLookup.RegistryLookup<?>> newRegistries) {
      record Entry<T>(HolderLookup.RegistryLookup<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
         Entry {
            super();
         }

         public static <T> Entry<T> createForContextRegistry(final HolderLookup.RegistryLookup<T> registryLookup) {
            return new Entry<T>(new EmptyTagLookupWrapper(registryLookup, registryLookup), RegistryOps.RegistryInfo.fromRegistryLookup(registryLookup));
         }

         public static <T> Entry<T> createForNewRegistry(final UniversalOwner owner, final HolderLookup.RegistryLookup<T> registryLookup) {
            return new Entry<T>(new EmptyTagLookupWrapper(owner.cast(), registryLookup), new RegistryOps.RegistryInfo(owner.cast(), registryLookup, registryLookup.registryLifecycle()));
         }
      }

      final Map<ResourceKey<? extends Registry<?>>, Entry<?>> lookups = new HashMap();
      context.registries().forEach((contextRegistry) -> lookups.put(contextRegistry.key(), Entry.createForContextRegistry(contextRegistry.value())));
      newRegistries.forEach((newRegistry) -> lookups.put(newRegistry.key(), Entry.createForNewRegistry(owner, newRegistry)));
      return new HolderLookup.Provider() {
         public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
            return lookups.keySet().stream();
         }

         private <T> Optional<Entry<T>> getEntry(final ResourceKey<? extends Registry<? extends T>> key) {
            return Optional.ofNullable((Entry)lookups.get(key));
         }

         public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(final ResourceKey<? extends Registry<? extends T>> key) {
            return this.getEntry(key).map(Entry::lookup);
         }

         public <V> RegistryOps<V> createSerializationContext(final DynamicOps<V> parent) {
            return RegistryOps.create(parent, new RegistryOps.RegistryInfoLookup() {
               {
                  Objects.requireNonNull(<VAR_NAMELESS_ENCLOSURE>);
               }

               public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(final ResourceKey<? extends Registry<? extends T>> registryKey) {
                  return getEntry(registryKey).map(Entry::opsInfo);
               }
            });
         }
      };
   }

   public HolderLookup.Provider build(final RegistryAccess context) {
      BuildState state = this.createState(context);
      Stream<HolderLookup.RegistryLookup<?>> newRegistries = this.entries.stream().map((stub) -> stub.collectRegisteredValues(state).buildAsLookup(state.owner));
      HolderLookup.Provider result = buildProviderWithContext(state.owner, context, newRegistries);
      state.reportNotCollectedHolders();
      state.reportUnclaimedRegisteredValues();
      state.throwOnError();
      return result;
   }

   private HolderLookup.Provider createLazyFullPatchedRegistries(final RegistryAccess context, final HolderLookup.Provider fallbackProvider, final Cloner.Factory clonerFactory, final Map<ResourceKey<? extends Registry<?>>, RegistryContents<?>> newRegistries, final HolderLookup.Provider patchOnlyRegistries) {
      UniversalOwner fullPatchedOwner = new UniversalOwner();
      MutableObject<HolderLookup.Provider> resultReference = new MutableObject();
      List<HolderLookup.RegistryLookup<?>> lazyFullRegistries = (List)newRegistries.keySet().stream().map((registryKey) -> this.createLazyFullPatchedRegistries(fullPatchedOwner, clonerFactory, registryKey, patchOnlyRegistries, fallbackProvider, resultReference)).collect(Collectors.toUnmodifiableList());
      HolderLookup.Provider result = buildProviderWithContext(fullPatchedOwner, context, lazyFullRegistries.stream());
      resultReference.setValue(result);
      return result;
   }

   private <T> HolderLookup.RegistryLookup<T> createLazyFullPatchedRegistries(final HolderOwner<T> owner, final Cloner.Factory clonerFactory, final ResourceKey<? extends Registry<? extends T>> registryKey, final HolderLookup.Provider patchProvider, final HolderLookup.Provider fallbackProvider, final MutableObject<HolderLookup.Provider> targetProvider) {
      Cloner<T> cloner = clonerFactory.<T>cloner(registryKey);
      if (cloner == null) {
         throw new NullPointerException("No cloner for " + String.valueOf(registryKey.identifier()));
      } else {
         Map<ResourceKey<T>, Holder.Reference<T>> entries = new HashMap();
         HolderLookup.RegistryLookup<T> patchContents = patchProvider.lookupOrThrow(registryKey);
         patchContents.listElements().forEach((elementHolder) -> {
            ResourceKey<T> elementKey = elementHolder.key();
            LazyHolder<T> holder = new LazyHolder<T>(owner, elementKey);
            holder.supplier = () -> cloner.clone(elementHolder.value(), patchProvider, (HolderLookup.Provider)targetProvider.get());
            entries.put(elementKey, holder);
         });
         HolderLookup.RegistryLookup<T> fallbackContents = fallbackProvider.lookupOrThrow(registryKey);
         fallbackContents.listElements().forEach((elementHolder) -> {
            ResourceKey<T> elementKey = elementHolder.key();
            entries.computeIfAbsent(elementKey, (key) -> {
               LazyHolder<T> holder = new LazyHolder<T>(owner, elementKey);
               holder.supplier = () -> cloner.clone(elementHolder.value(), fallbackProvider, (HolderLookup.Provider)targetProvider.get());
               return holder;
            });
         });
         Lifecycle lifecycle = patchContents.registryLifecycle().add(fallbackContents.registryLifecycle());
         return lookupFromMap(registryKey, lifecycle, owner, entries);
      }
   }

   public PatchedRegistries buildPatch(final RegistryAccess context, final HolderLookup.Provider fallbackProvider, final Cloner.Factory clonerFactory) {
      BuildState state = this.createState(context);
      Map<ResourceKey<? extends Registry<?>>, RegistryContents<?>> newRegistries = new HashMap();
      this.entries.stream().map((stub) -> stub.collectRegisteredValues(state)).forEach((e) -> newRegistries.put(e.key, e));
      Set<ResourceKey<? extends Registry<?>>> contextRegistries = (Set)context.listRegistryKeys().collect(Collectors.toUnmodifiableSet());
      fallbackProvider.listRegistryKeys().filter((k) -> !contextRegistries.contains(k)).forEach((resourceKey) -> newRegistries.putIfAbsent(resourceKey, new RegistryContents(resourceKey, Lifecycle.stable(), Map.of())));
      Stream<HolderLookup.RegistryLookup<?>> dynamicRegistries = newRegistries.values().stream().map((registryContents) -> registryContents.buildAsLookup(state.owner));
      HolderLookup.Provider patchOnlyRegistries = buildProviderWithContext(state.owner, context, dynamicRegistries);
      state.reportUnclaimedRegisteredValues();
      state.throwOnError();
      HolderLookup.Provider fullPatchedRegistries = this.createLazyFullPatchedRegistries(context, fallbackProvider, clonerFactory, newRegistries, patchOnlyRegistries);
      return new PatchedRegistries(fullPatchedRegistries, patchOnlyRegistries);
   }

   private static class LazyHolder<T> extends Holder.Reference<T> {
      private @Nullable Supplier<T> supplier;

      protected LazyHolder(final HolderOwner<T> owner, final @Nullable ResourceKey<T> key) {
         super(Holder.Reference.Type.STAND_ALONE, owner, key, (Object)null);
      }

      protected void bindValue(final T value) {
         super.bindValue(value);
         this.supplier = null;
      }

      public T value() {
         if (this.supplier != null) {
            this.bindValue(this.supplier.get());
         }

         return (T)super.value();
      }
   }

   private abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
      protected final HolderOwner<T> owner;

      protected EmptyTagLookup(final HolderOwner<T> owner) {
         super();
         this.owner = owner;
      }

      public Optional<HolderSet.Named<T>> get(final TagKey<T> id) {
         return Optional.of(HolderSet.emptyNamed(this.owner, id));
      }
   }

   private abstract static class EmptyTagRegistryLookup<T> extends EmptyTagLookup<T> implements HolderLookup.RegistryLookup<T> {
      protected EmptyTagRegistryLookup(final HolderOwner<T> owner) {
         super(owner);
      }

      public Stream<HolderSet.Named<T>> listTags() {
         throw new UnsupportedOperationException("Tags are not available in datagen");
      }
   }

   private static class EmptyTagLookupWrapper<T> extends EmptyTagRegistryLookup<T> implements HolderLookup.RegistryLookup.Delegate<T> {
      private final HolderLookup.RegistryLookup<T> parent;

      private EmptyTagLookupWrapper(final HolderOwner<T> owner, final HolderLookup.RegistryLookup<T> parent) {
         super(owner);
         this.parent = parent;
      }

      public HolderLookup.RegistryLookup<T> parent() {
         return this.parent;
      }
   }

   private static class UniversalOwner implements HolderOwner<Object> {
      private UniversalOwner() {
         super();
      }

      public <T> HolderOwner<T> cast() {
         return this;
      }
   }

   private static class UniversalLookup extends EmptyTagLookup<Object> {
      private final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();

      public UniversalLookup(final HolderOwner<Object> owner) {
         super(owner);
      }

      public Optional<Holder.Reference<Object>> get(final ResourceKey<Object> id) {
         return Optional.of(this.getOrCreate(id));
      }

      private <T> Holder.Reference<T> getOrCreate(final ResourceKey<T> id) {
         return (Holder.Reference)this.holders.computeIfAbsent(id, (k) -> Holder.Reference.createStandAlone(this.owner, k));
      }
   }

   private static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
      private RegisteredValue {
         super();
      }
   }

   private static record BuildState(UniversalOwner owner, UniversalLookup lookup, Map<Identifier, HolderGetter<?>> registries, Map<ResourceKey<?>, RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
      private BuildState {
         super();
      }

      public static BuildState create(final RegistryAccess context, final Stream<ResourceKey<? extends Registry<?>>> newRegistries) {
         UniversalOwner owner = new UniversalOwner();
         List<RuntimeException> errors = new ArrayList();
         UniversalLookup lookup = new UniversalLookup(owner);
         ImmutableMap.Builder<Identifier, HolderGetter<?>> registries = ImmutableMap.builder();
         context.registries().forEach((contextRegistry) -> registries.put(contextRegistry.key().identifier(), RegistrySetBuilder.wrapContextLookup(contextRegistry.value())));
         newRegistries.forEach((newRegistry) -> registries.put(newRegistry.identifier(), lookup));
         return new BuildState(owner, lookup, registries.build(), new HashMap(), errors);
      }

      public <T> BootstrapContext<T> bootstrapContext() {
         return new BootstrapContext<T>() {
            {
               Objects.requireNonNull(BuildState.this);
            }

            public Holder.Reference<T> register(final ResourceKey<T> key, final T value, final Lifecycle lifecycle) {
               RegisteredValue<?> previousValue = (RegisteredValue)BuildState.this.registeredValues.put(key, new RegisteredValue(value, lifecycle));
               if (previousValue != null) {
                  List var10000 = BuildState.this.errors;
                  String var10003 = String.valueOf(key);
                  var10000.add(new IllegalStateException("Duplicate registration for " + var10003 + ", new=" + String.valueOf(value) + ", old=" + String.valueOf(previousValue.value)));
               }

               return BuildState.this.lookup.<T>getOrCreate(key);
            }

            public <S> HolderGetter<S> lookup(final ResourceKey<? extends Registry<? extends S>> key) {
               return (HolderGetter)BuildState.this.registries.getOrDefault(key.identifier(), BuildState.this.lookup);
            }
         };
      }

      public void reportUnclaimedRegisteredValues() {
         this.registeredValues.forEach((key, registeredValue) -> {
            List var10000 = this.errors;
            String var10003 = String.valueOf(registeredValue.value);
            var10000.add(new IllegalStateException("Orpaned value " + var10003 + " for key " + String.valueOf(key)));
         });
      }

      public void reportNotCollectedHolders() {
         for(ResourceKey<Object> key : this.lookup.holders.keySet()) {
            this.errors.add(new IllegalStateException("Unreferenced key: " + String.valueOf(key)));
         }

      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException result = new IllegalStateException("Errors during registry creation");

            for(RuntimeException error : this.errors) {
               result.addSuppressed(error);
            }

            throw result;
         }
      }
   }

   private static record ValueAndHolder<T>(RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
      private ValueAndHolder {
         super();
      }
   }

   private static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBootstrap<T> bootstrap) {
      private RegistryStub {
         super();
      }

      private void apply(final BuildState state) {
         this.bootstrap.run(state.bootstrapContext());
      }

      public RegistryContents<T> collectRegisteredValues(final BuildState state) {
         Map<ResourceKey<T>, ValueAndHolder<T>> result = new HashMap();
         Iterator<Map.Entry<ResourceKey<?>, RegisteredValue<?>>> iterator = state.registeredValues.entrySet().iterator();

         while(iterator.hasNext()) {
            Map.Entry<ResourceKey<?>, RegisteredValue<?>> entry = (Map.Entry)iterator.next();
            ResourceKey<?> key = (ResourceKey)entry.getKey();
            if (key.isFor(this.key)) {
               RegisteredValue<T> value = (RegisteredValue)entry.getValue();
               Holder.Reference<T> holder = (Holder.Reference)state.lookup.holders.remove(key);
               result.put(key, new ValueAndHolder(value, Optional.ofNullable(holder)));
               iterator.remove();
            }
         }

         return new RegistryContents<T>(this.key, this.lifecycle, result);
      }
   }

   private static record RegistryContents<T>(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, ValueAndHolder<T>> values) {
      private RegistryContents {
         super();
      }

      public HolderLookup.RegistryLookup<T> buildAsLookup(final UniversalOwner owner) {
         Map<ResourceKey<T>, Holder.Reference<T>> entries = (Map)this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, (e) -> {
            ValueAndHolder<T> entry = (ValueAndHolder)e.getValue();
            Holder.Reference<T> holder = (Holder.Reference)entry.holder().orElseGet(() -> Holder.Reference.createStandAlone(owner.cast(), (ResourceKey)e.getKey()));
            holder.bindValue(entry.value().value());
            return holder;
         }));
         return RegistrySetBuilder.<T>lookupFromMap(this.key, this.lifecycle, owner.cast(), entries);
      }
   }

   public static record PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
      public PatchedRegistries {
         super();
      }
   }

   @FunctionalInterface
   public interface RegistryBootstrap<T> {
      void run(BootstrapContext<T> registry);
   }
}
