package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;

public class ServerboundPlayerCommandPacket implements Packet<ServerGamePacketListener> {
   private final int id;
   private final Action action;
   private final int data;

   public ServerboundPlayerCommandPacket(Entity var1, Action var2) {
      this(var1, var2, 0);
   }

   public ServerboundPlayerCommandPacket(Entity var1, Action var2, int var3) {
      super();
      this.id = var1.getId();
      this.action = var2;
      this.data = var3;
   }

   public ServerboundPlayerCommandPacket(FriendlyByteBuf var1) {
      super();
      this.id = var1.readVarInt();
      this.action = (Action)var1.readEnum(Action.class);
      this.data = var1.readVarInt();
   }

   public void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.id);
      var1.writeEnum(this.action);
      var1.writeVarInt(this.data);
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handlePlayerCommand(this);
   }

   public int getId() {
      return this.id;
   }

   public Action getAction() {
      return this.action;
   }

   public int getData() {
      return this.data;
   }

   public static enum Action {
      PRESS_SHIFT_KEY,
      RELEASE_SHIFT_KEY,
      STOP_SLEEPING,
      START_SPRINTING,
      STOP_SPRINTING,
      START_RIDING_JUMP,
      STOP_RIDING_JUMP,
      OPEN_INVENTORY,
      START_FALL_FLYING;

      private Action() {
      }

      // $FF: synthetic method
      private static Action[] $values() {
         return new Action[]{PRESS_SHIFT_KEY, RELEASE_SHIFT_KEY, STOP_SLEEPING, START_SPRINTING, STOP_SPRINTING, START_RIDING_JUMP, STOP_RIDING_JUMP, OPEN_INVENTORY, START_FALL_FLYING};
      }
   }
}
