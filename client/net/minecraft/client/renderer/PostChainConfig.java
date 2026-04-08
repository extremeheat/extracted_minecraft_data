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
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;

public record PostChainConfig(Map<Identifier, InternalTarget> internalTargets, List<Pass> passes) {
   public static final Codec<PostChainConfig> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.unboundedMap(Identifier.CODEC, PostChainConfig.InternalTarget.CODEC).optionalFieldOf("targets", Map.of()).forGetter(PostChainConfig::internalTargets), PostChainConfig.Pass.CODEC.listOf().optionalFieldOf("passes", List.of()).forGetter(PostChainConfig::passes)).apply(i, PostChainConfig::new));

   public PostChainConfig {
      super();
   }

   public static record InternalTarget(Optional<Integer> width, Optional<Integer> height, boolean persistent, int clearColor) {
      public static final Codec<InternalTarget> CODEC = RecordCodecBuilder.create((i) -> i.group(ExtraCodecs.POSITIVE_INT.optionalFieldOf("width").forGetter(InternalTarget::width), ExtraCodecs.POSITIVE_INT.optionalFieldOf("height").forGetter(InternalTarget::height), Codec.BOOL.optionalFieldOf("persistent", false).forGetter(InternalTarget::persistent), ExtraCodecs.ARGB_COLOR_CODEC.optionalFieldOf("clear_color", 0).forGetter(InternalTarget::clearColor)).apply(i, InternalTarget::new));

      public InternalTarget {
         super();
      }
   }

   public static record Pass(Identifier vertexShaderId, Identifier fragmentShaderId, List<Input> inputs, Identifier outputTarget, Map<String, List<UniformValue>> uniforms) {
      private static final Codec<List<Input>> INPUTS_CODEC;
      private static final Codec<Map<String, List<UniformValue>>> UNIFORM_BLOCKS_CODEC;
      public static final Codec<Pass> CODEC;

      public Pass {
         super();
      }

      public Stream<Identifier> referencedTargets() {
         Stream<Identifier> inputTargets = this.inputs.stream().flatMap((input) -> input.referencedTargets().stream());
         return Stream.concat(inputTargets, Stream.of(this.outputTarget));
      }

      static {
         INPUTS_CODEC = PostChainConfig.Input.CODEC.listOf().validate((inputs) -> {
            Set<String> samplerName = new ObjectArraySet(inputs.size());

            for(Input input : inputs) {
               if (!samplerName.add(input.samplerName())) {
                  return DataResult.error(() -> "Encountered repeated sampler name: " + input.samplerName());
               }
            }

            return DataResult.success(inputs);
         });
         UNIFORM_BLOCKS_CODEC = Codec.unboundedMap(Codec.STRING, UniformValue.CODEC.listOf());
         CODEC = RecordCodecBuilder.create((i) -> i.group(Identifier.CODEC.fieldOf("vertex_shader").forGetter(Pass::vertexShaderId), Identifier.CODEC.fieldOf("fragment_shader").forGetter(Pass::fragmentShaderId), INPUTS_CODEC.optionalFieldOf("inputs", List.of()).forGetter(Pass::inputs), Identifier.CODEC.fieldOf("output").forGetter(Pass::outputTarget), UNIFORM_BLOCKS_CODEC.optionalFieldOf("uniforms", Map.of()).forGetter(Pass::uniforms)).apply(i, Pass::new));
      }
   }

   public sealed interface Input permits PostChainConfig.TextureInput, PostChainConfig.TargetInput {
      Codec<Input> CODEC = Codec.xor(PostChainConfig.TextureInput.CODEC, PostChainConfig.TargetInput.CODEC).xmap((either) -> (Input)either.map(Function.identity(), Function.identity()), (input) -> {
         Objects.requireNonNull(input);
         int index$1 = 0;
         Either var10000;
         //$FF: index$1->value
         //0->net/minecraft/client/renderer/PostChainConfig$TextureInput
         //1->net/minecraft/client/renderer/PostChainConfig$TargetInput
         switch (input.typeSwitch<invokedynamic>(input, index$1)) {
            case 0:
               TextureInput texture = (TextureInput)input;
               var10000 = Either.left(texture);
               break;
            case 1:
               TargetInput target = (TargetInput)input;
               var10000 = Either.right(target);
               break;
            default:
               throw new MatchException((String)null, (Throwable)null);
         }

         return var10000;
      });

      String samplerName();

      Set<Identifier> referencedTargets();
   }

   public static record TextureInput(String samplerName, Identifier location, int width, int height, boolean bilinear) implements Input {
      public static final Codec<TextureInput> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("sampler_name").forGetter(TextureInput::samplerName), Identifier.CODEC.fieldOf("location").forGetter(TextureInput::location), ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(TextureInput::width), ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(TextureInput::height), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TextureInput::bilinear)).apply(i, TextureInput::new));

      public TextureInput {
         super();
      }

      public Set<Identifier> referencedTargets() {
         return Set.of();
      }
   }

   public static record TargetInput(String samplerName, Identifier targetId, boolean useDepthBuffer, boolean bilinear) implements Input {
      public static final Codec<TargetInput> CODEC = RecordCodecBuilder.create((i) -> i.group(Codec.STRING.fieldOf("sampler_name").forGetter(TargetInput::samplerName), Identifier.CODEC.fieldOf("target").forGetter(TargetInput::targetId), Codec.BOOL.optionalFieldOf("use_depth_buffer", false).forGetter(TargetInput::useDepthBuffer), Codec.BOOL.optionalFieldOf("bilinear", false).forGetter(TargetInput::bilinear)).apply(i, TargetInput::new));

      public TargetInput {
         super();
      }

      public Set<Identifier> referencedTargets() {
         return Set.of(this.targetId);
      }
   }
}
