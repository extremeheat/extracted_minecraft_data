package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import java.util.Optional;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class TagParseRule implements Rule<StringReader, Tag> {
   public static final Rule<StringReader, Tag> INSTANCE = new TagParseRule();

   private TagParseRule() {
      super();
   }

   public Optional<Tag> parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();
      int var2 = var1.mark();

      try {
         return Optional.of((new TagParser((StringReader)var1.input())).readValue());
      } catch (Exception var4) {
         var1.errorCollector().store(var2, var4);
         return Optional.empty();
      }
   }
}
