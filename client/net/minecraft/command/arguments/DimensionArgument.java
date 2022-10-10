package net.minecraft.command.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgument implements ArgumentType<DimensionType> {
   private static final Collection<String> field_212597_b;
   public static final DynamicCommandExceptionType field_212596_a;

   public DimensionArgument() {
      super();
   }

   public <S> DimensionType parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.func_195826_a(var1);
      DimensionType var3 = DimensionType.func_193417_a(var2);
      if (var3 == null) {
         throw field_212596_a.create(var2);
      } else {
         return var3;
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ISuggestionProvider.func_212476_a(Streams.stream(DimensionType.func_212681_b()).map(DimensionType::func_212678_a), var2);
   }

   public Collection<String> getExamples() {
      return field_212597_b;
   }

   public static DimensionArgument func_212595_a() {
      return new DimensionArgument();
   }

   public static DimensionType func_212592_a(CommandContext<CommandSource> var0, String var1) {
      return (DimensionType)var0.getArgument(var1, DimensionType.class);
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      field_212597_b = (Collection)Stream.of(DimensionType.OVERWORLD, DimensionType.NETHER).map((var0) -> {
         return DimensionType.func_212678_a(var0).toString();
      }).collect(Collectors.toList());
      field_212596_a = new DynamicCommandExceptionType((var0) -> {
         return new TextComponentTranslation("argument.dimension.invalid", new Object[]{var0});
      });
   }
}
