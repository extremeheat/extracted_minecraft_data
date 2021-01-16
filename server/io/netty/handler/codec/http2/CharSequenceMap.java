package io.netty.handler.codec.http2;

import io.netty.handler.codec.DefaultHeaders;
import io.netty.handler.codec.UnsupportedValueConverter;
import io.netty.handler.codec.ValueConverter;
import io.netty.util.AsciiString;

public final class CharSequenceMap<V> extends DefaultHeaders<CharSequence, V, CharSequenceMap<V>> {
   public CharSequenceMap() {
      this(true);
   }

   public CharSequenceMap(boolean var1) {
      this(var1, UnsupportedValueConverter.instance());
   }

   public CharSequenceMap(boolean var1, ValueConverter<V> var2) {
      super(var1 ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, var2);
   }

   public CharSequenceMap(boolean var1, ValueConverter<V> var2, int var3) {
      super(var1 ? AsciiString.CASE_SENSITIVE_HASHER : AsciiString.CASE_INSENSITIVE_HASHER, var2, DefaultHeaders.NameValidator.NOT_NULL, var3);
   }
}
