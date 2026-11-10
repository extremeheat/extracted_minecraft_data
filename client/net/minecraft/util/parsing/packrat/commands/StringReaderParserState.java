package net.minecraft.util.parsing.packrat.commands;

import net.minecraft.util.parsing.packrat.CachedParseState;
import net.minecraft.util.parsing.packrat.ErrorCollector;

public class StringReaderParserState<S extends CommandParserState> extends CachedParseState<S> {
   private final S input;

   public StringReaderParserState(final ErrorCollector errorCollector, final S input) {
      super(errorCollector);
      this.input = input;
   }

   public S input() {
      return this.input;
   }

   public int mark() {
      return this.input.reader().getCursor();
   }

   public void restore(final int mark) {
      this.input.reader().setCursor(mark);
   }
}
