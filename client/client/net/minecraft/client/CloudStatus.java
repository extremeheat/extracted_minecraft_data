package net.minecraft.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.OptionEnum;
import net.minecraft.util.StringRepresentable;

public enum CloudStatus implements OptionEnum, StringRepresentable {
   OFF(0, "false", "options.off"),
   FAST(1, "fast", "options.clouds.fast"),
   FANCY(2, "true", "options.clouds.fancy");

   public static final Codec<CloudStatus> CODEC = StringRepresentable.fromEnum(CloudStatus::values);
   private final int id;
   private final String legacyName;
   private final String key;

   private CloudStatus(final int param3, final String param4, final String param5) {
      this.id = nullxx;
      this.legacyName = nullxxx;
      this.key = nullxxxx;
   }

   @Override
   public String getSerializedName() {
      return this.legacyName;
   }

   @Override
   public int getId() {
      return this.id;
   }

   @Override
   public String getKey() {
      return this.key;
   }
}
