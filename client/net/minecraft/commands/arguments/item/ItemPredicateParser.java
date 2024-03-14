package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class ItemPredicateParser {
   private final ItemSyntaxParser syntax;

   public ItemPredicateParser(HolderLookup.Provider var1) {
      super();
      this.syntax = new ItemSyntaxParser(var1, true);
   }

   public Predicate<ItemStack> parse(StringReader var1) throws CommandSyntaxException {
      final ArrayList var2 = new ArrayList();
      final DataComponentPredicate.Builder var3 = DataComponentPredicate.builder();
      this.syntax.parse(var1, new ItemSyntaxParser.Visitor() {
         @Override
         public void visitItem(Holder<Item> var1) {
            var2.add(var1x -> var1x.is(var1));
         }

         @Override
         public void visitTag(HolderSet<Item> var1) {
            var2.add(var1x -> var1x.is(var1));
         }

         @Override
         public <T> void visitComponent(DataComponentType<T> var1, T var2x) {
            var3.expect(var1, (T)var2x);
         }

         @Override
         public void visitCustomData(CompoundTag var1) {
            var2.add(CustomData.itemMatcher(DataComponents.CUSTOM_DATA, var1));
         }
      });
      DataComponentPredicate var4 = var3.build();
      if (!var4.alwaysMatches()) {
         var2.add(var4::test);
      }

      return Util.allOf(var2);
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder var1) {
      return this.syntax.fillSuggestions(var1);
   }
}
