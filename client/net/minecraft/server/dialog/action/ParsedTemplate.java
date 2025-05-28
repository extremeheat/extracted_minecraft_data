package net.minecraft.server.dialog.action;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import java.util.List;
import java.util.Map;
import net.minecraft.commands.functions.StringTemplate;

public class ParsedTemplate {
   public static final Codec<ParsedTemplate> CODEC;
   public static final Codec<String> VARIABLE_CODEC;
   private final String raw;
   private final StringTemplate parsed;

   private ParsedTemplate(String var1, StringTemplate var2) {
      super();
      this.raw = var1;
      this.parsed = var2;
   }

   private static DataResult<ParsedTemplate> parse(String var0) {
      StringTemplate var1;
      try {
         var1 = StringTemplate.fromString(var0);
      } catch (Exception var3) {
         return DataResult.error(() -> "Failed to parse template " + var0 + ": " + var3.getMessage());
      }

      return DataResult.success(new ParsedTemplate(var0, var1));
   }

   public String instantiate(Map<String, String> var1) {
      List var2 = this.parsed.variables().stream().map((var1x) -> (String)var1.getOrDefault(var1x, "")).toList();
      return this.parsed.substitute(var2);
   }

   static {
      CODEC = Codec.STRING.comapFlatMap(ParsedTemplate::parse, (var0) -> var0.raw);
      VARIABLE_CODEC = Codec.STRING.validate((var0) -> StringTemplate.isValidVariableName(var0) ? DataResult.success(var0) : DataResult.error(() -> var0 + " is not a valid input name"));
   }
}
