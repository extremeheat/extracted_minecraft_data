package io.netty.handler.ssl;

/** @deprecated */
@Deprecated
public interface OpenSslApplicationProtocolNegotiator extends ApplicationProtocolNegotiator {
   ApplicationProtocolConfig.Protocol protocol();

   ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior();

   ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior();
}
