package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.ParserUtils;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.entity.Entity;

public class ComponentArgument implements ArgumentType<Component> {
   private static final Collection<String> EXAMPLES = Arrays.asList("\"hello world\"", "'hello world'", "\"\"", "{text:\"hello world\"}", "[\"\"]");
   public static final DynamicCommandExceptionType ERROR_INVALID_COMPONENT = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("argument.component.invalid", var0));
   private final HolderLookup.Provider registries;

   private ComponentArgument(HolderLookup.Provider var1) {
      super();
      this.registries = var1;
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

   public Component parse(StringReader var1) throws CommandSyntaxException {
      return (Component)ParserUtils.parseSnbtWithCodec(ComponentSerialization.CODEC, this.registries, ERROR_INVALID_COMPONENT, var1);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
