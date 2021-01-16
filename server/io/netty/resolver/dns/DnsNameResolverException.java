package io.netty.resolver.dns;

import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;

public class DnsNameResolverException extends RuntimeException {
   private static final long serialVersionUID = -8826717909627131850L;
   private final InetSocketAddress remoteAddress;
   private final DnsQuestion question;

   public DnsNameResolverException(InetSocketAddress var1, DnsQuestion var2, String var3) {
      super(var3);
      this.remoteAddress = validateRemoteAddress(var1);
      this.question = validateQuestion(var2);
   }

   public DnsNameResolverException(InetSocketAddress var1, DnsQuestion var2, String var3, Throwable var4) {
      super(var3, var4);
      this.remoteAddress = validateRemoteAddress(var1);
      this.question = validateQuestion(var2);
   }

   private static InetSocketAddress validateRemoteAddress(InetSocketAddress var0) {
      return (InetSocketAddress)ObjectUtil.checkNotNull(var0, "remoteAddress");
   }

   private static DnsQuestion validateQuestion(DnsQuestion var0) {
      return (DnsQuestion)ObjectUtil.checkNotNull(var0, "question");
   }

   public InetSocketAddress remoteAddress() {
      return this.remoteAddress;
   }

   public DnsQuestion question() {
      return this.question;
   }

   public Throwable fillInStackTrace() {
      this.setStackTrace(EmptyArrays.EMPTY_STACK_TRACE);
      return this;
   }
}
