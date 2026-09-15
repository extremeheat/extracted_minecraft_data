package net.minecraft.world.level.levelgen.densityfunction;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum TilingMode implements StringRepresentable {
   CLAMP_TO_EDGE("clamp_to_edge"),
   REPEAT("repeat"),
   MIRRORED_REPEAT("mirrored_repeat");

   public static final Codec<TilingMode> CODEC = StringRepresentable.<TilingMode>fromEnum(TilingMode::values);
   private final String name;

   private TilingMode(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static TilingMode[] $values() {
      return new TilingMode[]{CLAMP_TO_EDGE, REPEAT, MIRRORED_REPEAT};
   }
}
