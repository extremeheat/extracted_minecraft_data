package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.criterion.CollectionPredicate;
import net.minecraft.advancements.criterion.MinMaxBounds;
import net.minecraft.advancements.criterion.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WrittenBookContent;

public record WrittenBookPredicate(Optional<CollectionPredicate<Filterable<Component>, PagePredicate>> pages, Optional<String> author, Optional<String> title, MinMaxBounds.Ints generation, Optional<Boolean> resolved) implements SingleComponentItemPredicate<WrittenBookContent> {
   public static final Codec<WrittenBookPredicate> CODEC = RecordCodecBuilder.create((var0) -> var0.group(CollectionPredicate.codec(WrittenBookPredicate.PagePredicate.CODEC).optionalFieldOf("pages").forGetter(WrittenBookPredicate::pages), Codec.STRING.optionalFieldOf("author").forGetter(WrittenBookPredicate::author), Codec.STRING.optionalFieldOf("title").forGetter(WrittenBookPredicate::title), MinMaxBounds.Ints.CODEC.optionalFieldOf("generation", MinMaxBounds.Ints.ANY).forGetter(WrittenBookPredicate::generation), Codec.BOOL.optionalFieldOf("resolved").forGetter(WrittenBookPredicate::resolved)).apply(var0, WrittenBookPredicate::new));

   public WrittenBookPredicate(Optional<CollectionPredicate<Filterable<Component>, PagePredicate>> var1, Optional<String> var2, Optional<String> var3, MinMaxBounds.Ints var4, Optional<Boolean> var5) {
      super();
      this.pages = var1;
      this.author = var2;
      this.title = var3;
      this.generation = var4;
      this.resolved = var5;
   }

   public DataComponentType<WrittenBookContent> componentType() {
      return DataComponents.WRITTEN_BOOK_CONTENT;
   }

   public boolean matches(WrittenBookContent var1) {
      if (this.author.isPresent() && !((String)this.author.get()).equals(var1.author())) {
         return false;
      } else if (this.title.isPresent() && !((String)this.title.get()).equals(var1.title().raw())) {
         return false;
      } else if (!this.generation.matches(var1.generation())) {
         return false;
      } else if (this.resolved.isPresent() && (Boolean)this.resolved.get() != var1.resolved()) {
         return false;
      } else {
         return !this.pages.isPresent() || ((CollectionPredicate)this.pages.get()).test(var1.pages());
      }
   }

   public static record PagePredicate(Component contents) implements Predicate<Filterable<Component>> {
      public static final Codec<PagePredicate> CODEC;

      public PagePredicate(Component var1) {
         super();
         this.contents = var1;
      }

      public boolean test(Filterable<Component> var1) {
         return ((Component)var1.raw()).equals(this.contents);
      }

      // $FF: synthetic method
      public boolean test(final Object var1) {
         return this.test((Filterable)var1);
      }

      static {
         CODEC = ComponentSerialization.CODEC.xmap(PagePredicate::new, PagePredicate::contents);
      }
   }
}
