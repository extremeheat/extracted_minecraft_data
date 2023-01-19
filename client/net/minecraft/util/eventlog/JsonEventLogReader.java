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
import net.minecraft.Util;

public interface JsonEventLogReader<T> extends Closeable {
   static <T> JsonEventLogReader<T> create(final Codec<T> var0, Reader var1) {
      final JsonReader var2 = new JsonReader(var1);
      var2.setLenient(true);
      return new JsonEventLogReader<T>() {
         @Nullable
         @Override
         public T next() throws IOException {
            try {
               if (!var2.hasNext()) {
                  return null;
               } else {
                  JsonElement var1 = JsonParser.parseReader(var2);
                  return Util.getOrThrow(var0.parse(JsonOps.INSTANCE, var1), IOException::new);
               }
            } catch (JsonParseException var2x) {
               throw new IOException(var2x);
            } catch (EOFException var3) {
               return null;
            }
         }

         @Override
         public void close() throws IOException {
            var2.close();
         }
      };
   }

   @Nullable
   T next() throws IOException;
}
