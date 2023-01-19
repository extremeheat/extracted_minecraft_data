package net.minecraft.network.protocol.game;

import java.util.Optional;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.effect.MobEffect;

public class ServerboundSetBeaconPacket implements Packet<ServerGamePacketListener> {
   private final Optional<MobEffect> primary;
   private final Optional<MobEffect> secondary;

   public ServerboundSetBeaconPacket(Optional<MobEffect> var1, Optional<MobEffect> var2) {
      super();
      this.primary = var1;
      this.secondary = var2;
   }

   public ServerboundSetBeaconPacket(FriendlyByteBuf var1) {
      super();
      this.primary = var1.readOptional(var0 -> var0.readById(Registry.MOB_EFFECT));
      this.secondary = var1.readOptional(var0 -> var0.readById(Registry.MOB_EFFECT));
   }

   @Override
   public void write(FriendlyByteBuf var1) {
      var1.writeOptional(this.primary, (var0, var1x) -> var0.writeId(Registry.MOB_EFFECT, var1x));
      var1.writeOptional(this.secondary, (var0, var1x) -> var0.writeId(Registry.MOB_EFFECT, var1x));
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleSetBeaconPacket(this);
   }

   public Optional<MobEffect> getPrimary() {
      return this.primary;
   }

   public Optional<MobEffect> getSecondary() {
      return this.secondary;
   }
}
