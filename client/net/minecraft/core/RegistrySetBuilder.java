package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
   private final List<RegistryStub<?>> entries = new ArrayList();

   public RegistrySetBuilder() {
      super();
   }

   static <T> HolderGetter<T> wrapContextLookup(final HolderLookup.RegistryLookup<T> var0) {
      return new EmptyTagLookup<T>(var0) {
         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1) {
            return var0.get(var1);
         }
      };
   }

   static <T> HolderLookup.RegistryLookup<T> lookupFromMap(final ResourceKey<? extends Registry<? extends T>> var0, final Lifecycle var1, HolderOwner<T> var2, final Map<ResourceKey<T>, Holder.Reference<T>> var3) {
      return new EmptyTagRegistryLookup<T>(var2) {
         public ResourceKey<? extends Registry<? extends T>> key() {
            return var0;
         }

         public Lifecycle registryLifecycle() {
            return var1;
         }

         public Optional<Holder.Reference<T>> get(ResourceKey<T> var1x) {
            return Optional.ofNullable((Holder.Reference)var3.get(var1x));
         }

         public Stream<Holder.Reference<T>> listElements() {
            return var3.values().stream();
         }
      };
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, RegistryBootstrap<T> var3) {
      this.entries.add(new RegistryStub(var1, var2, var3));
      return this;
   }

   public <T> RegistrySetBuilder add(ResourceKey<? extends Registry<T>> var1, RegistryBootstrap<T> var2) {
      return this.add(var1, Lifecycle.stable(), var2);
   }

   private BuildState createState(RegistryAccess var1) {
      BuildState var2 = RegistrySetBuilder.BuildState.create(var1, this.entries.stream().map(RegistryStub::key));
      this.entries.forEach((var1x) -> {
         var1x.apply(var2);
      });
      return var2;
   }

   private static HolderLookup.Provider buildProviderWithContext(UniversalOwner var0, RegistryAccess var1, Stream<HolderLookup.RegistryLookup<?>> var2) {
      final HashMap var3 = new HashMap();
      var1.registries().forEach((var1x) -> {
         var3.put(var1x.key(), 1Entry.createForContextRegistry(var1x.value()));
      });
      var2.forEach((var2x) -> {
         var3.put(var2x.key(), 1Entry.createForNewRegistry(var0, var2x));
      });
      return new HolderLookup.Provider() {
         public Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
            return var3.keySet().stream();
         }

         <T> Optional<1Entry<T>> getEntry(ResourceKey<? extends Registry<? extends T>> var1) {
            return Optional.ofNullable((1Entry)var3.get(var1));
         }

         public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
            return this.getEntry(var1).map(1Entry::lookup);
         }

         public <V> RegistryOps<V> createSerializationContext(DynamicOps<V> var1) {
            return RegistryOps.create(var1, new RegistryOps.RegistryInfoLookup() {
               public <T> Optional<RegistryOps.RegistryInfo<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
                  return getEntry(var1).map(1Entry::opsInfo);
               }
            });
         }
      };

      record 1Entry<T>(HolderLookup.RegistryLookup<T> lookup, RegistryOps.RegistryInfo<T> opsInfo) {
         _Entry/* $FF was: 1Entry*/(HolderLookup.RegistryLookup<T> var1, RegistryOps.RegistryInfo<T> var2) {
            super();
            this.lookup = var1;
            this.opsInfo = var2;
         }

         public static <T> 1Entry<T> createForContextRegistry(HolderLookup.RegistryLookup<T> var0) {
            return new 1Entry(new EmptyTagLookupWrapper(var0, var0), RegistryOps.RegistryInfo.fromRegistryLookup(var0));
         }

         public static <T> 1Entry<T> createForNewRegistry(UniversalOwner var0, HolderLookup.RegistryLookup<T> var1) {
            return new 1Entry(new EmptyTagLookupWrapper(var0.cast(), var1), new RegistryOps.RegistryInfo(var0.cast(), var1, var1.registryLifecycle()));
         }

         public HolderLookup.RegistryLookup<T> lookup() {
            return this.lookup;
         }

         public RegistryOps.RegistryInfo<T> opsInfo() {
            return this.opsInfo;
         }
      }

   }

   public HolderLookup.Provider build(RegistryAccess var1) {
      BuildState var2 = this.createState(var1);
      Stream var3 = this.entries.stream().map((var1x) -> {
         return var1x.collectRegisteredValues(var2).buildAsLookup(var2.owner);
      });
      HolderLookup.Provider var4 = buildProviderWithContext(var2.owner, var1, var3);
      var2.reportNotCollectedHolders();
      var2.reportUnclaimedRegisteredValues();
      var2.throwOnError();
      return var4;
   }

   private HolderLookup.Provider createLazyFullPatchedRegistries(RegistryAccess var1, HolderLookup.Provider var2, Cloner.Factory var3, Map<ResourceKey<? extends Registry<?>>, RegistryContents<?>> var4, HolderLookup.Provider var5) {
      UniversalOwner var6 = new UniversalOwner();
      MutableObject var7 = new MutableObject();
      List var8 = (List)var4.keySet().stream().map((var6x) -> {
         return this.createLazyFullPatchedRegistries(var6, var3, var6x, var5, var2, var7);
      }).collect(Collectors.toUnmodifiableList());
      HolderLookup.Provider var9 = buildProviderWithContext(var6, var1, var8.stream());
      var7.setValue(var9);
      return var9;
   }

   private <T> HolderLookup.RegistryLookup<T> createLazyFullPatchedRegistries(HolderOwner<T> var1, Cloner.Factory var2, ResourceKey<? extends Registry<? extends T>> var3, HolderLookup.Provider var4, HolderLookup.Provider var5, MutableObject<HolderLookup.Provider> var6) {
      Cloner var7 = var2.cloner(var3);
      if (var7 == null) {
         throw new NullPointerException("No cloner for " + String.valueOf(var3.location()));
      } else {
         HashMap var8 = new HashMap();
         HolderLookup.RegistryLookup var9 = var4.lookupOrThrow(var3);
         var9.listElements().forEach((var5x) -> {
            ResourceKey var6x = var5x.key();
            LazyHolder var7x = new LazyHolder(var1, var6x);
            var7x.supplier = () -> {
               return var7.clone(var5x.value(), var4, (HolderLookup.Provider)var6.getValue());
            };
            var8.put(var6x, var7x);
         });
         HolderLookup.RegistryLookup var10 = var5.lookupOrThrow(var3);
         var10.listElements().forEach((var5x) -> {
            ResourceKey var6x = var5x.key();
            var8.computeIfAbsent(var6x, (var6xx) -> {
               LazyHolder var7x = new LazyHolder(var1, var6x);
               var7x.supplier = () -> {
                  return var7.clone(var5x.value(), var5, (HolderLookup.Provider)var6.getValue());
               };
               return var7x;
            });
         });
         Lifecycle var11 = var9.registryLifecycle().add(var10.registryLifecycle());
         return lookupFromMap(var3, var11, var1, var8);
      }
   }

   public PatchedRegistries buildPatch(RegistryAccess var1, HolderLookup.Provider var2, Cloner.Factory var3) {
      BuildState var4 = this.createState(var1);
      HashMap var5 = new HashMap();
      this.entries.stream().map((var1x) -> {
         return var1x.collectRegisteredValues(var4);
      }).forEach((var1x) -> {
         var5.put(var1x.key, var1x);
      });
      Set var6 = (Set)var1.listRegistryKeys().collect(Collectors.toUnmodifiableSet());
      var2.listRegistryKeys().filter((var1x) -> {
         return !var6.contains(var1x);
      }).forEach((var1x) -> {
         var5.putIfAbsent(var1x, new RegistryContents(var1x, Lifecycle.stable(), Map.of()));
      });
      Stream var7 = var5.values().stream().map((var1x) -> {
         return var1x.buildAsLookup(var4.owner);
      });
      HolderLookup.Provider var8 = buildProviderWithContext(var4.owner, var1, var7);
      var4.reportUnclaimedRegisteredValues();
      var4.throwOnError();
      HolderLookup.Provider var9 = this.createLazyFullPatchedRegistries(var1, var2, var3, var5, var8);
      return new PatchedRegistries(var9, var8);
   }

   private static record RegistryStub<T>(ResourceKey<? extends Registry<T>> key, Lifecycle lifecycle, RegistryBootstrap<T> bootstrap) {
      RegistryStub(ResourceKey<? extends Registry<T>> var1, Lifecycle var2, RegistryBootstrap<T> var3) {
         super();
         this.key = var1;
         this.lifecycle = var2;
         this.bootstrap = var3;
      }

      void apply(BuildState var1) {
         this.bootstrap.run(var1.bootstrapContext());
      }

      public RegistryContents<T> collectRegisteredValues(BuildState var1) {
         HashMap var2 = new HashMap();
         Iterator var3 = var1.registeredValues.entrySet().iterator();

         while(var3.hasNext()) {
            Map.Entry var4 = (Map.Entry)var3.next();
            ResourceKey var5 = (ResourceKey)var4.getKey();
            if (var5.isFor(this.key)) {
               RegisteredValue var7 = (RegisteredValue)var4.getValue();
               Holder.Reference var8 = (Holder.Reference)var1.lookup.holders.remove(var5);
               var2.put(var5, new ValueAndHolder(var7, Optional.ofNullable(var8)));
               var3.remove();
            }
         }

         return new RegistryContents(this.key, this.lifecycle, var2);
      }

      public ResourceKey<? extends Registry<T>> key() {
         return this.key;
      }

      public Lifecycle lifecycle() {
         return this.lifecycle;
      }

      public RegistryBootstrap<T> bootstrap() {
         return this.bootstrap;
      }
   }

   @FunctionalInterface
   public interface RegistryBootstrap<T> {
      void run(BootstrapContext<T> var1);
   }

   private static record BuildState(UniversalOwner owner, UniversalLookup lookup, Map<ResourceLocation, HolderGetter<?>> registries, Map<ResourceKey<?>, RegisteredValue<?>> registeredValues, List<RuntimeException> errors) {
      final UniversalOwner owner;
      final UniversalLookup lookup;
      final Map<ResourceLocation, HolderGetter<?>> registries;
      final Map<ResourceKey<?>, RegisteredValue<?>> registeredValues;
      final List<RuntimeException> errors;

      private BuildState(UniversalOwner var1, UniversalLookup var2, Map<ResourceLocation, HolderGetter<?>> var3, Map<ResourceKey<?>, RegisteredValue<?>> var4, List<RuntimeException> var5) {
         super();
         this.owner = var1;
         this.lookup = var2;
         this.registries = var3;
         this.registeredValues = var4;
         this.errors = var5;
      }

      public static BuildState create(RegistryAccess var0, Stream<ResourceKey<? extends Registry<?>>> var1) {
         UniversalOwner var2 = new UniversalOwner();
         ArrayList var3 = new ArrayList();
         UniversalLookup var4 = new UniversalLookup(var2);
         ImmutableMap.Builder var5 = ImmutableMap.builder();
         var0.registries().forEach((var1x) -> {
            var5.put(var1x.key().location(), RegistrySetBuilder.wrapContextLookup(var1x.value()));
         });
         var1.forEach((var2x) -> {
            var5.put(var2x.location(), var4);
         });
         return new BuildState(var2, var4, var5.build(), new HashMap(), var3);
      }

      public <T> BootstrapContext<T> bootstrapContext() {
         return new BootstrapContext<T>() {
            public Holder.Reference<T> register(ResourceKey<T> var1, T var2, Lifecycle var3) {
               RegisteredValue var4 = (RegisteredValue)BuildState.this.registeredValues.put(var1, new RegisteredValue(var2, var3));
               if (var4 != null) {
                  List var10000 = BuildState.this.errors;
                  String var10003 = String.valueOf(var1);
                  var10000.add(new IllegalStateException("Duplicate registration for " + var10003 + ", new=" + String.valueOf(var2) + ", old=" + String.valueOf(var4.value)));
               }

               return BuildState.this.lookup.getOrCreate(var1);
            }

            public <S> HolderGetter<S> lookup(ResourceKey<? extends Registry<? extends S>> var1) {
               return (HolderGetter)BuildState.this.registries.getOrDefault(var1.location(), BuildState.this.lookup);
            }
         };
      }

      public void reportUnclaimedRegisteredValues() {
         this.registeredValues.forEach((var1, var2) -> {
            List var10000 = this.errors;
            String var10003 = String.valueOf(var2.value);
            var10000.add(new IllegalStateException("Orpaned value " + var10003 + " for key " + String.valueOf(var1)));
         });
      }

      public void reportNotCollectedHolders() {
         Iterator var1 = this.lookup.holders.keySet().iterator();

         while(var1.hasNext()) {
            ResourceKey var2 = (ResourceKey)var1.next();
            this.errors.add(new IllegalStateException("Unreferenced key: " + String.valueOf(var2)));
         }

      }

      public void throwOnError() {
         if (!this.errors.isEmpty()) {
            IllegalStateException var1 = new IllegalStateException("Errors during registry creation");
            Iterator var2 = this.errors.iterator();

            while(var2.hasNext()) {
               RuntimeException var3 = (RuntimeException)var2.next();
               var1.addSuppressed(var3);
            }

            throw var1;
         }
      }

      public UniversalOwner owner() {
         return this.owner;
      }

      public UniversalLookup lookup() {
         return this.lookup;
      }

      public Map<ResourceLocation, HolderGetter<?>> registries() {
         return this.registries;
      }

      public Map<ResourceKey<?>, RegisteredValue<?>> registeredValues() {
         return this.registeredValues;
      }

      public List<RuntimeException> errors() {
         return this.errors;
      }
   }

   private static class UniversalOwner implements HolderOwner<Object> {
      UniversalOwner() {
         super();
      }

      public <T> HolderOwner<T> cast() {
         return this;
      }
   }

   public static record PatchedRegistries(HolderLookup.Provider full, HolderLookup.Provider patches) {
      public PatchedRegistries(HolderLookup.Provider var1, HolderLookup.Provider var2) {
         super();
         this.full = var1;
         this.patches = var2;
      }

      public HolderLookup.Provider full() {
         return this.full;
      }

      public HolderLookup.Provider patches() {
         return this.patches;
      }
   }

   private static record RegistryContents<T>(ResourceKey<? extends Registry<? extends T>> key, Lifecycle lifecycle, Map<ResourceKey<T>, ValueAndHolder<T>> values) {
      final ResourceKey<? extends Registry<? extends T>> key;

      RegistryContents(ResourceKey<? extends Registry<? extends T>> var1, Lifecycle var2, Map<ResourceKey<T>, ValueAndHolder<T>> var3) {
         super();
         this.key = var1;
         this.lifecycle = var2;
         this.values = var3;
      }

      public HolderLookup.RegistryLookup<T> buildAsLookup(UniversalOwner var1) {
         Map var2 = (Map)this.values.entrySet().stream().collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, (var1x) -> {
            ValueAndHolder var2 = (ValueAndHolder)var1x.getValue();
            Holder.Reference var3 = (Holder.Reference)var2.holder().orElseGet(() -> {
               return Holder.Reference.createStandAlone(var1.cast(), (ResourceKey)var1x.getKey());
            });
            var3.bindValue(var2.value().value());
            return var3;
         }));
         return RegistrySetBuilder.lookupFromMap(this.key, this.lifecycle, var1.cast(), var2);
      }

      public ResourceKey<? extends Registry<? extends T>> key() {
         return this.key;
      }

      public Lifecycle lifecycle() {
         return this.lifecycle;
      }

      public Map<ResourceKey<T>, ValueAndHolder<T>> values() {
         return this.values;
      }
   }

   private static class LazyHolder<T> extends Holder.Reference<T> {
      @Nullable
      Supplier<T> supplier;

      protected LazyHolder(HolderOwner<T> var1, @Nullable ResourceKey<T> var2) {
         super(Holder.Reference.Type.STAND_ALONE, var1, var2, (Object)null);
      }

      protected void bindValue(T var1) {
         super.bindValue(var1);
         this.supplier = null;
      }

      public T value() {
         if (this.supplier != null) {
            this.bindValue(this.supplier.get());
         }

         return super.value();
      }
   }

   private static record ValueAndHolder<T>(RegisteredValue<T> value, Optional<Holder.Reference<T>> holder) {
      ValueAndHolder(RegisteredValue<T> var1, Optional<Holder.Reference<T>> var2) {
         super();
         this.value = var1;
         this.holder = var2;
      }

      public RegisteredValue<T> value() {
         return this.value;
      }

      public Optional<Holder.Reference<T>> holder() {
         return this.holder;
      }
   }

   private static record RegisteredValue<T>(T value, Lifecycle lifecycle) {
      final T value;

      RegisteredValue(T var1, Lifecycle var2) {
         super();
         this.value = var1;
         this.lifecycle = var2;
      }

      public T value() {
         return this.value;
      }

      public Lifecycle lifecycle() {
         return this.lifecycle;
      }
   }

   private static class UniversalLookup extends EmptyTagLookup<Object> {
      final Map<ResourceKey<Object>, Holder.Reference<Object>> holders = new HashMap();

      public UniversalLookup(HolderOwner<Object> var1) {
         super(var1);
      }

      public Optional<Holder.Reference<Object>> get(ResourceKey<Object> var1) {
         return Optional.of(this.getOrCreate(var1));
      }

      <T> Holder.Reference<T> getOrCreate(ResourceKey<T> var1) {
         return (Holder.Reference)this.holders.computeIfAbsent(var1, (var1x) -> {
            return Holder.Reference.createStandAlone(this.owner, var1x);
         });
      }
   }

   private static class EmptyTagLookupWrapper<T> extends EmptyTagRegistryLookup<T> implements HolderLookup.RegistryLookup.Delegate<T> {
      private final HolderLookup.RegistryLookup<T> parent;

      EmptyTagLookupWrapper(HolderOwner<T> var1, HolderLookup.RegistryLookup<T> var2) {
         super(var1);
         this.parent = var2;
      }

      public HolderLookup.RegistryLookup<T> parent() {
         return this.parent;
      }
   }

   private abstract static class EmptyTagRegistryLookup<T> extends EmptyTagLookup<T> implements HolderLookup.RegistryLookup<T> {
      protected EmptyTagRegistryLookup(HolderOwner<T> var1) {
         super(var1);
      }

      public Stream<HolderSet.Named<T>> listTags() {
         throw new UnsupportedOperationException("Tags are not available in datagen");
      }
   }

   private abstract static class EmptyTagLookup<T> implements HolderGetter<T> {
      protected final HolderOwner<T> owner;

      protected EmptyTagLookup(HolderOwner<T> var1) {
         super();
         this.owner = var1;
      }

      public Optional<HolderSet.Named<T>> get(TagKey<T> var1) {
         return Optional.of(HolderSet.emptyNamed(this.owner, var1));
      }
   }
}
