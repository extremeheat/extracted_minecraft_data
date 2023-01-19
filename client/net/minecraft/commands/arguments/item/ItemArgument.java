package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");
   private final HolderLookup<Item> items;

   public ItemArgument(CommandBuildContext var1) {
      super();
      this.items = var1.holderLookup(Registries.ITEM);
   }

   public static ItemArgument item(CommandBuildContext var0) {
      return new ItemArgument(var0);
   }

   public ItemInput parse(StringReader var1) throws CommandSyntaxException {
      ItemParser.ItemResult var2 = ItemParser.parseForItem(this.items, var1);
      return new ItemInput(var2.item(), var2.nbt());
   }

   public static <S> ItemInput getItem(CommandContext<S> var0, String var1) {
      return (ItemInput)var0.getArgument(var1, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ItemParser.fillSuggestions(this.items, var2, false);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
