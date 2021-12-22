package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class StaticTagHelper<T> {
   private final ResourceKey<? extends Registry<T>> key;
   private final String directory;
   private TagCollection<T> source = TagCollection.empty();
   private final List<StaticTagHelper.Wrapper<T>> wrappers = Lists.newArrayList();

   public StaticTagHelper(ResourceKey<? extends Registry<T>> var1, String var2) {
      super();
      this.key = var1;
      this.directory = var2;
   }

   public Tag.Named<T> bind(String var1) {
      StaticTagHelper.Wrapper var2 = new StaticTagHelper.Wrapper(new ResourceLocation(var1));
      this.wrappers.add(var2);
      return var2;
   }

   public void resetToEmpty() {
      this.source = TagCollection.empty();
      SetTag var1 = SetTag.empty();
      this.wrappers.forEach((var1x) -> {
         var1x.rebind((var1xx) -> {
            return var1;
         });
      });
   }

   public void reset(TagContainer var1) {
      TagCollection var2 = var1.getOrEmpty(this.key);
      this.source = var2;
      this.wrappers.forEach((var1x) -> {
         Objects.requireNonNull(var2);
         var1x.rebind(var2::getTag);
      });
   }

   public TagCollection<T> getAllTags() {
      return this.source;
   }

   public Set<ResourceLocation> getMissingTags(TagContainer var1) {
      TagCollection var2 = var1.getOrEmpty(this.key);
      Set var3 = (Set)this.wrappers.stream().map(StaticTagHelper.Wrapper::getName).collect(Collectors.toSet());
      ImmutableSet var4 = ImmutableSet.copyOf(var2.getAvailableTags());
      return Sets.difference(var3, var4);
   }

   public ResourceKey<? extends Registry<T>> getKey() {
      return this.key;
   }

   public String getDirectory() {
      return this.directory;
   }

   protected void addToCollection(TagContainer.Builder var1) {
      var1.add(this.key, TagCollection.method_8((Map)this.wrappers.stream().collect(Collectors.toMap(Tag.Named::getName, (var0) -> {
         return var0;
      }))));
   }

   private static class Wrapper<T> implements Tag.Named<T> {
      @Nullable
      private Tag<T> tag;
      protected final ResourceLocation name;

      Wrapper(ResourceLocation var1) {
         super();
         this.name = var1;
      }

      public ResourceLocation getName() {
         return this.name;
      }

      private Tag<T> resolve() {
         if (this.tag == null) {
            throw new IllegalStateException("Tag " + this.name + " used before it was bound");
         } else {
            return this.tag;
         }
      }

      void rebind(Function<ResourceLocation, Tag<T>> var1) {
         this.tag = (Tag)var1.apply(this.name);
      }

      public boolean contains(T var1) {
         return this.resolve().contains(var1);
      }

      public List<T> getValues() {
         return this.resolve().getValues();
      }
   }
}
