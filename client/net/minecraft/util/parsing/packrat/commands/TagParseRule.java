package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class TagParseRule<T> implements Rule<StringReader, Dynamic<?>> {
   private final TagParser<T> parser;

   public TagParseRule(DynamicOps<T> var1) {
      super();
      this.parser = TagParser.<T>create(var1);
   }

   @Nullable
   public Dynamic<T> parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();
      int var2 = var1.mark();

      try {
         return new Dynamic(this.parser.getOps(), this.parser.parseAsArgument((StringReader)var1.input()));
      } catch (Exception var4) {
         var1.errorCollector().store(var2, var4);
         return null;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParseState var1) {
      return this.parse(var1);
   }
}
