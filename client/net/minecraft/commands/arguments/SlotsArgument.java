package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.SlotRange;
import net.minecraft.world.inventory.SlotRanges;

public class SlotsArgument implements ArgumentType<SlotRange> {
   private static final Collection<String> EXAMPLES = List.of("container.*", "container.5", "weapon");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_SLOT = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("slot.unknown", var0));

   public SlotsArgument() {
      super();
   }

   public static SlotsArgument slots() {
      return new SlotsArgument();
   }

   public static SlotRange getSlots(CommandContext<CommandSourceStack> var0, String var1) {
      return (SlotRange)var0.getArgument(var1, SlotRange.class);
   }

   public SlotRange parse(StringReader var1) throws CommandSyntaxException {
      String var2 = ParserUtils.readWhile(var1, (var0) -> var0 != ' ');
      SlotRange var3 = SlotRanges.nameToIds(var2);
      if (var3 == null) {
         throw ERROR_UNKNOWN_SLOT.createWithContext(var1, var2);
      } else {
         return var3;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggest(SlotRanges.allNames(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
