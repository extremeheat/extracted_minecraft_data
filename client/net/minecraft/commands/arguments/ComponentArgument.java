package net.minecraft.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.serialization.DynamicOps;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;
import net.minecraft.world.entity.Entity;
import org.jspecify.annotations.Nullable;

public class ComponentArgument extends ParserBasedArgument<Component> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "'hello world'", "\"\"", "{text:\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType ERROR_INVALID_COMPONENT = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.component.invalid", var0));
   private static final DynamicOps<Tag> OPS;
   private static final CommandArgumentParser<Tag> TAG_PARSER;

   private ComponentArgument(HolderLookup.Provider var1) {
      super(TAG_PARSER.withCodec(var1.createSerializationContext(OPS), TAG_PARSER, ComponentSerialization.CODEC, ERROR_INVALID_COMPONENT));
   }

   public static Component getRawComponent(CommandContext<CommandSourceStack> var0, String var1) {
      return (Component)var0.getArgument(var1, Component.class);
   }

   public static Component getResolvedComponent(CommandContext<CommandSourceStack> var0, String var1, @Nullable Entity var2) throws CommandSyntaxException {
      return ComponentUtils.updateForEntity((CommandSourceStack)var0.getSource(), getRawComponent(var0, var1), var2, 0);
   }

   public static Component getResolvedComponent(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      return getResolvedComponent(var0, var1, ((CommandSourceStack)var0.getSource()).getEntity());
   }

   public static ComponentArgument textComponent(CommandBuildContext var0) {
      return new ComponentArgument(var0);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      OPS = NbtOps.INSTANCE;
      TAG_PARSER = SnbtGrammar.<Tag>createParser(OPS);
   }
}
