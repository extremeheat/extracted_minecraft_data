package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.List;

/** @deprecated */
@Deprecated
public final class OpenSslDefaultApplicationProtocolNegotiator implements OpenSslApplicationProtocolNegotiator {
   private final ApplicationProtocolConfig config;

   public OpenSslDefaultApplicationProtocolNegotiator(ApplicationProtocolConfig var1) {
      super();
      this.config = (ApplicationProtocolConfig)ObjectUtil.checkNotNull(var1, "config");
   }

   public List<String> protocols() {
      return this.config.supportedProtocols();
   }

   public ApplicationProtocolConfig.Protocol protocol() {
      return this.config.protocol();
   }

   public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
      return this.config.selectorFailureBehavior();
   }

   public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
      return this.config.selectedListenerFailureBehavior();
   }
}
