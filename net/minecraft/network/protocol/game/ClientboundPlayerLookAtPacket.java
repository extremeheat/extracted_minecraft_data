package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ClientboundPlayerLookAtPacket implements Packet {
   private double x;
   private double y;
   private double z;
   private int entity;
   private EntityAnchorArgument.Anchor fromAnchor;
   private EntityAnchorArgument.Anchor toAnchor;
   private boolean atEntity;

   public ClientboundPlayerLookAtPacket() {
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor var1, double var2, double var4, double var6) {
      this.fromAnchor = var1;
      this.x = var2;
      this.y = var4;
      this.z = var6;
   }

   public ClientboundPlayerLookAtPacket(EntityAnchorArgument.Anchor var1, Entity var2, EntityAnchorArgument.Anchor var3) {
      this.fromAnchor = var1;
      this.entity = var2.getId();
      this.toAnchor = var3;
      Vec3 var4 = var3.apply(var2);
      this.x = var4.x;
      this.y = var4.y;
      this.z = var4.z;
      this.atEntity = true;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.fromAnchor = (EntityAnchorArgument.Anchor)var1.readEnum(EntityAnchorArgument.Anchor.class);
      this.x = var1.readDouble();
      this.y = var1.readDouble();
      this.z = var1.readDouble();
      if (var1.readBoolean()) {
         this.atEntity = true;
         this.entity = var1.readVarInt();
         this.toAnchor = (EntityAnchorArgument.Anchor)var1.readEnum(EntityAnchorArgument.Anchor.class);
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
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
