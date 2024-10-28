package net.minecraft.util.eventlog;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;

public class JsonEventLog<T> implements Closeable {
   private static final Gson GSON = new Gson();
   private final Codec<T> codec;
   final FileChannel channel;
   private final AtomicInteger referenceCount = new AtomicInteger(1);

   public JsonEventLog(Codec<T> var1, FileChannel var2) {
      super();
      this.codec = var1;
      this.channel = var2;
   }

   public static <T> JsonEventLog<T> open(Codec<T> var0, Path var1) throws IOException {
      FileChannel var2 = FileChannel.open(var1, StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE);
      return new JsonEventLog(var0, var2);
   }

   public void write(T var1) throws IOException {
      JsonElement var2 = (JsonElement)this.codec.encodeStart(JsonOps.INSTANCE, var1).getOrThrow(IOException::new);
      this.channel.position(this.channel.size());
      Writer var3 = Channels.newWriter(this.channel, StandardCharsets.UTF_8);
      GSON.toJson(var2, GSON.newJsonWriter(var3));
      var3.write(10);
      var3.flush();
   }

   public JsonEventLogReader<T> openReader() throws IOException {
      if (this.referenceCount.get() <= 0) {
         throw new IOException("Event log has already been closed");
      } else {
         this.referenceCount.incrementAndGet();
         final JsonEventLogReader var1 = JsonEventLogReader.create(this.codec, Channels.newReader(this.channel, StandardCharsets.UTF_8));
         return new JsonEventLogReader<T>() {
            private volatile long position;

            @Nullable
            public T next() throws IOException {
               Object var1x;
               try {
                  JsonEventLog.this.channel.position(this.position);
                  var1x = var1.next();
               } finally {
                  this.position = JsonEventLog.this.channel.position();
               }

               return var1x;
            }

            public void close() throws IOException {
               JsonEventLog.this.releaseReference();
            }
         };
      }
   }

   public void close() throws IOException {
      this.releaseReference();
   }

   void releaseReference() throws IOException {
      if (this.referenceCount.decrementAndGet() <= 0) {
         this.channel.close();
      }

   }
}
