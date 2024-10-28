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

public record ItemWritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, PagePredicate>> pages) implements SingleComponentItemPredicate<WritableBookContent> {
   public static final Codec<ItemWritableBookPredicate> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(CollectionPredicate.codec(ItemWritableBookPredicate.PagePredicate.CODEC).optionalFieldOf("pages").forGetter(ItemWritableBookPredicate::pages)).apply(var0, ItemWritableBookPredicate::new);
   });

   public ItemWritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, PagePredicate>> var1) {
      super();
      this.pages = var1;
   }

   public DataComponentType<WritableBookContent> componentType() {
      return DataComponents.WRITABLE_BOOK_CONTENT;
   }

   public boolean matches(ItemStack var1, WritableBookContent var2) {
      return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test((Iterable)var2.pages());
   }

   public Optional<CollectionPredicate<Filterable<String>, PagePredicate>> pages() {
      return this.pages;
   }

   public static record PagePredicate(String contents) implements Predicate<Filterable<String>> {
      public static final Codec<PagePredicate> CODEC;

      public PagePredicate(String var1) {
         super();
         this.contents = var1;
      }

      public boolean test(Filterable<String> var1) {
         return ((String)var1.raw()).equals(this.contents);
      }

      public String contents() {
         return this.contents;
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Filterable)var1);
      }

      static {
         CODEC = Codec.STRING.xmap(PagePredicate::new, PagePredicate::contents);
      }
   }
}
