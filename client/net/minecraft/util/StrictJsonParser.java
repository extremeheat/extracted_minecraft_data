package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.Strictness;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class StrictJsonParser {
   public StrictJsonParser() {
      super();
   }

   public static JsonElement parse(Reader var0) throws JsonIOException, JsonSyntaxException {
      try {
         JsonReader var1 = new JsonReader(var0);
         var1.setStrictness(Strictness.STRICT);
         JsonElement var2 = JsonParser.parseReader(var1);
         if (!var2.isJsonNull() && var1.peek() != JsonToken.END_DOCUMENT) {
            throw new JsonSyntaxException("Did not consume the entire document.");
         } else {
            return var2;
         }
      } catch (NumberFormatException | MalformedJsonException var3) {
         throw new JsonSyntaxException(var3);
      } catch (IOException var4) {
         throw new JsonIOException(var4);
      }
   }

   public static JsonElement parse(String var0) throws JsonSyntaxException {
      return parse((Reader)(new StringReader(var0)));
   }
}
