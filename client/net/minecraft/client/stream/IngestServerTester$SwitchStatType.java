package net.minecraft.client.stream;

import tv.twitch.broadcast.StatType;

// $VF: synthetic class
class IngestServerTester$SwitchStatType {
   static {
      try {
         field_153027_b[IngestServerTester$IngestTestState.Starting.ordinal()] = 1;
      } catch (NoSuchFieldError var10) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.DoneTestingServer.ordinal()] = 2;
      } catch (NoSuchFieldError var9) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.ConnectingToServer.ordinal()] = 3;
      } catch (NoSuchFieldError var8) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.TestingServer.ordinal()] = 4;
      } catch (NoSuchFieldError var7) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.Uninitalized.ordinal()] = 5;
      } catch (NoSuchFieldError var6) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.Finished.ordinal()] = 6;
      } catch (NoSuchFieldError var5) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.Cancelled.ordinal()] = 7;
      } catch (NoSuchFieldError var4) {
      }

      try {
         field_153027_b[IngestServerTester$IngestTestState.Failed.ordinal()] = 8;
      } catch (NoSuchFieldError var3) {
      }

      field_153026_a = new int[StatType.values().length];

      try {
         field_153026_a[StatType.TTV_ST_RTMPSTATE.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      try {
         field_153026_a[StatType.TTV_ST_RTMPDATASENT.ordinal()] = 2;
      } catch (NoSuchFieldError var1) {
      }
   }
}
