package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.ImmutableStringReader;
import com.mojang.brigadier.StringReader;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.Atom;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public abstract class ResourceLookupRule<C, V> implements Rule<StringReader, V>, ResourceSuggestion {
   private final Atom<ResourceLocation> idParser;
   protected final C context;

   protected ResourceLookupRule(Atom<ResourceLocation> var1, C var2) {
      super();
      this.idParser = var1;
      this.context = var2;
   }

   public Optional<V> parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();
      int var2 = var1.mark();
      Optional var3 = var1.parse(this.idParser);
      if (var3.isPresent()) {
         try {
            return Optional.of(this.validateElement((ImmutableStringReader)var1.input(), (ResourceLocation)var3.get()));
         } catch (Exception var5) {
            var1.errorCollector().store(var2, this, var5);
            return Optional.empty();
         }
      } else {
         var1.errorCollector().store(var2, this, ResourceLocation.ERROR_INVALID.createWithContext((ImmutableStringReader)var1.input()));
         return Optional.empty();
      }
   }

   protected abstract V validateElement(ImmutableStringReader var1, ResourceLocation var2) throws Exception;
}
