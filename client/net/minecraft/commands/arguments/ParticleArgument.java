package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType<ParticleOptions> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("particle.notFound", var0));
   public static final DynamicCommandExceptionType ERROR_INVALID_OPTIONS = new DynamicCommandExceptionType((var0) -> Component.translatableEscape("particle.invalidOptions", var0));
   private final HolderLookup.Provider registries;
   private static final TagParser<?> VALUE_PARSER;

   public ParticleArgument(CommandBuildContext var1) {
      super();
      this.registries = var1;
   }

   public static ParticleArgument particle(CommandBuildContext var0) {
      return new ParticleArgument(var0);
   }

   public static ParticleOptions getParticle(CommandContext<CommandSourceStack> var0, String var1) {
      return (ParticleOptions)var0.getArgument(var1, ParticleOptions.class);
   }

   public ParticleOptions parse(StringReader var1) throws CommandSyntaxException {
      return readParticle(var1, this.registries);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   public static ParticleOptions readParticle(StringReader var0, HolderLookup.Provider var1) throws CommandSyntaxException {
      ParticleType var2 = readParticleType(var0, var1.lookupOrThrow(Registries.PARTICLE_TYPE));
      return readParticle(VALUE_PARSER, var0, var2, var1);
   }

   private static ParticleType<?> readParticleType(StringReader var0, HolderLookup<ParticleType<?>> var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var0);
      ResourceKey var3 = ResourceKey.create(Registries.PARTICLE_TYPE, var2);
      return (ParticleType)((Holder.Reference)var1.get(var3).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.createWithContext(var0, var2))).value();
   }

   private static <T extends ParticleOptions, O> T readParticle(TagParser<O> var0, StringReader var1, ParticleType<T> var2, HolderLookup.Provider var3) throws CommandSyntaxException {
      RegistryOps var5 = var3.createSerializationContext(var0.getOps());
      Object var4;
      if (var1.canRead() && var1.peek() == '{') {
         var4 = var0.parseAsArgument(var1);
      } else {
         var4 = var5.emptyMap();
      }

      DataResult var10000 = var2.codec().codec().parse(var5, var4);
      DynamicCommandExceptionType var10001 = ERROR_INVALID_OPTIONS;
      Objects.requireNonNull(var10001);
      return (T)(var10000.getOrThrow(var10001::create));
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      HolderLookup.RegistryLookup var3 = this.registries.lookupOrThrow(Registries.PARTICLE_TYPE);
      return SharedSuggestionProvider.suggestResource(var3.listElementIds().map(ResourceKey::location), var2);
   }

   // $FF: synthetic method
   public Object parse(final StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      VALUE_PARSER = TagParser.create(NbtOps.INSTANCE);
   }
}
