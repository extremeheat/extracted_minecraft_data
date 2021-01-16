package io.netty.util.internal;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

class UnpaddedInternalThreadLocalMap {
   static final ThreadLocal<InternalThreadLocalMap> slowThreadLocalMap = new ThreadLocal();
   static final AtomicInteger nextIndex = new AtomicInteger();
   Object[] indexedVariables;
   int futureListenerStackDepth;
   int localChannelReaderStackDepth;
   Map<Class<?>, Boolean> handlerSharableCache;
   IntegerHolder counterHashCode;
   ThreadLocalRandom random;
   Map<Class<?>, TypeParameterMatcher> typeParameterMatcherGetCache;
   Map<Class<?>, Map<String, TypeParameterMatcher>> typeParameterMatcherFindCache;
   StringBuilder stringBuilder;
   Map<Charset, CharsetEncoder> charsetEncoderCache;
   Map<Charset, CharsetDecoder> charsetDecoderCache;
   ArrayList<Object> arrayList;

   UnpaddedInternalThreadLocalMap(Object[] var1) {
      super();
      this.indexedVariables = var1;
   }
}
