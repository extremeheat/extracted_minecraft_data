package com.mojang.blaze3d.shaders;

import com.mojang.blaze3d.DontObfuscate;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

@DontObfuscate
public enum ShaderType {
   VERTEX("vertex", ".vsh"),
   FRAGMENT("fragment", ".fsh");

   private static final ShaderType[] TYPES = values();
   private final String name;
   private final String extension;

   private ShaderType(final String var3, final String var4) {
      this.name = var3;
      this.extension = var4;
   }

   public static @Nullable ShaderType byLocation(Identifier var0) {
      for(ShaderType var4 : TYPES) {
         if (var0.getPath().endsWith(var4.extension)) {
            return var4;
         }
      }

      return null;
   }

   public String getName() {
      return this.name;
   }

   public FileToIdConverter idConverter() {
      return new FileToIdConverter("shaders", this.extension);
   }

   // $FF: synthetic method
   private static ShaderType[] $values() {
      return new ShaderType[]{VERTEX, FRAGMENT};
   }
}
