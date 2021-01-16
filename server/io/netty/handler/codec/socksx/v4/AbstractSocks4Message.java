package io.netty.handler.codec.socksx.v4;

import io.netty.handler.codec.socksx.AbstractSocksMessage;
import io.netty.handler.codec.socksx.SocksVersion;

public abstract class AbstractSocks4Message extends AbstractSocksMessage implements Socks4Message {
   public AbstractSocks4Message() {
      super();
   }

   public final SocksVersion version() {
      return SocksVersion.SOCKS4a;
   }
}
