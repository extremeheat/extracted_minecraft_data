package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ServerboundInteractPacket implements Packet {
   private int entityId;
   private ServerboundInteractPacket.Action action;
   private Vec3 location;
   private InteractionHand hand;

   public ServerboundInteractPacket() {
   }

   public ServerboundInteractPacket(Entity var1) {
      this.entityId = var1.getId();
      this.action = ServerboundInteractPacket.Action.ATTACK;
   }

   public ServerboundInteractPacket(Entity var1, InteractionHand var2) {
      this.entityId = var1.getId();
      this.action = ServerboundInteractPacket.Action.INTERACT;
      this.hand = var2;
   }

   public ServerboundInteractPacket(Entity var1, InteractionHand var2, Vec3 var3) {
      this.entityId = var1.getId();
      this.action = ServerboundInteractPacket.Action.INTERACT_AT;
      this.hand = var2;
      this.location = var3;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.entityId = var1.readVarInt();
      this.action = (ServerboundInteractPacket.Action)var1.readEnum(ServerboundInteractPacket.Action.class);
      if (this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         this.location = new Vec3((double)var1.readFloat(), (double)var1.readFloat(), (double)var1.readFloat());
      }

      if (this.action == ServerboundInteractPacket.Action.INTERACT || this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         this.hand = (InteractionHand)var1.readEnum(InteractionHand.class);
      }

   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeVarInt(this.entityId);
      var1.writeEnum(this.action);
      if (this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         var1.writeFloat((float)this.location.x);
         var1.writeFloat((float)this.location.y);
         var1.writeFloat((float)this.location.z);
      }

      if (this.action == ServerboundInteractPacket.Action.INTERACT || this.action == ServerboundInteractPacket.Action.INTERACT_AT) {
         var1.writeEnum(this.hand);
      }

   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleInteract(this);
   }

   @Nullable
   public Entity getTarget(Level var1) {
      return var1.getEntity(this.entityId);
   }

   public ServerboundInteractPacket.Action getAction() {
      return this.action;
   }

   public InteractionHand getHand() {
      return this.hand;
   }

   public Vec3 getLocation() {
      return this.location;
   }

   public static enum Action {
      INTERACT,
      ATTACK,
      INTERACT_AT;
   }
}
