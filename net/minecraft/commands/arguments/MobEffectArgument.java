package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("spooky", "effect");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_EFFECT = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("effect.effectNotFound", new Object[]{var0});
   });

   public static MobEffectArgument effect() {
      return new MobEffectArgument();
   }

   public static MobEffect getEffect(CommandContext var0, String var1) throws CommandSyntaxException {
      return (MobEffect)var0.getArgument(var1, MobEffect.class);
   }

   public MobEffect parse(StringReader var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var1);
      return (MobEffect)Registry.MOB_EFFECT.getOptional(var2).orElseThrow(() -> {
         return ERROR_UNKNOWN_EFFECT.create(var2);
      });
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggestResource((Iterable)Registry.MOB_EFFECT.keySet(), var2);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
