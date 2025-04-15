package net.minecraft.client.renderer;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

public record PostChainConfig(Map<ResourceLocation, InternalTarget> internalTargets, List<Pass> passes) {
   public static final Codec<PostChainConfig> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.unboundedMap(ResourceLocation.CODEC, PostChainConfig.InternalTarget.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostChainConfig::internalTargets), PostChainConfig.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostChainConfig::passes)).apply(var0, PostChainConfig::new));

   public PostChainConfig(Map<ResourceLocation, InternalTarget> var1, List<Pass> var2) {
      super();
      this.internalTargets = var1;
      this.passes = var2;
   }

   public static record InternalTarget(Optional<Integer> width, Optional<Integer> height, boolean persistent, int clearColor) {
      public static final Codec<InternalTarget> CODEC = RecordCodecBuilder.create((var0) -> var0.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("width").forGetter(InternalTarget::width), ExtraCodecs.POSITIVE_INT.optionalFieldOf("height").forGetter(InternalTarget::height), Codec.BOOL.optionalFieldOf("persistent", false).forGetter(InternalTarget::persistent), ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("clear_color", 0).forGetter(InternalTarget::clearColor)).apply(var0, InternalTarget::new));

      public InternalTarget(Optional<Integer> var1, Optional<Integer> var2, boolean var3, int var4) {
         super();
         this.width = var1;
         this.height = var2;
         this.persistent = var3;
         this.clearColor = var4;
      }
   }

   public static record Pass(ResourceLocation vertexShaderId, ResourceLocation fragmentShaderId, List<Input> inputs, ResourceLocation outputTarget, Map<String, List<UniformValue>> uniforms) {
      private static final Codec<List<Input>> INPUTS_CODEC;
      private static final Codec<Map<String, List<UniformValue>>> UNIFORM_BLOCKS_CODEC;
      public static final Codec<Pass> CODEC;

      public Pass(ResourceLocation var1, ResourceLocation var2, List<Input> var3, ResourceLocation var4, Map<String, List<UniformValue>> var5) {
         super();
         this.vertexShaderId = var1;
         this.fragmentShaderId = var2;
         this.inputs = var3;
         this.outputTarget = var4;
         this.uniforms = var5;
      }

      public Stream<ResourceLocation> referencedTargets() {
         Stream var1 = this.inputs.stream().flatMap((var0) -> var0.referencedTargets().stream());
         return Stream.concat(var1, Stream.of(this.outputTarget));
      }

      static {
         INPUTS_CODEC = PostChainConfig.Input.CODEC.listOf().validate((var0) -> {
            ObjectArraySet var1 = new ObjectArraySet(var0.size());

            for(Input var3 : var0) {
               if (!var1.add(var3.samplerName())) {
                  return DataResult.error(() -> "Encountered repeated sampler name: " + var3.samplerName());
               }
            }

            return DataResult.success(var0);
         });
         UNIFORM_BLOCKS_CODEC = Codec.unboundedMap(Codec.STRING, UniformValue.CODEC.listOf());
         CODEC = RecordCodecBuilder.create((var0) -> var0.group(ResourceLocation.CODEC.fieldOf("vertex_shader").forGetter(Pass::vertexShaderId), ResourceLocation.CODEC.fieldOf("fragment_shader").forGetter(Pass::fragmentShaderId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(Pass::inputs), ResourceLocation.CODEC.fieldOf("output").forGetter(Pass::outputTarget), UNIFORM_BLOCKS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(Pass::uniforms)).apply(var0, Pass::new));
      }
   }

   public sealed interface Input permits PostChainConfig.TextureInput, PostChainConfig.TargetInput {
      Codec<Input> CODEC = Codec.xor(PostChainConfig.TextureInput.CODEC, PostChainConfig.TargetInput.CODEC).xmap((var0) -> (Input)var0.map(Function.identity(), Function.identity()), (var0) -> {
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

   public static record TextureInput(String samplerName, ResourceLocation location, int width, int height, boolean bilinear) implements Input {
      public static final Codec<TextureInput> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("sampler_name").forGetter(TextureInput::samplerName), ResourceLocation.CODEC.fieldOf("location").forGetter(TextureInput::location), ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(TextureInput::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(TextureInput::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TextureInput::bilinear)).apply(var0, TextureInput::new));

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
   }

   public static record TargetInput(String samplerName, ResourceLocation targetId, boolean useDepthBuffer, boolean bilinear) implements Input {
      public static final Codec<TargetInput> CODEC = RecordCodecBuilder.create((var0) -> var0.group(Codec.STRING.fieldOf("sampler_name").forGetter(TargetInput::samplerName), ResourceLocation.CODEC.fieldOf("target").forGetter(TargetInput::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(TargetInput::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TargetInput::bilinear)).apply(var0, TargetInput::new));

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
   }
}
