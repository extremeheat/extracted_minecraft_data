package net.minecraft.network.protocol.game;

import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket implements Packet<ClientGamePacketListener> {
   // $FF: renamed from: x double
   private final double field_508;
   // $FF: renamed from: y double
   private final double field_509;
   // $FF: renamed from: z double
   private final double field_510;
   private final int entity;
   private final EntityAnchorArgument.Anchor fromAnchor;
   private final EntityAnchorArgument.Anchor toAnchor;
   private final boolean atEntity;

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor var1, double var2, double var4, double var6) {
      super();
      this.fromAnchor = var1;
      this.field_508 = var2;
      this.field_509 = var4;
      this.field_510 = var6;
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
      this.field_508 = var4.field_414;
      this.field_509 = var4.field_415;
      this.field_510 = var4.field_416;
      this.atEntity = true;
   }

   public ClientboundPlayerLookAtPacket(FriendlyByteBuf var1) {
      super();
      this.fromAnchor = (EntityAnchorArgument.Anchor)var1.readEnum(EntityAnchorArgument.Anchor.class);
      this.field_508 = var1.readDouble();
      this.field_509 = var1.readDouble();
      this.field_510 = var1.readDouble();
      this.atEntity = var1.readBoolean();
      if (this.atEntity) {
         this.entity = var1.readVarInt();
         this.toAnchor = (EntityAnchorArgument.Anchor)var1.readEnum(EntityAnchorArgument.Anchor.class);
      } else {
         this.entity = 0;
         this.toAnchor = null;
      }

   }

   public void write(FriendlyByteBuf var1) {
      var1.writeEnum(this.fromAnchor);
      var1.writeDouble(this.field_508);
      var1.writeDouble(this.field_509);
      var1.writeDouble(this.field_510);
      var1.writeBoolean(this.atEntity);
      if (this.atEntity) {
         var1.writeVarInt(this.entity);
         var1.writeEnum(this.toAnchor);
      }

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
         return var2 == null ? new Vec3(this.field_508, this.field_509, this.field_510) : this.toAnchor.apply(var2);
      } else {
         return new Vec3(this.field_508, this.field_509, this.field_510);
      }
   }
}
