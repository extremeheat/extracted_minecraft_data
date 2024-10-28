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
   public static final MapCodec<SetBookCoverFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(Filterable.codec(Codec.string(0, 32)).optionalFieldOf("title").forGetter((var0x) -> {
         return var0x.title;
      }), Codec.STRING.optionalFieldOf("author").forGetter((var0x) -> {
         return var0x.author;
      }), ExtraCodecs.intRange(0, 3).optionalFieldOf("generation").forGetter((var0x) -> {
         return var0x.generation;
      }))).apply(var0, SetBookCoverFunction::new);
   });
   private final Optional<String> author;
   private final Optional<Filterable<String>> title;
   private final Optional<Integer> generation;

   public SetBookCoverFunction(List<LootItemCondition> var1, Optional<Filterable<String>> var2, Optional<String> var3, Optional<Integer> var4) {
      super(var1);
      this.author = var3;
      this.title = var2;
      this.generation = var4;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY, this::apply);
      return var1;
   }

   private WrittenBookContent apply(WrittenBookContent var1) {
      Optional var10002 = this.title;
      Objects.requireNonNull(var1);
      Filterable var2 = (Filterable)var10002.orElseGet(var1::title);
      Optional var10003 = this.author;
      Objects.requireNonNull(var1);
      String var3 = (String)var10003.orElseGet(var1::author);
      Optional var10004 = this.generation;
      Objects.requireNonNull(var1);
      return new WrittenBookContent(var2, var3, (Integer)var10004.orElseGet(var1::generation), var1.pages(), var1.resolved());
   }

   public LootItemFunctionType<SetBookCoverFunction> getType() {
      return LootItemFunctions.SET_BOOK_COVER;
   }
}
