package net.minecraft.core.component;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class DataComponentInitializers {
   private final List<InitializerEntry<?>> initializers = new ArrayList();

   public DataComponentInitializers() {
      super();
   }

   public <T> void add(final ResourceKey<T> key, final Initializer<T> initializer) {
      this.initializers.add(new InitializerEntry(key, initializer));
   }

   private Map<ResourceKey<?>, DataComponentMap.Builder> runInitializers(final HolderLookup.Provider context) {
      Map<ResourceKey<?>, DataComponentMap.Builder> results = new HashMap();

      for(InitializerEntry<?> initializer : this.initializers) {
         DataComponentMap.Builder builder = (DataComponentMap.Builder)results.computeIfAbsent(initializer.key, (k) -> DataComponentMap.builder());
         initializer.run(builder, context);
      }

      return results;
   }

   private static <T> void registryEmpty(final Map<ResourceKey<? extends Registry<?>>, PendingComponentBuilders<?>> buildersByRegistry, final ResourceKey<? extends Registry<? extends T>> registryKey) {
      buildersByRegistry.put(registryKey, new PendingComponentBuilders(registryKey, new HashMap()));
   }

   private static <T> void addBuilder(final Map<ResourceKey<? extends Registry<?>>, PendingComponentBuilders<?>> buildersByRegistry, final ResourceKey<T> key, final DataComponentMap.Builder builder) {
      PendingComponentBuilders<T> buildersForRegistry = (PendingComponentBuilders)buildersByRegistry.get(key.registryKey());
      buildersForRegistry.builders.put(key, builder);
   }

   public List<PendingComponents<?>> build(final HolderLookup.Provider context) {
      Map<ResourceKey<? extends Registry<?>>, PendingComponentBuilders<?>> buildersByRegistry = new HashMap();
      context.listRegistryKeys().forEach((registryKey) -> registryEmpty(buildersByRegistry, registryKey));
      this.runInitializers(context).forEach((key, builder) -> addBuilder(buildersByRegistry, key, builder));
      return (List)buildersByRegistry.values().stream().map((elementBuilders) -> createInitializerForRegistry(context, elementBuilders)).collect(Collectors.toUnmodifiableList());
   }

   private static <T> PendingComponents<T> createInitializerForRegistry(final HolderLookup.Provider context, final PendingComponentBuilders<T> elementBuilders) {
      final List<BakedEntry<T>> entries = new ArrayList();
      final ResourceKey<? extends Registry<T>> registryKey = elementBuilders.registryKey;
      HolderLookup.RegistryLookup<T> registry = context.lookupOrThrow(registryKey);
      Set<Holder.Reference<T>> elementsWithComponents = Sets.newIdentityHashSet();
      elementBuilders.builders.forEach((elementKey, elementBuilder) -> {
         Holder.Reference<T> element = registry.getOrThrow(elementKey);
         DataComponentMap components = elementBuilder.build();
         entries.add(new BakedEntry(element, components));
         elementsWithComponents.add(element);
      });
      registry.listElements().filter((e) -> !elementsWithComponents.contains(e)).forEach((elementWithoutComponents) -> entries.add(new BakedEntry(elementWithoutComponents, DataComponentMap.EMPTY)));
      return new PendingComponents<T>() {
         public ResourceKey<? extends Registry<? extends T>> key() {
            return registryKey;
         }

         public void forEach(final BiConsumer<Holder.Reference<T>, DataComponentMap> output) {
            entries.forEach((e) -> output.accept(e.element, e.components));
         }

         public void apply() {
            entries.forEach(BakedEntry::apply);
         }
      };
   }

   private static record InitializerEntry<T>(ResourceKey<T> key, Initializer<T> initializer) {
      private InitializerEntry {
         super();
      }

      public void run(final DataComponentMap.Builder components, final HolderLookup.Provider context) {
         this.initializer.run(components, context, this.key);
      }
   }

   private static record BakedEntry<T>(Holder.Reference<T> element, DataComponentMap components) {
      private BakedEntry {
         super();
      }

      public void apply() {
         this.element.bindComponents(this.components);
      }
   }

   @FunctionalInterface
   public interface Initializer<T> {
      void run(DataComponentMap.Builder components, HolderLookup.Provider context, ResourceKey<T> key);

      default Initializer<T> andThen(final Initializer<T> other) {
         return (components, context, key) -> {
            this.run(components, context, key);
            other.run(components, context, key);
         };
      }

      default <C> Initializer<T> add(final DataComponentType<C> type, final C value) {
         return this.andThen((components, context, key) -> components.set(type, value));
      }
   }

   @FunctionalInterface
   public interface SingleComponentInitializer<C> {
      C create(HolderLookup.Provider context);

      default <T> Initializer<T> asInitializer(final DataComponentType<C> type) {
         return (components, context, key) -> components.set(type, this.create(context));
      }
   }

   private static record PendingComponentBuilders<T>(ResourceKey<? extends Registry<T>> registryKey, Map<ResourceKey<T>, DataComponentMap.Builder> builders) {
      private PendingComponentBuilders {
         super();
      }
   }

   public interface PendingComponents<T> {
      ResourceKey<? extends Registry<? extends T>> key();

      void forEach(BiConsumer<Holder.Reference<T>, DataComponentMap> output);

      void apply();
   }
}
