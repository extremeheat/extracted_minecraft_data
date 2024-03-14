package net.minecraft.network.protocol;

import net.minecraft.resources.ResourceLocation;

public record PacketType<T extends Packet<?>>(PacketFlow a, ResourceLocation b) {
   private final PacketFlow flow;
   private final ResourceLocation id;

   public PacketType(PacketFlow var1, ResourceLocation var2) {
      super();
      this.flow = var1;
      this.id = var2;
   }

   public String toString() {
      return this.flow.id() + "/" + this.id;
   }
}
