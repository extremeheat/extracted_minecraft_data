package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import net.minecraft.util.parsing.packrat.CachedParseState;
import net.minecraft.util.parsing.packrat.ErrorCollector;

public class StringReaderParserState extends CachedParseState<StringReader> {
   private final StringReader input;

   public StringReaderParserState(ErrorCollector<StringReader> var1, StringReader var2) {
      super(var1);
      this.input = var2;
   }

   public StringReader input() {
      return this.input;
   }

   public int mark() {
      return this.input.getCursor();
   }

   public void restore(int var1) {
      this.input.setCursor(var1);
   }

   // $FF: synthetic method
   public Object input() {
      return this.input();
   }
}
