package net.minecraft.world.entity;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum HumanoidArm implements StringRepresentable {
   LEFT(0, "left", "options.mainHand.left"),
   RIGHT(1, "right", "options.mainHand.right");

   public static final Codec<HumanoidArm> CODEC = StringRepresentable.<HumanoidArm>fromEnum(HumanoidArm::values);
   private static final IntFunction<HumanoidArm> BY_ID = ByIdMap.<HumanoidArm>continuous((a) -> a.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, HumanoidArm> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (a) -> a.id);
   private final int id;
   private final String name;
   private final Component caption;

   private HumanoidArm(final int id, final String name, final String translationKey) {
      this.id = id;
      this.name = name;
      this.caption = Component.translatable(translationKey);
   }

   public HumanoidArm getOpposite() {
      HumanoidArm var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = RIGHT;
         case 1 -> var10000 = LEFT;
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   public Component caption() {
      return this.caption;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static HumanoidArm[] $values() {
      return new HumanoidArm[]{LEFT, RIGHT};
   }
}
