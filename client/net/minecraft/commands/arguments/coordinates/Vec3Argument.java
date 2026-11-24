package net.minecraft.commands.arguments.coordinates;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec3;

public class Vec3Argument implements SerializableArgument.ContextFree<Coordinates> {
   private static final MapCodec<Vec3Argument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.fieldOf("auto_center").forGetter((o) -> o.centerCorrect)).apply(i, Vec3Argument::new));
   private static final StreamCodec<ByteBuf, Vec3Argument> STREAM_CODEC;
   public static final SerializableArgument.Type<Vec3Argument> TYPE;
   private static final Collection<String> EXAMPLES;
   private final boolean centerCorrect;

   @VisibleForTesting
   public Vec3Argument(final boolean centerCorrect) {
      super();
      this.centerCorrect = centerCorrect;
   }

   public SerializableArgument.Type<Vec3Argument> type() {
      return TYPE;
   }

   public static ArgumentType<Coordinates> vec3() {
      return vec3(true);
   }

   public static ArgumentType<Coordinates> vec3(final boolean centerCorrect) {
      return (new Vec3Argument(centerCorrect)).toBrigadier();
   }

   public static Vec3 getVec3(final CommandContext<CommandSourceStack> context, final String name) {
      return ((Coordinates)context.getArgument(name, Coordinates.class)).getPosition((CommandSourceStack)context.getSource());
   }

   public static Coordinates getCoordinates(final CommandContext<?> context, final String name) {
      return (Coordinates)context.getArgument(name, Coordinates.class);
   }

   public Coordinates parse(final StringReader reader) throws CommandSyntaxException {
      return Coordinates.parseXYZ(reader, WorldCoordinate.doubleParser(this.centerCorrect), WorldCoordinate.DOUBLE_PARSER_CORNER);
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      String remainder = builder.getRemaining();
      return SuggestionContext.suggestCoordinates(remainder, Coordinates.suggestAbsoluteCoordinates(context, remainder), builder, Commands.createValidator(this::parse));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, (o) -> o.centerCorrect, Vec3Argument::new);
      TYPE = new SerializableArgument.Type<Vec3Argument>(MAP_CODEC, STREAM_CODEC);
      EXAMPLES = List.of("0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5");
   }
}
