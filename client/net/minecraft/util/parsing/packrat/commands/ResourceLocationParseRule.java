package net.minecraft.util.parsing.packrat.commands;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.parsing.packrat.ParseState;
import net.minecraft.util.parsing.packrat.Rule;

public class ResourceLocationParseRule implements Rule<StringReader, ResourceLocation> {
   public static final Rule<StringReader, ResourceLocation> INSTANCE = new ResourceLocationParseRule();

   private ResourceLocationParseRule() {
      super();
   }

   @Nullable
   public ResourceLocation parse(ParseState<StringReader> var1) {
      ((StringReader)var1.input()).skipWhitespace();

      try {
         return ResourceLocation.readNonEmpty((StringReader)var1.input());
      } catch (CommandSyntaxException var3) {
         return null;
      }
   }

   // $FF: synthetic method
   @Nullable
   public Object parse(final ParseState var1) {
      return this.parse(var1);
   }
}
