package net.minecraft.tags;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class StaticTagHelper<T> {
   private TagCollection<T> source = TagCollection.empty();
   private final List<StaticTagHelper.Wrapper<T>> wrappers = Lists.newArrayList();
   private final Function<TagContainer, TagCollection<T>> collectionGetter;

   public StaticTagHelper(Function<TagContainer, TagCollection<T>> var1) {
      super();
      this.collectionGetter = var1;
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
      TagCollection var2 = (TagCollection)this.collectionGetter.apply(var1);
      this.source = var2;
      this.wrappers.forEach((var1x) -> {
         var1x.rebind(var2::getTag);
      });
   }

   public TagCollection<T> getAllTags() {
      return this.source;
   }

   public List<? extends Tag.Named<T>> getWrappers() {
      return this.wrappers;
   }

   public Set<ResourceLocation> getMissingTags(TagContainer var1) {
      TagCollection var2 = (TagCollection)this.collectionGetter.apply(var1);
      Set var3 = (Set)this.wrappers.stream().map(StaticTagHelper.Wrapper::getName).collect(Collectors.toSet());
      ImmutableSet var4 = ImmutableSet.copyOf(var2.getAvailableTags());
      return Sets.difference(var3, var4);
   }

   static class Wrapper<T> implements Tag.Named<T> {
      @Nullable
      private Tag<T> tag;
      protected final ResourceLocation name;

      private Wrapper(ResourceLocation var1) {
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

      // $FF: synthetic method
      Wrapper(ResourceLocation var1, Object var2) {
         this(var1);
      }
   }
}
