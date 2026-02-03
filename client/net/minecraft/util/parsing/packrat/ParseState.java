package net.minecraft.util.parsing.packrat;

import java.util.Optional;
import org.jspecify.annotations.Nullable;

public interface ParseState<S> {
   Scope scope();

   ErrorCollector<S> errorCollector();

   default <T> Optional<T> parseTopRule(final NamedRule<S, T> rule) {
      T result = (T)this.parse(rule);
      if (result != null) {
         this.errorCollector().finish(this.mark());
      }

      if (!this.scope().hasOnlySingleFrame()) {
         throw new IllegalStateException("Malformed scope: " + String.valueOf(this.scope()));
      } else {
         return Optional.ofNullable(result);
      }
   }

   <T> @Nullable T parse(NamedRule<S, T> rule);

   S input();

   int mark();

   void restore(int mark);

   Control acquireControl();

   void releaseControl();

   ParseState<S> silent();
}
