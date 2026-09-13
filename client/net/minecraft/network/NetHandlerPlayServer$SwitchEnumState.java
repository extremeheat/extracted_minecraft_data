package net.minecraft.network;

import net.minecraft.network.play.client.C16PacketClientStatus$EnumState;

// $VF: synthetic class
class NetHandlerPlayServer$SwitchEnumState {
   static {
      try {
         field_151290_a[C16PacketClientStatus$EnumState.PERFORM_RESPAWN.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         field_151290_a[C16PacketClientStatus$EnumState.REQUEST_STATS.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      try {
         field_151290_a[C16PacketClientStatus$EnumState.OPEN_INVENTORY_ACHIEVEMENT.ordinal()] = 3;
      } catch (NoSuchFieldError var1) {
      }
   }
}
