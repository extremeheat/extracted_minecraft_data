package com.mojang.authlib.minecraft;

import java.util.Map;
import javax.annotation.Nullable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MinecraftProfileTexture {
   public static final int PROFILE_TEXTURE_COUNT = MinecraftProfileTexture.Type.values().length;
   private final String url;
   private final Map<String, String> metadata;

   public MinecraftProfileTexture(String var1, Map<String, String> var2) {
      super();
      this.url = var1;
      this.metadata = var2;
   }

   public String getUrl() {
      return this.url;
   }

   @Nullable
   public String getMetadata(String var1) {
      return this.metadata == null ? null : (String)this.metadata.get(var1);
   }

   public String getHash() {
      return FilenameUtils.getBaseName(this.url);
   }

   public String toString() {
      return (new ToStringBuilder(this)).append("url", (Object)this.url).append("hash", (Object)this.getHash()).toString();
   }

   public static enum Type {
      SKIN,
      CAPE,
      ELYTRA;

      private Type() {
      }
   }
}
