package net.minecraft.util.parsing.packrat;

import java.util.Optional;
import javax.annotation.Nullable;

public interface ParseState<S> {
   Scope scope();

   ErrorCollector<S> errorCollector();

   default <T> Optional<T> parseTopRule(NamedRule<S, T> var1) {
      Object var2 = this.parse(var1);
      if (var2 != null) {
         this.errorCollector().finish(this.mark());
      }

      if (!this.scope().hasOnlySingleFrame()) {
         throw new IllegalStateException("Malformed scope: " + String.valueOf(this.scope()));
      } else {
         return Optional.ofNullable(var2);
      }
   }

   @Nullable
   <T> T parse(NamedRule<S, T> var1);

   S input();

   int mark();

   void restore(int var1);

   Control acquireControl();

   void releaseControl();

   ParseState<S> silent();
}
