package com.mojang.renderpearl.api.pipeline;

import com.mojang.renderpearl.api.GpuFormat;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import it.unimi.dsi.fastutil.bytes.ByteImmutableList;
import it.unimi.dsi.fastutil.bytes.ByteList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

public class RenderPassLayout {
   public static final int MAXIMUM_ATTACHMENTS = 64;
   public static final int MAXIMUM_SUBPASSES = 64;
   public static final int MAX_COLOR_ATTACHMENTS = 4;
   public static final int MAX_INPUT_ATTACHMENTS = 4;
   public static final int ATTACHMENT_UNUSED = -1;
   private final String name;
   private final List<GpuFormat> attachmentFormats;
   private final List<Subpass> subpasses;
   private final ByteList compatibilityKey;
   private final int compatabilityHash;

   public static Builder builder(final String name) {
      return new Builder(name);
   }

   public static RenderPassLayout simple(final String name, final GpuFormat format, final int count) {
      GpuFormat[] formatArray = new GpuFormat[count];
      Arrays.fill(formatArray, format);
      return simple(name, formatArray);
   }

   public static RenderPassLayout simple(final String name, final GpuFormat format, final int count, final GpuFormat depthStencil) {
      GpuFormat[] formatArray = new GpuFormat[count + 1];
      Arrays.fill(formatArray, format);
      formatArray[count] = depthStencil;
      return simple(name, formatArray);
   }

   public static RenderPassLayout simple(final String name, final GpuFormat firstAttachmentFormat, final GpuFormat... additionalAttachmentFormats) {
      GpuFormat[] formatArray = new GpuFormat[additionalAttachmentFormats.length + 1];
      formatArray[0] = firstAttachmentFormat;
      System.arraycopy(additionalAttachmentFormats, 0, formatArray, 1, additionalAttachmentFormats.length);
      return simple(name, formatArray);
   }

   private static RenderPassLayout simple(final String name, final GpuFormat[] formats) {
      if (formats.length < 1) {
         throw new IllegalArgumentException("At least one attachment required");
      } else {
         boolean hasDepthAttachment = false;

         for(GpuFormat attachmentFormat : formats) {
            if (attachmentFormat.hasDepthAspect() || attachmentFormat.hasStencilAspect()) {
               if (hasDepthAttachment) {
                  throw new IllegalArgumentException("Only one depth/stencil attachment may be specified for a simple RenderPass");
               }

               hasDepthAttachment = true;
            }
         }

         Builder builder = builder(name);
         Builder.Subpass subpass = builder.withSubpass(name);

         for(GpuFormat attachmentFormat : formats) {
            Builder.Attachment attachment = builder.withAttachment(attachmentFormat);
            if (attachmentFormat.hasColorAspect()) {
               subpass.withColorAttachment(attachment);
            } else {
               subpass.withDepthAttachment(attachment);
            }
         }

         return builder.build();
      }
   }

   private RenderPassLayout(final String name, final List<Builder.Attachment> attachments, final List<Builder.Subpass> builderPasses) {
      super();
      this.name = name;
      this.attachmentFormats = attachments.stream().map(Builder.Attachment::format).toList();
      List<Subpass> subpasses = new ReferenceArrayList();

      for(int index = 0; index < builderPasses.size(); ++index) {
         Builder.Subpass subpass = (Builder.Subpass)builderPasses.get(index);
         ByteArrayList inputAttachments = new ByteArrayList();
         ObjectListIterator var8 = subpass.inputAttachments.iterator();

         while(var8.hasNext()) {
            Builder.Attachment attachment = (Builder.Attachment)var8.next();
            if (attachment == null) {
               inputAttachments.add((byte)-1);
            } else {
               inputAttachments.add((byte)attachments.indexOf(attachment));
            }
         }

         ByteArrayList colorAttachments = new ByteArrayList();
         ObjectListIterator depthAttachmentIndex = subpass.colorAttachments.iterator();

         while(depthAttachmentIndex.hasNext()) {
            Builder.Attachment attachment = (Builder.Attachment)depthAttachmentIndex.next();
            if (attachment == null) {
               colorAttachments.add((byte)-1);
            } else {
               colorAttachments.add((byte)attachments.indexOf(attachment));
            }
         }

         int depthAttachmentIndex = subpass.depthAttachment != null ? attachments.indexOf(subpass.depthAttachment) : -1;
         subpasses.add(new Subpass(subpass.name, this, index, new ByteImmutableList(inputAttachments), new ByteImmutableList(colorAttachments), depthAttachmentIndex));
      }

      this.subpasses = List.copyOf(subpasses);
      ByteArrayList compatKey = new ByteArrayList();
      compatKey.add((byte)this.attachmentFormats.size());
      compatKey.add((byte)subpasses.size());

      assert GpuFormat.values().length < 256;

      for(GpuFormat attachmentFormat : this.attachmentFormats) {
         int ordinal = attachmentFormat.ordinal();
         compatKey.add((byte)ordinal);
         compatKey.add((byte)(ordinal >> 8));
      }

      for(Subpass subpass : subpasses) {
         subpass.addToCompatKey(compatKey);
      }

      this.compatibilityKey = new ByteImmutableList(compatKey);
      this.compatabilityHash = this.compatibilityKey.hashCode();
   }

