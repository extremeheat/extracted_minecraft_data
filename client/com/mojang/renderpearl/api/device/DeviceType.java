package com.mojang.renderpearl.api.device;

public enum DeviceType {
   OTHER,
   INTEGRATED,
   DISCRETE,
   VIRTUAL,
   CPU;

   private DeviceType() {
   }

   // $FF: synthetic method
   private static DeviceType[] $values() {
      return new DeviceType[]{OTHER, INTEGRATED, DISCRETE, VIRTUAL, CPU};
   }
}
