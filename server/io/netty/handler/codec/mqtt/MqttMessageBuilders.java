package io.netty.handler.codec.mqtt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;
import java.util.ArrayList;
import java.util.List;

public final class MqttMessageBuilders {
   public static MqttMessageBuilders.ConnectBuilder connect() {
      return new MqttMessageBuilders.ConnectBuilder();
   }

   public static MqttMessageBuilders.ConnAckBuilder connAck() {
      return new MqttMessageBuilders.ConnAckBuilder();
   }

   public static MqttMessageBuilders.PublishBuilder publish() {
      return new MqttMessageBuilders.PublishBuilder();
   }

   public static MqttMessageBuilders.SubscribeBuilder subscribe() {
      return new MqttMessageBuilders.SubscribeBuilder();
   }

   public static MqttMessageBuilders.UnsubscribeBuilder unsubscribe() {
      return new MqttMessageBuilders.UnsubscribeBuilder();
   }

   private MqttMessageBuilders() {
      super();
   }

   public static final class ConnAckBuilder {
      private MqttConnectReturnCode returnCode;
      private boolean sessionPresent;

      ConnAckBuilder() {
         super();
      }

      public MqttMessageBuilders.ConnAckBuilder returnCode(MqttConnectReturnCode var1) {
         this.returnCode = var1;
         return this;
      }

      public MqttMessageBuilders.ConnAckBuilder sessionPresent(boolean var1) {
         this.sessionPresent = var1;
         return this;
      }

      public MqttConnAckMessage build() {
         MqttFixedHeader var1 = new MqttFixedHeader(MqttMessageType.CONNACK, false, MqttQoS.AT_MOST_ONCE, false, 0);
         MqttConnAckVariableHeader var2 = new MqttConnAckVariableHeader(this.returnCode, this.sessionPresent);
         return new MqttConnAckMessage(var1, var2);
      }
   }

   public static final class UnsubscribeBuilder {
      private List<String> topicFilters;
      private int messageId;

      UnsubscribeBuilder() {
         super();
      }

      public MqttMessageBuilders.UnsubscribeBuilder addTopicFilter(String var1) {
         if (this.topicFilters == null) {
            this.topicFilters = new ArrayList(5);
         }

         this.topicFilters.add(var1);
         return this;
      }

      public MqttMessageBuilders.UnsubscribeBuilder messageId(int var1) {
         this.messageId = var1;
         return this;
      }

      public MqttUnsubscribeMessage build() {
         MqttFixedHeader var1 = new MqttFixedHeader(MqttMessageType.UNSUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
         MqttMessageIdVariableHeader var2 = MqttMessageIdVariableHeader.from(this.messageId);
         MqttUnsubscribePayload var3 = new MqttUnsubscribePayload(this.topicFilters);
         return new MqttUnsubscribeMessage(var1, var2, var3);
      }
   }

   public static final class SubscribeBuilder {
      private List<MqttTopicSubscription> subscriptions;
      private int messageId;

      SubscribeBuilder() {
         super();
      }

      public MqttMessageBuilders.SubscribeBuilder addSubscription(MqttQoS var1, String var2) {
         if (this.subscriptions == null) {
            this.subscriptions = new ArrayList(5);
         }

         this.subscriptions.add(new MqttTopicSubscription(var2, var1));
         return this;
      }

      public MqttMessageBuilders.SubscribeBuilder messageId(int var1) {
         this.messageId = var1;
         return this;
      }

      public MqttSubscribeMessage build() {
         MqttFixedHeader var1 = new MqttFixedHeader(MqttMessageType.SUBSCRIBE, false, MqttQoS.AT_LEAST_ONCE, false, 0);
         MqttMessageIdVariableHeader var2 = MqttMessageIdVariableHeader.from(this.messageId);
         MqttSubscribePayload var3 = new MqttSubscribePayload(this.subscriptions);
         return new MqttSubscribeMessage(var1, var2, var3);
      }
   }

