package net.minecraft.commands.arguments.coordinates;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.SerializableArgument;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Vec2Argument implements SerializableArgument.ContextFree<Coordinates> {
   private static final MapCodec<Vec2Argument> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.BOOL.fieldOf("auto_center").forGetter((o) -> o.centerCorrect)).apply(i, Vec2Argument::new));
   private static final StreamCodec<ByteBuf, Vec2Argument> STREAM_CODEC;
   public static final SerializableArgument.Type<Vec2Argument> TYPE;
   private static final Collection<String> EXAMPLES;
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE;
   private final boolean centerCorrect;

   @VisibleForTesting
   public Vec2Argument(final boolean centerCorrect) {
      super();
      this.centerCorrect = centerCorrect;
   }

   public SerializableArgument.Type<Vec2Argument> type() {
      return TYPE;
   }

   public static ArgumentType<Coordinates> vec2() {
      return vec2(true);
   }

   public static ArgumentType<Coordinates> vec2(final boolean centerCorrect) {
      return (new Vec2Argument(centerCorrect)).toBrigadier();
   }

   public static Vec2 getVec2(final CommandContext<CommandSourceStack> context, final String name) {
      Vec3 vec3 = ((Coordinates)context.getArgument(name, Coordinates.class)).getPosition((CommandSourceStack)context.getSource());
      return new Vec2((float)vec3.x, (float)vec3.z);
   }

   public Coordinates parse(final StringReader reader) throws CommandSyntaxException {
      int start = reader.getCursor();
      if (!reader.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(reader);
      } else {
         WorldCoordinate x = WorldCoordinate.parseDouble(reader, this.centerCorrect);
         if (reader.canRead() && reader.peek() == ' ') {
            reader.skip();
            WorldCoordinate z = WorldCoordinate.parseDouble(reader, this.centerCorrect);
            return new WorldCoordinates(x, new WorldCoordinate(true, 0.0), z);
         } else {
            reader.setCursor(start);
            throw ERROR_NOT_COMPLETE.createWithContext(reader);
         }
      }
   }

   public CompletableFuture<Suggestions> listSuggestions(final CommandContext<? extends SuggestionContext> context, final SuggestionsBuilder builder) {
      String remainder = builder.getRemaining();
      Collection<SuggestionContext.TextCoordinates> suggestedCoordinates;
      if (!remainder.isEmpty() && remainder.charAt(0) == '^') {
         suggestedCoordinates = Collections.singleton(SuggestionContext.TextCoordinates.DEFAULT_LOCAL);
      } else {
         suggestedCoordinates = ((SuggestionContext)context.getSource()).absoluteCoordinates();
      }

      return SuggestionContext.suggest2DCoordinates(remainder, suggestedCoordinates, builder, Commands.createValidator(this::parse));
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }

   static {
      STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, (o) -> o.centerCorrect, Vec2Argument::new);
      TYPE = new SerializableArgument.Type<Vec2Argument>(MAP_CODEC, STREAM_CODEC);
      EXAMPLES = List.of("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
      ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(Component.translatable("argument.pos2d.incomplete"));
   }
}
