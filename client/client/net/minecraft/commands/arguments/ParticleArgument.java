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
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType<ParticleOptions> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "particle{foo:bar}");
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("particle.notFound", var0)
   );
   public static final DynamicCommandExceptionType ERROR_INVALID_OPTIONS = new DynamicCommandExceptionType(
      var0 -> Component.translatableEscape("particle.invalidOptions", var0)
   );
   private final HolderLookup.Provider registries;

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
      return readParticle(var0, var2, var1);
   }

   private static ParticleType<?> readParticleType(StringReader var0, HolderLookup<ParticleType<?>> var1) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(var0);
      ResourceKey var3 = ResourceKey.create(Registries.PARTICLE_TYPE, var2);
      return (ParticleType<?>)((Holder.Reference)var1.get(var3).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.createWithContext(var0, var2))).value();
   }

   private static <T extends ParticleOptions> T readParticle(StringReader var0, ParticleType<T> var1, HolderLookup.Provider var2) throws CommandSyntaxException {
      CompoundTag var3;
      if (var0.canRead() && var0.peek() == '{') {
         var3 = new TagParser(var0).readStruct();
      } else {
         var3 = new CompoundTag();
      }

      return (T)var1.codec().codec().parse(var2.createSerializationContext(NbtOps.INSTANCE), var3).getOrThrow(ERROR_INVALID_OPTIONS::create);
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> var1, SuggestionsBuilder var2) {
      HolderLookup.RegistryLookup var3 = this.registries.lookupOrThrow(Registries.PARTICLE_TYPE);
      return SharedSuggestionProvider.suggestResource(var3.listElementIds().map(ResourceKey::location), var2);
   }
}
