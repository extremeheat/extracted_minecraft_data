package net.minecraft.client;

import com.mojang.renderpearl.api.device.GpuBackend;
import com.mojang.renderpearl.backend.opengl.GlBackend;
import com.mojang.renderpearl.backend.vulkan.VulkanBackend;
import com.mojang.serialization.Codec;
import java.util.Random;
import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum PreferredGraphicsApi implements StringRepresentable {
   DEFAULT("default", "options.graphicsApi.default"),
   OPENGL("opengl", "options.graphicsApi.opengl"),
   VULKAN("vulkan", "options.graphicsApi.vulkanNonExperimental");

   public static final Codec<PreferredGraphicsApi> CODEC = StringRepresentable.<PreferredGraphicsApi>fromEnum(PreferredGraphicsApi::values);
   private final String serializedName;
   private final Component key;

   private PreferredGraphicsApi(final String serializedName, final String key) {
      this.serializedName = serializedName;
      this.key = Component.translatable(key);
   }

   public Component caption() {
      return this.key;
   }

   public String getSerializedName() {
      return this.serializedName;
   }

   public GpuBackend[] getBackendsToTry() {
      GlBackend gl = new GlBackend();
      VulkanBackend vulkan = new VulkanBackend();
      GpuBackend[] var10000;
      switch (this.ordinal()) {
         case 0 -> var10000 = (new Random()).nextBoolean() ? new GpuBackend[]{vulkan, gl} : new GpuBackend[]{gl, vulkan};
         case 1 -> var10000 = new GpuBackend[]{gl, vulkan};
         case 2 -> var10000 = new GpuBackend[]{vulkan, gl};
         default -> throw new MatchException((String)null, (Throwable)null);
      }

      return var10000;
   }

   // $FF: synthetic method
   private static PreferredGraphicsApi[] $values() {
      return new PreferredGraphicsApi[]{DEFAULT, OPENGL, VULKAN};
   }
}
