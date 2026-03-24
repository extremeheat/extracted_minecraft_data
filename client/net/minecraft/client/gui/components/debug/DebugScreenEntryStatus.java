package net.minecraft.client.gui.components.debug;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum DebugScreenEntryStatus implements StringRepresentable {
   ALWAYS_ON("alwaysOn"),
   IN_OVERLAY("inOverlay"),
   NEVER("never");

   public static final Codec<DebugScreenEntryStatus> CODEC = StringRepresentable.<DebugScreenEntryStatus>fromEnum(DebugScreenEntryStatus::values);
   private final String name;

   private DebugScreenEntryStatus(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static DebugScreenEntryStatus[] $values() {
      return new DebugScreenEntryStatus[]{ALWAYS_ON, IN_OVERLAY, NEVER};
   }
}
