package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum SwingAnimationType implements StringRepresentable {
   NONE(0, "none"),
   WHACK(1, "whack"),
   STAB(2, "stab");

   private static final IntFunction<SwingAnimationType> BY_ID = ByIdMap.<SwingAnimationType>continuous(SwingAnimationType::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec<SwingAnimationType> CODEC = StringRepresentable.<SwingAnimationType>fromEnum(SwingAnimationType::values);
   public static final StreamCodec<ByteBuf, SwingAnimationType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, SwingAnimationType::getId);
   private final int id;
   private final String name;

   private SwingAnimationType(final int var3, final String var4) {
      this.id = var3;
      this.name = var4;
   }

   public int getId() {
      return this.id;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static SwingAnimationType[] $values() {
      return new SwingAnimationType[]{NONE, WHACK, STAB};
   }
}
