package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum CookingBookCategory implements StringRepresentable {
   FOOD(0, "food"),
   BLOCKS(1, "blocks"),
   MISC(2, "misc");

   public static final Codec<CookingBookCategory> CODEC = StringRepresentable.<CookingBookCategory>fromEnum(CookingBookCategory::values);
   public static final StreamCodec<ByteBuf, CookingBookCategory> STREAM_CODEC = ByteBufCodecs.enumCodec(CookingBookCategory.class, (e) -> e.id);
   private final int id;
   private final String name;

   private CookingBookCategory(final int id, final String name) {
      this.id = id;
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static CookingBookCategory[] $values() {
      return new CookingBookCategory[]{FOOD, BLOCKS, MISC};
   }
}
