package net.minecraft.world.item;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum Rarity implements StringRepresentable {
   COMMON(0, "common", ChatFormatting.WHITE),
   UNCOMMON(1, "uncommon", ChatFormatting.YELLOW),
   RARE(2, "rare", ChatFormatting.AQUA),
   EPIC(3, "epic", ChatFormatting.LIGHT_PURPLE);

   public static final Codec<Rarity> CODEC = StringRepresentable.<Rarity>fromValues(Rarity::values);
   public static final IntFunction<Rarity> BY_ID = ByIdMap.<Rarity>continuous((var0) -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final StreamCodec<ByteBuf, Rarity> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> var0.id);
   private final int id;
   private final String name;
   private final ChatFormatting color;

   private Rarity(final int var3, final String var4, final ChatFormatting var5) {
      this.id = var3;
      this.name = var4;
      this.color = var5;
   }

   public ChatFormatting color() {
      return this.color;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static Rarity[] $values() {
      return new Rarity[]{COMMON, UNCOMMON, RARE, EPIC};
   }
}
