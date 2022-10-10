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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;

public class ColumnPosArgument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> field_212605_b = Arrays.asList("0 0", "~ ~", "~1 ~-2", "^ ^", "^-1 ^0");
   public static final SimpleCommandExceptionType field_212604_a = new SimpleCommandExceptionType(new TextComponentTranslation("argument.pos2d.incomplete", new Object[0]));

   public ColumnPosArgument() {
      super();
   }

   public static ColumnPosArgument func_212603_a() {
      return new ColumnPosArgument();
   }

   public static ColumnPosArgument.ColumnPos func_212602_a(CommandContext<CommandSource> var0, String var1) {
      BlockPos var2 = ((ILocationArgument)var0.getArgument(var1, ILocationArgument.class)).func_197280_c((CommandSource)var0.getSource());
      return new ColumnPosArgument.ColumnPos(var2.func_177958_n(), var2.func_177952_p());
   }

   public ILocationArgument parse(StringReader var1) throws CommandSyntaxException {
      int var2 = var1.getCursor();
      if (!var1.canRead()) {
         throw field_212604_a.createWithContext(var1);
      } else {
         LocationPart var3 = LocationPart.func_197307_a(var1);
         if (var1.canRead() && var1.peek() == ' ') {
            var1.skip();
            LocationPart var4 = LocationPart.func_197307_a(var1);
            return new LocationInput(var3, new LocationPart(true, 0.0D), var4);
         } else {
            var1.setCursor(var2);
            throw field_212604_a.createWithContext(var1);
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
            var4 = ((ISuggestionProvider)var1.getSource()).func_199613_a(false);
         }

         return ISuggestionProvider.func_211269_a(var3, (Collection)var4, var2, Commands.func_212590_a(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return field_212605_b;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   public static class ColumnPos {
      public final int field_212600_a;
      public final int field_212601_b;

      public ColumnPos(int var1, int var2) {
         super();
         this.field_212600_a = var1;
         this.field_212601_b = var2;
      }

      public String toString() {
         return "[" + this.field_212600_a + ", " + this.field_212601_b + "]";
      }
   }
}
