package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundPlayerLookAtPacket> STREAM_CODEC = Packet.codec(ClientboundPlayerLookAtPacket::write, ClientboundPlayerLookAtPacket::new);
   private final double x;
   private final double y;
   private final double z;
   private final int entity;
   private final EntityAnchorArgument.Anchor fromAnchor;
   private final EntityAnchorArgument.Anchor toAnchor;
   private final boolean atEntity;

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor var1, double var2, double var4, double var6) {
      super();
      this.fromAnchor = var1;
      this.x = var2;
      this.y = var4;
      this.z = var6;
      this.entity = 0;
      this.atEntity = false;
      this.toAnchor = null;
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor var1, Entity var2, EntityAnchorArgument.Anchor var3) {
      super();
      this.fromAnchor = var1;
      this.entity = var2.getId();
      this.toAnchor = var3;
      Vec3 var4 = var3.apply(var2);
      this.x = var4.x;
      this.y = var4.y;
      this.z = var4.z;
      this.atEntity = true;
   }

   private ClientboundPlayerLookAtPacket(FriendlyByteBuf var1) {
      super();
      this.fromAnchor = (EntityAnchorArgument.Anchor)var1.readEnum(EntityAnchorArgument.Anchor.class);
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      this.atEntity = var1.readBoolean();
      if (this.atEntity) {
         this.entity = var1.readVarInt();
         this.toAnchor = (EntityAnchorArgument.Anchor)var1.readEnum(EntityAnchorArgument.Anchor.class);
      } else {
         this.entity = 0;
         this.toAnchor = null;
      }

   }

   private void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.fromAnchor);
      var1.writeDouble(this.x);
      var1.writeDouble(this.y);
      var1.writeDouble(this.z);
      var1.writeBoolean(this.atEntity);
      if (this.atEntity) {
         var1.writeVarInt(this.entity);
         var1.writeEnum(this.toAnchor);
      }

   }

   public PacketType<ClientboundPlayerLookAtPacket> type() {
      return GamePacketTypes.CLIENTBOUND_PLAYER_LOOK_AT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleLookAt(this);
   }

   public EntityAnchorArgument.Anchor getFromAnchor() {
      return this.fromAnchor;
   }

   @Nullable
   public Vec3 getPosition(Level var1) {
      if (this.atEntity) {
         Entity var2 = var1.getEntity(this.entity);
         return var2 == null ? new Vec3(this.x, this.y, this.z) : this.toAnchor.apply(var2);
      } else {
         return new Vec3(this.x, this.y, this.z);
      }
   }
}
