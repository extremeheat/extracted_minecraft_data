package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.List;

public final class ApplicationProtocolConfig {
   public static final ApplicationProtocolConfig DISABLED = new ApplicationProtocolConfig();
   private final List<String> supportedProtocols;
   private final ApplicationProtocolConfig.Protocol protocol;
   private final ApplicationProtocolConfig.SelectorFailureBehavior selectorBehavior;
   private final ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedBehavior;

   public ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol var1, ApplicationProtocolConfig.SelectorFailureBehavior var2, ApplicationProtocolConfig.SelectedListenerFailureBehavior var3, Iterable<String> var4) {
      this(var1, var2, var3, ApplicationProtocolUtil.toList(var4));
   }

   public ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol var1, ApplicationProtocolConfig.SelectorFailureBehavior var2, ApplicationProtocolConfig.SelectedListenerFailureBehavior var3, String... var4) {
      this(var1, var2, var3, ApplicationProtocolUtil.toList(var4));
   }

   private ApplicationProtocolConfig(ApplicationProtocolConfig.Protocol var1, ApplicationProtocolConfig.SelectorFailureBehavior var2, ApplicationProtocolConfig.SelectedListenerFailureBehavior var3, List<String> var4) {
      super();
      this.supportedProtocols = Collections.unmodifiableList((List)ObjectUtil.checkNotNull(var4, "supportedProtocols"));
      this.protocol = (ApplicationProtocolConfig.Protocol)ObjectUtil.checkNotNull(var1, "protocol");
      this.selectorBehavior = (ApplicationProtocolConfig.SelectorFailureBehavior)ObjectUtil.checkNotNull(var2, "selectorBehavior");
      this.selectedBehavior = (ApplicationProtocolConfig.SelectedListenerFailureBehavior)ObjectUtil.checkNotNull(var3, "selectedBehavior");
      if (var1 == ApplicationProtocolConfig.Protocol.NONE) {
         throw new IllegalArgumentException("protocol (" + ApplicationProtocolConfig.Protocol.NONE + ") must not be " + ApplicationProtocolConfig.Protocol.NONE + '.');
      } else if (var4.isEmpty()) {
         throw new IllegalArgumentException("supportedProtocols must be not empty");
      }
   }

   private ApplicationProtocolConfig() {
      super();
      this.supportedProtocols = Collections.emptyList();
      this.protocol = ApplicationProtocolConfig.Protocol.NONE;
      this.selectorBehavior = ApplicationProtocolConfig.SelectorFailureBehavior.CHOOSE_MY_LAST_PROTOCOL;
      this.selectedBehavior = ApplicationProtocolConfig.SelectedListenerFailureBehavior.ACCEPT;
   }

   public List<String> supportedProtocols() {
      return this.supportedProtocols;
   }

   public ApplicationProtocolConfig.Protocol protocol() {
      return this.protocol;
   }

   public ApplicationProtocolConfig.SelectorFailureBehavior selectorFailureBehavior() {
      return this.selectorBehavior;
   }

   public ApplicationProtocolConfig.SelectedListenerFailureBehavior selectedListenerFailureBehavior() {
      return this.selectedBehavior;
   }

   public static enum SelectedListenerFailureBehavior {
      ACCEPT,
      FATAL_ALERT,
      CHOOSE_MY_LAST_PROTOCOL;

      private SelectedListenerFailureBehavior() {
      }
   }

   public static enum SelectorFailureBehavior {
      FATAL_ALERT,
      NO_ADVERTISE,
      CHOOSE_MY_LAST_PROTOCOL;

      private SelectorFailureBehavior() {
      }
   }

   public static enum Protocol {
      NONE,
      NPN,
      ALPN,
      NPN_AND_ALPN;

      private Protocol() {
      }
   }
}
