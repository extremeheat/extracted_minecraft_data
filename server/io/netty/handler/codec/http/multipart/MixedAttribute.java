package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpConstants;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedAttribute implements Attribute {
   private Attribute attribute;
   private final long limitSize;
   private long maxSize;

   public MixedAttribute(String var1, long var2) {
      this(var1, var2, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String var1, long var2, long var4) {
      this(var1, var2, var4, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String var1, long var2, Charset var4) {
      super();
      this.maxSize = -1L;
      this.limitSize = var2;
      this.attribute = new MemoryAttribute(var1, var4);
   }

   public MixedAttribute(String var1, long var2, long var4, Charset var6) {
      super();
      this.maxSize = -1L;
      this.limitSize = var4;
      this.attribute = new MemoryAttribute(var1, var2, var6);
   }

   public MixedAttribute(String var1, String var2, long var3) {
      this(var1, var2, var3, HttpConstants.DEFAULT_CHARSET);
   }

   public MixedAttribute(String var1, String var2, long var3, Charset var5) {
      super();
      this.maxSize = -1L;
      this.limitSize = var3;
      if ((long)var2.length() > this.limitSize) {
         try {
            this.attribute = new DiskAttribute(var1, var2, var5);
         } catch (IOException var10) {
            try {
               this.attribute = new MemoryAttribute(var1, var2, var5);
            } catch (IOException var9) {
               throw new IllegalArgumentException(var10);
            }
         }
      } else {
         try {
            this.attribute = new MemoryAttribute(var1, var2, var5);
         } catch (IOException var8) {
            throw new IllegalArgumentException(var8);
         }
      }

   }

   public long getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(long var1) {
      this.maxSize = var1;
      this.attribute.setMaxSize(var1);
   }

   public void checkSize(long var1) throws IOException {
      if (this.maxSize >= 0L && var1 > this.maxSize) {
         throw new IOException("Size exceed allowed maximum capacity");
      }
   }

   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      if (this.attribute instanceof MemoryAttribute) {
         this.checkSize(this.attribute.length() + (long)var1.readableBytes());
         if (this.attribute.length() + (long)var1.readableBytes() > this.limitSize) {
            DiskAttribute var3 = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
            var3.setMaxSize(this.maxSize);
            if (((MemoryAttribute)this.attribute).getByteBuf() != null) {
               var3.addContent(((MemoryAttribute)this.attribute).getByteBuf(), false);
            }

            this.attribute = var3;
         }
      }

      this.attribute.addContent(var1, var2);
   }

   public void delete() {
      this.attribute.delete();
   }

   public byte[] get() throws IOException {
      return this.attribute.get();
   }

   public ByteBuf getByteBuf() throws IOException {
      return this.attribute.getByteBuf();
   }

   public Charset getCharset() {
      return this.attribute.getCharset();
   }

   public String getString() throws IOException {
      return this.attribute.getString();
   }

   public String getString(Charset var1) throws IOException {
      return this.attribute.getString(var1);
   }

   public boolean isCompleted() {
      return this.attribute.isCompleted();
   }

   public boolean isInMemory() {
      return this.attribute.isInMemory();
   }

   public long length() {
      return this.attribute.length();
   }

   public long definedLength() {
      return this.attribute.definedLength();
   }

   public boolean renameTo(File var1) throws IOException {
      return this.attribute.renameTo(var1);
   }

   public void setCharset(Charset var1) {
      this.attribute.setCharset(var1);
   }

   public void setContent(ByteBuf var1) throws IOException {
      this.checkSize((long)var1.readableBytes());
      if ((long)var1.readableBytes() > this.limitSize && this.attribute instanceof MemoryAttribute) {
         this.attribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
         this.attribute.setMaxSize(this.maxSize);
      }

      this.attribute.setContent(var1);
   }

   public void setContent(File var1) throws IOException {
      this.checkSize(var1.length());
      if (var1.length() > this.limitSize && this.attribute instanceof MemoryAttribute) {
         this.attribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
         this.attribute.setMaxSize(this.maxSize);
      }

      this.attribute.setContent(var1);
   }

   public void setContent(InputStream var1) throws IOException {
      if (this.attribute instanceof MemoryAttribute) {
         this.attribute = new DiskAttribute(this.attribute.getName(), this.attribute.definedLength());
         this.attribute.setMaxSize(this.maxSize);
      }

      this.attribute.setContent(var1);
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return this.attribute.getHttpDataType();
   }

   public String getName() {
      return this.attribute.getName();
   }

   public int hashCode() {
      return this.attribute.hashCode();
   }

   public boolean equals(Object var1) {
      return this.attribute.equals(var1);
   }

   public int compareTo(InterfaceHttpData var1) {
      return this.attribute.compareTo(var1);
   }

   public String toString() {
      return "Mixed: " + this.attribute;
   }

   public String getValue() throws IOException {
      return this.attribute.getValue();
   }

   public void setValue(String var1) throws IOException {
      if (var1 != null) {
         this.checkSize((long)var1.getBytes().length);
      }

      this.attribute.setValue(var1);
   }

   public ByteBuf getChunk(int var1) throws IOException {
      return this.attribute.getChunk(var1);
   }

   public File getFile() throws IOException {
      return this.attribute.getFile();
   }

   public Attribute copy() {
      return this.attribute.copy();
   }

   public Attribute duplicate() {
      return this.attribute.duplicate();
   }

   public Attribute retainedDuplicate() {
      return this.attribute.retainedDuplicate();
   }

   public Attribute replace(ByteBuf var1) {
      return this.attribute.replace(var1);
   }

   public ByteBuf content() {
      return this.attribute.content();
   }

   public int refCnt() {
      return this.attribute.refCnt();
   }

   public Attribute retain() {
      this.attribute.retain();
      return this;
   }

   public Attribute retain(int var1) {
      this.attribute.retain(var1);
      return this;
   }

   public Attribute touch() {
      this.attribute.touch();
      return this;
   }

   public Attribute touch(Object var1) {
      this.attribute.touch(var1);
      return this;
   }

   public boolean release() {
      return this.attribute.release();
   }

   public boolean release(int var1) {
      return this.attribute.release(var1);
   }
}
