package io.netty.handler.codec.spdy;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class DefaultSpdyHeaders extends DefaultHeaders<CharSequence, CharSequence, SpdyHeaders> implements SpdyHeaders {
   private static final DefaultHeaders.NameValidator<CharSequence> SpdyNameValidator = new DefaultHeaders.NameValidator<CharSequence>() {
      public void validateName(CharSequence var1) {
         SpdyCodecUtil.validateHeaderName(var1);
      }
   };

   public DefaultSpdyHeaders() {
      this(true);
   }

   public DefaultSpdyHeaders(boolean var1) {
      super(AsciiString.CASE_INSENSITIVE_HASHER, (ValueConverter)(var1 ? DefaultSpdyHeaders.HeaderValueConverterAndValidator.INSTANCE : CharSequenceValueConverter.INSTANCE), var1 ? SpdyNameValidator : DefaultHeaders.NameValidator.NOT_NULL);
   }

   public String getAsString(CharSequence var1) {
      return HeadersUtils.getAsString(this, var1);
   }

   public List<String> getAllAsString(CharSequence var1) {
      return HeadersUtils.getAllAsString(this, var1);
   }

   public Iterator<Entry<String, String>> iteratorAsString() {
      return HeadersUtils.iteratorAsString(this);
   }

   public boolean contains(CharSequence var1, CharSequence var2) {
      return this.contains(var1, var2, false);
   }

   public boolean contains(CharSequence var1, CharSequence var2, boolean var3) {
      return this.contains(var1, var2, var3 ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
   }

   private static final class HeaderValueConverterAndValidator extends CharSequenceValueConverter {
      public static final DefaultSpdyHeaders.HeaderValueConverterAndValidator INSTANCE = new DefaultSpdyHeaders.HeaderValueConverterAndValidator();

      private HeaderValueConverterAndValidator() {
         super();
      }

      public CharSequence convertObject(Object var1) {
         CharSequence var2 = super.convertObject(var1);
         SpdyCodecUtil.validateHeaderValue(var2);
         return var2;
      }
   }
}
