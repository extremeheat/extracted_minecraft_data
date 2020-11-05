package net.minecraft.commands.arguments.coordinates;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec3;

public class Vec3Argument implements ArgumentType<Coordinates> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos3d.incomplete"));
   public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.mixed"));
   private final boolean centerCorrect;

   public Vec3Argument(boolean var1) {
      super();
      this.centerCorrect = var1;
   }

   public static Vec3Argument vec3() {
      return new Vec3Argument(true);
   }

   public static Vec3Argument vec3(boolean var0) {
      return new Vec3Argument(var0);
   }

   public static Vec3 getVec3(CommandContext<CommandSourceStack> var0, String var1) {
      return ((Coordinates)var0.getArgument(var1, Coordinates.class)).getPosition((CommandSourceStack)var0.getSource());
   }

   public static Coordinates getCoordinates(CommandContext<CommandSourceStack> var0, String var1) {
      return (Coordinates)var0.getArgument(var1, Coordinates.class);
   }

   public Coordinates parse(StringReader var1) throws CommandSyntaxException {
      return (Coordinates)(var1.canRead() && var1.peek() == '^' ? LocalCoordinates.parse(var1) : WorldCoordinates.parseDouble(var1, this.centerCorrect));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      if (!(var1.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String var3 = var2.getRemaining();
         Object var4;
         if (!var3.isEmpty() && var3.charAt(0) == '^') {
            var4 = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else {
            var4 = ((SharedSuggestionProvider)var1.getSource()).getAbsoluteCoordinates();
         }

         return SharedSuggestionProvider.suggestCoordinates(var3, (Collection)var4, var2, Commands.createValidator(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
