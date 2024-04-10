package net.minecraft.network.protocol.game;

import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class ServerboundInteractPacket implements Packet<ServerGamePacketListener> {
   public static final StreamCodec<FriendlyByteBuf, ServerboundInteractPacket> STREAM_CODEC = Packet.codec(
      ServerboundInteractPacket::write, ServerboundInteractPacket::new
   );
   private final int entityId;
   private final ServerboundInteractPacket.Action action;
   private final boolean usingSecondaryAction;
   static final ServerboundInteractPacket.Action ATTACK_ACTION = new ServerboundInteractPacket.Action() {
      @Override
      public ServerboundInteractPacket.ActionType getType() {
         return ServerboundInteractPacket.ActionType.ATTACK;
      }

      @Override
      public void dispatch(ServerboundInteractPacket.Handler var1) {
         var1.onAttack();
      }

      @Override
      public void write(FriendlyByteBuf var1) {
      }
   };

   private ServerboundInteractPacket(int var1, boolean var2, ServerboundInteractPacket.Action var3) {
      super();
      this.entityId = var1;
      this.action = var3;
      this.usingSecondaryAction = var2;
   }

   public static ServerboundInteractPacket createAttackPacket(Entity var0, boolean var1) {
      return new ServerboundInteractPacket(var0.getId(), var1, ATTACK_ACTION);
   }

   public static ServerboundInteractPacket createInteractionPacket(Entity var0, boolean var1, InteractionHand var2) {
      return new ServerboundInteractPacket(var0.getId(), var1, new ServerboundInteractPacket.InteractionAction(var2));
   }

   public static ServerboundInteractPacket createInteractionPacket(Entity var0, boolean var1, InteractionHand var2, Vec3 var3) {
      return new ServerboundInteractPacket(var0.getId(), var1, new ServerboundInteractPacket.InteractionAtLocationAction(var2, var3));
   }

   private ServerboundInteractPacket(FriendlyByteBuf var1) {
      super();
      this.entityId = var1.readVarInt();
      ServerboundInteractPacket.ActionType var2 = var1.readEnum(ServerboundInteractPacket.ActionType.class);
      this.action = var2.reader.apply(var1);
      this.usingSecondaryAction = var1.readBoolean();
   }

   private void write(FriendlyByteBuf var1) {
      var1.writeVarInt(this.entityId);
      var1.writeEnum(this.action.getType());
      this.action.write(var1);
      var1.writeBoolean(this.usingSecondaryAction);
   }

   @Override
   public PacketType<ServerboundInteractPacket> type() {
      return GamePacketTypes.SERVERBOUND_INTERACT;
   }

   public void handle(ServerGamePacketListener var1) {
      var1.handleInteract(this);
   }

   @Nullable
   public Entity getTarget(ServerLevel var1) {
      return var1.getEntityOrPart(this.entityId);
   }

   public boolean isUsingSecondaryAction() {
      return this.usingSecondaryAction;
   }

   public void dispatch(ServerboundInteractPacket.Handler var1) {
      this.action.dispatch(var1);
   }

   interface Action {
      ServerboundInteractPacket.ActionType getType();

      void dispatch(ServerboundInteractPacket.Handler var1);

      void write(FriendlyByteBuf var1);
   }

   static enum ActionType {
      INTERACT(ServerboundInteractPacket.InteractionAction::new),
      ATTACK(var0 -> ServerboundInteractPacket.ATTACK_ACTION),
      INTERACT_AT(ServerboundInteractPacket.InteractionAtLocationAction::new);

      final Function<FriendlyByteBuf, ServerboundInteractPacket.Action> reader;

      private ActionType(final Function<FriendlyByteBuf, ServerboundInteractPacket.Action> param3) {
         this.reader = nullxx;
      }
   }

   public interface Handler {
      void onInteraction(InteractionHand var1);

      void onInteraction(InteractionHand var1, Vec3 var2);

      void onAttack();
   }

   static class InteractionAction implements ServerboundInteractPacket.Action {
      private final InteractionHand hand;

      InteractionAction(InteractionHand var1) {
         super();
         this.hand = var1;
      }

      private InteractionAction(FriendlyByteBuf var1) {
         super();
         this.hand = var1.readEnum(InteractionHand.class);
      }

      @Override
      public ServerboundInteractPacket.ActionType getType() {
         return ServerboundInteractPacket.ActionType.INTERACT;
      }

      @Override
      public void dispatch(ServerboundInteractPacket.Handler var1) {
         var1.onInteraction(this.hand);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeEnum(this.hand);
      }
   }

   static class InteractionAtLocationAction implements ServerboundInteractPacket.Action {
      private final InteractionHand hand;
      private final Vec3 location;

      InteractionAtLocationAction(InteractionHand var1, Vec3 var2) {
         super();
         this.hand = var1;
         this.location = var2;
      }

      private InteractionAtLocationAction(FriendlyByteBuf var1) {
         super();
         this.location = new Vec3((double)var1.readFloat(), (double)var1.readFloat(), (double)var1.readFloat());
         this.hand = var1.readEnum(InteractionHand.class);
      }

      @Override
      public ServerboundInteractPacket.ActionType getType() {
         return ServerboundInteractPacket.ActionType.INTERACT_AT;
      }

      @Override
      public void dispatch(ServerboundInteractPacket.Handler var1) {
         var1.onInteraction(this.hand, this.location);
      }

      @Override
      public void write(FriendlyByteBuf var1) {
         var1.writeFloat((float)this.location.x);
         var1.writeFloat((float)this.location.y);
         var1.writeFloat((float)this.location.z);
         var1.writeEnum(this.hand);
      }
   }
}
