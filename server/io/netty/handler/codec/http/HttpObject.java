package io.netty.handler.codec.http;

import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.DecoderResultProvider;

public interface HttpObject extends DecoderResultProvider {
   /** @deprecated */
   @Deprecated
   DecoderResult getDecoderResult();
}
