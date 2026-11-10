package net.minecraft.commands.arguments;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.List;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.SnbtGrammar;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.ResolutionContext;
import net.minecraft.util.parsing.packrat.commands.CommandArgumentParser;
import net.minecraft.util.parsing.packrat.commands.ParserBasedArgument;
import net.minecraft.world.entity.Entity;

public class ComponentArgument extends ParserBasedArgument<CommandArgumentParser.RegistryAwareState, Component> {
   private static final ComponentArgument INSTANCE = new ComponentArgument();
   public static final SerializableArgument.Type<ComponentArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_COMPONENT;

   private ComponentArgument() {
      super(CommandArgumentParser.RegistryAwareState.FACTORY, CommandArgumentParser.withCodec(NbtOps.INSTANCE, SnbtGrammar.createParser(NbtOps.INSTANCE), ComponentSerialization.CODEC, ERROR_INVALID_COMPONENT));
   }

   public SerializableArgument.Type<ComponentArgument> type() {
      return TYPE;
   }

   public static Component getRawComponent(final CommandContext<?> context, final String name) {
      return (Component)context.getArgument(name, Component.class);
   }

   public static Component getResolvedComponent(final CommandContext<CommandSourceStack> context, final String name, final Entity contentEntity) throws CommandSyntaxException {
      return ComponentUtils.resolve(ResolutionContext.builder().withSource((CommandSourceStack)context.getSource()).withEntityOverride(contentEntity).build(), getRawComponent(context, name));
   }

   public static Component getResolvedComponent(final CommandContext<CommandSourceStack> context, final String name) throws CommandSyntaxException {
      return ComponentUtils.resolve(ResolutionContext.create((CommandSourceStack)context.getSource()), getRawComponent(context, name));
   }

   public static ArgumentType<Component> textComponent() {
      return INSTANCE.toBrigadier();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      TYPE = SerializableArgument.<ComponentArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("\"hello world\"", "'hello world'", "\"\"", "{text:\"hello world\"}", "[\"\"]");
      ERROR_INVALID_COMPONENT = new DynamicCommandExceptionType((message) -> Component.translatableEscape("argument.component.invalid", message));
   }
}
