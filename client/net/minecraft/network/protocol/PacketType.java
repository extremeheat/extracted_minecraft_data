package net.minecraft.network.protocol;

import net.minecraft.resources.Identifier;

public record PacketType<T extends Packet<?>>(PacketFlow flow, Identifier id) {
   public PacketType(PacketFlow var1, Identifier var2) {
      super();
      this.flow = var1;
      this.id = var2;
   }

   public String toString() {
      String var10000 = this.flow.id();
      return var10000 + "/" + String.valueOf(this.id);
   }
}