   public String name() {
      return this.name;
   }

   public List<GpuFormat> attachmentFormats() {
      return this.attachmentFormats;
   }

   public List<Subpass> subpasses() {
      return this.subpasses;
   }

   public Subpass subpass(final int index) {
      if (index >= 0 && index <= this.subpasses.size()) {
         return (Subpass)this.subpasses.get(index);
      } else {
         throw new IllegalArgumentException("Subpass index out of range");
      }
   }

   public int hashCode() {
      return this.compatabilityHash;
   }

   public boolean equals(final Object obj) {
      if (this == obj) {
         return true;
      } else if (obj instanceof RenderPassLayout) {
         RenderPassLayout other = (RenderPassLayout)obj;
         return this.compatabilityHash != other.compatabilityHash ? false : this.compatibilityKey.equals(other.compatibilityKey);
      } else {
         return false;
      }
   }

   public static class Subpass {
      private final String name;
      private final RenderPassLayout renderPassLayout;
      private final int index;
      private final ByteList inputAttachments;
      private final ByteList colorAttachments;
      private final int depthStencilAttachment;

      private Subpass(final String name, final RenderPassLayout renderPassLayout, final int index, final ByteList inputAttachments, final ByteList colorAttachments, final int depthStencilAttachment) {
         super();
         this.name = name;
         this.renderPassLayout = renderPassLayout;
         this.index = index;
         this.inputAttachments = inputAttachments;
         this.colorAttachments = colorAttachments;
         this.depthStencilAttachment = depthStencilAttachment;
      }

      private void addToCompatKey(final ByteArrayList compatKey) {
         compatKey.add((byte)this.inputAttachments.size());
         compatKey.add((byte)this.colorAttachments.size());
         compatKey.addAll(this.inputAttachments);
         compatKey.addAll(this.colorAttachments);
         compatKey.add((byte)this.depthStencilAttachment);
      }

      public String name() {
         return this.name;
      }

      public RenderPassLayout renderPassLayout() {
         return this.renderPassLayout;
      }

      public int index() {
         return this.index;
      }

      public ByteList inputAttachments() {
         return this.inputAttachments;
      }

      public ByteList colorAttachments() {
         return this.colorAttachments;
      }

      public int depthStencilAttachment() {
         return this.depthStencilAttachment;
      }

      public int hashCode() {
         return this.renderPassLayout.hashCode() ^ this.index;
      }

      public boolean equals(final Object obj) {
         if (this == obj) {
            return true;
         } else if (obj instanceof Subpass) {
            Subpass other = (Subpass)obj;
            return other.index != this.index ? false : this.renderPassLayout.equals(other.renderPassLayout);
         } else {
            return false;
         }
      }
   }

   public static class Builder {
      private final String name;
      private final List<Attachment> attachments = new ReferenceArrayList();
      private final List<Subpass> subpasses = new ReferenceArrayList();

      public Builder(final String name) {
         super();
         this.name = name;
      }

      public RenderPassLayout build() {
         return new RenderPassLayout(this.name, this.attachments, this.subpasses);
      }

      public Attachment withAttachment(final GpuFormat format) {
         if (this.attachments.size() >= 64) {
            throw new IllegalStateException("Cannot add another attachment, maximum reached");
         } else {
            Attachment attachment = new Attachment(format);
            this.attachments.add(attachment);
            return attachment;
         }
      }

      public List<Attachment> withAttachments(final GpuFormat format, final int count) {
         List<Attachment> attachments = new ReferenceArrayList();

         for(int i = 0; i < count; ++i) {
            attachments.add(this.withAttachment(format));
         }

         return attachments;
      }

