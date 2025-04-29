package net.minecraft.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.Reader;

public class LenientJsonParser {
   public LenientJsonParser() {
      super();
   }

   public static JsonElement parse(Reader var0) throws JsonIOException, JsonSyntaxException {
      return JsonParser.parseReader(var0);
   }

   public static JsonElement parse(String var0) throws JsonSyntaxException {
      return JsonParser.parseString(var0);
   }
}
