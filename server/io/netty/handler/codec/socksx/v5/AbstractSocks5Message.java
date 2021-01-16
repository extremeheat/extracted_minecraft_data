package io.netty.handler.codec.socksx.v5;

import io.netty.handler.codec.socksx.AbstractSocksMessage;
import io.netty.handler.codec.socksx.SocksVersion;

public abstract class AbstractSocks5Message extends AbstractSocksMessage implements Socks5Message {
   public AbstractSocks5Message() {
      super();
   }

   public final SocksVersion version() {
      return SocksVersion.SOCKS5;
   }
}