   public static final class ConnectBuilder {
      private MqttVersion version;
      private String clientId;
      private boolean cleanSession;
      private boolean hasUser;
      private boolean hasPassword;
      private int keepAliveSecs;
      private boolean willFlag;
      private boolean willRetain;
      private MqttQoS willQos;
      private String willTopic;
      private byte[] willMessage;
      private String username;
      private byte[] password;

      ConnectBuilder() {
         super();
         this.version = MqttVersion.MQTT_3_1_1;
         this.willQos = MqttQoS.AT_MOST_ONCE;
      }

      public MqttMessageBuilders.ConnectBuilder protocolVersion(MqttVersion var1) {
         this.version = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder clientId(String var1) {
         this.clientId = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder cleanSession(boolean var1) {
         this.cleanSession = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder keepAlive(int var1) {
         this.keepAliveSecs = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder willFlag(boolean var1) {
         this.willFlag = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder willQoS(MqttQoS var1) {
         this.willQos = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder willTopic(String var1) {
         this.willTopic = var1;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public MqttMessageBuilders.ConnectBuilder willMessage(String var1) {
         this.willMessage(var1 == null ? null : var1.getBytes(CharsetUtil.UTF_8));
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder willMessage(byte[] var1) {
         this.willMessage = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder willRetain(boolean var1) {
         this.willRetain = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder hasUser(boolean var1) {
         this.hasUser = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder hasPassword(boolean var1) {
         this.hasPassword = var1;
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder username(String var1) {
         this.hasUser = var1 != null;
         this.username = var1;
         return this;
      }

      /** @deprecated */
      @Deprecated
      public MqttMessageBuilders.ConnectBuilder password(String var1) {
         this.password(var1 == null ? null : var1.getBytes(CharsetUtil.UTF_8));
         return this;
      }

      public MqttMessageBuilders.ConnectBuilder password(byte[] var1) {
         this.hasPassword = var1 != null;
         this.password = var1;
         return this;
      }

      public MqttConnectMessage build() {
         MqttFixedHeader var1 = new MqttFixedHeader(MqttMessageType.CONNECT, false, MqttQoS.AT_MOST_ONCE, false, 0);
         MqttConnectVariableHeader var2 = new MqttConnectVariableHeader(this.version.protocolName(), this.version.protocolLevel(), this.hasUser, this.hasPassword, this.willRetain, this.willQos.value(), this.willFlag, this.cleanSession, this.keepAliveSecs);
         MqttConnectPayload var3 = new MqttConnectPayload(this.clientId, this.willTopic, this.willMessage, this.username, this.password);
         return new MqttConnectMessage(var1, var2, var3);
      }
   }

   public static final class PublishBuilder {
      private String topic;
      private boolean retained;
      private MqttQoS qos;
      private ByteBuf payload;
      private int messageId;

      PublishBuilder() {
         super();
      }

      public MqttMessageBuilders.PublishBuilder topicName(String var1) {
         this.topic = var1;
         return this;
      }

      public MqttMessageBuilders.PublishBuilder retained(boolean var1) {
         this.retained = var1;
         return this;
      }

      public MqttMessageBuilders.PublishBuilder qos(MqttQoS var1) {
         this.qos = var1;
         return this;
      }

      public MqttMessageBuilders.PublishBuilder payload(ByteBuf var1) {
         this.payload = var1;
         return this;
      }

      public MqttMessageBuilders.PublishBuilder messageId(int var1) {
         this.messageId = var1;
         return this;
      }

      public MqttPublishMessage build() {
         MqttFixedHeader var1 = new MqttFixedHeader(MqttMessageType.PUBLISH, false, this.qos, this.retained, 0);
         MqttPublishVariableHeader var2 = new MqttPublishVariableHeader(this.topic, this.messageId);
         return new MqttPublishMessage(var1, var2, Unpooled.buffer().writeBytes(this.payload));
      }
   }
}
