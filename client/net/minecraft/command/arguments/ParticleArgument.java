package net.minecraft.command.arguments;

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
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.TextComponentTranslation;

public class ParticleArgument implements ArgumentType<IParticleData> {
   private static final Collection<String> field_201320_b = Arrays.asList("foo", "foo:bar", "particle with options");
   public static final DynamicCommandExceptionType field_197191_a = new DynamicCommandExceptionType((var0) -> {
      return new TextComponentTranslation("particle.notFound", new Object[]{var0});
   });

   public ParticleArgument() {
      super();
   }

   public static ParticleArgument func_197190_a() {
      return new ParticleArgument();
   }

   public static IParticleData func_197187_a(CommandContext<CommandSource> var0, String var1) {
      return (IParticleData)var0.getArgument(var1, IParticleData.class);
   }

   public IParticleData parse(StringReader var1) throws CommandSyntaxException {
      return func_197189_a(var1);
   }

   public Collection<String> getExamples() {
      return field_201320_b;
   }

   public static IParticleData func_197189_a(StringReader var0) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocation.func_195826_a(var0);
      ParticleType var2 = (ParticleType)IRegistry.field_212632_u.func_212608_b(var1);
      if (var2 == null) {
         throw field_197191_a.create(var1);
      } else {
         return func_199816_a(var0, var2);
      }
   }

   private static <T extends IParticleData> T func_199816_a(StringReader var0, ParticleType<T> var1) throws CommandSyntaxException {
      return var1.func_197571_g().func_197544_b(var1, var0);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      return ISuggestionProvider.func_197014_a(IRegistry.field_212632_u.func_148742_b(), var2);
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
