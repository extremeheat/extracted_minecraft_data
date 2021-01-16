package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import io.netty.util.AbstractReferenceCounted;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public abstract class AbstractHttpData extends AbstractReferenceCounted implements HttpData {
   private static final Pattern STRIP_PATTERN = Pattern.compile("(?:^\\s+|\\s+$|\\n)");
   private static final Pattern REPLACE_PATTERN = Pattern.compile("[\\r\\t]");
   private final String name;
   protected long definedSize;
   protected long size;
   private Charset charset;
   private boolean completed;
   private long maxSize;

   protected AbstractHttpData(String var1, Charset var2, long var3) {
      super();
      this.charset = HttpConstants.DEFAULT_CHARSET;
      this.maxSize = -1L;
      if (var1 == null) {
         throw new NullPointerException("name");
      } else {
         var1 = REPLACE_PATTERN.matcher(var1).replaceAll(" ");
         var1 = STRIP_PATTERN.matcher(var1).replaceAll("");
         if (var1.isEmpty()) {
            throw new IllegalArgumentException("empty name");
         } else {
            this.name = var1;
            if (var2 != null) {
               this.setCharset(var2);
            }

            this.definedSize = var3;
         }
      }
   }

   public long getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(long var1) {
      this.maxSize = var1;
   }

   public void checkSize(long var1) throws IOException {
      if (this.maxSize >= 0L && var1 > this.maxSize) {
         throw new IOException("Size exceed allowed maximum capacity");
      }
   }

   public String getName() {
      return this.name;
   }

   public boolean isCompleted() {
      return this.completed;
   }

   protected void setCompleted() {
      this.completed = true;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public void setCharset(Charset var1) {
      if (var1 == null) {
         throw new NullPointerException("charset");
      } else {
         this.charset = var1;
      }
   }

   public long length() {
      return this.size;
   }

   public long definedLength() {
      return this.definedSize;
   }

   public ByteBuf content() {
      try {
         return this.getByteBuf();
      } catch (IOException var2) {
         throw new ChannelException(var2);
      }
   }

   protected void deallocate() {
      this.delete();
   }

   public HttpData retain() {
      super.retain();
      return this;
   }

   public HttpData retain(int var1) {
      super.retain(var1);
      return this;
   }

   public abstract HttpData touch();

   public abstract HttpData touch(Object var1);
}
