package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.AbstractReferenceCounted;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

final class InternalAttribute extends AbstractReferenceCounted implements InterfaceHttpData {
   private final List<ByteBuf> value = new ArrayList();
   private final Charset charset;
   private int size;

   InternalAttribute(Charset var1) {
      super();
      this.charset = var1;
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.InternalAttribute;
   }

   public void addValue(String var1) {
      if (var1 == null) {
         throw new NullPointerException("value");
      } else {
         ByteBuf var2 = Unpooled.copiedBuffer((CharSequence)var1, this.charset);
         this.value.add(var2);
         this.size += var2.readableBytes();
      }
   }

   public void addValue(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("value");
      } else {
         ByteBuf var3 = Unpooled.copiedBuffer((CharSequence)var1, this.charset);
         this.value.add(var2, var3);
         this.size += var3.readableBytes();
      }
   }

   public void setValue(String var1, int var2) {
      if (var1 == null) {
         throw new NullPointerException("value");
      } else {
         ByteBuf var3 = Unpooled.copiedBuffer((CharSequence)var1, this.charset);
         ByteBuf var4 = (ByteBuf)this.value.set(var2, var3);
         if (var4 != null) {
            this.size -= var4.readableBytes();
            var4.release();
         }

         this.size += var3.readableBytes();
      }
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof InternalAttribute)) {
         return false;
      } else {
         InternalAttribute var2 = (InternalAttribute)var1;
         return this.getName().equalsIgnoreCase(var2.getName());
      }
   }

   public int compareTo(InterfaceHttpData var1) {
      if (!(var1 instanceof InternalAttribute)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + var1.getHttpDataType());
      } else {
         return this.compareTo((InternalAttribute)var1);
      }
   }

   public int compareTo(InternalAttribute var1) {
      return this.getName().compareToIgnoreCase(var1.getName());
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.value.iterator();

      while(var2.hasNext()) {
         ByteBuf var3 = (ByteBuf)var2.next();
         var1.append(var3.toString(this.charset));
      }

      return var1.toString();
   }

   public int size() {
      return this.size;
   }

   public ByteBuf toByteBuf() {
      return Unpooled.compositeBuffer().addComponents((Iterable)this.value).writerIndex(this.size()).readerIndex(0);
   }

   public String getName() {
      return "InternalAttribute";
   }

   protected void deallocate() {
   }

   public InterfaceHttpData retain() {
      Iterator var1 = this.value.iterator();

      while(var1.hasNext()) {
         ByteBuf var2 = (ByteBuf)var1.next();
         var2.retain();
      }

      return this;
   }

   public InterfaceHttpData retain(int var1) {
      Iterator var2 = this.value.iterator();

      while(var2.hasNext()) {
         ByteBuf var3 = (ByteBuf)var2.next();
         var3.retain(var1);
      }

      return this;
   }

   public InterfaceHttpData touch() {
      Iterator var1 = this.value.iterator();

      while(var1.hasNext()) {
         ByteBuf var2 = (ByteBuf)var1.next();
         var2.touch();
      }

      return this;
   }

   public InterfaceHttpData touch(Object var1) {
      Iterator var2 = this.value.iterator();

      while(var2.hasNext()) {
         ByteBuf var3 = (ByteBuf)var2.next();
         var3.touch(var1);
      }

      return this;
   }
}
