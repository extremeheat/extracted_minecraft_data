package net.minecraft.client.stream;

import tv.twitch.chat.ChatEvent;

// $VF: synthetic class
class ChatController$SwitchChatState {
   static {
      try {
         field_152983_b[ChatController$ChatState.Uninitialized.ordinal()] = 1;
      } catch (NoSuchFieldError var7) {
      }

      try {
         field_152983_b[ChatController$ChatState.Initialized.ordinal()] = 2;
      } catch (NoSuchFieldError var6) {
      }

      try {
         field_152983_b[ChatController$ChatState.Connecting.ordinal()] = 3;
      } catch (NoSuchFieldError var5) {
      }

      try {
         field_152983_b[ChatController$ChatState.Connected.ordinal()] = 4;
      } catch (NoSuchFieldError var4) {
      }

      try {
         field_152983_b[ChatController$ChatState.Disconnected.ordinal()] = 5;
      } catch (NoSuchFieldError var3) {
      }

      field_152982_a = new int[ChatEvent.values().length];

      try {
         field_152982_a[ChatEvent.TTV_CHAT_JOINED_CHANNEL.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         field_152982_a[ChatEvent.TTV_CHAT_LEFT_CHANNEL.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
