package net.minecraft.network.protocol;

import net.minecraft.resources.ResourceLocation;

public record PacketType<T extends Packet<?>>(PacketFlow flow, ResourceLocation id) {
   public PacketType(PacketFlow flow, ResourceLocation id) {
      super();
      this.flow = flow;
      this.id = id;
   }

   public String toString() {
      String var10000 = this.flow.id();
      return var10000 + "/" + String.valueOf(this.id);
   }

   public PacketFlow flow() {
      return this.flow;
   }

   public ResourceLocation id() {
      return this.id;
   }
}
