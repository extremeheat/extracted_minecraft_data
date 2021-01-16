package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;

public interface DnsQueryLifecycleObserverFactory {
   DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1);
}
