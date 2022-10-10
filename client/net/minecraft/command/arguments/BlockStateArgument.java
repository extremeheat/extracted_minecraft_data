package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;

public class BlockStateArgument implements ArgumentType<BlockStateInput> {
   private static final Collection<String> field_201332_a = Arrays.asList("stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}");

   public BlockStateArgument() {
      super();
   }

   public static BlockStateArgument func_197239_a() {
      return new BlockStateArgument();
   }

   public BlockStateInput parse(StringReader var1) throws CommandSyntaxException {
      BlockStateParser var2 = (new BlockStateParser(var1, false)).func_197243_a(true);
      return new BlockStateInput(var2.func_197249_b(), var2.func_197254_a().keySet(), var2.func_197241_c());
   }

   public static BlockStateInput func_197238_a(CommandContext<CommandSource> var0, String var1) {
      return (BlockStateInput)var0.getArgument(var1, BlockStateInput.class);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      StringReader var3 = new StringReader(var2.getInput());
      var3.setCursor(var2.getStart());
      BlockStateParser var4 = new BlockStateParser(var3, false);

      try {
         var4.func_197243_a(true);
      } catch (CommandSyntaxException var6) {
      }

      return var4.func_197245_a(var2);
   }

   public Collection<String> getExamples() {
      return field_201332_a;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
