package net.minecraft.world;

import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;

public enum InteractionHand {
   MAIN_HAND(0),
   OFF_HAND(1);

   private static final IntFunction<InteractionHand> BY_ID = ByIdMap.<InteractionHand>continuous((h) -> h.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, InteractionHand> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (h) -> h.id);
   private final int id;

   private InteractionHand(final int id) {
      this.id = id;
   }

   public HumanoidArm asArm(final HumanoidArm mainArm) {
      HumanoidArm var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = mainArm;
         case 1 -> var10000 = mainArm.getOpposite();
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public EquipmentSlot asEquipmentSlot() {
      return this == MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
   }

   // $FF: synthetic method
   private static InteractionHand[] $values() {
      return new InteractionHand[]{MAIN_HAND, OFF_HAND};
   }
}
