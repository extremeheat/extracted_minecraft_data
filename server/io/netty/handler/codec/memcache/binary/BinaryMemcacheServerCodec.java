package io.netty.handler.codec.memcache.binary;

import io.netty.channel.CombinedChannelDuplexHandler;

public class BinaryMemcacheServerCodec extends CombinedChannelDuplexHandler<BinaryMemcacheRequestDecoder, BinaryMemcacheResponseEncoder> {
   public BinaryMemcacheServerCodec() {
      this(8192);
   }

   public BinaryMemcacheServerCodec(int var1) {
      super(new BinaryMemcacheRequestDecoder(var1), new BinaryMemcacheResponseEncoder());
   }
}
