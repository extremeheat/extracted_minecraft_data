package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundGameEventPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundGameEventPacket> STREAM_CODEC = Packet.codec(ClientboundGameEventPacket::write, ClientboundGameEventPacket::new);
   public static final Type NO_RESPAWN_BLOCK_AVAILABLE = new Type(0);
   public static final Type START_RAINING = new Type(1);
   public static final Type STOP_RAINING = new Type(2);
   public static final Type CHANGE_GAME_MODE = new Type(3);
   public static final Type WIN_GAME = new Type(4);
   public static final Type DEMO_EVENT = new Type(5);
   public static final Type ARROW_HIT_PLAYER = new Type(6);
   public static final Type RAIN_LEVEL_CHANGE = new Type(7);
   public static final Type THUNDER_LEVEL_CHANGE = new Type(8);
   public static final Type PUFFER_FISH_STING = new Type(9);
   public static final Type GUARDIAN_ELDER_EFFECT = new Type(10);
   public static final Type IMMEDIATE_RESPAWN = new Type(11);
   public static final Type LIMITED_CRAFTING = new Type(12);
   public static final Type LEVEL_CHUNKS_LOAD_START = new Type(13);
   public static final int DEMO_PARAM_INTRO = 0;
   public static final int DEMO_PARAM_HINT_1 = 101;
   public static final int DEMO_PARAM_HINT_2 = 102;
   public static final int DEMO_PARAM_HINT_3 = 103;
   public static final int DEMO_PARAM_HINT_4 = 104;
   private final Type event;
   private final float param;

   public ClientboundGameEventPacket(Type var1, float var2) {
      super();
      this.event = var1;
      this.param = var2;
   }

   private ClientboundGameEventPacket(FriendlyByteBuf var1) {
      super();
      this.event = (Type)ClientboundGameEventPacket.Type.TYPES.get(var1.readUnsignedByte());
      this.param = var1.readFloat();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeByte(this.event.id);
      var1.writeFloat(this.param);
   }

   public PacketType<ClientboundGameEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_GAME_EVENT;
   }

   public void handle(ClientGamePacketListener var1) {
      var1.handleGameEvent(this);
   }

   public Type getEvent() {
      return this.event;
   }

   public float getParam() {
      return this.param;
   }

   public static class Type {
      static final Int2ObjectMap<Type> TYPES = new Int2ObjectOpenHashMap();
      final int id;

      public Type(int var1) {
         super();
         this.id = var1;
         TYPES.put(var1, this);
      }
   }
}
