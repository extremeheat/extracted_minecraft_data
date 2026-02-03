package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.criterion.CollectionPredicate;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WritableBookContent;

public record WritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, PagePredicate>> pages) implements SingleComponentItemPredicate<WritableBookContent> {
   public static final Codec<WritableBookPredicate> CODEC = RecordCodecBuilder.create((i) -> i.group(CollectionPredicate.codec(WritableBookPredicate.PagePredicate.CODEC).optionalFieldOf("pages").forGetter(WritableBookPredicate::pages)).apply(i, WritableBookPredicate::new));

   public WritableBookPredicate {
      super();
   }

   public DataComponentType<WritableBookContent> componentType() {
      return DataComponents.WRITABLE_BOOK_CONTENT;
   }

   public boolean matches(final WritableBookContent value) {
      return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test(value.pages());
   }

   public static record PagePredicate(String contents) implements Predicate<Filterable<String>> {
      public static final Codec<PagePredicate> CODEC;

      public PagePredicate {
         super();
      }

      public boolean test(final Filterable<String> value) {
         return ((String)value.raw()).equals(this.contents);
      }

      static {
         CODEC = Codec.STRING.xmap(PagePredicate::new, PagePredicate::contents);
      }
   }
}
