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
import net.minecraft.tags.ItemTags;

public class ItemArgument implements ArgumentType<ItemInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stick", "minecraft:stick", "stick{foo=bar}");

   public ItemArgument() {
      super();
   }

   public static ItemArgument item() {
      return new ItemArgument();
   }

   public ItemInput parse(StringReader var1) throws CommandSyntaxException {
      ItemParser var2 = (new ItemParser(var1, false)).parse();
      return new ItemInput(var2.getItem(), var2.getNbt());
   }

   public static <S> ItemInput getItem(CommandContext<S> var0, String var1) {
      return (ItemInput)var0.getArgument(var1, ItemInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      ItemParser var4 = new ItemParser(var3, false);

      try {
         var4.parse();
      } catch (CommandSyntaxException var6) {
      }

      return var4.fillSuggestions(var2, ItemTags.getAllTags());
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
