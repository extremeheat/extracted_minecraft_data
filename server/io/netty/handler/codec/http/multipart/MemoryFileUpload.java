package io.netty.handler.codec.http.multipart;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import java.io.IOException;
import java.nio.charset.Charset;

public class MemoryFileUpload extends AbstractMemoryHttpData implements FileUpload {
   private String filename;
   private String contentType;
   private String contentTransferEncoding;

   public MemoryFileUpload(String var1, String var2, String var3, String var4, Charset var5, long var6) {
      super(var1, var5, var6);
      this.setFilename(var2);
      this.setContentType(var3);
      this.setContentTransferEncoding(var4);
   }

   public InterfaceHttpData.HttpDataType getHttpDataType() {
      return InterfaceHttpData.HttpDataType.FileUpload;
   }

   public String getFilename() {
      return this.filename;
   }

   public void setFilename(String var1) {
      if (var1 == null) {
         throw new NullPointerException("filename");
      } else {
         this.filename = var1;
      }
   }

   public int hashCode() {
      return FileUploadUtil.hashCode(this);
   }

   public boolean equals(Object var1) {
      return var1 instanceof FileUpload && FileUploadUtil.equals(this, (FileUpload)var1);
   }

   public int compareTo(InterfaceHttpData var1) {
      if (!(var1 instanceof FileUpload)) {
         throw new ClassCastException("Cannot compare " + this.getHttpDataType() + " with " + var1.getHttpDataType());
      } else {
         return this.compareTo((FileUpload)var1);
      }
   }

   public int compareTo(FileUpload var1) {
      return FileUploadUtil.compareTo(this, var1);
   }

   public void setContentType(String var1) {
      if (var1 == null) {
         throw new NullPointerException("contentType");
      } else {
         this.contentType = var1;
      }
   }

   public String getContentType() {
      return this.contentType;
   }

   public String getContentTransferEncoding() {
      return this.contentTransferEncoding;
   }

   public void setContentTransferEncoding(String var1) {
      this.contentTransferEncoding = var1;
   }

   public String toString() {
      return HttpHeaderNames.CONTENT_DISPOSITION + ": " + HttpHeaderValues.FORM_DATA + "; " + HttpHeaderValues.NAME + "=\"" + this.getName() + "\"; " + HttpHeaderValues.FILENAME + "=\"" + this.filename + "\"\r\n" + HttpHeaderNames.CONTENT_TYPE + ": " + this.contentType + (this.getCharset() != null ? "; " + HttpHeaderValues.CHARSET + '=' + this.getCharset().name() + "\r\n" : "\r\n") + HttpHeaderNames.CONTENT_LENGTH + ": " + this.length() + "\r\nCompleted: " + this.isCompleted() + "\r\nIsInMemory: " + this.isInMemory();
   }

   public FileUpload copy() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.copy() : var1);
   }

   public FileUpload duplicate() {
      ByteBuf var1 = this.content();
      return this.replace(var1 != null ? var1.duplicate() : var1);
   }

   public FileUpload retainedDuplicate() {
      ByteBuf var1 = this.content();
      if (var1 != null) {
         var1 = var1.retainedDuplicate();
         boolean var2 = false;

         FileUpload var4;
         try {
            FileUpload var3 = this.replace(var1);
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

   public FileUpload replace(ByteBuf var1) {
      MemoryFileUpload var2 = new MemoryFileUpload(this.getName(), this.getFilename(), this.getContentType(), this.getContentTransferEncoding(), this.getCharset(), this.size);
      if (var1 != null) {
         try {
            var2.setContent(var1);
            return var2;
         } catch (IOException var4) {
            throw new ChannelException(var4);
         }
      } else {
         return var2;
      }
   }

   public FileUpload retain() {
      super.retain();
      return this;
   }

   public FileUpload retain(int var1) {
      super.retain(var1);
      return this;
   }

   public FileUpload touch() {
      super.touch();
      return this;
   }

   public FileUpload touch(Object var1) {
      super.touch(var1);
      return this;
   }
}
