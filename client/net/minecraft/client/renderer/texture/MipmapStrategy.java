package net.minecraft.client.renderer.texture;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MipmapStrategy implements StringRepresentable {
   AUTO("auto"),
   MEAN("mean"),
   CUTOUT("cutout"),
   STRICT_CUTOUT("strict_cutout"),
   DARK_CUTOUT("dark_cutout");

   public static final Codec<MipmapStrategy> CODEC = StringRepresentable.<MipmapStrategy>fromValues(MipmapStrategy::values);
   private final String name;

   private MipmapStrategy(final String name) {
      this.name = name;
   }

   public String getSerializedName() {
      return this.name;
   }

   // $FF: synthetic method
   private static MipmapStrategy[] $values() {
      return new MipmapStrategy[]{AUTO, MEAN, CUTOUT, STRICT_CUTOUT, DARK_CUTOUT};
   }
}
