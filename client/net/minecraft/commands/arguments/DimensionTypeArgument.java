package net.minecraft.commands.arguments;

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
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypeArgument implements ArgumentType<DimensionType> {
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

   public DimensionTypeArgument() {
      super();
   }

   public DimensionType parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var1);
      return (DimensionType)Registry.DIMENSION_TYPE.getOptional(var2).orElseThrow(() -> {
         return ERROR_INVALID_VALUE.create(var2);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggestResource(Streams.stream(DimensionType.getAllTypes()).map(DimensionType::getName), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static DimensionTypeArgument dimension() {
      return new DimensionTypeArgument();
   }

   public static DimensionType getDimension(CommandContext<CommandSourceStack> var0, String var1) {
      return (DimensionType)var0.getArgument(var1, DimensionType.class);
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      EXAMPLES = (Collection)Stream.of(DimensionType.OVERWORLD, DimensionType.NETHER).map((var0) -> {
         return DimensionType.getName(var0).toString();
      }).collect(Collectors.toList());
      ERROR_INVALID_VALUE = new DynamicCommandExceptionType((var0) -> {
         return new TranslatableComponent("argument.dimension.invalid", new Object[]{var0});
      });
   }
}
