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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((var0) -> {
      return new TranslatableComponent("particle.notFound", new Object[]{var0});
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static ParticleOptions getParticle(CommandContext var0, String var1) {
      return (ParticleOptions)var0.getArgument(var1, ParticleOptions.class);
   }

   public ParticleOptions parse(StringReader var1) throws CommandSyntaxException {
      return readParticle(var1);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static ParticleOptions readParticle(StringReader var0) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocation.read(var0);
      ParticleType var2 = (ParticleType)Registry.PARTICLE_TYPE.getOptional(var1).orElseThrow(() -> {
         return ERROR_UNKNOWN_PARTICLE.create(var1);
      });
      return readParticle(var0, var2);
   }

   private static ParticleOptions readParticle(StringReader var0, ParticleType var1) throws CommandSyntaxException {
      return var1.getDeserializer().fromCommand(var1, var0);
   }

   public CompletableFuture listSuggestions(CommandContext var1, SuggestionsBuilder var2) {
      return SharedSuggestionProvider.suggestResource((Iterable)Registry.PARTICLE_TYPE.keySet(), var2);
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
