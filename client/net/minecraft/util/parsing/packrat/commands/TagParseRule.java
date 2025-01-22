package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class TagParseRule<T> implements Rule<StringReader, Dynamic<? extends T>> {
   private final DynamicOps<T> ops;

   public TagParseRule(DynamicOps<T> var1) {
      super();
      this.ops = var1;
   }

   public Optional<Dynamic<? extends T>> parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();
      int var2 = var1.mark();

      try {
         return Optional.of(new Dynamic(this.ops, TagParser.parseAsArgument(this.ops, (StringReader)var1.input())));
      } catch (Exception var4) {
         var1.errorCollector().store(var2, var4);
         return Optional.empty();
      }
   }
}
