package net.minecraft.world;

import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.EnumStreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.Nullable;

public enum Difficulty implements StringRepresentable {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   public static final StringRepresentable.EnumCodec<Difficulty> CODEC = StringRepresentable.<Difficulty>fromEnum(Difficulty::values);
   public static final EnumStreamCodec<Difficulty> STREAM_CODEC = ByteBufCodecs.<Difficulty>enumCodec(Difficulty.class, Difficulty::getId, ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private Difficulty(final int id, final String key) {
      this.id = id;
      this.key = key;
   }

   public int getId() {
      return this.id;
   }

   public Component getDisplayName() {
      return Component.translatable("options.difficulty." + this.key);
   }

   public Component getInfo() {
      return Component.translatable("options.difficulty." + this.key + ".info");
   }

   /** @deprecated */
   @Deprecated
   public static Difficulty byId(final int id) {
      return STREAM_CODEC.byId(id);
   }

   public static @Nullable Difficulty byName(final String name) {
      return CODEC.byName(name);
   }

   public String getSerializedName() {
      return this.key;
   }

   // $FF: synthetic method
   private static Difficulty[] $values() {
      return new Difficulty[]{PEACEFUL, EASY, NORMAL, HARD};
   }
}
