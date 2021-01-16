package io.netty.handler.codec.http2;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class DefaultHttp2SettingsFrame implements Http2SettingsFrame {
   private final Http2Settings settings;

   public DefaultHttp2SettingsFrame(Http2Settings var1) {
      super();
      this.settings = (Http2Settings)ObjectUtil.checkNotNull(var1, "settings");
   }

   public Http2Settings settings() {
      return this.settings;
   }

   public String name() {
      return "SETTINGS";
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "(settings=" + this.settings + ')';
   }
}
