package net.minecraft.world.item.crafting;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;

public enum CraftingBookCategory implements StringRepresentable {
   BUILDING("building", 0),
   REDSTONE("redstone", 1),
   EQUIPMENT("equipment", 2),
   MISC("misc", 3);

   public static final Codec<CraftingBookCategory> CODEC = StringRepresentable.<CraftingBookCategory>fromEnum(CraftingBookCategory::values);
   public static final StreamCodec<ByteBuf, CraftingBookCategory> STREAM_CODEC = ByteBufCodecs.enumCodec(CraftingBookCategory.class, CraftingBookCategory::id);
   private final String name;
   private final int id;

   private CraftingBookCategory(final String name, final int id) {
      this.name = name;
      this.id = id;
   }

   public String getSerializedName() {
      return this.name;
   }

   private int id() {
      return this.id;
   }

   // $FF: synthetic method
   private static CraftingBookCategory[] $values() {
      return new CraftingBookCategory[]{BUILDING, REDSTONE, EQUIPMENT, MISC};
   }
}
