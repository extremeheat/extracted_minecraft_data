package com.mojang.renderpearl.api.commands;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.pipeline.RenderPassLayout;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;

public record RenderPassDescriptor(Supplier<String> label, RenderPassLayout layout, List<Attachment> attachments, RenderPass.RenderArea renderArea) {
   public RenderPassDescriptor(Supplier<String> label, RenderPassLayout layout, List<Attachment> attachments, RenderPass.RenderArea renderArea) {
      super();
      attachments = List.copyOf(attachments);
      this.label = label;
      this.layout = layout;
      this.attachments = attachments;
      this.renderArea = renderArea;
   }

   public static Builder builder(final RenderPassLayout layout) {
      Objects.requireNonNull(layout);
      return new Builder(layout::name, layout);
   }

   public static Builder builder(final Supplier<String> label, final RenderPassLayout layout) {
      return new Builder(label, layout);
   }

   public static record Attachment(GpuTextureView textureView, boolean load, boolean store, @Nullable ClearValue clearValue) {
      public Attachment {
         super();
      }

      public static record ClearValue(Vector4fc color, float depth, byte stencil) {
         public ClearValue(Vector4fc color, float depth, byte stencil) {
            super();
            Vector4fc var4 = new Vector4f(color);
            this.color = var4;
            this.depth = depth;
            this.stencil = stencil;
         }
      }
   }

   public static class Builder {
      private final Supplier<String> label;
      private final RenderPassLayout layout;
      private final List<Attachment> attachments = new ReferenceArrayList();

      public Builder(final Supplier<String> label, final RenderPassLayout layout) {
         super();
         this.label = label;
         this.layout = layout;
      }

      public RenderPassDescriptor build(final RenderPass.RenderArea renderArea) {
         if (this.attachments.size() != this.layout.attachmentFormats().size()) {
            int var10002 = this.attachments.size();
            throw new IllegalStateException("Attachment count (" + var10002 + ") does not match expectation from layout (" + this.layout.attachmentFormats().size() + ")");
         } else {
            return new RenderPassDescriptor(this.label, this.layout, this.attachments, renderArea);
         }
      }

      public RenderPassDescriptor build() {
         return this.build(new RenderPass.RenderArea(((Attachment)this.attachments.getFirst()).textureView()));
      }

      public void withAttachment(final Attachment attachment) {
         int attachmentIndex = this.attachments.size();
         if (attachmentIndex >= this.layout.attachmentFormats().size()) {
            throw new IllegalStateException("Cannot add another attachment, already reached expected number from layout (" + this.layout.attachmentFormats().size() + ")");
         } else {
            GpuFormat expectedFormat = (GpuFormat)this.layout.attachmentFormats().get(attachmentIndex);
            if (attachment.textureView.texture().getFormat() != expectedFormat) {
               String var10002 = String.valueOf(attachment.textureView.texture().getFormat());
               throw new IllegalArgumentException("Attachment format " + var10002 + " does not match expected format " + String.valueOf(expectedFormat));
            } else {
               if (attachmentIndex != 0) {
                  GpuTextureView firstView = ((Attachment)this.attachments.getFirst()).textureView();
                  GpuTextureView currentView = attachment.textureView();
                  if (firstView.getWidth(0) != currentView.getWidth(0) || firstView.getHeight(0) != currentView.getHeight(0)) {
                     throw new IllegalArgumentException("All texture views must be the same size to start a RenderPass");
                  }
               }

               this.attachments.add(attachment);
            }
         }
      }

      public GpuTextureView withAttachment(final GpuTextureView textureView) {
         this.withAttachment(new Attachment(textureView, true, true, (Attachment.ClearValue)null));
         return textureView;
      }

      public GpuTextureView withAttachment(final GpuTextureView textureView, final Attachment.ClearValue clearValue) {
         this.withAttachment(new Attachment(textureView, true, true, clearValue));
         return textureView;
      }

      public GpuTextureView withAttachment(final GpuTextureView textureView, final Vector4fc clearValue) {
         if (!textureView.texture().getFormat().hasColorAspect()) {
            throw new IllegalArgumentException("Cannot use color clear value on non-color texture");
         } else {
            this.withAttachment(new Attachment(textureView, true, true, new Attachment.ClearValue(clearValue, 0.0F, (byte)0)));
            return textureView;
         }
      }

      public GpuTextureView withAttachment(final GpuTextureView textureView, final float clearValue) {
         if (textureView.texture().getFormat().hasColorAspect()) {
            throw new IllegalArgumentException("Cannot clear a color texture with a depth value");
         } else {
            this.withAttachment(new Attachment(textureView, true, true, new Attachment.ClearValue(new Vector4f(), clearValue, (byte)0)));
            return textureView;
         }
      }

      public GpuTextureView withAttachment(final GpuTextureView textureView, final float clearValueDepth, final byte clearValueStencil) {
         if (textureView.texture().getFormat().hasColorAspect()) {
            throw new IllegalArgumentException("Cannot clear a color texture with a depth/stencil value");
         } else {
            this.withAttachment(new Attachment(textureView, true, true, new Attachment.ClearValue(new Vector4f(), clearValueDepth, clearValueStencil)));
            return textureView;
         }
      }

      public GpuTextureView withAttachmentDontStore(final GpuTextureView textureView, final Attachment.ClearValue clearValue) {
         this.withAttachment(new Attachment(textureView, true, false, clearValue));
         return textureView;
      }

      public GpuTextureView withAttachmentDontStore(final GpuTextureView textureView, final Vector4fc clearValue) {
         if (!textureView.texture().getFormat().hasColorAspect()) {
            throw new IllegalArgumentException("Cannot use color clear value on non-color texture");
         } else {
            this.withAttachment(new Attachment(textureView, true, false, new Attachment.ClearValue(clearValue, 0.0F, (byte)0)));
            return textureView;
         }
      }

      public GpuTextureView withAttachmentDontStore(final GpuTextureView textureView, final float clearValue) {
         if (textureView.texture().getFormat().hasColorAspect()) {
            throw new IllegalArgumentException("Cannot clear a color texture with a depth/stencil value");
         } else {
            this.withAttachment(new Attachment(textureView, true, false, new Attachment.ClearValue(new Vector4f(), clearValue, (byte)0)));
            return textureView;
         }
      }

      public GpuTextureView withAttachmentDontStore(final GpuTextureView textureView, final float clearValueDepth, final byte clearValueStencil) {
         if (textureView.texture().getFormat().hasColorAspect()) {
            throw new IllegalArgumentException("Cannot clear a color texture with a depth/stencil value");
         } else {
            this.withAttachment(new Attachment(textureView, true, false, new Attachment.ClearValue(new Vector4f(), clearValueDepth, clearValueStencil)));
            return textureView;
         }
      }

      public GpuTextureView withAttachmentDontLoad(final GpuTextureView textureView) {
         this.withAttachment(new Attachment(textureView, false, true, (Attachment.ClearValue)null));
         return textureView;
      }

      public GpuTextureView withAttachmentDontLoadDontStore(final GpuTextureView textureView) {
         this.withAttachment(new Attachment(textureView, false, false, (Attachment.ClearValue)null));
         return textureView;
      }
   }
}
