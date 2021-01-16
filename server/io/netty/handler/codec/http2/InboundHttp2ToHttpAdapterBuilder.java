package io.netty.handler.codec.http2;

public final class InboundHttp2ToHttpAdapterBuilder extends AbstractInboundHttp2ToHttpAdapterBuilder<InboundHttp2ToHttpAdapter, InboundHttp2ToHttpAdapterBuilder> {
   public InboundHttp2ToHttpAdapterBuilder(Http2Connection var1) {
      super(var1);
   }

   public InboundHttp2ToHttpAdapterBuilder maxContentLength(int var1) {
      return (InboundHttp2ToHttpAdapterBuilder)super.maxContentLength(var1);
   }

   public InboundHttp2ToHttpAdapterBuilder validateHttpHeaders(boolean var1) {
      return (InboundHttp2ToHttpAdapterBuilder)super.validateHttpHeaders(var1);
   }

   public InboundHttp2ToHttpAdapterBuilder propagateSettings(boolean var1) {
      return (InboundHttp2ToHttpAdapterBuilder)super.propagateSettings(var1);
   }

   public InboundHttp2ToHttpAdapter build() {
      return super.build();
   }

   protected InboundHttp2ToHttpAdapter build(Http2Connection var1, int var2, boolean var3, boolean var4) throws Exception {
      return new InboundHttp2ToHttpAdapter(var1, var2, var3, var4);
   }
}
