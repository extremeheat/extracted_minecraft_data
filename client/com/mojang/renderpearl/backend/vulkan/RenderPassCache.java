package com.mojang.renderpearl.backend.vulkan;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.LongBuffer;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import org.joml.Vector4fc;
import org.jspecify.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkAttachmentDescription2;
import org.lwjgl.vulkan.VkAttachmentReference2;
import org.lwjgl.vulkan.VkRenderPassCreateInfo2;
import org.lwjgl.vulkan.VkSubpassDescription2;

public class RenderPassCache implements Destroyable {
   private final VulkanDevice device;
   private final Object2LongOpenHashMap<RenderPassDescription> renderpasses = new Object2LongOpenHashMap();

   public RenderPassCache(final VulkanDevice device) {
      super();
      this.device = device;
   }

   public void destroy() {
      this.renderpasses.values().forEach((renderpass) -> VK12.vkDestroyRenderPass(this.device.vkDevice(), renderpass, (VkAllocationCallbacks)null));
   }

   private long createRenderPass(final RenderPassDescription description) {
      MemoryStack stack = MemoryStack.stackPush();

      long var8;
      try {
         VkAttachmentDescription2.Buffer attachments = VkAttachmentDescription2.calloc(description.colorAttachments.size() + 1, stack);

         for(int i = 0; i < description.colorAttachments.size(); ++i) {
            VkAttachmentDescription2 attachment = ((VkAttachmentDescription2)attachments.get()).sType$Default();
            attachment.format(((AttachmentDescription)description.colorAttachments.get(i)).format);
            attachment.samples(1);
            attachment.loadOp(((AttachmentDescription)description.colorAttachments.get(i)).loadOp);
            attachment.storeOp(0);
            attachment.initialLayout(1);
            attachment.finalLayout(1);
         }

         if (description.depthAttachment != null) {
            VkAttachmentDescription2 attachment = ((VkAttachmentDescription2)attachments.get()).sType$Default();
            attachment.format(description.depthAttachment.format);
            attachment.samples(1);
            attachment.loadOp(description.depthAttachment.loadOp);
            attachment.storeOp(0);
            attachment.initialLayout(1);
            attachment.finalLayout(1);
         }

         attachments.flip();
         VkSubpassDescription2.Buffer subpass = VkSubpassDescription2.calloc(1, stack).sType$Default();
         subpass.pipelineBindPoint(0);
         subpass.viewMask(0);
         subpass.pInputAttachments((VkAttachmentReference2.Buffer)null);
         VkAttachmentReference2.Buffer colorReferences = VkAttachmentReference2.calloc(description.colorAttachments.size(), stack);

         for(int i = 0; i < description.colorAttachments.size(); ++i) {
            VkAttachmentReference2 reference = ((VkAttachmentReference2)colorReferences.get()).sType$Default();
            reference.attachment(i);
            reference.layout(1);
            reference.aspectMask(1);
         }

         subpass.pColorAttachments((VkAttachmentReference2.Buffer)colorReferences.flip());
         subpass.colorAttachmentCount(colorReferences.limit());
         if (description.depthAttachment != null) {
            VkAttachmentReference2 depthReference = VkAttachmentReference2.calloc(stack).sType$Default();
            depthReference.attachment(description.colorAttachments.size());
            depthReference.layout(1);
            depthReference.aspectMask(2);
            subpass.pDepthStencilAttachment(depthReference);
         }

         VkRenderPassCreateInfo2 renderPassCreateInfo = VkRenderPassCreateInfo2.calloc(stack).sType$Default();
         renderPassCreateInfo.pAttachments(attachments);
         renderPassCreateInfo.pSubpasses(subpass);
         LongBuffer renderPassReturn = stack.callocLong(1);
         VulkanUtils.crashIfFailure(this.device, VK12.vkCreateRenderPass2(this.device.vkDevice(), renderPassCreateInfo, (VkAllocationCallbacks)null, renderPassReturn), "Failed to create VkRenderPass");
         var8 = renderPassReturn.get(0);
      } catch (Throwable var11) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var10) {
               var11.addSuppressed(var10);
            }
         }

         throw var11;
      }

      if (stack != null) {
         stack.close();
      }

      return var8;
   }

   private synchronized long getRenderPass(final RenderPassDescription description) {
      return this.renderpasses.computeIfAbsent(description, this::createRenderPass);
   }

   public long getRenderPass(final int depthFormat, final List<@Nullable ColorTargetState> colorFormats) {
      ReferenceArrayList<AttachmentDescription> colorAttachments = new ReferenceArrayList();

      for(ColorTargetState colorTargetState : colorFormats) {
         if (colorTargetState != null) {
            colorAttachments.add(new AttachmentDescription(VulkanConst.toVk(colorTargetState.format()), 0));
         } else {
            colorAttachments.add(new AttachmentDescription(0, 2));
         }
      }

      AttachmentDescription depthAttachment = depthFormat == 0 ? null : new AttachmentDescription(depthFormat, 0);
      return this.getRenderPass(new RenderPassDescription(colorAttachments, depthAttachment));
   }

   public long getRenderPass(final RenderPassDescriptor renderPassDescriptor) {
      ReferenceArrayList<AttachmentDescription> colorAttachments = new ReferenceArrayList();

      for(RenderPassDescriptor.Attachment<Optional<Vector4fc>> colorAttachment : renderPassDescriptor.colorAttachments()) {
         if (colorAttachment == null) {
            colorAttachments.add(new AttachmentDescription(0, 2));
         } else {
            GpuFormat colorFormat = colorAttachment.textureView().texture().getFormat();
            int depthLoadOp = ((Optional)colorAttachment.clearValue()).isPresent() ? 1 : 0;
            colorAttachments.add(new AttachmentDescription(VulkanConst.toVk(colorFormat), depthLoadOp));
         }
      }

      AttachmentDescription depthAttachment;
      if (renderPassDescriptor.depthAttachment() != null) {
         GpuFormat depthFormat = renderPassDescriptor.depthAttachment().textureView().texture().getFormat();
         int depthLoadOp = ((OptionalDouble)renderPassDescriptor.depthAttachment().clearValue()).isPresent() ? 1 : 0;
         depthAttachment = new AttachmentDescription(VulkanConst.toVk(depthFormat), depthLoadOp);
      } else {
         depthAttachment = null;
      }

      return this.getRenderPass(new RenderPassDescription(colorAttachments, depthAttachment));
   }

   private static record AttachmentDescription(int format, int loadOp) {
      private AttachmentDescription {
         super();
      }
   }

   private static record RenderPassDescription(List<AttachmentDescription> colorAttachments, @Nullable AttachmentDescription depthAttachment) {
      private RenderPassDescription {
         super();
      }
   }
}
