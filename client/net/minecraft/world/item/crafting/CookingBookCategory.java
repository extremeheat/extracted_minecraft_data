package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import java.util.function.IntFunction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum CookingBookCategory implements StringRepresentable {
   FOOD(0, "food"),
   BLOCKS(1, "blocks"),
   MISC(2, "misc");

   private static final IntFunction<CookingBookCategory> BY_ID = ByIdMap.continuous(var0 -> var0.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec<CookingBookCategory> CODEC = StringRepresentable.fromEnum(CookingBookCategory::values);
   public static final StreamCodec<ByteBuf, CookingBookCategory> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, var0 -> var0.id);
   private final int id;
   private final String name;

   private CookingBookCategory(final int nullxx, final String nullxxx) {
      this.id = nullxx;
      this.name = nullxxx;
   }

   @Override
   public String getSerializedName() {
      return this.name;
   }
}
