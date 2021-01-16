package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import java.net.InetSocketAddress;
import java.util.List;

public interface DnsQueryLifecycleObserver {
   void queryWritten(InetSocketAddress var1, ChannelFuture var2);

   void queryCancelled(int var1);

   DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> var1);

   DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion var1);

   DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode var1);

   void queryFailed(Throwable var1);

   void querySucceed();
}
