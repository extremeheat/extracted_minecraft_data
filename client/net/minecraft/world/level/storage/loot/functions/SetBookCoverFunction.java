package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetBookCoverFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetBookCoverFunction> CODEC = RecordCodecBuilder.mapCodec(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  Filterable.codec(Codec.string(0, 32)).optionalFieldOf("title").forGetter(var0x -> var0x.title),
                  Codec.STRING.optionalFieldOf("author").forGetter(var0x -> var0x.author),
                  ExtraCodecs.intRange(0, 3).optionalFieldOf("generation").forGetter(var0x -> var0x.generation)
               )
            )
            .apply(var0, SetBookCoverFunction::new)
   );
   private final Optional<String> author;
   private final Optional<Filterable<String>> title;
   private final Optional<Integer> generation;

   public SetBookCoverFunction(List<LootItemCondition> var1, Optional<Filterable<String>> var2, Optional<String> var3, Optional<Integer> var4) {
      super(var1);
      this.author = var3;
      this.title = var2;
      this.generation = var4;
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY, this::apply);
      return var1;
   }

   private WrittenBookContent apply(WrittenBookContent var1) {
      return new WrittenBookContent(
         (Filterable<String>)this.title.orElseGet(var1::title),
         this.author.orElseGet(var1::author),
         this.generation.orElseGet(var1::generation),
         var1.pages(),
         var1.resolved()
      );
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_BOOK_COVER;
   }
}
