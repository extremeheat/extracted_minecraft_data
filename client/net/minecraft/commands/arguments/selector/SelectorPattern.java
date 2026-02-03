package net.minecraft.commands.arguments.selector;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public record SelectorPattern(String pattern, EntitySelector resolved) {
   public static final Codec<SelectorPattern> CODEC;

   public SelectorPattern {
      super();
   }

   public static DataResult<SelectorPattern> parse(final String pattern) {
      try {
         EntitySelectorParser parser = new EntitySelectorParser(new StringReader(pattern), true);
         return DataResult.success(new SelectorPattern(pattern, parser.parse()));
      } catch (CommandSyntaxException ex) {
         return DataResult.error(() -> "Invalid selector component: " + pattern + ": " + ex.getMessage());
      }
   }

   public boolean equals(final Object obj) {
      boolean var10000;
      if (obj instanceof SelectorPattern selector) {
         if (this.pattern.equals(selector.pattern)) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }

   public int hashCode() {
      return this.pattern.hashCode();
   }

   public String toString() {
      return this.pattern;
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(SelectorPattern::parse, SelectorPattern::pattern);
   }
}
