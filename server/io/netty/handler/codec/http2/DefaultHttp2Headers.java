package io.netty.handler.codec.http2;

import io.netty.handler.codec.CharSequenceValueConverter;
import io.netty.handler.codec.DefaultHeaders;
import io.netty.util.AsciiString;
import io.netty.util.ByteProcessor;
import io.netty.util.internal.PlatformDependent;

public class DefaultHttp2Headers extends DefaultHeaders<CharSequence, CharSequence, Http2Headers> implements Http2Headers {
   private static final ByteProcessor HTTP2_NAME_VALIDATOR_PROCESSOR = new ByteProcessor() {
      public boolean process(byte var1) {
         return !AsciiString.isUpperCase(var1);
      }
   };
   static final DefaultHeaders.NameValidator<CharSequence> HTTP2_NAME_VALIDATOR = new DefaultHeaders.NameValidator<CharSequence>() {
      public void validateName(CharSequence var1) {
         if (var1 == null || var1.length() == 0) {
            PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "empty headers are not allowed [%s]", var1));
         }

         int var2;
         if (var1 instanceof AsciiString) {
            try {
               var2 = ((AsciiString)var1).forEachByte(DefaultHttp2Headers.HTTP2_NAME_VALIDATOR_PROCESSOR);
            } catch (Http2Exception var4) {
               PlatformDependent.throwException(var4);
               return;
            } catch (Throwable var5) {
               PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, var5, "unexpected error. invalid header name [%s]", var1));
               return;
            }

            if (var2 != -1) {
               PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "invalid header name [%s]", var1));
            }
         } else {
            for(var2 = 0; var2 < var1.length(); ++var2) {
               if (AsciiString.isUpperCase(var1.charAt(var2))) {
                  PlatformDependent.throwException(Http2Exception.connectionError(Http2Error.PROTOCOL_ERROR, "invalid header name [%s]", var1));
               }
            }
         }

      }
   };
   private DefaultHeaders.HeaderEntry<CharSequence, CharSequence> firstNonPseudo;

   public DefaultHttp2Headers() {
      this(true);
   }

   public DefaultHttp2Headers(boolean var1) {
      super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, var1 ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL);
      this.firstNonPseudo = this.head;
   }

   public DefaultHttp2Headers(boolean var1, int var2) {
      super(AsciiString.CASE_SENSITIVE_HASHER, CharSequenceValueConverter.INSTANCE, var1 ? HTTP2_NAME_VALIDATOR : DefaultHeaders.NameValidator.NOT_NULL, var2);
      this.firstNonPseudo = this.head;
   }

   public Http2Headers clear() {
      this.firstNonPseudo = this.head;
      return (Http2Headers)super.clear();
   }

   public boolean equals(Object var1) {
      return var1 instanceof Http2Headers && this.equals((Http2Headers)var1, AsciiString.CASE_SENSITIVE_HASHER);
   }

   public int hashCode() {
      return this.hashCode(AsciiString.CASE_SENSITIVE_HASHER);
   }

   public Http2Headers method(CharSequence var1) {
      this.set(Http2Headers.PseudoHeaderName.METHOD.value(), var1);
      return this;
   }

   public Http2Headers scheme(CharSequence var1) {
      this.set(Http2Headers.PseudoHeaderName.SCHEME.value(), var1);
      return this;
   }

   public Http2Headers authority(CharSequence var1) {
      this.set(Http2Headers.PseudoHeaderName.AUTHORITY.value(), var1);
      return this;
   }

   public Http2Headers path(CharSequence var1) {
      this.set(Http2Headers.PseudoHeaderName.PATH.value(), var1);
      return this;
   }

   public Http2Headers status(CharSequence var1) {
      this.set(Http2Headers.PseudoHeaderName.STATUS.value(), var1);
      return this;
   }

   public CharSequence method() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.METHOD.value());
   }

   public CharSequence scheme() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.SCHEME.value());
   }

   public CharSequence authority() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.AUTHORITY.value());
   }

   public CharSequence path() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.PATH.value());
   }

   public CharSequence status() {
      return (CharSequence)this.get(Http2Headers.PseudoHeaderName.STATUS.value());
   }

   public boolean contains(CharSequence var1, CharSequence var2) {
      return this.contains(var1, var2, false);
   }

   public boolean contains(CharSequence var1, CharSequence var2, boolean var3) {
      return this.contains(var1, var2, var3 ? AsciiString.CASE_INSENSITIVE_HASHER : AsciiString.CASE_SENSITIVE_HASHER);
   }

   protected final DefaultHeaders.HeaderEntry<CharSequence, CharSequence> newHeaderEntry(int var1, CharSequence var2, CharSequence var3, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> var4) {
      return new DefaultHttp2Headers.Http2HeaderEntry(var1, var2, var3, var4);
   }

   private final class Http2HeaderEntry extends DefaultHeaders.HeaderEntry<CharSequence, CharSequence> {
      protected Http2HeaderEntry(int var2, CharSequence var3, CharSequence var4, DefaultHeaders.HeaderEntry<CharSequence, CharSequence> var5) {
         super(var2, var3);
         this.value = var4;
         this.next = var5;
         if (Http2Headers.PseudoHeaderName.hasPseudoHeaderFormat(var3)) {
            this.after = DefaultHttp2Headers.this.firstNonPseudo;
            this.before = DefaultHttp2Headers.this.firstNonPseudo.before();
         } else {
            this.after = DefaultHttp2Headers.this.head;
            this.before = DefaultHttp2Headers.this.head.before();
            if (DefaultHttp2Headers.this.firstNonPseudo == DefaultHttp2Headers.this.head) {
               DefaultHttp2Headers.this.firstNonPseudo = this;
            }
         }

         this.pointNeighborsToThis();
      }

      protected void remove() {
         if (this == DefaultHttp2Headers.this.firstNonPseudo) {
            DefaultHttp2Headers.this.firstNonPseudo = DefaultHttp2Headers.this.firstNonPseudo.after();
         }

         super.remove();
      }
   }
}
