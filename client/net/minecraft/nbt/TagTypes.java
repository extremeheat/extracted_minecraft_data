package net.minecraft.nbt;

public class TagTypes {
   private static final TagType<?>[] TYPES;

   public TagTypes() {
      super();
   }

   public static TagType<?> getType(int var0) {
      return var0 >= 0 && var0 < TYPES.length ? TYPES[var0] : TagType.createInvalid(var0);
   }

   static {
      TYPES = new TagType[]{EndTag.TYPE, ByteTag.TYPE, ShortTag.TYPE, IntTag.TYPE, LongTag.TYPE, FloatTag.TYPE, DoubleTag.TYPE, ByteArrayTag.TYPE, StringTag.TYPE, ListTag.TYPE, CompoundTag.TYPE, IntArrayTag.TYPE, LongArrayTag.TYPE};
   }
}
