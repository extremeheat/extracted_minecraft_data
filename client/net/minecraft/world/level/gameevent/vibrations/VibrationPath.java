package net.minecraft.world.level.gameevent.vibrations;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;

public class VibrationPath {
   public static final Codec<VibrationPath> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(BlockPos.CODEC.fieldOf("origin").forGetter((var0x) -> {
         return var0x.origin;
      }), PositionSource.CODEC.fieldOf("destination").forGetter((var0x) -> {
         return var0x.destination;
      }), Codec.INT.fieldOf("arrival_in_ticks").forGetter((var0x) -> {
         return var0x.arrivalInTicks;
      })).apply(var0, VibrationPath::new);
   });
   private final BlockPos origin;
   private final PositionSource destination;
   private final int arrivalInTicks;

   public VibrationPath(BlockPos var1, PositionSource var2, int var3) {
      super();
      this.origin = var1;
      this.destination = var2;
      this.arrivalInTicks = var3;
   }

   public int getArrivalInTicks() {
      return this.arrivalInTicks;
   }

   public BlockPos getOrigin() {
      return this.origin;
   }

   public PositionSource getDestination() {
      return this.destination;
   }

   public static VibrationPath read(FriendlyByteBuf var0) {
      BlockPos var1 = var0.readBlockPos();
      PositionSource var2 = PositionSourceType.fromNetwork(var0);
      int var3 = var0.readVarInt();
      return new VibrationPath(var1, var2, var3);
   }

   public static void write(FriendlyByteBuf var0, VibrationPath var1) {
      var0.writeBlockPos(var1.origin);
      PositionSourceType.toNetwork(var1.destination, var0);
      var0.writeVarInt(var1.arrivalInTicks);
   }
}
