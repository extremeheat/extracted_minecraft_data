package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;

public record ItemWrittenBookPredicate(
   Optional<CollectionPredicate<Filterable<Component>, ItemWrittenBookPredicate.PagePredicate>> pages,
   Optional<String> author,
   Optional<String> title,
   MinMaxBounds.Ints generation,
   Optional<Boolean> resolved
) implements SingleComponentItemPredicate<WrittenBookContent> {
   public static final Codec<ItemWrittenBookPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CollectionPredicate.codec(ItemWrittenBookPredicate.PagePredicate.CODEC).optionalFieldOf("pages").forGetter(ItemWrittenBookPredicate::pages),
               Codec.STRING.optionalFieldOf("author").forGetter(ItemWrittenBookPredicate::author),
               Codec.STRING.optionalFieldOf("title").forGetter(ItemWrittenBookPredicate::title),
               MinMaxBounds.Ints.CODEC.optionalFieldOf("generation", MinMaxBounds.Ints.ANY).forGetter(ItemWrittenBookPredicate::generation),
               Codec.BOOL.optionalFieldOf("resolved").forGetter(ItemWrittenBookPredicate::resolved)
            )
            .apply(var0, ItemWrittenBookPredicate::new)
   );

   public ItemWrittenBookPredicate(
      Optional<CollectionPredicate<Filterable<Component>, ItemWrittenBookPredicate.PagePredicate>> pages,
      Optional<String> author,
      Optional<String> title,
      MinMaxBounds.Ints generation,
      Optional<Boolean> resolved
   ) {
      super();
      this.pages = pages;
      this.author = author;
      this.title = title;
      this.generation = generation;
      this.resolved = resolved;
   }

   @Override
   public DataComponentType<WrittenBookContent> componentType() {
      return DataComponents.WRITTEN_BOOK_CONTENT;
   }

   public boolean matches(ItemStack var1, WrittenBookContent var2) {
      if (this.author.isPresent() && !this.author.get().equals(var2.author())) {
         return false;
      } else if (this.title.isPresent() && !this.title.get().equals(var2.title().raw())) {
         return false;
      } else if (!this.generation.matches(var2.generation())) {
         return false;
      } else {
         return this.resolved.isPresent() && this.resolved.get() != var2.resolved() ? false : !this.pages.isPresent() || this.pages.get().test(var2.pages());
      }
   }

   public static record PagePredicate(Component contents) implements Predicate<Filterable<Component>> {
      public static final Codec<ItemWrittenBookPredicate.PagePredicate> CODEC = ComponentSerialization.CODEC
         .xmap(ItemWrittenBookPredicate.PagePredicate::new, ItemWrittenBookPredicate.PagePredicate::contents);

      public PagePredicate(Component contents) {
         super();
         this.contents = contents;
      }

      public boolean test(Filterable<Component> var1) {
         return ((Component)var1.raw()).equals(this.contents);
      }
   }
}
