package net.minecraft.network.play.client;

public enum C02PacketUseEntity$Action {
   INTERACT(0),
   ATTACK(1);

   private static final C02PacketUseEntity$Action[] field_151421_c = new C02PacketUseEntity$Action[values().length];
   private final int field_151418_d;

   private C02PacketUseEntity$Action(int var3) {
      this.field_151418_d = var3;
   }

   static {
      for(C02PacketUseEntity$Action var3 : values()) {
         field_151421_c[var3.field_151418_d] = var3;
      }
   }
}
