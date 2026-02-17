package com.mojang.blaze3d.audio;

import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALUtil;

public record DeviceList(@Nullable String defaultDevice, List<String> allDevices) {
   public static final DeviceList EMPTY = new DeviceList((String)null, List.of());

   public DeviceList {
      super();
   }

   public static DeviceList query() {
      if (!ALC10.alcIsExtensionPresent(0L, "ALC_ENUMERATE_ALL_EXT")) {
         return EMPTY;
      } else {
         List<String> allDevices = (List)Objects.requireNonNullElse(ALUtil.getStringList(0L, 4115), List.of());
         String defaultDevice = ALC10.alcGetString(0L, 4114);
         return new DeviceList(defaultDevice, allDevices);
      }
   }
}
