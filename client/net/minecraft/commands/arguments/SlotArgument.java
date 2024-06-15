package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public class SlotArgument implements ArgumentType<Integer> {
   private static final Collection<String> EXAMPLES = Arrays.asList("container.5", "weapon");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("slot.unknown", var0)
   );
   private static final DynamicCommandExceptionType ERROR_ONLY_SINGLE_SLOT_ALLOWED = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("slot.only_single_allowed", var0)
   );

   public SlotArgument() {
      super();
   }

   public static SlotArgument slot() {
      return new SlotArgument();
   }

   public static int getSlot(CommandContext<CommandSourceStack> var0, String var1) {
      return (Integer)var0.getArgument(var1, Integer.class);
   }

   public Integer parse(StringReader var1) throws CommandSyntaxException {
      String var2 = ParserUtils.readWhile(var1, var0 -> var0 != ' ');
      SlotRange var3 = SlotRanges.nameToIds(var2);
      if (var3 == null) {
         throw ERROR_UNKNOWN_SLOT.createWithContext(var1, var2);
      } else if (var3.size() != 1) {
         throw ERROR_ONLY_SINGLE_SLOT_ALLOWED.createWithContext(var1, var2);
      } else {
         return var3.slots().getInt(0);
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(SlotRanges.singleSlotNames(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
