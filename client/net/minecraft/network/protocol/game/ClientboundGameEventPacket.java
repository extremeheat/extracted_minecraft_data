package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;

public class ClientboundGameEventPacket implements Packet<ClientGamePacketListener> {
   public static final ClientboundGameEventPacket.Type NO_RESPAWN_BLOCK_AVAILABLE = new ClientboundGameEventPacket.Type(0);
   public static final ClientboundGameEventPacket.Type START_RAINING = new ClientboundGameEventPacket.Type(1);
   public static final ClientboundGameEventPacket.Type STOP_RAINING = new ClientboundGameEventPacket.Type(2);
   public static final ClientboundGameEventPacket.Type CHANGE_GAME_MODE = new ClientboundGameEventPacket.Type(3);
   public static final ClientboundGameEventPacket.Type WIN_GAME = new ClientboundGameEventPacket.Type(4);
   public static final ClientboundGameEventPacket.Type DEMO_EVENT = new ClientboundGameEventPacket.Type(5);
   public static final ClientboundGameEventPacket.Type ARROW_HIT_PLAYER = new ClientboundGameEventPacket.Type(6);
   public static final ClientboundGameEventPacket.Type RAIN_LEVEL_CHANGE = new ClientboundGameEventPacket.Type(7);
   public static final ClientboundGameEventPacket.Type THUNDER_LEVEL_CHANGE = new ClientboundGameEventPacket.Type(8);
   public static final ClientboundGameEventPacket.Type PUFFER_FISH_STING = new ClientboundGameEventPacket.Type(9);
   public static final ClientboundGameEventPacket.Type GUARDIAN_ELDER_EFFECT = new ClientboundGameEventPacket.Type(10);
   public static final ClientboundGameEventPacket.Type IMMEDIATE_RESPAWN = new ClientboundGameEventPacket.Type(11);
   private ClientboundGameEventPacket.Type event;
   private float param;

   public ClientboundGameEventPacket() {
      super();
   }

   public ClientboundGameEventPacket(ClientboundGameEventPacket.Type var1, float var2) {
      super();
      this.event = var1;
      this.param = var2;
   }

   public void read(FriendlyByteBuf var1) throws IOException {
      this.event = (ClientboundGameEventPacket.Type)ClientboundGameEventPacket.Type.TYPES.get(var1.readUnsignedByte());
      this.param = var1.readFloat();
   }

   public void write(FriendlyByteBuf var1) throws IOException {
      var1.writeByte(this.event.id);
      var1.writeFloat(this.param);
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleGameEvent(this);
   }

   public ClientboundGameEventPacket.Type getEvent() {
      return this.event;
   }

   public float getParam() {
      return this.param;
   }

   public static class Type {
      private static final Int2ObjectMap<ClientboundGameEventPacket.Type> TYPES = new Int2ObjectOpenHashMap();
      private final int id;

      public Type(int var1) {
         super();
         this.id = var1;
         TYPES.put(var1, this);
      }
   }
}
