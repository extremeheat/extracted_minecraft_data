package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

public class MixedFileUpload implements FileUpload {
   private FileUpload fileUpload;
   private final long limitSize;
   private final long definedSize;
   private long maxSize = -1L;

   public MixedFileUpload(String var1, String var2, String var3, String var4, Charset var5, long var6, long var8) {
      super();
      this.limitSize = var8;
      if (var6 > this.limitSize) {
         this.fileUpload = new DiskFileUpload(var1, var2, var3, var4, var5, var6);
      } else {
         this.fileUpload = new MemoryFileUpload(var1, var2, var3, var4, var5, var6);
      }

      this.definedSize = var6;
   }

   public long getMaxSize() {
      return this.maxSize;
   }

   public void setMaxSize(long var1) {
      this.maxSize = var1;
      this.fileUpload.setMaxSize(var1);
   }

   public void checkSize(long var1) throws IOException {
      if (this.maxSize >= 0L && var1 > this.maxSize) {
         throw new IOException("Size exceed allowed maximum capacity");
      }
   }

   public void addContent(ByteBuf var1, boolean var2) throws IOException {
      if (this.fileUpload instanceof MemoryFileUpload) {
         this.checkSize(this.fileUpload.length() + (long)var1.readableBytes());
         if (this.fileUpload.length() + (long)var1.readableBytes() > this.limitSize) {
            DiskFileUpload var3 = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
            var3.setMaxSize(this.maxSize);
            ByteBuf var4 = this.fileUpload.getByteBuf();
            if (var4 != null && var4.isReadable()) {
               var3.addContent(var4.retain(), false);
            }

            this.fileUpload.release();
            this.fileUpload = var3;
         }
      }

      this.fileUpload.addContent(var1, var2);
   }

   public void delete() {
      this.fileUpload.delete();
   }

   public byte[] get() throws IOException {
      return this.fileUpload.get();
   }

   public ByteBuf getByteBuf() throws IOException {
      return this.fileUpload.getByteBuf();
   }

   public Charset getCharset() {
      return this.fileUpload.getCharset();
   }

   public String getContentType() {
      return this.fileUpload.getContentType();
   }

   public String getContentTransferEncoding() {
      return this.fileUpload.getContentTransferEncoding();
   }

   public String getFilename() {
      return this.fileUpload.getFilename();
   }

   public String getString() throws IOException {
      return this.fileUpload.getString();
   }

   public String getString(Charset var1) throws IOException {
      return this.fileUpload.getString(var1);
   }

   public boolean isCompleted() {
      return this.fileUpload.isCompleted();
   }

   public boolean isInMemory() {
      return this.fileUpload.isInMemory();
   }

   public long length() {
      return this.fileUpload.length();
   }

   public long definedLength() {
      return this.fileUpload.definedLength();
   }

   public boolean renameTo(File var1) throws IOException {
      return this.fileUpload.renameTo(var1);
   }

   public void setCharset(Charset var1) {
      this.fileUpload.setCharset(var1);
   }

   public void setContent(ByteBuf var1) throws IOException {
      this.checkSize((long)var1.readableBytes());
      if ((long)var1.readableBytes() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
         FileUpload var2 = this.fileUpload;
         this.fileUpload = new DiskFileUpload(var2.getName(), var2.getFilename(), var2.getContentType(), var2.getContentTransferEncoding(), var2.getCharset(), this.definedSize);
         this.fileUpload.setMaxSize(this.maxSize);
         var2.release();
      }

      this.fileUpload.setContent(var1);
   }

   public void setContent(File var1) throws IOException {
      this.checkSize(var1.length());
      if (var1.length() > this.limitSize && this.fileUpload instanceof MemoryFileUpload) {
         FileUpload var2 = this.fileUpload;
         this.fileUpload = new DiskFileUpload(var2.getName(), var2.getFilename(), var2.getContentType(), var2.getContentTransferEncoding(), var2.getCharset(), this.definedSize);
         this.fileUpload.setMaxSize(this.maxSize);
         var2.release();
      }

      this.fileUpload.setContent(var1);
   }

   public void setContent(InputStream var1) throws IOException {
      if (this.fileUpload instanceof MemoryFileUpload) {
         FileUpload var2 = this.fileUpload;
         this.fileUpload = new DiskFileUpload(this.fileUpload.getName(), this.fileUpload.getFilename(), this.fileUpload.getContentType(), this.fileUpload.getContentTransferEncoding(), this.fileUpload.getCharset(), this.definedSize);
         this.fileUpload.setMaxSize(this.maxSize);
         var2.release();
      }

      this.fileUpload.setContent(var1);
   }

   public void setContentType(String var1) {
      this.fileUpload.setContentType(var1);
   }

   public void setContentTransferEncoding(String var1) {
      this.fileUpload.setContentTransferEncoding(var1);
   }

   public void setFilename(String var1) {
      this.fileUpload.setFilename(var1);
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return this.fileUpload.getHttpDataType();
   }

   public String getName() {
      return this.fileUpload.getName();
   }

   public int hashCode() {
      return this.fileUpload.hashCode();
   }

   public boolean equals(Object var1) {
      return this.fileUpload.equals(var1);
   }

   public int compareTo(InterfaceHttpData var1) {
      return this.fileUpload.compareTo(var1);
   }

   public String toString() {
      return "Mixed: " + this.fileUpload;
   }

   public ByteBuf getChunk(int var1) throws IOException {
      return this.fileUpload.getChunk(var1);
   }

   public File getFile() throws IOException {
      return this.fileUpload.getFile();
   }

   public FileUpload copy() {
      return this.fileUpload.copy();
   }

   public FileUpload duplicate() {
      return this.fileUpload.duplicate();
   }

   public FileUpload retainedDuplicate() {
      return this.fileUpload.retainedDuplicate();
   }

   public FileUpload replace(ByteBuf var1) {
      return this.fileUpload.replace(var1);
   }

   public ByteBuf content() {
      return this.fileUpload.content();
   }

   public int refCnt() {
      return this.fileUpload.refCnt();
   }

   public FileUpload retain() {
      this.fileUpload.retain();
      return this;
   }

   public FileUpload retain(int var1) {
      this.fileUpload.retain(var1);
      return this;
   }

   public FileUpload touch() {
      this.fileUpload.touch();
      return this;
   }

   public FileUpload touch(Object var1) {
      this.fileUpload.touch(var1);
      return this;
   }

   public boolean release() {
      return this.fileUpload.release();
   }

   public boolean release(int var1) {
      return this.fileUpload.release(var1);
   }
}
