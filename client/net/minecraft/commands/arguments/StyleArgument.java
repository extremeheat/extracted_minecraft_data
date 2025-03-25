package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;

public class StyleArgument extends ParserBasedArgument<Style> {
   private static final Collection<String> EXAMPLES = List.of("{bold: true}", "{color: 'red'}", "{}");
   public static final DynamicCommandExceptionType ERROR_INVALID_STYLE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.style.invalid", var0));
   private static final DynamicOps<Tag> OPS;
   private static final CommandArgumentParser<Tag> TAG_PARSER;

   private StyleArgument(HolderLookup.Provider var1) {
      super(TAG_PARSER.withCodec(var1.createSerializationContext(OPS), TAG_PARSER, Style.Serializer.CODEC, ERROR_INVALID_STYLE));
   }

   public static Style getStyle(CommandContext<CommandSourceStack> var0, String var1) {
      return (Style)var0.getArgument(var1, Style.class);
   }

   public static StyleArgument style(CommandBuildContext var0) {
      return new StyleArgument(var0);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      OPS = NbtOps.INSTANCE;
      TAG_PARSER = SnbtGrammar.<Tag>createParser(OPS);
   }
}
