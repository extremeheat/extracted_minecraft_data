package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import net.minecraft.util.parsing.packrat.Dictionary;
import net.minecraft.util.parsing.packrat.ErrorCollector;
import net.minecraft.util.parsing.packrat.ParseState;

public class StringReaderParserState extends ParseState<StringReader> {
   private final StringReader input;

   public StringReaderParserState(Dictionary<StringReader> var1, ErrorCollector<StringReader> var2, StringReader var3) {
      super(var1, var2);
      this.input = var3;
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
