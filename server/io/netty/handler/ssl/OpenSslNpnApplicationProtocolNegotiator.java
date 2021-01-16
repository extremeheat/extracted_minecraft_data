package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.List;

/** @deprecated */
@Deprecated
public final class OpenSslNpnApplicationProtocolNegotiator implements OpenSslApplicationProtocolNegotiator {
   private final List<String> protocols;

   public OpenSslNpnApplicationProtocolNegotiator(Iterable<String> var1) {
      super();
      this.protocols = (List)ObjectUtil.checkNotNull(ApplicationProtocolUtil.toList(var1), "protocols");
   }

   public OpenSslNpnApplicationProtocolNegotiator(String... var1) {
      super();
      this.protocols = (List)ObjectUtil.checkNotNull(ApplicationProtocolUtil.toList(var1), "protocols");
   }

   public ApplicationProtocolConfig.Protocol protocol() {
      return ApplicationProtocolConfig.Protocol.NPN;
   }

   public List<String> protocols() {
      return this.protocols;
   }

   public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
      return ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
   }

   public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
      return ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
   }
}
