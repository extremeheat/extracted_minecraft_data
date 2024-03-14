package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemParser {
   private final ItemSyntaxParser syntax;

   public ItemParser(HolderLookup.Provider var1) {
      super();
      this.syntax = new ItemSyntaxParser(var1, false);
   }

   public ItemParser.ItemResult parse(StringReader var1) throws CommandSyntaxException {
      final MutableObject var2 = new MutableObject();
      final DataComponentMap.Builder var3 = DataComponentMap.builder();
      this.syntax.parse(var1, new ItemSyntaxParser.Visitor() {
         @Override
         public void visitItem(Holder<Item> var1) {
            var2.setValue(var1);
         }

         @Override
         public <T> void visitComponent(DataComponentType<T> var1, T var2x) {
            var3.set(var1, (T)var2x);
         }

         @Override
         public void visitCustomData(CompoundTag var1) {
            var3.set(DataComponents.CUSTOM_DATA, CustomData.of(var1));
         }
      });
      return new ItemParser.ItemResult(Objects.requireNonNull((Holder<Item>)var2.getValue(), "Parser gave no item"), var3.build());
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder var1) {
      return this.syntax.fillSuggestions(var1);
   }

   public static record ItemResult(Holder<Item> a, DataComponentMap b) {
      private final Holder<Item> item;
      private final DataComponentMap components;

      public ItemResult(Holder<Item> var1, DataComponentMap var2) {
         super();
         this.item = var1;
         this.components = var2;
      }
   }
}
