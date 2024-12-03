package net.minecraft.world;

import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

public enum Difficulty implements StringRepresentable {
   PEACEFUL(0, "peaceful"),
   EASY(1, "easy"),
   NORMAL(2, "normal"),
   HARD(3, "hard");

   public static final StringRepresentable.EnumCodec<Difficulty> CODEC = StringRepresentable.<Difficulty>fromEnum(Difficulty::values);
   private static final IntFunction<Difficulty> BY_ID = ByIdMap.<Difficulty>continuous(Difficulty::getId, values(), ByIdMap.OutOfBoundsStrategy.WRAP);
   private final int id;
   private final String key;

   private Difficulty(final int var3, final String var4) {
      this.id = var3;
      this.key = var4;
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

   public static Difficulty byId(int var0) {
      return (Difficulty)BY_ID.apply(var0);
   }

   @Nullable
   public static Difficulty byName(String var0) {
      return CODEC.byName(var0);
   }

   public String getKey() {
      return this.key;
   }

   public String getSerializedName() {
      return this.key;
   }

   // $FF: synthetic method
   private static Difficulty[] $values() {
      return new Difficulty[]{PEACEFUL, EASY, NORMAL, HARD};
   }
}
