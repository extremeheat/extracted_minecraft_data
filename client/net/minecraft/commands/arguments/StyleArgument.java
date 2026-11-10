package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;

public class StyleArgument extends ParserBasedArgument<CommandArgumentParser.RegistryAwareState, Style> {
   private static final StyleArgument INSTANCE = new StyleArgument();
   public static final SerializableArgument.Type<StyleArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_STYLE;

   private StyleArgument() {
      super(CommandArgumentParser.RegistryAwareState.FACTORY, CommandArgumentParser.withCodec(NbtOps.INSTANCE, SnbtGrammar.createParser(NbtOps.INSTANCE), Style.Serializer.CODEC, ERROR_INVALID_STYLE));
   }

   public SerializableArgument.Type<StyleArgument> type() {
      return TYPE;
   }

   public static Style getStyle(final CommandContext<?> context, final String name) {
      return (Style)context.getArgument(name, Style.class);
   }

   public static ArgumentType<Style> style() {
      return INSTANCE.toBrigadier();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<StyleArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("{bold: true}", "{color: 'red'}", "{}");
      ERROR_INVALID_STYLE = new DynamicCommandExceptionType((message) -> Component.translatableEscape("argument.style.invalid", message));
   }
}
