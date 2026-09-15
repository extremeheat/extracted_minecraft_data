package com.mojang.renderpearl.api.commands;

import com.mojang.renderpearl.api.textures.GpuTextureView;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public record RenderPassDescriptor(Supplier<String> label, List<@Nullable Attachment<Optional<Vector4fc>>> colorAttachments, @Nullable Attachment<OptionalDouble> depthAttachment, RenderPass.RenderArea renderArea) {
   public @Nullable RenderPassDescriptor(Supplier<String> label, List<Attachment<Optional<Vector4fc>>> colorAttachments, @Nullable Attachment<OptionalDouble> depthAttachment, RenderPass.RenderArea renderArea) {
      super();
      List<Attachment<Optional<Vector4fc>>> var5 = ReferenceLists.unmodifiable(new ReferenceArrayList(colorAttachments));
      this.label = label;
      this.colorAttachments = var5;
      this.depthAttachment = depthAttachment;
      this.renderArea = renderArea;
   }

   public static Builder builder(final Supplier<String> label) {
      return new Builder(label);
   }

   public static record Attachment<T>(GpuTextureView textureView, T clearValue) {
      public Attachment {
         super();
      }
   }

   public static class Builder {
      private final Supplier<String> label;
      private final List<@Nullable Attachment<Optional<Vector4fc>>> colorAttachments = new ArrayList();
      private @Nullable Attachment<OptionalDouble> depthAttachment;
      private RenderPass.@Nullable RenderArea renderArea;

      private Builder(final Supplier<String> label) {
         super();
         this.label = label;
      }

      public Builder withColorAttachment(final GpuTextureView textureView) {
         this.colorAttachments.add(new Attachment(textureView, Optional.empty()));
         return this;
      }

      public Builder withColorAttachment(final GpuTextureView textureView, final Optional<Vector4fc> clearValue) {
         this.colorAttachments.add(new Attachment(textureView, clearValue));
         return this;
      }

      public Builder withUnusedColorAttachment() {
         this.colorAttachments.add((Object)null);
         return this;
      }

      public Builder withDepthAttachment(final GpuTextureView textureView) {
         this.depthAttachment = new Attachment<OptionalDouble>(textureView, OptionalDouble.empty());
         return this;
      }

      public Builder withDepthAttachment(final GpuTextureView textureView, final OptionalDouble clearValue) {
         this.depthAttachment = new Attachment<OptionalDouble>(textureView, clearValue);
         return this;
      }

      public Builder withRenderArea(final RenderPass.RenderArea renderArea) {
         this.renderArea = renderArea;
         return this;
      }

      public RenderPassDescriptor build() {
         RenderPass.RenderArea renderArea = this.renderArea != null ? this.renderArea : defaultRenderArea(this.colorAttachments, this.depthAttachment);
         return new RenderPassDescriptor(this.label, this.colorAttachments, this.depthAttachment, renderArea);
      }

      private static RenderPass.RenderArea defaultRenderArea(final List<@Nullable Attachment<Optional<Vector4fc>>> colorAttachments, final @Nullable Attachment<OptionalDouble> depthAttachment) {
         int width = 0;
         int height = 0;
         if (!colorAttachments.isEmpty()) {
            for(Attachment<Optional<Vector4fc>> colorAttachment : colorAttachments) {
               if (colorAttachment != null) {
                  GpuTextureView textureView = colorAttachment.textureView();
                  width = textureView.getWidth(0);
                  height = textureView.getHeight(0);
               }
            }
         } else if (depthAttachment != null) {
            width = depthAttachment.textureView().getWidth(0);
            height = depthAttachment.textureView().getHeight(0);
         }

         return new RenderPass.RenderArea(0, 0, width, height);
      }
   }
}
