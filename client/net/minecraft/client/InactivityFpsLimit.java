package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public enum InactivityFpsLimit implements OptionEnum, StringRepresentable {
   MINIMIZED(0, "minimized", "options.inactivityFpsLimit.minimized"),
   AFK(1, "afk", "options.inactivityFpsLimit.afk");

   public static final Codec<InactivityFpsLimit> CODEC = StringRepresentable.<InactivityFpsLimit>fromEnum(InactivityFpsLimit::values);
   private final int id;
   private final String serializedName;
   private final String key;

   private InactivityFpsLimit(final int var3, final String var4, final String var5) {
      this.id = var3;
      this.serializedName = var4;
      this.key = var5;
   }

   public int getId() {
      return this.id;
   }

   public String getKey() {
      return this.key;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   // $FF: synthetic method
   private static InactivityFpsLimit[] $values() {
      return new InactivityFpsLimit[]{MINIMIZED, AFK};
   }
}
