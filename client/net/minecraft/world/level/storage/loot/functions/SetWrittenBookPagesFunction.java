package net.minecraft.world.level.storage.loot.functions;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WrittenBookContent;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class SetWrittenBookPagesFunction extends LootItemConditionalFunction {
   public static final Codec<SetWrittenBookPagesFunction> CODEC = RecordCodecBuilder.create(
      var0 -> commonFields(var0)
            .and(
               var0.group(
                  WrittenBookContent.PAGES_CODEC.fieldOf("pages").forGetter(var0x -> var0x.pages),
                  ListOperation.Type.OPERATION_MAP_CODEC.forGetter(var0x -> var0x.pageOperation)
               )
            )
            .apply(var0, SetWrittenBookPagesFunction::new)
   );
   private final List<Filterable<Component>> pages;
   private final ListOperation pageOperation;

   protected SetWrittenBookPagesFunction(List<LootItemCondition> var1, List<Filterable<Component>> var2, ListOperation var3) {
      super(var1);
      this.pages = var2;
      this.pageOperation = var3;
   }

   @Override
   protected ItemStack run(ItemStack var1, LootContext var2) {
      var1.update(DataComponents.WRITTEN_BOOK_CONTENT, WrittenBookContent.EMPTY, this::apply);
      return var1;
   }

   @VisibleForTesting
   public WrittenBookContent apply(WrittenBookContent var1) {
      List var2 = this.pageOperation.apply(var1.pages(), this.pages, 100);
      return var1.withReplacedPages(var2);
   }

   @Override
   public LootItemFunctionType getType() {
      return LootItemFunctions.SET_WRITTEN_BOOK_PAGES;
   }
}
