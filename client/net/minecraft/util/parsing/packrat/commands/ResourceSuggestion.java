package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.SuggestionSupplier;

public interface ResourceSuggestion extends SuggestionSupplier<StringReader> {
   Stream<ResourceLocation> possibleResources();

   default Stream<String> possibleValues(ParseState<StringReader> var1) {
      return this.possibleResources().map(ResourceLocation::toString);
   }
}
