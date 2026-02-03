package net.minecraft.network.protocol.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;

public class ClientboundGameEventPacket implements Packet<ClientGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ClientboundGameEventPacket> STREAM_CODEC = Packet.<FriendlyByteBuf, ClientboundGameEventPacket>codec(ClientboundGameEventPacket::write, ClientboundGameEventPacket::new);
   public static final Type NO_RESPAWN_BLOCK_AVAILABLE = new Type(0);
   public static final Type START_RAINING = new Type(1);
   public static final Type STOP_RAINING = new Type(2);
   public static final Type CHANGE_GAME_MODE = new Type(3);
   public static final Type WIN_GAME = new Type(4);
   public static final Type DEMO_EVENT = new Type(5);
   public static final Type PLAY_ARROW_HIT_SOUND = new Type(6);
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

   public ClientboundGameEventPacket(final Type event, final float param) {
      super();
      this.event = event;
      this.param = param;
   }

   private ClientboundGameEventPacket(final FriendlyByteBuf input) {
      super();
      this.event = (Type)ClientboundGameEventPacket.Type.TYPES.get(input.readUnsignedByte());
      this.param = input.readFloat();
   }

   private void write(final FriendlyByteBuf output) {
      output.writeByte(this.event.id);
      output.writeFloat(this.param);
   }

   public PacketType<ClientboundGameEventPacket> type() {
      return GamePacketTypes.CLIENTBOUND_GAME_EVENT;
   }

   public void handle(final ClientGamePacketListener listener) {
      listener.handleGameEvent(this);
   }

   public Type getEvent() {
      return this.event;
   }

   public float getParam() {
      return this.param;
   }

   public static class Type {
      private static final Int2ObjectMap<Type> TYPES = new Int2ObjectOpenHashMap();
      private final int id;

      public Type(final int id) {
         super();
         this.id = id;
         TYPES.put(id, this);
      }
   }
}
