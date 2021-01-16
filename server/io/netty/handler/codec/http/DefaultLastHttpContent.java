package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.util.internal.StringUtil;
import java.util.Iterator;
import java.util.Map.Entry;

public class DefaultLastHttpContent extends DefaultHttpContent implements LastHttpContent {
   private final HttpHeaders trailingHeaders;
   private final boolean validateHeaders;

   public DefaultLastHttpContent() {
      this(Unpooled.buffer(0));
   }

   public DefaultLastHttpContent(ByteBuf var1) {
      this(var1, true);
   }

   public DefaultLastHttpContent(ByteBuf var1, boolean var2) {
      super(var1);
      this.trailingHeaders = new DefaultLastHttpContent.TrailingHttpHeaders(var2);
      this.validateHeaders = var2;
   }

   public LastHttpContent copy() {
      return this.replace(this.content().copy());
   }

   public LastHttpContent duplicate() {
      return this.replace(this.content().duplicate());
   }

   public LastHttpContent retainedDuplicate() {
      return this.replace(this.content().retainedDuplicate());
   }

   public LastHttpContent replace(ByteBuf var1) {
      DefaultLastHttpContent var2 = new DefaultLastHttpContent(var1, this.validateHeaders);
      var2.trailingHeaders().set(this.trailingHeaders());
      return var2;
   }

   public LastHttpContent retain(int var1) {
      super.retain(var1);
      return this;
   }

   public LastHttpContent retain() {
      super.retain();
      return this;
   }

   public LastHttpContent touch() {
      super.touch();
      return this;
   }

   public LastHttpContent touch(Object var1) {
      super.touch(var1);
      return this;
   }

   public HttpHeaders trailingHeaders() {
      return this.trailingHeaders;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(super.toString());
      var1.append(StringUtil.NEWLINE);
      this.appendHeaders(var1);
      var1.setLength(var1.length() - StringUtil.NEWLINE.length());
      return var1.toString();
   }

   private void appendHeaders(StringBuilder var1) {
      Iterator var2 = this.trailingHeaders().iterator();

      while(var2.hasNext()) {
         Entry var3 = (Entry)var2.next();
         var1.append((String)var3.getKey());
         var1.append(": ");
         var1.append((String)var3.getValue());
         var1.append(StringUtil.NEWLINE);
      }

   }

   private static final class TrailingHttpHeaders extends DefaultHttpHeaders {
      private static final DefaultHeaders.NameValidator<CharSequence> TrailerNameValidator = new DefaultHeaders.NameValidator<CharSequence>() {
         public void validateName(CharSequence var1) {
            DefaultHttpHeaders.HttpNameValidator.validateName(var1);
            if (HttpHeaderNames.CONTENT_LENGTH.contentEqualsIgnoreCase(var1) || HttpHeaderNames.TRANSFER_ENCODING.contentEqualsIgnoreCase(var1) || HttpHeaderNames.TRAILER.contentEqualsIgnoreCase(var1)) {
               throw new IllegalArgumentException("prohibited trailing header: " + var1);
            }
         }
      };

      TrailingHttpHeaders(boolean var1) {
         super(var1, var1 ? TrailerNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
      }
   }
}
