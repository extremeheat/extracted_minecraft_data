package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public enum InactivityFpsLimit implements OptionEnum, StringRepresentable {
   MINIMIZED(0, "minimized", "options.inactivityFpsLimit.minimized"),
   AFK(1, "afk", "options.inactivityFpsLimit.afk");

   public static final Codec<InactivityFpsLimit> CODEC = StringRepresentable.fromEnum(InactivityFpsLimit::values);
   private final int id;
   private final String serializedName;
   private final String key;

   private InactivityFpsLimit(final int nullxx, final String nullxxx, final String nullxxxx) {
      this.id = nullxx;
      this.serializedName = nullxxx;
      this.key = nullxxxx;
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public String getKey() {
      return this.key;
   }

   @Override
   public String getSerializedName() {
      return this.serializedName;
   }
}
