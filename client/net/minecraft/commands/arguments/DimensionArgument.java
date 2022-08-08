package net.minecraft.commands.arguments;

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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;

public class DimensionArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES;
   private static final DynamicCommandExceptionType ERROR_INVALID_VALUE;

   public DimensionArgument() {
      super();
   }

   public ResourceLocation parse(StringReader var1) throws CommandSyntaxException {
      return ResourceLocation.read(var1);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return var1.getSource() instanceof SharedSuggestionProvider ? SharedSuggestionProvider.suggestResource(((SharedSuggestionProvider)var1.getSource()).levels().stream().map(ResourceKey::location), var2) : Suggestions.empty();
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static DimensionArgument dimension() {
      return new DimensionArgument();
   }

   public static ServerLevel getDimension(CommandContext<CommandSourceStack> var0, String var1) throws CommandSyntaxException {
      ResourceLocation var2 = (ResourceLocation)var0.getArgument(var1, ResourceLocation.class);
      ResourceKey var3 = ResourceKey.create(Registry.DIMENSION_REGISTRY, var2);
      ServerLevel var4 = ((CommandSourceStack)var0.getSource()).getServer().getLevel(var3);
      if (var4 == null) {
         throw ERROR_INVALID_VALUE.create(var2);
      } else {
         return var4;
      }
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      EXAMPLES = (Collection)Stream.of(Level.OVERWORLD, Level.NETHER).map((var0) -> {
         return var0.location().toString();
      }).collect(Collectors.toList());
      ERROR_INVALID_VALUE = new DynamicCommandExceptionType((var0) -> {
         return Component.translatable("argument.dimension.invalid", var0);
      });
   }
}
