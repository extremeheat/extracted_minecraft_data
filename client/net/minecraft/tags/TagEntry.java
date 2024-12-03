package net.minecraft.tags;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public class TagEntry {
   private static final Codec<TagEntry> FULL_CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("id").forGetter(TagEntry::elementOrTag), Codec.BOOL.optionalFieldOf("required", true).forGetter((var0x) -> var0x.required)).apply(var0, TagEntry::new));
   public static final Codec<TagEntry> CODEC;
   private final ResourceLocation id;
   private final boolean tag;
   private final boolean required;

   private TagEntry(ResourceLocation var1, boolean var2, boolean var3) {
      super();
      this.id = var1;
      this.tag = var2;
      this.required = var3;
   }

   private TagEntry(ExtraCodecs.TagOrElementLocation var1, boolean var2) {
      super();
      this.id = var1.id();
      this.tag = var1.tag();
      this.required = var2;
   }

   private ExtraCodecs.TagOrElementLocation elementOrTag() {
      return new ExtraCodecs.TagOrElementLocation(this.id, this.tag);
   }

   public static TagEntry element(ResourceLocation var0) {
      return new TagEntry(var0, false, true);
   }

   public static TagEntry optionalElement(ResourceLocation var0) {
      return new TagEntry(var0, false, false);
   }

   public static TagEntry tag(ResourceLocation var0) {
      return new TagEntry(var0, true, true);
   }

   public static TagEntry optionalTag(ResourceLocation var0) {
      return new TagEntry(var0, true, false);
   }

   public <T> boolean build(Lookup<T> var1, Consumer<T> var2) {
      if (this.tag) {
         Collection var3 = var1.tag(this.id);
         if (var3 == null) {
            return !this.required;
         }

         var3.forEach(var2);
      } else {
         Object var4 = var1.element(this.id, this.required);
         if (var4 == null) {
            return !this.required;
         }

         var2.accept(var4);
      }

      return true;
   }

   public void visitRequiredDependencies(Consumer<ResourceLocation> var1) {
      if (this.tag && this.required) {
         var1.accept(this.id);
      }

   }

   public void visitOptionalDependencies(Consumer<ResourceLocation> var1) {
      if (this.tag && !this.required) {
         var1.accept(this.id);
      }

   }

   public boolean verifyIfPresent(Predicate<ResourceLocation> var1, Predicate<ResourceLocation> var2) {
      return !this.required || (this.tag ? var2 : var1).test(this.id);
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      if (this.tag) {
         var1.append('#');
      }

      var1.append(this.id);
      if (!this.required) {
         var1.append('?');
      }

      return var1.toString();
   }

   static {
      CODEC = Codec.either(ExtraCodecs.TAG_OR_ELEMENT_ID, FULL_CODEC).xmap((var0) -> (TagEntry)var0.map((var0x) -> new TagEntry(var0x, true), (var0x) -> var0x), (var0) -> var0.required ? Either.left(var0.elementOrTag()) : Either.right(var0));
   }

   public interface Lookup<T> {
      @Nullable
      T element(ResourceLocation var1, boolean var2);

      @Nullable
      Collection<T> tag(ResourceLocation var1);
   }
}
