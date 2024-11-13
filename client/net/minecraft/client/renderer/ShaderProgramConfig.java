package net.minecraft.client.renderer;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public record ShaderProgramConfig(ResourceLocation vertex, ResourceLocation fragment, List<Sampler> samplers, List<Uniform> uniforms, ShaderDefines defines) {
   public static final Codec<ShaderProgramConfig> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("vertex").forGetter(ShaderProgramConfig::vertex), ResourceLocation.CODEC.fieldOf("fragment").forGetter(ShaderProgramConfig::fragment), ShaderProgramConfig.Sampler.CODEC.listOf().optionalFieldOf("samplers", List.of()).forGetter(ShaderProgramConfig::samplers), ShaderProgramConfig.Uniform.CODEC.listOf().optionalFieldOf("uniforms", List.of()).forGetter(ShaderProgramConfig::uniforms), ShaderDefines.CODEC.optionalFieldOf("defines", ShaderDefines.EMPTY).forGetter(ShaderProgramConfig::defines)).apply(var0, ShaderProgramConfig::new));

   public ShaderProgramConfig(ResourceLocation var1, ResourceLocation var2, List<Sampler> var3, List<Uniform> var4, ShaderDefines var5) {
      super();
      this.vertex = var1;
      this.fragment = var2;
      this.samplers = var3;
      this.uniforms = var4;
      this.defines = var5;
   }

   public static record Sampler(String name) {
      public static final Codec<Sampler> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("name").forGetter(Sampler::name)).apply(var0, Sampler::new));

      public Sampler(String var1) {
         super();
         this.name = var1;
      }
   }

   public static record Uniform(String name, String type, int count, List<Float> values) {
      public static final Codec<Uniform> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("name").forGetter(Uniform::name), Codec.STRING.fieldOf("type").forGetter(Uniform::type), Codec.INT.fieldOf("count").forGetter(Uniform::count), Codec.FLOAT.listOf().fieldOf("values").forGetter(Uniform::values)).apply(var0, Uniform::new)).validate(Uniform::validate);

      public Uniform(String var1, String var2, int var3, List<Float> var4) {
         super();
         this.name = var1;
         this.type = var2;
         this.count = var3;
         this.values = var4;
      }

      private static DataResult<Uniform> validate(Uniform var0) {
         int var1 = var0.count;
         int var2 = var0.values.size();
         return var2 != var1 && var2 > 1 ? DataResult.error(() -> "Invalid amount of uniform values specified (expected " + var1 + ", found " + var2 + ")") : DataResult.success(var0);
      }
   }
}
