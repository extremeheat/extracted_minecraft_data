package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.player.Abilities;

public class ClientboundPlayerAbilitiesPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerAbilitiesPacket> STREAM_CODEC = Packet.codec(ClientboundPlayerAbilitiesPacket::write, ClientboundPlayerAbilitiesPacket::new);
   private static final int FLAG_INVULNERABLE = 1;
   private static final int FLAG_FLYING = 2;
   private static final int FLAG_CAN_FLY = 4;
   private static final int FLAG_INSTABUILD = 8;
   private final boolean invulnerable;
   private final boolean isFlying;
   private final boolean canFly;
   private final boolean instabuild;
   private final float flyingSpeed;
   private final float walkingSpeed;

   public ClientboundPlayerAbilitiesPacket(Abilities var1) {
      super();
      this.invulnerable = var1.invulnerable;
      this.isFlying = var1.flying;
      this.canFly = var1.mayfly;
      this.instabuild = var1.instabuild;
      this.flyingSpeed = var1.getFlyingSpeed();
      this.walkingSpeed = var1.getWalkingSpeed();
   }

   private ClientboundPlayerAbilitiesPacket(FriendlyByteBuf var1) {
      super();
      byte var2 = var1.readByte();
      this.invulnerable = (var2 & 1) != 0;
      this.isFlying = (var2 & 2) != 0;
      this.canFly = (var2 & 4) != 0;
      this.instabuild = (var2 & 8) != 0;
      this.flyingSpeed = var1.readFloat();
      this.walkingSpeed = var1.readFloat();
   }

   private void write(FriendlyByteBuf var1) {
      byte var2 = 0;
      if (this.invulnerable) {
         var2 = (byte)(var2 | 1);
      }

      if (this.isFlying) {
         var2 = (byte)(var2 | 2);
      }

      if (this.canFly) {
         var2 = (byte)(var2 | 4);
      }

      if (this.instabuild) {
         var2 = (byte)(var2 | 8);
      }

      var1.writeByte(var2);
      var1.writeFloat(this.flyingSpeed);
      var1.writeFloat(this.walkingSpeed);
   }

   public PacketType<ClientboundPlayerAbilitiesPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_ABILITIES;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public boolean isFlying() {
      return this.isFlying;
   }

   public boolean canFly() {
      return this.canFly;
   }

   public boolean canInstabuild() {
      return this.instabuild;
   }

   public float getFlyingSpeed() {
      return this.flyingSpeed;
   }

   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }
}
