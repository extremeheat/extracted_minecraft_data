package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBookCoverFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetBookCoverFunction> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> commonFields(i).and(i.group(Filterable.codec(Codec.string(0, 32)).optionalFieldOf("title").forGetter((f) -> f.title), Codec.STRING.optionalFieldOf("author").forGetter((f) -> f.author), ExtraCodecs.intRange(0, 3).optionalFieldOf("generation").forGetter((f) -> f.generation))).apply(i, SetBookCoverFunction::new));
   private final Optional<String> author;
   private final Optional<Filterable<String>> title;
   private final Optional<Integer> generation;

   public SetBookCoverFunction(final List<LootItemCondition> predicates, final Optional<Filterable<String>> title, final Optional<String> author, final Optional<Integer> generation) {
      super(predicates);
      this.author = author;
      this.title = title;
      this.generation = generation;
   }

   protected ItemStack run(final ItemStack itemStack, final LootContext context) {
      itemStack.update(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY, this::apply);
      return itemStack;
   }

   private WrittenBookContent apply(final WrittenBookContent original) {
      Optional var10002 = this.title;
      Objects.requireNonNull(original);
      Filterable var2 = (Filterable)var10002.orElseGet(original::title);
      Optional var10003 = this.author;
      Objects.requireNonNull(original);
      String var3 = (String)var10003.orElseGet(original::author);
      Optional var10004 = this.generation;
      Objects.requireNonNull(original);
      return new WrittenBookContent(var2, var3, (Integer)var10004.orElseGet(original::generation), original.pages(), original.resolved());
   }

   public MapCodec<SetBookCoverFunction> codec() {
      return MAP_CODEC;
   }
}
