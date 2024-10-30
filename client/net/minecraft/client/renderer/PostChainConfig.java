package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record PostChainConfig(Map<ResourceLocation, InternalTarget> internalTargets, List<Pass> passes) {
   public static final Codec<PostChainConfig> CODEC = RecordCodecBuilder.create((var0) -> {
      return var0.group(Codec.unboundedMap(ResourceLocation.CODEC, PostChainConfig.InternalTarget.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostChainConfig::internalTargets), PostChainConfig.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostChainConfig::passes)).apply(var0, PostChainConfig::new);
   });

   public PostChainConfig(Map<ResourceLocation, InternalTarget> var1, List<Pass> var2) {
      super();
      this.internalTargets = var1;
      this.passes = var2;
   }

   public Map<ResourceLocation, InternalTarget> internalTargets() {
      return this.internalTargets;
   }

   public List<Pass> passes() {
      return this.passes;
   }

   public sealed interface InternalTarget permits PostChainConfig.FullScreenTarget, PostChainConfig.FixedSizedTarget {
      Codec<InternalTarget> CODEC = Codec.either(PostChainConfig.FixedSizedTarget.CODEC, PostChainConfig.FullScreenTarget.CODEC).xmap((var0) -> {
         return (InternalTarget)var0.map(Function.identity(), Function.identity());
      }, (var0) -> {
         Objects.requireNonNull(var0);
         byte var2 = 0;
         Either var10000;
         //$FF: var2->value
         //0->net/minecraft/client/renderer/PostChainConfig$FixedSizedTarget
         //1->net/minecraft/client/renderer/PostChainConfig$FullScreenTarget
         switch (var0.typeSwitch<invokedynamic>(var0, var2)) {
            case 0:
               FixedSizedTarget var3 = (FixedSizedTarget)var0;
               var10000 = Either.left(var3);
               break;
            case 1:
               FullScreenTarget var4 = (FullScreenTarget)var0;
               var10000 = Either.right(var4);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });
   }

   public static record Pass(ResourceLocation programId, List<Input> inputs, ResourceLocation outputTarget, List<Uniform> uniforms) {
      private static final Codec<List<Input>> INPUTS_CODEC;
      public static final Codec<Pass> CODEC;

      public Pass(ResourceLocation var1, List<Input> var2, ResourceLocation var3, List<Uniform> var4) {
         super();
         this.programId = var1;
         this.inputs = var2;
         this.outputTarget = var3;
         this.uniforms = var4;
      }

      public ShaderProgram program() {
         return new ShaderProgram(this.programId, DefaultVertexFormat.POSITION, ShaderDefines.EMPTY);
      }

      public ResourceLocation programId() {
         return this.programId;
      }

      public List<Input> inputs() {
         return this.inputs;
      }

      public ResourceLocation outputTarget() {
         return this.outputTarget;
      }

      public List<Uniform> uniforms() {
         return this.uniforms;
      }

      static {
         INPUTS_CODEC = PostChainConfig.Input.CODEC.listOf().validate((var0) -> {
            ObjectArraySet var1 = new ObjectArraySet(var0.size());
            Iterator var2 = var0.iterator();

            Input var3;
            do {
               if (!var2.hasNext()) {
                  return DataResult.success(var0);
               }

               var3 = (Input)var2.next();
            } while(var1.add(var3.samplerName()));

            return DataResult.error(() -> {
               return "Encountered repeated sampler name: " + var3.samplerName();
            });
         });
         CODEC = RecordCodecBuilder.create((var0) -> {
            return var0.group(ResourceLocation.CODEC.fieldOf("program").forGetter(Pass::programId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(Pass::inputs), ResourceLocation.CODEC.fieldOf("output").forGetter(Pass::outputTarget), PostChainConfig.Uniform.CODEC.listOf().optionalFieldOf("uniforms", List.of()).forGetter(Pass::uniforms)).apply(var0, Pass::new);
         });
      }
   }

   public static record Uniform(String name, List<Float> values) {
      public static final Codec<Uniform> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("name").forGetter(Uniform::name), Codec.FLOAT.sizeLimitedListOf(4).fieldOf("values").forGetter(Uniform::values)).apply(var0, Uniform::new);
      });

      public Uniform(String var1, List<Float> var2) {
         super();
         this.name = var1;
         this.values = var2;
      }

      public String name() {
         return this.name;
      }

      public List<Float> values() {
         return this.values;
      }
   }

   public static record TargetInput(String samplerName, ResourceLocation targetId, boolean useDepthBuffer, boolean bilinear) implements Input {
      public static final Codec<TargetInput> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("sampler_name").forGetter(TargetInput::samplerName), ResourceLocation.CODEC.fieldOf("target").forGetter(TargetInput::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(TargetInput::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TargetInput::bilinear)).apply(var0, TargetInput::new);
      });

      public TargetInput(String var1, ResourceLocation var2, boolean var3, boolean var4) {
         super();
         this.samplerName = var1;
         this.targetId = var2;
         this.useDepthBuffer = var3;
         this.bilinear = var4;
      }

      public Set<ResourceLocation> referencedTargets() {
         return Set.of(this.targetId);
      }

      public String samplerName() {
         return this.samplerName;
      }

      public ResourceLocation targetId() {
         return this.targetId;
      }

      public boolean useDepthBuffer() {
         return this.useDepthBuffer;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   public static record TextureInput(String samplerName, ResourceLocation location, int width, int height, boolean bilinear) implements Input {
      public static final Codec<TextureInput> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(Codec.STRING.fieldOf("sampler_name").forGetter(TextureInput::samplerName), ResourceLocation.CODEC.fieldOf("location").forGetter(TextureInput::location), ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(TextureInput::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(TextureInput::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TextureInput::bilinear)).apply(var0, TextureInput::new);
      });

      public TextureInput(String var1, ResourceLocation var2, int var3, int var4, boolean var5) {
         super();
         this.samplerName = var1;
         this.location = var2;
         this.width = var3;
         this.height = var4;
         this.bilinear = var5;
      }

      public Set<ResourceLocation> referencedTargets() {
         return Set.of();
      }

      public String samplerName() {
         return this.samplerName;
      }

      public ResourceLocation location() {
         return this.location;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }

      public boolean bilinear() {
         return this.bilinear;
      }
   }

   public sealed interface Input permits PostChainConfig.TextureInput, PostChainConfig.TargetInput {
      Codec<Input> CODEC = Codec.xor(PostChainConfig.TextureInput.CODEC, PostChainConfig.TargetInput.CODEC).xmap((var0) -> {
         return (Input)var0.map(Function.identity(), Function.identity());
      }, (var0) -> {
         Objects.requireNonNull(var0);
         byte var2 = 0;
         Either var10000;
         //$FF: var2->value
         //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
         //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
         switch (var0.typeSwitch<invokedynamic>(var0, var2)) {
            case 0:
               TextureInput var3 = (TextureInput)var0;
               var10000 = Either.left(var3);
               break;
            case 1:
               TargetInput var4 = (TargetInput)var0;
               var10000 = Either.right(var4);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });

      String samplerName();

      Set<ResourceLocation> referencedTargets();
   }

   public static record FixedSizedTarget(int width, int height) implements InternalTarget {
      public static final Codec<FixedSizedTarget> CODEC = RecordCodecBuilder.create((var0) -> {
         return var0.group(ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(FixedSizedTarget::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(FixedSizedTarget::height)).apply(var0, FixedSizedTarget::new);
      });

      public FixedSizedTarget(int var1, int var2) {
         super();
         this.width = var1;
         this.height = var2;
      }

      public int width() {
         return this.width;
      }

      public int height() {
         return this.height;
      }
   }

   public static record FullScreenTarget() implements InternalTarget {
      public static final Codec<FullScreenTarget> CODEC = Codec.unit(FullScreenTarget::new);

      public FullScreenTarget() {
         super();
      }
   }
}
