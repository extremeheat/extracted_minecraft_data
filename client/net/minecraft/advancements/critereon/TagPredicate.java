package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record TagPredicate<T>(TagKey<T> tag, boolean expected) {
   public TagPredicate(TagKey<T> tag, boolean expected) {
      super();
      this.tag = tag;
      this.expected = expected;
   }

   public static <T> Codec<TagPredicate<T>> codec(ResourceKey<? extends Registry<T>> var0) {
      return RecordCodecBuilder.create(
         var1 -> var1.group(TagKey.codec(var0).fieldOf("id").forGetter(TagPredicate::tag), Codec.BOOL.fieldOf("expected").forGetter(TagPredicate::expected))
               .apply(var1, TagPredicate::new)
      );
   }

   public static <T> TagPredicate<T> is(TagKey<T> var0) {
      return new TagPredicate<>(var0, true);
   }

   public static <T> TagPredicate<T> isNot(TagKey<T> var0) {
      return new TagPredicate<>(var0, false);
   }

   public boolean matches(Holder<T> var1) {
      return var1.is(this.tag) == this.expected;
   }
}
