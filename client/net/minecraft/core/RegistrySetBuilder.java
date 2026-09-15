package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.core.registries.BootstrapRegistry;
import net.minecraft.core.registries.EmptyTagLookupWrapper;
import net.minecraft.core.registries.MultiRegistryBootstrap;
import net.minecraft.core.registries.PatchedRegistry;
import net.minecraft.core.registries.SingleRegistryBootstrap;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class RegistrySetBuilder {
   private final List<RegistryStub> entries = new ArrayList();

   public RegistrySetBuilder() {
      super();
   }

   private static RegistryStub placeholderStub(final ResourceKey<? extends Registry<?>> key) {
      return new RegistryStub() {
         public Stream<ResourceKey<? extends Registry<?>>> requiredRegistries() {
            return Stream.of(key);
         }

         public void apply(final BuildState state) {
         }
      };
   }

   public <T> RegistrySetBuilder add(final ResourceKey<? extends Registry<T>> key, final SingleRegistryBootstrap<T> bootstrap) {
      this.entries.add(new RegistryStub() {
         {
            Objects.requireNonNull(RegistrySetBuilder.this);
         }

         public Stream<ResourceKey<? extends Registry<?>>> requiredRegistries() {
            return Stream.of(key);
         }

         public void apply(final BuildState state) {
            bootstrap.run(state.createBootstrap(key));
         }
      });
      return this;
   }

   public RegistrySetBuilder add(final MultiRegistryBootstrap bootstrap) {
      this.entries.add(new RegistryStub() {
         {
            Objects.requireNonNull(RegistrySetBuilder.this);
         }

         public Stream<ResourceKey<? extends Registry<?>>> requiredRegistries() {
            return bootstrap.requestedRegistries().stream();
         }

         public void apply(final BuildState state) {
            MultiRegistryBootstrap var10000 = bootstrap;
            Objects.requireNonNull(state);
            var10000.run(state::createBootstrap);
         }
      });
      return this;
   }

   private static HolderLookup.Provider buildProviderWithContext(final HolderLookup.Provider context, final Stream<? extends HolderLookup.RegistryLookup<?>> newRegistries) {
      Map<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> lookups = new HashMap();
      context.listRegistries().forEach((contextRegistry) -> lookups.put(contextRegistry.key(), EmptyTagLookupWrapper.wrap(contextRegistry)));
      newRegistries.forEach((newRegistry) -> lookups.put(newRegistry.key(), EmptyTagLookupWrapper.wrap(newRegistry)));
      return HolderLookup.Provider.create(lookups.values().stream());
   }

   public HolderLookup.Provider build(final HolderLookup.Provider context) {
      BuildState state = RegistrySetBuilder.BuildState.createAndApply(context, this.entries);
      List<HolderLookup.RegistryLookup<?>> bootstrappedRegistries = new ArrayList(state.bootstrappedRegistries.size());

      for(BootstrappedRegistryState<?> newRegistry : state.bootstrappedRegistries.values()) {
         newRegistry.bindHolders();
         newRegistry.freeze();
         newRegistry.errorOnMissingHolders(state);
         bootstrappedRegistries.add(newRegistry.registry());
      }

      state.throwOnError();
      return buildProviderWithContext(context, bootstrappedRegistries.stream());
   }

   private static Set<ResourceKey<? extends Registry<?>>> findRegistriesMissingFromPatch(final HolderLookup.Provider contextRegistries, final HolderLookup.Provider baseRegistries, final List<RegistryStub> entries) {
      Set<? extends ResourceKey<? extends Registry<?>>> existingKeys = (Set)Stream.concat(newRegistryKeys(entries.stream()), contextRegistries.listRegistryKeys()).collect(Collectors.toSet());
      return (Set)baseRegistries.listRegistryKeys().filter((e) -> !existingKeys.contains(e)).collect(Collectors.toSet());
   }

   public PatchedRegistries buildPatch(final HolderLookup.Provider context, final HolderLookup.Provider fallbackProvider, final Cloner.Factory clonerFactory) {
      Set<ResourceKey<? extends Registry<?>>> missingFromPatch = findRegistriesMissingFromPatch(context, fallbackProvider, this.entries);
      List<RegistryStub> expandedEntries = Stream.concat(this.entries.stream(), missingFromPatch.stream().map(RegistrySetBuilder::placeholderStub)).toList();
      BuildState state = RegistrySetBuilder.BuildState.createAndApply(context, expandedEntries);
      List<HolderLookup.RegistryLookup<?>> bootstrappedRegistries = new ArrayList(state.bootstrappedRegistries.size());

      for(BootstrappedRegistryState<?> newRegistry : state.bootstrappedRegistries.values()) {
         newRegistry.bindHolders();
         newRegistry.validatePatchHolders(state, fallbackProvider);
         newRegistry.freeze();
         bootstrappedRegistries.add(newRegistry.registry());
      }

      HolderLookup.Provider patchOnlyRegistries = buildProviderWithContext(context, bootstrappedRegistries.stream());
      state.throwOnError();
      HolderLookup.Provider fullPatchedRegistries = EmptyTagLookupWrapper.wrap(PatchedRegistry.applyPatches(context, fallbackProvider, patchOnlyRegistries, clonerFactory, state.bootstrappedRegistries.keySet()));
      return new PatchedRegistries(fullPatchedRegistries, patchOnlyRegistries);
   }

   private static <T> ResourceKey<? extends Registry<T>> eyerollCast(final ResourceKey<? extends Registry<? extends T>> registryKey) {
      return registryKey;
   }

   private static Stream<ResourceKey<? extends Registry<?>>> newRegistryKeys(final Stream<RegistryStub> entries) {
      return entries.flatMap(RegistryStub::requiredRegistries).distinct();
   }

   private static record BootstrappedRegistryState<T>(BootstrapRegistry<T> registry, Map<ResourceKey<T>, T> registeredValues) {
      private BootstrappedRegistryState {
         super();
      }

      public static <T> BootstrappedRegistryState<T> create(final ResourceKey<? extends Registry<T>> key, final Lifecycle lifecycle) {
         BootstrapRegistry<T> newRegistry = new BootstrapRegistry<T>(key, lifecycle);
         return new BootstrappedRegistryState<T>(newRegistry, new HashMap());
      }

      public BootstrapContext<T> createBootstrapContext(final BuildState state) {
         return new BootstrapContext<T>() {
            {
               Objects.requireNonNull(BootstrappedRegistryState.this);
            }

            public Holder.Reference<T> register(final ResourceKey<T> key, final T value) {
               T previousValue = (T)BootstrappedRegistryState.this.registeredValues.put(key, value);
               if (previousValue != null) {
                  List var10000 = state.errors;
                  String var10003 = String.valueOf(key);
                  var10000.add(new IllegalStateException("Duplicate registration for " + var10003 + ", new=" + String.valueOf(value) + ", old=" + String.valueOf(previousValue)));
               }

               return BootstrappedRegistryState.this.registry.getOrThrow(key);
            }

            public <S> HolderGetter<S> lookup(final ResourceKey<? extends Registry<? extends S>> key) {
               return state.allRegistries.lookupOrThrow(key);
            }

            public <S> Stream<Holder.Reference<S>> listContextElements(final ResourceKey<? extends Registry<? extends S>> key) {
               return state.contextRegistries.lookupOrThrow(key).listElements();
            }
         };
      }

      public void bindHolders() {
         this.registeredValues.forEach((key, value) -> this.registry.getOrThrow(key).bindValue(value));
      }

      public void freeze() {
         this.registry.freeze();
      }

      public void errorOnMissingHolders(final BuildState state) {
         this.registry.listElements().forEach((element) -> {
            if (!element.isBound()) {
               state.errors().add(new IllegalStateException("No value registered for key " + String.valueOf(element.key().identifier())));
            }

         });
      }

      public void validatePatchHolders(final BuildState state, final HolderLookup.Provider fallback) {
         HolderLookup<T> baseRegistry = fallback.lookupOrThrow(this.registry.key());
         this.registry.removeIf((element) -> {
            if (element.isBound()) {
               return false;
            } else {
               if (baseRegistry.get(element.key()).isEmpty()) {
                  state.errors().add(new IllegalStateException("Value " + String.valueOf(element.key().identifier()) + " referenced by patched element is not present in base"));
               }

               return true;
            }
         });
      }
   }

   private static record BuildState(HolderLookup.Provider contextRegistries, HolderLookup.Provider allRegistries, Map<ResourceKey<? extends Registry<?>>, BootstrappedRegistryState<?>> bootstrappedRegistries, List<RuntimeException> errors) {
      private BuildState {
         super();
      }

      public static BuildState createAndApply(final HolderLookup.Provider context, final List<RegistryStub> entries) {
         BuildState state = create(context, entries);
         entries.forEach((e) -> e.apply(state));
         return state;
      }

      private static BuildState create(final HolderLookup.Provider context, final List<RegistryStub> entries) {
         List<RuntimeException> errors = new ArrayList();
         ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, HolderLookup.RegistryLookup<?>> allRegistries = ImmutableMap.builder();
         ImmutableMap.Builder<ResourceKey<? extends Registry<?>>, BootstrappedRegistryState<?>> bootstrappedRegistries = ImmutableMap.builder();
         context.listRegistries().forEach((contextRegistry) -> allRegistries.put(contextRegistry.key(), EmptyTagLookupWrapper.wrap(contextRegistry)));
         RegistrySetBuilder.newRegistryKeys(entries.stream()).forEach((newRegistryKey) -> {
            BootstrappedRegistryState<?> newRegistryEntry = RegistrySetBuilder.BootstrappedRegistryState.create(RegistrySetBuilder.eyerollCast(newRegistryKey), Lifecycle.stable());
            BootstrapRegistry<?> newRegistry = newRegistryEntry.registry();
            bootstrappedRegistries.put(newRegistry.key(), newRegistryEntry);
            allRegistries.put(newRegistry.key(), newRegistry);
         });
         return new BuildState(context, HolderLookup.Provider.create(allRegistries.build().values().stream()), bootstrappedRegistries.build(), errors);
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

      public <T> BootstrapContext<T> createBootstrap(final ResourceKey<? extends Registry<T>> key) {
         BootstrappedRegistryState<T> targetRegistry = (BootstrappedRegistryState)Objects.requireNonNull((BootstrappedRegistryState)this.bootstrappedRegistries.get(key), () -> "No registry named " + String.valueOf(key.identifier()));
         return targetRegistry.createBootstrapContext(this);
      }
   }

   public static record PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
      public PatchedRegistries {
         super();
      }
   }

   private interface RegistryStub {
      Stream<ResourceKey<? extends Registry<?>>> requiredRegistries();

      void apply(BuildState state);
   }
}
