package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import java.net.InetSocketAddress;
import java.util.List;

final class NoopDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver {
   static final NoopDnsQueryLifecycleObserver INSTANCE = new NoopDnsQueryLifecycleObserver();

   private NoopDnsQueryLifecycleObserver() {
      super();
   }

   public void queryWritten(InetSocketAddress var1, ChannelFuture var2) {
   }

   public void queryCancelled(int var1) {
   }

   public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> var1) {
      return this;
   }

   public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion var1) {
      return this;
   }

   public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode var1) {
      return this;
   }

   public void queryFailed(Throwable var1) {
   }

   public void querySucceed() {
   }
}
