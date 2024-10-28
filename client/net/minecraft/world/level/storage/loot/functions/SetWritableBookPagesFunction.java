package net.minecraft.world.level.storage.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetWritableBookPagesFunction extends LootItemConditionalFunction {
   public static final MapCodec<SetWritableBookPagesFunction> CODEC = RecordCodecBuilder.mapCodec((var0) -> {
      return commonFields(var0).and(var0.group(WritableBookContent.PAGES_CODEC.fieldOf("pages").forGetter((var0x) -> {
         return var0x.pages;
      }), ListOperation.codec(100).forGetter((var0x) -> {
         return var0x.pageOperation;
      }))).apply(var0, SetWritableBookPagesFunction::new);
   });
   private final List<Filterable<String>> pages;
   private final ListOperation pageOperation;

   protected SetWritableBookPagesFunction(List<LootItemCondition> var1, List<Filterable<String>> var2, ListOperation var3) {
      super(var1);
      this.pages = var2;
      this.pageOperation = var3;
   }

   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.WRITABLE_BOOK_CONTENT, WritableBookContent.EMPTY, this::apply);
      return var1;
   }

   public WritableBookContent apply(WritableBookContent var1) {
      List var2 = this.pageOperation.apply(var1.pages(), this.pages, 100);
      return var1.withReplacedPages(var2);
   }

   public LootItemFunctionType<SetWritableBookPagesFunction> getType() {
      return LootItemFunctions.SET_WRITABLE_BOOK_PAGES;
   }
}
