package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;

public final class NoopDnsQueryLifecycleObserverFactory implements DnsQueryLifecycleObserverFactory {
   public static final NoopDnsQueryLifecycleObserverFactory INSTANCE = new NoopDnsQueryLifecycleObserverFactory();

   private NoopDnsQueryLifecycleObserverFactory() {
      super();
   }

   public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1) {
      return NoopDnsQueryLifecycleObserver.INSTANCE;
   }
}