      public Subpass withSubpass(final String name) {
         if (this.subpasses.size() >= 64) {
            throw new IllegalStateException("Cannot add another subpass, maximum reached");
         } else {
            Subpass subpass = new Subpass(name, this.subpasses.size());
            this.subpasses.add(subpass);
            return subpass;
         }
      }

      public static record Attachment(GpuFormat format) {
         public Attachment {
            super();
         }
      }

      public class Subpass {
         private final String name;
         private final int index;
         private final ReferenceArrayList<@Nullable Attachment> inputAttachments;
         private final ReferenceArrayList<@Nullable Attachment> colorAttachments;
         private @Nullable Attachment depthAttachment;

         public Subpass(final String name, final int index) {
            Objects.requireNonNull(Builder.this);
            super();
            this.inputAttachments = new ReferenceArrayList();
            this.colorAttachments = new ReferenceArrayList();
            this.name = name;
            this.index = index;
         }

         public int index() {
            return this.index;
         }

         public Subpass withInputAttachments(final List<@Nullable Attachment> attachments) {
            attachments.forEach(this::withInputAttachment);
            return this;
         }

         public Subpass withInputAttachment(final @Nullable Attachment attachment) {
            this.withInputAttachment(attachment, this.inputAttachments.size());
            return this;
         }

         public Subpass withInputAttachment(final @Nullable Attachment attachment, final int index) {
            if (index > 4) {
               throw new IllegalArgumentException("input attachment index " + index + " out of range, maximum 4");
            } else {
               if (this.inputAttachments.size() <= index) {
                  this.inputAttachments.size(index + 1);
               }

               if (this.inputAttachments.get(index) == attachment) {
                  return this;
               } else {
                  if (attachment != null) {
                     if (!Builder.this.attachments.contains(attachment)) {
                        throw new IllegalArgumentException("Subpass attachment must be from parent RenderPass");
                     }

                     if (this.hasAttachment(attachment)) {
                        throw new IllegalStateException("Attachment may only be used once within a subpass");
                     }
                  }

                  this.inputAttachments.set(index, attachment);
                  return this;
               }
            }
         }

         public Subpass withColorAttachments(final List<@Nullable Attachment> attachments) {
            attachments.forEach(this::withColorAttachment);
            return this;
         }

         public Subpass withColorAttachment(final @Nullable Attachment attachment) {
            this.withColorAttachment(attachment, this.colorAttachments.size());
            return this;
         }

         public Subpass withColorAttachment(final @Nullable Attachment attachment, final int index) {
            if (index > 4) {
               throw new IllegalArgumentException("Color attachment index " + index + " out of range, maximum 4");
            } else {
               if (this.colorAttachments.size() <= index) {
                  this.colorAttachments.size(index + 1);
               }

               if (this.colorAttachments.get(index) == attachment) {
                  return this;
               } else {
                  if (attachment != null) {
                     if (!Builder.this.attachments.contains(attachment)) {
                        throw new IllegalArgumentException("Subpass attachment must be from parent RenderPass");
                     }

                     if (!attachment.format().hasColorAspect()) {
                        throw new IllegalArgumentException("Color attachment format must have color aspect");
                     }

                     if (this.hasAttachment(attachment)) {
                        throw new IllegalStateException("Attachment may only be used once within a subpass");
                     }
                  }

                  this.colorAttachments.set(index, attachment);
                  return this;
               }
            }
         }

         public Subpass withDepthAttachment(final @Nullable Attachment attachment) {
            if (this.depthAttachment == attachment) {
               return this;
            } else {
               if (attachment != null) {
                  if (!Builder.this.attachments.contains(attachment)) {
                     throw new IllegalArgumentException("Subpass attachment must be from parent RenderPass");
                  }

                  if (!attachment.format().hasDepthAspect() && !attachment.format.hasStencilAspect()) {
                     throw new IllegalArgumentException("Depth attachment format must have at depth or stencil aspect");
                  }

                  if (this.hasAttachment(attachment)) {
                     throw new IllegalStateException("Attachment may only be used once within a subpass");
                  }
               }

               this.depthAttachment = attachment;
               return this;
            }
         }

         private boolean hasAttachment(final Attachment attachment) {
            if (this.depthAttachment == attachment) {
               return true;
            } else {
               return this.colorAttachments.contains(attachment) ? true : this.inputAttachments.contains(attachment);
            }
         }
      }
   }
}
