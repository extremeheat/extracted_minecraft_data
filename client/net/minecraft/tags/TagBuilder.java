package net.minecraft.tags;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class TagBuilder {
   private final List<TagEntry> entries = new ArrayList();

   public TagBuilder() {
      super();
   }

   public static TagBuilder create() {
      return new TagBuilder();
   }

   public List<TagEntry> build() {
      return List.copyOf(this.entries);
   }

   public TagBuilder add(TagEntry var1) {
      this.entries.add(var1);
      return this;
   }

   public TagBuilder addElement(ResourceLocation var1) {
      return this.add(TagEntry.element(var1));
   }

   public TagBuilder addOptionalElement(ResourceLocation var1) {
      return this.add(TagEntry.optionalElement(var1));
   }

   public TagBuilder addTag(ResourceLocation var1) {
      return this.add(TagEntry.tag(var1));
   }

   public TagBuilder addOptionalTag(ResourceLocation var1) {
      return this.add(TagEntry.optionalTag(var1));
   }
}
