package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import java.net.InetSocketAddress;

public final class DnsNameResolverTimeoutException extends DnsNameResolverException {
   private static final long serialVersionUID = -8826717969627131854L;

   public DnsNameResolverTimeoutException(InetSocketAddress var1, DnsQuestion var2, String var3) {
      super(var1, var2, var3);
   }
}
