package net.minecraft.core;

import com.google.common.collect.ImmutableMap;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;

public interface RegistryAccess extends HolderLookup.Provider {
   Logger LOGGER = LogUtils.getLogger();
   RegistryAccess.Frozen EMPTY = new RegistryAccess.ImmutableRegistryAccess(Map.of()).freeze();

   <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1);

   @Override
   default <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> var1) {
      return this.registry(var1).map(Registry::asLookup);
   }

   default <E> Registry<E> registryOrThrow(ResourceKey<? extends Registry<? extends E>> var1) {
      return this.<E>registry(var1).orElseThrow(() -> new IllegalStateException("Missing registry: " + var1));
   }

   Stream<RegistryAccess.RegistryEntry<?>> registries();

   @Override
   default Stream<ResourceKey<? extends Registry<?>>> listRegistryKeys() {
      return this.registries().map(var0 -> var0.key);
   }

   static RegistryAccess.Frozen fromRegistryOfRegistries(final Registry<? extends Registry<?>> var0) {
      return new RegistryAccess.Frozen() {
         @Override
         public <T> Optional<Registry<T>> registry(ResourceKey<? extends Registry<? extends T>> var1) {
            Registry var2 = var0;
            return var2.getOptional(var1);
         }

         @Override
         public Stream<RegistryAccess.RegistryEntry<?>> registries() {
            return var0.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
         }

         @Override
         public RegistryAccess.Frozen freeze() {
            return this;
         }
      };
   }

   default RegistryAccess.Frozen freeze() {
      class 1FrozenAccess extends RegistryAccess.ImmutableRegistryAccess implements RegistryAccess.Frozen {
         protected _FrozenAccess/* $VF was: 1FrozenAccess*/(final Stream<RegistryAccess.RegistryEntry<?>> nullx) {
            super(nullx);
         }
      }

      return new 1FrozenAccess(this.registries().map(RegistryAccess.RegistryEntry::freeze));
   }

   public interface Frozen extends RegistryAccess {
   }

   public static class ImmutableRegistryAccess implements RegistryAccess {
      private final Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

      public ImmutableRegistryAccess(List<? extends Registry<?>> var1) {
         super();
         this.registries = var1.stream().collect(Collectors.toUnmodifiableMap(Registry::key, var0 -> (Registry<?>)var0));
      }

      public ImmutableRegistryAccess(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> var1) {
         super();
         this.registries = Map.copyOf(var1);
      }

      public ImmutableRegistryAccess(Stream<RegistryAccess.RegistryEntry<?>> var1) {
         super();
         this.registries = var1.collect(ImmutableMap.toImmutableMap(RegistryAccess.RegistryEntry::key, RegistryAccess.RegistryEntry::value));
      }

      @Override
      public <E> Optional<Registry<E>> registry(ResourceKey<? extends Registry<? extends E>> var1) {
         return Optional.ofNullable(this.registries.get(var1)).map(var0 -> (Registry<E>)var0);
      }

      @Override
      public Stream<RegistryAccess.RegistryEntry<?>> registries() {
         return this.registries.entrySet().stream().map(RegistryAccess.RegistryEntry::fromMapEntry);
      }
   }

// $VF: Couldn't be decompiled
// Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
// java.lang.NullPointerException
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.isExprentIndependent(InitializerProcessor.java:423)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractDynamicInitializers(InitializerProcessor.java:335)
//   at org.jetbrains.java.decompiler.main.InitializerProcessor.extractInitializers(InitializerProcessor.java:44)
//   at org.jetbrains.java.decompiler.main.ClassWriter.invokeProcessors(ClassWriter.java:97)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:348)
//   at org.jetbrains.java.decompiler.main.ClassWriter.writeClass(ClassWriter.java:492)
//   at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:474)
//   at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:191)
//   at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:187)
}
