package net.minecraft.network.protocol.common.custom;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;

public record GameEventDebugPayload(ResourceKey<GameEvent> b, Vec3 c) implements CustomPacketPayload {
   private final ResourceKey<GameEvent> type;
   private final Vec3 pos;
   public static final ResourceLocation ID = new ResourceLocation("debug/game_event");

   public GameEventDebugPayload(FriendlyByteBuf var1) {
      this(var1.readResourceKey(Registries.GAME_EVENT), var1.readVec3());
   }

   public GameEventDebugPayload(ResourceKey<GameEvent> var1, Vec3 var2) {
      super();
      this.type = var1;
      this.pos = var2;
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeResourceKey(this.type);
      var1.writeVec3(this.pos);
   }

   @Override
   public ResourceLocation id() {
      return ID;
   }
}
