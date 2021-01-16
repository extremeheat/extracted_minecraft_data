package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;

final class TraceDnsQueryLifeCycleObserverFactory implements DnsQueryLifecycleObserverFactory {
   private static final InternalLogger DEFAULT_LOGGER = InternalLoggerFactory.getInstance(TraceDnsQueryLifeCycleObserverFactory.class);
   private static final InternalLogLevel DEFAULT_LEVEL;
   private final InternalLogger logger;
   private final InternalLogLevel level;

   TraceDnsQueryLifeCycleObserverFactory() {
      this(DEFAULT_LOGGER, DEFAULT_LEVEL);
   }

   TraceDnsQueryLifeCycleObserverFactory(InternalLogger var1, InternalLogLevel var2) {
      super();
      this.logger = (InternalLogger)ObjectUtil.checkNotNull(var1, "logger");
      this.level = (InternalLogLevel)ObjectUtil.checkNotNull(var2, "level");
   }

   public DnsQueryLifecycleObserver newDnsQueryLifecycleObserver(DnsQuestion var1) {
      return new TraceDnsQueryLifecycleObserver(var1, this.logger, this.level);
   }

   static {
      DEFAULT_LEVEL = InternalLogLevel.DEBUG;
   }
}
