package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.DataResult;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.ParseContext;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;

public class ParticleArgument implements SerializableArgument<ParticleOptions> {
   private static final ParticleArgument INSTANCE = new ParticleArgument();
   public static final SerializableArgument.Type<ParticleArgument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE;
   public static final DynamicCommandExceptionType ERROR_INVALID_OPTIONS;
   private static final TagParser<?> VALUE_PARSER;

   private ParticleArgument() {
      super();
   }

   public SerializableArgument.Type<ParticleArgument> type() {
      return TYPE;
   }

   public static ArgumentType<ParticleOptions> particle() {
      return INSTANCE.toBrigadier();
   }

   public static ParticleOptions getParticle(final CommandContext<?> context, final String name) {
      return (ParticleOptions)context.getArgument(name, ParticleOptions.class);
   }

   public ParticleOptions parse(final StringReader reader, final ParseContext context) throws CommandSyntaxException {
      HolderGetter.Provider registries = context.holderGetters();
      ParticleType<?> type = readParticleType(reader, registries.lookupOrThrow(Registries.PARTICLE_TYPE));
      return readParticle(VALUE_PARSER, reader, type, registries);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   private static ParticleType<?> readParticleType(final StringReader reader, final HolderGetter<ParticleType<?>> particles) throws CommandSyntaxException {
      Identifier id = Identifier.read(reader);
      ResourceKey<ParticleType<?>> key = ResourceKey.create(Registries.PARTICLE_TYPE, id);
      return (ParticleType)((Holder.Reference)particles.get(key).orElseThrow(() -> ERROR_UNKNOWN_PARTICLE.createWithContext(reader, id))).value();
   }

   private static <T extends ParticleOptions, O> T readParticle(final TagParser<O> parser, final StringReader reader, final ParticleType<T> type, final HolderGetter.Provider registries) throws CommandSyntaxException {
      RegistryOps<O> ops = registries.<O>createSerializationContext(parser.getOps());
      O extraData;
      if (reader.canRead() && reader.peek() == '{') {
         extraData = parser.parseAsArgument(reader);
      } else {
         extraData = (O)ops.emptyMap();
      }

      DataResult var10000 = type.codec().codec().parse(ops, extraData);
      DynamicCommandExceptionType var10001 = ERROR_INVALID_OPTIONS;
      Objects.requireNonNull(var10001);
      return (T)(var10000.getOrThrow(var10001::create));
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      return ((SuggestionContext)context.getSource()).suggestRegistryElements(context, Registries.PARTICLE_TYPE, SuggestionContext.ElementSuggestionType.ELEMENTS, builder);
   }

   static {
      TYPE = SerializableArgument.<ParticleArgument>singletonType(INSTANCE);
      EXAMPLES = List.of("foo", "foo:bar", "particle{foo:bar}");
      ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((value) -> Component.translatableEscape("particle.notFound", value));
      ERROR_INVALID_OPTIONS = new DynamicCommandExceptionType((message) -> Component.translatableEscape("particle.invalidOptions", message));
      VALUE_PARSER = TagParser.create(NbtOps.INSTANCE);
   }
}
