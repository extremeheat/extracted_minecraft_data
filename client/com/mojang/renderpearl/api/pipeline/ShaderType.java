package com.mojang.renderpearl.api.pipeline;

public enum ShaderType {
   VERTEX("vertex", ".vsh"),
   FRAGMENT("fragment", ".fsh");

   private final String name;
   private final String extension;

   private ShaderType(final String name, final String extension) {
      this.name = name;
      this.extension = extension;
   }

   public String getName() {
      return this.name;
   }

   public String getExtension() {
      return this.extension;
   }

   // $FF: synthetic method
   private static ShaderType[] $values() {
      return new ShaderType[]{VERTEX, FRAGMENT};
   }
}
