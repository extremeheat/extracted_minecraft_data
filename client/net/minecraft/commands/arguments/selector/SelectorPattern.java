package net.minecraft.commands.arguments.selector;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public record SelectorPattern(String pattern, EntitySelector resolved) {
   public static final Codec<SelectorPattern> CODEC;

   public SelectorPattern(String var1, EntitySelector var2) {
      super();
      this.pattern = var1;
      this.resolved = var2;
   }

   public static DataResult<SelectorPattern> parse(String var0) {
      try {
         EntitySelectorParser var1 = new EntitySelectorParser(new StringReader(var0), true);
         return DataResult.success(new SelectorPattern(var0, var1.parse()));
      } catch (CommandSyntaxException var2) {
         return DataResult.error(() -> "Invalid selector component: " + var0 + ": " + var2.getMessage());
      }
   }

   public boolean equals(Object var1) {
      boolean var10000;
      if (var1 instanceof SelectorPattern var2) {
         if (this.pattern.equals(var2.pattern)) {
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
