package com.mojang.blaze3d.systems;

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
