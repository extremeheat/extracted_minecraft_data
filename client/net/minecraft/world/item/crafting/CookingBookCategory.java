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

   private static final IntFunction<CookingBookCategory> BY_ID = ByIdMap.continuous((var0) -> {
      return var0.id;
   }, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
   public static final Codec<CookingBookCategory> CODEC = StringRepresentable.fromEnum(CookingBookCategory::values);
   public static final StreamCodec<ByteBuf, CookingBookCategory> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, (var0) -> {
      return var0.id;
   });
   private final int id;
   private final String name;

   private CookingBookCategory(final int var3, final String var4) {
      this.id = var3;
      this.name = var4;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static CookingBookCategory[] $values() {
      return new CookingBookCategory[]{FOOD, BLOCKS, MISC};
   }
}
