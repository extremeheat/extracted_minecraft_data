package net.minecraft.client.gui.components.debug;

import net.minecraft.util.StringRepresentable;

public enum DebugScreenProfile implements StringRepresentable {
   DEFAULT("default", "debug.options.profile.default"),
   PERFORMANCE("performance", "debug.options.profile.performance");

   public static final StringRepresentable.EnumCodec<DebugScreenProfile> CODEC = StringRepresentable.<DebugScreenProfile>fromEnum(DebugScreenProfile::values);
   private final String name;
   private final String translationKey;

   private DebugScreenProfile(final String var3, final String var4) {
      this.name = var3;
      this.translationKey = var4;
   }

   public String translationKey() {
      return this.translationKey;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static DebugScreenProfile[] $values() {
      return new DebugScreenProfile[]{DEFAULT, PERFORMANCE};
   }
}
