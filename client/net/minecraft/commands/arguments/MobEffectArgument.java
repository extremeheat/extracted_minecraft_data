package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectArgument implements ArgumentType<MobEffect> {
   private static final Collection<String> EXAMPLES = Arrays.asList("spooky", "effect");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_EFFECT = new DynamicCommandExceptionType(
      var0 -> Component.translatable("effect.effectNotFound", var0)
   );

   public MobEffectArgument() {
      super();
   }

   public static MobEffectArgument effect() {
      return new MobEffectArgument();
   }

   public static MobEffect getEffect(CommandContext<CommandSourceStack> var0, String var1) {
      return (MobEffect)var0.getArgument(var1, MobEffect.class);
   }

   public MobEffect parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var1);
      return Registry.MOB_EFFECT.getOptional(var2).orElseThrow(() -> ERROR_UNKNOWN_EFFECT.create(var2));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggestResource(Registry.MOB_EFFECT.keySet(), var2);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}
