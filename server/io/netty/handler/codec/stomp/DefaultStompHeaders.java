package io.netty.handler.codec.stomp;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.HeadersUtils;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;
import io.netty.util.HashingStrategy;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class DefaultStompHeaders extends DefaultHeaders<CharSequence, CharSequence, StompHeaders> implements StompHeaders {
   public DefaultStompHeaders() {
      super((HashingStrategy)AsciiString.CASE_SENSITIVE_HASHER, (ValueConverter)CharSequenceValueConverter.INSTANCE);
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

   public DefaultStompHeaders copy() {
      DefaultStompHeaders var1 = new DefaultStompHeaders();
      var1.addImpl(this);
      return var1;
   }
}
