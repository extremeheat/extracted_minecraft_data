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
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

public class Vec2Argument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> field_201336_b = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
   public static final SimpleCommandExceptionType field_197298_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos2d.incomplete", new Object[0]));
   private final boolean field_197299_b;

   public Vec2Argument(boolean var1) {
      super();
      this.field_197299_b = var1;
   }

   public static Vec2Argument func_197296_a() {
      return new Vec2Argument(true);
   }

   public static Vec2f func_197295_a(CommandContext<CommandSource> var0, String var1) throws CommandSyntaxException {
      Vec3d var2 = ((ILocationArgument)var0.getArgument(var1, ILocationArgument.class)).func_197281_a((CommandSource)var0.getSource());
      return new Vec2f((float)var2.field_72450_a, (float)var2.field_72449_c);
   }

   public ILocationArgument parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      if (!var1.canRead()) {
         throw field_197298_a.createWithContext(var1);
      } else {
         LocationPart var3 = LocationPart.func_197308_a(var1, this.field_197299_b);
         if (var1.canRead() && var1.peek() == ' ') {
            var1.skip();
            LocationPart var4 = LocationPart.func_197308_a(var1, this.field_197299_b);
            return new LocationInput(var3, new LocationPart(true, 0.0D), var4);
         } else {
            var1.setCursor(var2);
            throw field_197298_a.createWithContext(var1);
         }
      }
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

         return ISuggestionProvider.func_211269_a(var3, (Collection)var4, var2, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return field_201336_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
