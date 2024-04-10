package net.minecraft.advancements.critereon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;

public record ItemWritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, ItemWritableBookPredicate.PagePredicate>> pages)
   implements SingleComponentItemPredicate<WritableBookContent> {
   public static final Codec<ItemWritableBookPredicate> CODEC = RecordCodecBuilder.create(
      var0 -> var0.group(
               CollectionPredicate.codec(ItemWritableBookPredicate.PagePredicate.CODEC).optionalFieldOf("pages").forGetter(ItemWritableBookPredicate::pages)
            )
            .apply(var0, ItemWritableBookPredicate::new)
   );

   public ItemWritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, ItemWritableBookPredicate.PagePredicate>> pages) {
      super();
      this.pages = pages;
   }

   @Override
   public DataComponentType<WritableBookContent> componentType() {
      return DataComponents.WRITABLE_BOOK_CONTENT;
   }

   public boolean matches(ItemStack var1, WritableBookContent var2) {
      return !this.pages.isPresent() || this.pages.get().test(var2.pages());
   }

   public static record PagePredicate(String contents) implements Predicate<Filterable<String>> {
      public static final Codec<ItemWritableBookPredicate.PagePredicate> CODEC = Codec.STRING
         .xmap(ItemWritableBookPredicate.PagePredicate::new, ItemWritableBookPredicate.PagePredicate::contents);

      public PagePredicate(String contents) {
         super();
         this.contents = contents;
      }

      public boolean test(Filterable<String> var1) {
         return ((String)var1.raw()).equals(this.contents);
      }
   }
}
