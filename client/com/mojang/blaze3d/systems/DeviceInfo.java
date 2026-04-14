package com.mojang.blaze3d.systems;

import java.util.Set;

public record DeviceInfo(String name, String vendorName, String driverInfo, boolean isZZeroToOne, String backendName, float timestampPeriod, DeviceLimits limits, Set<String> underlyingExtensions, HintsAndWorkarounds hintsAndWorkarounds, DeviceType type) {
   public DeviceInfo {
      super();
   }
}
