package net.minecraft.advancements.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

public record TagPredicate<T>(HolderSet<T> tag, boolean expected) {
   public TagPredicate {
      super();
   }

   public static <T> Codec<TagPredicate<T>> codec(final ResourceKey<? extends Registry<T>> registryKey) {
      return RecordCodecBuilder.create((i) -> i.group(RegistryCodecs.holderSet(registryKey).fieldOf("id").forGetter(TagPredicate::tag), Codec.BOOL.fieldOf("expected").forGetter(TagPredicate::expected)).apply(i, TagPredicate::new));
   }

   public static <T> TagPredicate<T> is(final HolderGetter<T> lookup, final TagKey<T> tag) {
      return is(lookup.getOrThrow(tag));
   }

   public static <T> TagPredicate<T> is(final HolderSet<T> tag) {
      return new TagPredicate<T>(tag, true);
   }

   public static <T> TagPredicate<T> isNot(final HolderGetter<T> lookup, final TagKey<T> tag) {
      return isNot(lookup.getOrThrow(tag));
   }

   public static <T> TagPredicate<T> isNot(final HolderSet<T> tag) {
      return new TagPredicate<T>(tag, false);
   }

   public boolean matches(final Holder<T> holder) {
      return this.tag.contains(holder) == this.expected;
   }
}
