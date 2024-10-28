package net.minecraft.util.eventlog;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import javax.annotation.Nullable;

public interface JsonEventLogReader<T> extends Closeable {
   static <T> JsonEventLogReader<T> create(final Codec<T> var0, Reader var1) {
      final JsonReader var2 = new JsonReader(var1);
      var2.setLenient(true);
      return new JsonEventLogReader<T>() {
         @Nullable
         public T next() throws IOException {
            JsonParseException var1;
            try {
               try {
                  if (!var2.hasNext()) {
                     return null;
                  }
               } catch (JsonParseException var3) {
                  var1 = var3;
                  throw new IOException(var1);
               }

               try {
                  JsonElement var5 = JsonParser.parseReader(var2);
                  return var0.parse(JsonOps.INSTANCE, var5).getOrThrow(IOException::new);
               } catch (JsonParseException var2x) {
                  var1 = var2x;
               }
            } catch (EOFException var4) {
               return null;
            }

            throw new IOException(var1);
         }

         public void close() throws IOException {
            var2.close();
         }
      };
   }

   @Nullable
   T next() throws IOException;
}
