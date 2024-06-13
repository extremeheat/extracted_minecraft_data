package net.minecraft.data.tags;

import com.google.common.collect.Maps;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagFile;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;

public abstract class TagsProvider<T> implements DataProvider {
   protected final PackOutput.PathProvider pathProvider;
   private final CompletableFuture<HolderLookup.Provider> lookupProvider;
   private final CompletableFuture<Void> contentsDone = new CompletableFuture<>();
   private final CompletableFuture<TagsProvider.TagLookup<T>> parentProvider;
   protected final ResourceKey<? extends Registry<T>> registryKey;
   private final Map<ResourceLocation, TagBuilder> builders = Maps.newLinkedHashMap();

   protected TagsProvider(PackOutput var1, ResourceKey<? extends Registry<T>> var2, CompletableFuture<HolderLookup.Provider> var3) {
      this(var1, var2, var3, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()));
   }

   protected TagsProvider(
      PackOutput var1,
      ResourceKey<? extends Registry<T>> var2,
      CompletableFuture<HolderLookup.Provider> var3,
      CompletableFuture<TagsProvider.TagLookup<T>> var4
   ) {
      super();
      this.pathProvider = var1.createPathProvider(PackOutput.Target.DATA_PACK, TagManager.getTagDir(var2));
      this.registryKey = var2;
      this.parentProvider = var4;
      this.lookupProvider = var3;
   }

   @Override
   public final String getName() {
      return "Tags for " + this.registryKey.location();
   }

   protected abstract void addTags(HolderLookup.Provider var1);

   @Override
   public CompletableFuture<?> run(CachedOutput var1) {
      record 1CombinedData<T>(HolderLookup.Provider contents, TagsProvider.TagLookup<T> parent) {

         _CombinedData/* $VF was: 1CombinedData*/(HolderLookup.Provider contents, TagsProvider.TagLookup<T> parent) {
            super();
            this.contents = contents;
            this.parent = parent;
         }
      }

      return this.createContentsProvider()
         .thenApply(var1x -> {
            this.contentsDone.complete(null);
            return (HolderLookup.Provider)var1x;
         })
         .thenCombineAsync(this.parentProvider, (var0, var1x) -> new 1CombinedData<>(var0, (TagsProvider.TagLookup<T>)var1x), Util.backgroundExecutor())
         .thenCompose(
            var2 -> {
               HolderLookup.RegistryLookup var3 = var2.contents.lookupOrThrow(this.registryKey);
               Predicate var4 = var2x -> var3.get(ResourceKey.create(this.registryKey, var2x)).isPresent();
               Predicate var5 = var2x -> this.builders.containsKey(var2x) || var2.parent.contains(TagKey.create(this.registryKey, var2x));
               return CompletableFuture.allOf(
                  this.builders
                     .entrySet()
                     .stream()
                     .map(
                        var5x -> {
                           ResourceLocation var6 = var5x.getKey();
                           TagBuilder var7 = var5x.getValue();
                           List var8 = var7.build();
                           List var9 = var8.stream().filter(var2xx -> !var2xx.verifyIfPresent(var4, var5)).toList();
                           if (!var9.isEmpty()) {
                              throw new IllegalArgumentException(
                                 String.format(
                                    Locale.ROOT,
                                    "Couldn't define tag %s as it is missing following references: %s",
                                    var6,
                                    var9.stream().map(Objects::toString).collect(Collectors.joining(","))
                                 )
                              );
                           } else {
                              Path var10 = this.pathProvider.json(var6);
                              return DataProvider.saveStable(var1, var2.contents, TagFile.CODEC, new TagFile(var8, false), var10);
                           }
                        }
                     )
                     .toArray(CompletableFuture[]::new)
               );
            }
         );
   }

   protected TagsProvider.TagAppender<T> tag(TagKey<T> var1) {
      TagBuilder var2 = this.getOrCreateRawBuilder(var1);
      return new TagsProvider.TagAppender<>(var2);
   }

   protected TagBuilder getOrCreateRawBuilder(TagKey<T> var1) {
      return this.builders.computeIfAbsent(var1.location(), var0 -> TagBuilder.create());
   }

   public CompletableFuture<TagsProvider.TagLookup<T>> contentsGetter() {
      return this.contentsDone.thenApply(var1 -> var1x -> Optional.ofNullable(this.builders.get(var1x.location())));
   }

   protected CompletableFuture<HolderLookup.Provider> createContentsProvider() {
      return this.lookupProvider.thenApply(var1 -> {
         this.builders.clear();
         this.addTags(var1);
         return (HolderLookup.Provider)var1;
      });
   }

   protected static class TagAppender<T> {
      private final TagBuilder builder;

      protected TagAppender(TagBuilder var1) {
         super();
         this.builder = var1;
      }

      public final TagsProvider.TagAppender<T> add(ResourceKey<T> var1) {
         this.builder.addElement(var1.location());
         return this;
      }

      @SafeVarargs
      public final TagsProvider.TagAppender<T> add(ResourceKey<T>... var1) {
         for (ResourceKey var5 : var1) {
            this.builder.addElement(var5.location());
         }

         return this;
      }

      public final TagsProvider.TagAppender<T> addAll(List<ResourceKey<T>> var1) {
         for (ResourceKey var3 : var1) {
            this.builder.addElement(var3.location());
         }

         return this;
      }

      public TagsProvider.TagAppender<T> addOptional(ResourceLocation var1) {
         this.builder.addOptionalElement(var1);
         return this;
      }

      public TagsProvider.TagAppender<T> addTag(TagKey<T> var1) {
         this.builder.addTag(var1.location());
         return this;
      }

      public TagsProvider.TagAppender<T> addOptionalTag(ResourceLocation var1) {
         this.builder.addOptionalTag(var1);
         return this;
      }
   }

   @FunctionalInterface
   public interface TagLookup<T> extends Function<TagKey<T>, Optional<TagBuilder>> {
      static <T> TagsProvider.TagLookup<T> empty() {
         return var0 -> Optional.empty();
      }

      default boolean contains(TagKey<T> var1) {
         return this.apply(var1).isPresent();
      }
   }
}
