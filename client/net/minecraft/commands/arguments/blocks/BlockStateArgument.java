package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.tags.BlockTags;

public class BlockStateArgument implements ArgumentType<BlockInput> {
   private static final Collection<String> EXAMPLES = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

   public BlockStateArgument() {
      super();
   }

   public static BlockStateArgument block() {
      return new BlockStateArgument();
   }

   public BlockInput parse(StringReader var1) throws CommandSyntaxException {
      BlockStateParser var2 = (new BlockStateParser(var1, false)).parse(true);
      return new BlockInput(var2.getState(), var2.getProperties().keySet(), var2.getNbt());
   }

   public static BlockInput getBlock(CommandContext<CommandSourceStack> var0, String var1) {
      return (BlockInput)var0.getArgument(var1, BlockInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      BlockStateParser var4 = new BlockStateParser(var3, false);

      try {
         var4.parse(true);
      } catch (CommandSyntaxException var6) {
      }

      return var4.fillSuggestions(var2, BlockTags.getAllTags());
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
