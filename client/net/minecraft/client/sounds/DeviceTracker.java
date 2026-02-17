package net.minecraft.client.sounds;

import com.mojang.blaze3d.audio.DeviceList;

public interface DeviceTracker {
   DeviceList currentDevices();

   void tick();

   void forceRefresh();
}
