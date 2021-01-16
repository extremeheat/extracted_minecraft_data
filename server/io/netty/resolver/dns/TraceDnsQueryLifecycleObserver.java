package io.netty.resolver.dns;

import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsResponseCode;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogLevel;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetSocketAddress;
import java.util.List;

final class TraceDnsQueryLifecycleObserver implements DnsQueryLifecycleObserver {
   private final InternalLogger logger;
   private final InternalLogLevel level;
   private final DnsQuestion question;
   private InetSocketAddress dnsServerAddress;

   TraceDnsQueryLifecycleObserver(DnsQuestion var1, InternalLogger var2, InternalLogLevel var3) {
      super();
      this.question = (DnsQuestion)ObjectUtil.checkNotNull(var1, "question");
      this.logger = (InternalLogger)ObjectUtil.checkNotNull(var2, "logger");
      this.level = (InternalLogLevel)ObjectUtil.checkNotNull(var3, "level");
   }

   public void queryWritten(InetSocketAddress var1, ChannelFuture var2) {
      this.dnsServerAddress = var1;
   }

   public void queryCancelled(int var1) {
      if (this.dnsServerAddress != null) {
         this.logger.log(this.level, "from {} : {} cancelled with {} queries remaining", this.dnsServerAddress, this.question, var1);
      } else {
         this.logger.log(this.level, "{} query never written and cancelled with {} queries remaining", this.question, var1);
      }

   }

   public DnsQueryLifecycleObserver queryRedirected(List<InetSocketAddress> var1) {
      this.logger.log(this.level, "from {} : {} redirected", this.dnsServerAddress, this.question);
      return this;
   }

   public DnsQueryLifecycleObserver queryCNAMEd(DnsQuestion var1) {
      this.logger.log(this.level, "from {} : {} CNAME question {}", this.dnsServerAddress, this.question, var1);
      return this;
   }

   public DnsQueryLifecycleObserver queryNoAnswer(DnsResponseCode var1) {
      this.logger.log(this.level, "from {} : {} no answer {}", this.dnsServerAddress, this.question, var1);
      return this;
   }

   public void queryFailed(Throwable var1) {
      if (this.dnsServerAddress != null) {
         this.logger.log(this.level, "from {} : {} failure", this.dnsServerAddress, this.question, var1);
      } else {
         this.logger.log(this.level, "{} query never written and failed", this.question, var1);
      }

   }

   public void querySucceed() {
   }
}
