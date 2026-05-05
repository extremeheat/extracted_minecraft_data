package com.mojang.blaze3d.systems;

import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public class RenderPassDescriptor {
   private final Supplier<String> label;
   public List<@Nullable Attachment<Optional<Vector4fc>>> colorAttachments = new ArrayList();
   public @Nullable Attachment<OptionalDouble> depthAttachment;
   public RenderPass.@Nullable RenderArea renderArea;

   public static RenderPassDescriptor create(final Supplier<String> label) {
      return new RenderPassDescriptor(label);
   }

   private RenderPassDescriptor(final Supplier<String> label) {
      super();
      this.label = label;
   }

   public RenderPassDescriptor withColorAttachment(final GpuTextureView textureView) {
      this.colorAttachments.add(new Attachment(textureView, Optional.empty()));
      return this;
   }

   public RenderPassDescriptor withColorAttachment(final GpuTextureView textureView, final Optional<Vector4fc> clearValue) {
      this.colorAttachments.add(new Attachment(textureView, clearValue));
      return this;
   }

   public RenderPassDescriptor withUnusedColorAttachment() {
      this.colorAttachments.add((Object)null);
      return this;
   }

   public RenderPassDescriptor withDepthAttachment(final GpuTextureView textureView) {
      this.depthAttachment = new Attachment<OptionalDouble>(textureView, OptionalDouble.empty());
      return this;
   }

   public RenderPassDescriptor withDepthAttachment(final GpuTextureView textureView, final OptionalDouble clearValue) {
      this.depthAttachment = new Attachment<OptionalDouble>(textureView, clearValue);
      return this;
   }

   public RenderPassDescriptor withRenderArea(final RenderPass.RenderArea renderArea) {
      this.renderArea = renderArea;
      return this;
   }

   public Supplier<String> label() {
      return this.label;
   }

   public List<@Nullable Attachment<Optional<Vector4fc>>> colorAttachments() {
      return this.colorAttachments;
   }

   public @Nullable Attachment<OptionalDouble> depthAttachment() {
      return this.depthAttachment;
   }

   public static record Attachment<T>(GpuTextureView textureView, T clearValue) {
      public Attachment {
         super();
      }
   }
}
