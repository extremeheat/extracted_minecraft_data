package net.minecraft.world.entity.livingblock.movement;

import io.netty.buffer.ByteBuf;
import java.util.Objects;
import net.minecraft.network.codec.StreamCodec;

public interface MovementData {
   MovementData EMPTY = new MovementData() {
   };
   StreamCodec<ByteBuf, MovementData> STREAM_CODEC = new StreamCodec<ByteBuf, MovementData>() {
      public MovementData decode(final ByteBuf input) {
         byte type = input.readByte();
         Object var10000;
         switch (type) {
            case 1 -> var10000 = (BouncingMovement.Data)BouncingMovement.Data.STREAM_CODEC.decode(input);
            case 2 -> var10000 = (RollingMovement.Data)RollingMovement.Data.STREAM_CODEC.decode(input);
            case 3 -> var10000 = (FloatingMovement.Data)FloatingMovement.Data.STREAM_CODEC.decode(input);
            default -> var10000 = MovementData.EMPTY;
         }

         return (MovementData)var10000;
      }

      public void encode(final ByteBuf output, final MovementData value) {
         Objects.requireNonNull(value);
         byte var5 = 0;
         byte var10000;
         //$FF: var5->value
         //0->net/minecraft/world/entity/livingblock/movement/BouncingMovement$Data
         //1->net/minecraft/world/entity/livingblock/movement/RollingMovement$Data
         //2->net/minecraft/world/entity/livingblock/movement/FloatingMovement$Data
         switch (value.typeSwitch<invokedynamic>(value, var5)) {
            case 0 -> var10000 = 1;
            case 1 -> var10000 = 2;
            case 2 -> var10000 = 3;
            default -> var10000 = 0;
         }

         int type = var10000;
         output.writeByte(type);
         Objects.requireNonNull(value);
         var5 = 0;
         //$FF: var5->value
         //0->net/minecraft/world/entity/livingblock/movement/BouncingMovement$Data
         //1->net/minecraft/world/entity/livingblock/movement/RollingMovement$Data
         //2->net/minecraft/world/entity/livingblock/movement/FloatingMovement$Data
         switch (value.typeSwitch<invokedynamic>(value, var5)) {
            case 0:
               BouncingMovement.Data data = (BouncingMovement.Data)value;
               BouncingMovement.Data.STREAM_CODEC.encode(output, data);
               break;
            case 1:
               RollingMovement.Data data = (RollingMovement.Data)value;
               RollingMovement.Data.STREAM_CODEC.encode(output, data);
               break;
            case 2:
               FloatingMovement.Data data = (FloatingMovement.Data)value;
               FloatingMovement.Data.STREAM_CODEC.encode(output, data);
         }

      }
   };
}
