package net.minecraft.tags;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.SequencedSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.DependencySorter;
import net.minecraft.util.StrictJsonParser;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class TagLoader<T> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ElementLookup<T> elementLookup;
   private final String directory;

   public TagLoader(final ElementLookup<T> elementLookup, final String directory) {
      super();
      this.elementLookup = elementLookup;
      this.directory = directory;
   }

   public Map<Identifier, List<EntryWithSource>> load(final ResourceManager resourceManager) {
      Map<Identifier, List<EntryWithSource>> builders = new HashMap();
      FileToIdConverter lister = FileToIdConverter.json(this.directory);

      for(Map.Entry<Identifier, List<Resource>> entry : lister.listMatchingResourceStacks(resourceManager).entrySet()) {
         Identifier location = (Identifier)entry.getKey();
         Identifier id = lister.fileToId(location);

         for(Resource resource : (List)entry.getValue()) {
            try {
               Reader reader = resource.openAsReader();

               try {
                  JsonElement element = StrictJsonParser.parse(reader);
                  List<EntryWithSource> tagContents = (List)builders.computeIfAbsent(id, (key) -> new ArrayList());
                  TagFile parsedContents = (TagFile)TagFile.CODEC.parse(new Dynamic(JsonOps.INSTANCE, element)).getOrThrow();
                  if (parsedContents.replace()) {
                     tagContents.clear();
                  }

                  String sourceId = resource.sourcePackId();
                  parsedContents.entries().forEach((ex) -> tagContents.add(new EntryWithSource(ex, sourceId)));
               } catch (Throwable var16) {
                  if (reader != null) {
                     try {
                        reader.close();
                     } catch (Throwable var15) {
                        var16.addSuppressed(var15);
                     }
                  }

                  throw var16;
               }

               if (reader != null) {
                  reader.close();
               }
            } catch (Exception e) {
               LOGGER.error("Couldn't read tag list {} from {} in data pack {}", new Object[]{id, location, resource.sourcePackId(), e});
            }
         }
      }

      return builders;
   }

   private Either<List<EntryWithSource>, List<T>> tryBuildTag(final TagEntry.Lookup<T> lookup, final List<EntryWithSource> entries) {
      SequencedSet<T> values = new LinkedHashSet();
      List<EntryWithSource> missingElements = new ArrayList();

      for(EntryWithSource entry : entries) {
         TagEntry var10000 = entry.entry();
         Objects.requireNonNull(values);
         if (!var10000.build(lookup, values::add)) {
            missingElements.add(entry);
         }
      }

      return missingElements.isEmpty() ? Either.right(List.copyOf(values)) : Either.left(missingElements);
   }

   public Map<Identifier, List<T>> build(final Map<Identifier, List<EntryWithSource>> builders) {
      final Map<Identifier, List<T>> newTags = new HashMap();
      TagEntry.Lookup<T> lookup = new TagEntry.Lookup<T>() {
         {
            Objects.requireNonNull(TagLoader.this);
         }

         public @Nullable T element(final Identifier key, final boolean required) {
            return (T)TagLoader.this.elementLookup.get(key, required).orElse((Object)null);
         }

         public @Nullable Collection<T> tag(final Identifier key) {
            return (Collection)newTags.get(key);
         }
      };
      DependencySorter<Identifier, SortingEntry> sorter = new DependencySorter<Identifier, SortingEntry>();
      builders.forEach((id, entry) -> sorter.addEntry(id, new SortingEntry(entry)));
      sorter.orderByDependencies((id, contents) -> this.tryBuildTag(lookup, contents.entries).ifLeft((missing) -> LOGGER.error("Couldn't load tag {} as it is missing following references: {}", id, missing.stream().map(Objects::toString).collect(Collectors.joining(", ")))).ifRight((tag) -> newTags.put(id, tag)));
      return newTags;
   }

   public static <T> Map<TagKey<T>, List<Holder<T>>> loadTagsFromNetwork(final TagNetworkSerialization.NetworkPayload tags, final Registry<T> registry) {
      return tags.resolve(registry).tags;
   }

   public static List<Registry.PendingTags<?>> loadTagsForExistingRegistries(final ResourceManager manager, final RegistryAccess layer) {
      return (List)layer.registries().map((entry) -> loadPendingTags(manager, entry.value())).flatMap(Optional::stream).collect(Collectors.toUnmodifiableList());
   }

   public static <T> void loadTagsForRegistry(final ResourceManager manager, final WritableRegistry<T> registry) {
      loadTagsForRegistry(manager, registry.key(), TagLoader.ElementLookup.fromWritableRegistry(registry));
   }

   public static <T> Map<TagKey<T>, List<Holder<T>>> loadTagsForRegistry(final ResourceManager manager, final ResourceKey<? extends Registry<T>> registryKey, final ElementLookup<Holder<T>> lookup) {
      TagLoader<Holder<T>> loader = new TagLoader<Holder<T>>(lookup, Registries.tagsDirPath(registryKey));
      return wrapTags(registryKey, loader.build(loader.load(manager)));
   }

   private static <T> Map<TagKey<T>, List<Holder<T>>> wrapTags(final ResourceKey<? extends Registry<T>> registryKey, final Map<Identifier, List<Holder<T>>> tags) {
      return (Map)tags.entrySet().stream().collect(Collectors.toUnmodifiableMap((e) -> TagKey.create(registryKey, (Identifier)e.getKey()), Map.Entry::getValue));
   }

   private static <T> Optional<Registry.PendingTags<T>> loadPendingTags(final ResourceManager manager, final Registry<T> registry) {
      ResourceKey<? extends Registry<T>> key = registry.key();
      TagLoader<Holder<T>> loader = new TagLoader<Holder<T>>(TagLoader.ElementLookup.fromFrozenRegistry(registry), Registries.tagsDirPath(key));
      LoadResult<T> tags = new LoadResult<T>(key, wrapTags(registry.key(), loader.build(loader.load(manager))));
      return tags.tags().isEmpty() ? Optional.empty() : Optional.of(registry.prepareTagReload(tags));
   }

   public static List<HolderLookup.RegistryLookup<?>> buildUpdatedLookups(final RegistryAccess.Frozen registries, final List<Registry.PendingTags<?>> tags) {
      List<HolderLookup.RegistryLookup<?>> result = new ArrayList();
      registries.registries().forEach((lookup) -> {
         Registry.PendingTags<?> foundTags = findTagsForRegistry(tags, lookup.key());
         result.add(foundTags != null ? foundTags.lookup() : lookup.value());
      });
      return result;
   }

   private static Registry.@Nullable PendingTags<?> findTagsForRegistry(final List<Registry.PendingTags<?>> tags, final ResourceKey<? extends Registry<?>> registryKey) {
      for(Registry.PendingTags<?> tag : tags) {
         if (tag.key() == registryKey) {
            return tag;
         }
      }

      return null;
   }

   public static record EntryWithSource(TagEntry entry, String source) {
      public EntryWithSource {
         super();
      }

      public String toString() {
         String var10000 = String.valueOf(this.entry);
         return var10000 + " (from " + this.source + ")";
      }
   }

   private static record SortingEntry(List<EntryWithSource> entries) implements DependencySorter.Entry<Identifier> {
      private SortingEntry {
         super();
      }

      public void visitRequiredDependencies(final Consumer<Identifier> output) {
         this.entries.forEach((e) -> e.entry.visitRequiredDependencies(output));
      }

      public void visitOptionalDependencies(final Consumer<Identifier> output) {
         this.entries.forEach((e) -> e.entry.visitOptionalDependencies(output));
      }
   }

   public static record LoadResult<T>(ResourceKey<? extends Registry<T>> key, Map<TagKey<T>, List<Holder<T>>> tags) {
      public LoadResult {
         super();
      }
   }

   public interface ElementLookup<T> {
      Optional<? extends T> get(Identifier id, boolean required);

      static <T> ElementLookup<? extends Holder<T>> fromFrozenRegistry(final Registry<T> registry) {
         return (id, required) -> registry.get(id);
      }

      static <T> ElementLookup<Holder<T>> fromWritableRegistry(final WritableRegistry<T> registry) {
         return fromGetters(registry.key(), registry.createRegistrationLookup(), registry);
      }

      static <T> ElementLookup<Holder<T>> fromGetters(final ResourceKey<? extends Registry<T>> registryKey, final HolderGetter<T> writable, final HolderGetter<T> immutable) {
         return (id, required) -> (required ? writable : immutable).get(ResourceKey.create(registryKey, id));
      }
   }
}
