package net.minecraft.network.protocol.game;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;

public abstract class ClientboundMoveEntityPacket implements MovementPacket<ClientGamePacketListener> {
   private static final int ON_GROUND_FLAG = 1;
   private static final int STEP_COUNT_OFFSET = 1;
   protected final int entityId;
   protected final VecDelta delta;
   protected final byte yRot;
   protected final byte xRot;
   protected final boolean onGround;
   protected final boolean hasRot;
   protected final boolean hasPos;

   protected static boolean unpackOnGround(final int properties) {
      return (properties & 1) != 0;
   }

   protected static int unpackStepCount(final int properties) {
      return properties >>> 1;
   }

   protected static int packProperties(final boolean onGround, final int stepCount) {
      return (onGround ? 1 : 0) | stepCount << 1;
   }

   protected ClientboundMoveEntityPacket(final int entityId, final VecDelta delta, final byte yRot, final byte xRot, final boolean onGround, final boolean hasRot, final boolean hasPos) {
      super();
      this.entityId = entityId;
      this.delta = delta;
      this.yRot = yRot;
      this.xRot = xRot;
      this.onGround = onGround;
      this.hasRot = hasRot;
      this.hasPos = hasPos;
   }

   public abstract PacketType<? extends ClientboundMoveEntityPacket> type();

   public void handle(final ClientGamePacketListener listener) {
      listener.handleMoveEntity(this);
   }

   public String toString() {
      return "Entity_" + super.toString();
   }

   public @Nullable Entity getEntity(final Level level) {
      return level.getEntity(this.entityId);
   }

   public VecDelta getPositionDelta() {
      return this.delta;
   }

   public float getYRot() {
      return Mth.unpackDegrees(this.yRot);
   }

   public float getXRot() {
      return Mth.unpackDegrees(this.xRot);
   }

   public boolean hasRotation() {
      return this.hasRot;
   }

   public boolean hasPosition() {
      return this.hasPos;
   }

   public boolean isOnGround() {
      return this.onGround;
   }

   public static class PosRot extends ClientboundMoveEntityPacket {
      public static final StreamCodec<FriendlyByteBuf, PosRot> STREAM_CODEC = Packet.<FriendlyByteBuf, PosRot>codec(PosRot::write, PosRot::read);

      public PosRot(final int id, final VecDelta delta, final byte yRot, final byte xRot, final boolean onGround) {
         super(id, delta, yRot, xRot, onGround, true, true);
      }

      private static PosRot read(final FriendlyByteBuf input) {
         int entityId = input.readVarInt();
         int properties = input.readVarInt();
         VecDelta delta = VecDelta.read(input, unpackStepCount(properties));
         byte yRot = input.readByte();
         byte xRot = input.readByte();
         return new PosRot(entityId, delta, yRot, xRot, unpackOnGround(properties));
      }

      private void write(final FriendlyByteBuf output) {
         output.writeVarInt(this.entityId);
         output.writeVarInt(packProperties(this.onGround, this.delta.stepCount()));
         VecDelta.write(output, this.delta);
         output.writeByte(this.yRot);
         output.writeByte(this.xRot);
      }

      public PacketType<PosRot> type() {
         return GamePacketTypes.CLIENTBOUND_MOVE_ENTITY_POS_ROT;
      }
   }

   public static class Pos extends ClientboundMoveEntityPacket {
      public static final StreamCodec<FriendlyByteBuf, Pos> STREAM_CODEC = Packet.<FriendlyByteBuf, Pos>codec(Pos::write, Pos::read);

      public Pos(final int id, final VecDelta delta, final boolean onGround) {
         super(id, delta, (byte)0, (byte)0, onGround, false, true);
      }

      private static Pos read(final FriendlyByteBuf input) {
         int entityId = input.readVarInt();
         int properties = input.readVarInt();
         VecDelta delta = VecDelta.read(input, unpackStepCount(properties));
         return new Pos(entityId, delta, unpackOnGround(properties));
      }

      private void write(final FriendlyByteBuf output) {
         output.writeVarInt(this.entityId);
         output.writeVarInt(packProperties(this.onGround, this.delta.stepCount()));
         VecDelta.write(output, this.delta);
      }

      public PacketType<Pos> type() {
         return GamePacketTypes.CLIENTBOUND_MOVE_ENTITY_POS;
      }
   }

   public static class Rot extends ClientboundMoveEntityPacket {
      public static final StreamCodec<FriendlyByteBuf, Rot> STREAM_CODEC = Packet.<FriendlyByteBuf, Rot>codec(Rot::write, Rot::read);

      public Rot(final int id, final byte yRot, final byte xRot, final boolean onGround) {
         super(id, VecDelta.ZERO, yRot, xRot, onGround, true, false);
      }

      private static Rot read(final FriendlyByteBuf input) {
         int entityId = input.readVarInt();
         boolean onGround = input.readBoolean();
         byte yRot = input.readByte();
         byte xRot = input.readByte();
         return new Rot(entityId, yRot, xRot, onGround);
      }

      private void write(final FriendlyByteBuf output) {
         output.writeVarInt(this.entityId);
         output.writeBoolean(this.onGround);
         output.writeByte(this.yRot);
         output.writeByte(this.xRot);
      }

      public PacketType<Rot> type() {
         return GamePacketTypes.CLIENTBOUND_MOVE_ENTITY_ROT;
      }
   }
}
