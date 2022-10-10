package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class Vec3Argument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> field_201337_c = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType field_197304_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos3d.incomplete", new Object[0]));
   public static final SimpleCommandExceptionType field_200149_b = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos.mixed", new Object[0]));
   private final boolean field_197305_b;

   public Vec3Argument(boolean var1) {
      super();
      this.field_197305_b = var1;
   }

   public static Vec3Argument func_197301_a() {
      return new Vec3Argument(true);
   }

   public static Vec3Argument func_197303_a(boolean var0) {
      return new Vec3Argument(var0);
   }

   public static Vec3d func_197300_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      return ((ILocationArgument)var0.getArgument(var1, ILocationArgument.class)).func_197281_a((CommandSource)var0.getSource());
   }

   public static ILocationArgument func_200385_b(CommandContext<CommandSource> var0, String var1) {
      return (ILocationArgument)var0.getArgument(var1, ILocationArgument.class);
   }

   public ILocationArgument parse(StringReader var1) throws CommandSyntaxException {
      return (ILocationArgument)(var1.canRead() && var1.peek() == '^' ? LocalLocationArgument.func_200142_a(var1) : LocationInput.func_200147_a(var1, this.field_197305_b));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (!(var1.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String var3 = var2.getRemaining();
         Object var4;
         if (!var3.isEmpty() && var3.charAt(0) == '^') {
            var4 = Collections.singleton(ISuggestionProvider.Coordinates.field_209004_a);
         } else {
            var4 = ((ISuggestionProvider)var1.getSource()).func_199613_a(true);
         }

         return ISuggestionProvider.func_209000_a(var3, (Collection)var4, var2, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return field_201337_c;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
