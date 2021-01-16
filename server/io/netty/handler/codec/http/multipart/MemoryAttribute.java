package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpConstants;
import java.io.IOException;
import java.nio.charset.Charset;

public class MemoryAttribute extends AbstractMemoryHttpData implements Attribute {
   public MemoryAttribute(String var1) {
      this(var1, HttpConstants.DEFAULT_CHARSET);
   }

   public MemoryAttribute(String var1, long var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public MemoryAttribute(String var1, Charset var2) {
      super(var1, var2, 0L);
   }

   public MemoryAttribute(String var1, long var2, Charset var4) {
      super(var1, var4, var2);
   }

   public MemoryAttribute(String var1, String var2) throws IOException {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public MemoryAttribute(String var1, String var2, Charset var3) throws IOException {
      super(var1, var3, 0L);
      this.setValue(var2);
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.Attribute;
   }

   public String getValue() {
      return this.getByteBuf().toString(this.getCharset());
   }

   public void setValue(String var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("value");
      } else {
         byte[] var2 = var1.getBytes(this.getCharset());
         this.checkSize((long)var2.length);
         ByteBuf var3 = Unpooled.wrappedBuffer(var2);
         if (this.definedSize > 0L) {
            this.definedSize = (long)var3.readableBytes();
         }

         this.setContent(var3);
      }
   }

   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      int var3 = var1.readableBytes();
      this.checkSize(this.size + (long)var3);
      if (this.definedSize > 0L && this.definedSize < this.size + (long)var3) {
         this.definedSize = this.size + (long)var3;
      }

      super.addContent(var1, var2);
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof Attribute)) {
         return false;
      } else {
         Attribute var2 = (Attribute)var1;
         return this.getName().equalsIgnoreCase(var2.getName());
      }
   }

   public int compareTo(InterfaceHttpData var1) {
      if (!(var1 instanceof Attribute)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + var1.getHttpDataType());
      } else {
         return this.compareTo((Attribute)var1);
      }
   }

   public int compareTo(Attribute var1) {
      return this.getName().compareToIgnoreCase(var1.getName());
   }

   public String toString() {
      return this.getName() + '=' + this.getValue();
   }

   public Attribute copy() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.copy() : null);
   }

   public Attribute duplicate() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.duplicate() : null);
   }

   public Attribute retainedDuplicate() {
      ByteBuf var1 = this.content();
      if (var1 != null) {
         var1 = var1.retainedDuplicate();
         boolean var2 = false;

         Attribute var4;
         try {
            Attribute var3 = this.replace(var1);
            var2 = true;
            var4 = var3;
         } finally {
            if (!var2) {
               var1.release();
            }

         }

         return var4;
      } else {
         return this.replace((ByteBuf)null);
      }
   }

   public Attribute replace(ByteBuf var1) {
      MemoryAttribute var2 = new MemoryAttribute(this.getName());
      var2.setCharset(this.getCharset());
      if (var1 != null) {
         try {
            var2.setContent(var1);
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      }

      return var2;
   }

   public Attribute retain() {
      super.retain();
      return this;
   }

   public Attribute retain(int var1) {
      super.retain(var1);
      return this;
   }

   public Attribute touch() {
      super.touch();
      return this;
   }

   public Attribute touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
