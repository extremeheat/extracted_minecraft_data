package net.minecraft.client.renderer.oit;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.renderer.RenderPipelines;

public record OitPipelineSet(RenderPipeline depthBoundsPipeline, RenderPipeline transmittancePipeline, RenderPipeline accumulatePipeline) {
   public OitPipelineSet {
      super();
   }

   public RenderPipeline getPipeline(final OitStage stage) {
      RenderPipeline var10000;
      switch (stage) {
         case DEPTH_BOUNDS -> var10000 = this.depthBoundsPipeline;
         case TRANSMITTANCE -> var10000 = this.transmittancePipeline;
         case ACCUMULATE -> var10000 = this.accumulatePipeline;
         default -> throw new IllegalArgumentException("Unsupported OIT stage.");
      }

      return var10000;
   }

   public static Builder builder(final String locationSuffix, final RenderPipeline.Builder builder) {
      return new Builder(builder.buildSnippet(), locationSuffix);
   }

   public static class Builder {
      private static final Consumer<RenderPipeline.Builder> DISABLE_DEPTH_TEST = (builder) -> builder.withDepthStencilState(Optional.empty());
      private final RenderPipeline.Snippet baseSnippet;
      private final String locationSuffix;
      private Optional<Consumer<RenderPipeline.Builder>> depthBoundsModifier = Optional.empty();
      private Optional<Consumer<RenderPipeline.Builder>> transmittanceModifier = Optional.empty();
      private Optional<Consumer<RenderPipeline.Builder>> accumulateModifier = Optional.empty();

      public Builder(final RenderPipeline.Snippet baseSnippet, final String locationSuffix) {
         super();
         this.baseSnippet = baseSnippet;
         this.locationSuffix = locationSuffix;
      }

      public Builder withDepthBoundsModifier(final Consumer<RenderPipeline.Builder> modifier) {
         this.depthBoundsModifier = composeModifiers(this.depthBoundsModifier, modifier);
         return this;
      }

      public Builder withTransmittanceModifier(final Consumer<RenderPipeline.Builder> modifier) {
         this.transmittanceModifier = composeModifiers(this.transmittanceModifier, modifier);
         return this;
      }

      public Builder withAccumulateModifier(final Consumer<RenderPipeline.Builder> modifier) {
         this.accumulateModifier = composeModifiers(this.accumulateModifier, modifier);
         return this;
      }

      public Builder withoutDepthTest() {
         return this.withDepthBoundsModifier(DISABLE_DEPTH_TEST).withTransmittanceModifier(DISABLE_DEPTH_TEST).withAccumulateModifier(DISABLE_DEPTH_TEST);
      }

      private static Optional<Consumer<RenderPipeline.Builder>> composeModifiers(final Optional<Consumer<RenderPipeline.Builder>> currentModifier, final Consumer<RenderPipeline.Builder> newModifier) {
         return currentModifier.isPresent() ? Optional.of((Consumer)(builder) -> {
            ((Consumer)currentModifier.get()).accept(builder);
            newModifier.accept(builder);
         }) : Optional.of(newModifier);
      }

      public OitPipelineSet build() {
         RenderPipeline.Builder depthBoundsBuilder = RenderPipeline.builder(this.baseSnippet, RenderPipelines.OIT_DEPTH_BOUNDS_SNIPPET).withLocation("pipeline/oit_depth_bounds_" + this.locationSuffix);
         this.depthBoundsModifier.ifPresent((modifier) -> modifier.accept(depthBoundsBuilder));
         RenderPipeline.Builder transmittanceBuilder = RenderPipeline.builder(this.baseSnippet, RenderPipelines.OIT_TRANSMITTANCE_SNIPPET).withLocation("pipeline/oit_transmittance_" + this.locationSuffix);
         this.transmittanceModifier.ifPresent((modifier) -> modifier.accept(transmittanceBuilder));
         RenderPipeline.Builder accumulateBuilder = RenderPipeline.builder(this.baseSnippet, RenderPipelines.OIT_ACCUMULATE_SNIPPET).withLocation("pipeline/oit_accumulate_" + this.locationSuffix);
         this.accumulateModifier.ifPresent((modifier) -> modifier.accept(accumulateBuilder));
         return new OitPipelineSet(depthBoundsBuilder.build(), transmittanceBuilder.build(), accumulateBuilder.build());
      }
   }
}
