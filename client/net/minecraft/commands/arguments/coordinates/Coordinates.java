package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.suggestions.SuggestionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public interface Coordinates {
   static Coordinates parseXYZ(final StringReader reader, final WorldCoordinate.Parser parser) throws CommandSyntaxException {
      return parseXYZ(reader, parser, parser);
   }

   static Coordinates parseXYZ(final StringReader reader, final WorldCoordinate.Parser parserXZ, final WorldCoordinate.Parser parserY) throws CommandSyntaxException {
      return (Coordinates)(reader.canRead() && reader.peek() == '^' ? LocalCoordinates.parseXYZ(reader) : WorldCoordinates.parseXYZ(reader, parserXZ, parserY));
   }

   static Coordinates parseXZ(final StringReader reader, final WorldCoordinate.Parser parser) throws CommandSyntaxException {
      return (Coordinates)(reader.canRead() && reader.peek() == '^' ? LocalCoordinates.parseXZ(reader) : WorldCoordinates.parseXZ(reader, parser));
   }

   static Collection<SuggestionContext.TextCoordinates> suggestAbsoluteCoordinates(final CommandContext<? extends SuggestionContext> context, final String remainder) {
      return (Collection<SuggestionContext.TextCoordinates>)(!remainder.isEmpty() && remainder.charAt(0) == '^' ? Collections.singleton(SuggestionContext.TextCoordinates.DEFAULT_LOCAL) : ((SuggestionContext)context.getSource()).absoluteCoordinates());
   }

   static Collection<SuggestionContext.TextCoordinates> suggestRelevantCoordinates(final CommandContext<? extends SuggestionContext> context, final String remainder) {
      return (Collection<SuggestionContext.TextCoordinates>)(!remainder.isEmpty() && remainder.charAt(0) == '^' ? Collections.singleton(SuggestionContext.TextCoordinates.DEFAULT_LOCAL) : ((SuggestionContext)context.getSource()).relevantCoordinates());
   }

   Vec3 getPosition(CommandSourceStack sender);

   Vec2 getRotation(CommandSourceStack sender);

   default BlockPos getBlockPos(final CommandSourceStack sender) {
      return BlockPos.containing(this.getPosition(sender));
   }

   boolean isXRelative();

   boolean isYRelative();

   boolean isZRelative();
}
