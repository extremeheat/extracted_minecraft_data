package io.netty.handler.codec.mqtt;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.StringUtil;

public final class MqttConnectPayload {
   private final String clientIdentifier;
   private final String willTopic;
   private final byte[] willMessage;
   private final String userName;
   private final byte[] password;

   /** @deprecated */
   @Deprecated
   public MqttConnectPayload(String var1, String var2, String var3, String var4, String var5) {
      this(var1, var2, var3.getBytes(CharsetUtil.UTF_8), var4, var5.getBytes(CharsetUtil.UTF_8));
   }

   public MqttConnectPayload(String var1, String var2, byte[] var3, String var4, byte[] var5) {
      super();
      this.clientIdentifier = var1;
      this.willTopic = var2;
      this.willMessage = var3;
      this.userName = var4;
      this.password = var5;
   }

   public String clientIdentifier() {
      return this.clientIdentifier;
   }

   public String willTopic() {
      return this.willTopic;
   }

   /** @deprecated */
   @Deprecated
   public String willMessage() {
      return this.willMessage == null ? null : new String(this.willMessage, CharsetUtil.UTF_8);
   }

   public byte[] willMessageInBytes() {
      return this.willMessage;
   }

   public String userName() {
      return this.userName;
   }

   /** @deprecated */
   @Deprecated
   public String password() {
      return this.password == null ? null : new String(this.password, CharsetUtil.UTF_8);
   }

   public byte[] passwordInBytes() {
      return this.password;
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + '[' + "clientIdentifier=" + this.clientIdentifier + ", willTopic=" + this.willTopic + ", willMessage=" + this.willMessage + ", userName=" + this.userName + ", password=" + this.password + ']';
   }
}
