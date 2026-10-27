package com.mojang.renderpearl.backend.vulkan;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.commands.RenderPassDescriptor;
import com.mojang.renderpearl.api.pipeline.RenderPassLayout;
import it.unimi.dsi.fastutil.ints.IntIntImmutablePair;
import it.unimi.dsi.fastutil.ints.IntIntPair;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkAttachmentDescription2;
import org.lwjgl.vulkan.VkAttachmentReference2;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkRenderPassCreateInfo2;
import org.lwjgl.vulkan.VkSubpassDependency2;
import org.lwjgl.vulkan.VkSubpassDescription2;

public class RenderPassCache implements Destroyable {
   private final VulkanDevice device;
   private final Object2ReferenceOpenHashMap<RenderPassLayout, Object2LongMap<PermutationKey>> renderpasses = new Object2ReferenceOpenHashMap();

   public RenderPassCache(final VulkanDevice device) {
      super();
      this.device = device;
   }

   public void destroy() {
      this.renderpasses.values().forEach((renderpassLayout) -> renderpassLayout.values().forEach((renderpass) -> VK12.vkDestroyRenderPass(this.device.vkDevice(), renderpass, (VkAllocationCallbacks)null)));
   }

   private synchronized long getRenderPass(final RenderPassLayout layout, final PermutationKey permutation) {
      Object2LongMap<PermutationKey> permutations = (Object2LongMap)this.renderpasses.computeIfAbsent(layout, (var0) -> new Object2LongOpenHashMap());
      long cachedRenderPass = permutations.getLong(permutation);
      if (cachedRenderPass != 0L) {
         return cachedRenderPass;
      } else {
         MemoryStack stack = MemoryStack.stackPush();

         long var56;
         try {
            VkAttachmentDescription2.Buffer attachmentDescriptions = VkAttachmentDescription2.calloc(layout.attachmentFormats().size(), stack);
            int[] firstSubpassUsingAttachment = new int[layout.attachmentFormats().size()];
            Arrays.fill(firstSubpassUsingAttachment, 64);
            int[] lastSubpassUsingAttachment = new int[layout.attachmentFormats().size()];
            Arrays.fill(lastSubpassUsingAttachment, -1);

            for(int attachmentIndex = 0; attachmentIndex < layout.attachmentFormats().size(); ++attachmentIndex) {
               int loadOp = permutation.loadOp(attachmentIndex);
               int storeOp = permutation.storeOp(attachmentIndex);
               if (loadOp != 2) {
                  firstSubpassUsingAttachment[attachmentIndex] = -1;
               }

               if (storeOp != 1) {
                  lastSubpassUsingAttachment[attachmentIndex] = 64;
               }

               VkAttachmentDescription2 attachment = (VkAttachmentDescription2)attachmentDescriptions.get(attachmentIndex);
               attachment.format(VulkanConst.toVk((GpuFormat)layout.attachmentFormats().get(attachmentIndex))).sType$Default();
               attachment.samples(1);
               attachment.loadOp(loadOp);
               attachment.storeOp(storeOp);
               attachment.stencilLoadOp(loadOp);
               attachment.stencilStoreOp(storeOp);
               attachment.initialLayout(loadOp != 2 ? 1 : 0);
               attachment.finalLayout(1);
            }

            VkSubpassDescription2.Buffer subpasses = VkSubpassDescription2.calloc(layout.subpasses().size(), stack);
            long[] subpassUsedAttachments = new long[layout.subpasses().size()];

            for(int subpassIndex = 0; subpassIndex < layout.subpasses().size(); ++subpassIndex) {
               RenderPassLayout.Subpass subpass = (RenderPassLayout.Subpass)layout.subpasses().get(subpassIndex);
               VkAttachmentReference2.Buffer inputAttachmentReferences;
               if (!subpass.inputAttachments().isEmpty()) {
                  inputAttachmentReferences = VkAttachmentReference2.calloc(subpass.inputAttachments().size(), stack);

                  for(int inputAttachmentLocation = 0; inputAttachmentLocation < subpass.inputAttachments().size(); ++inputAttachmentLocation) {
                     int inputAttachmentIndex = subpass.inputAttachments().getByte(inputAttachmentLocation);
                     VkAttachmentReference2 attachmentReference = ((VkAttachmentReference2)inputAttachmentReferences.get(inputAttachmentLocation)).sType$Default();
                     attachmentReference.attachment(inputAttachmentIndex);
                     attachmentReference.layout(1);
                     if (inputAttachmentIndex != -1) {
                        attachmentReference.aspectMask(VulkanConst.formatAspectMask((GpuFormat)layout.attachmentFormats().get(inputAttachmentIndex)));
                        subpassUsedAttachments[subpassIndex] |= 1L << inputAttachmentIndex;
                        firstSubpassUsingAttachment[inputAttachmentIndex] = Math.min(firstSubpassUsingAttachment[inputAttachmentIndex], subpassIndex);
                        lastSubpassUsingAttachment[inputAttachmentIndex] = Math.max(lastSubpassUsingAttachment[inputAttachmentIndex], subpassIndex);
                     }
                  }
               } else {
                  inputAttachmentReferences = null;
               }

               VkAttachmentReference2.Buffer colorAttachmentReferences;
               if (!subpass.colorAttachments().isEmpty()) {
                  colorAttachmentReferences = VkAttachmentReference2.calloc(subpass.colorAttachments().size(), stack);

                  for(int colorAttachmentLocation = 0; colorAttachmentLocation < subpass.colorAttachments().size(); ++colorAttachmentLocation) {
                     int colorAttachmentIndex = subpass.colorAttachments().getByte(colorAttachmentLocation);
                     VkAttachmentReference2 attachmentReference = ((VkAttachmentReference2)colorAttachmentReferences.get(colorAttachmentLocation)).sType$Default();
                     attachmentReference.attachment(colorAttachmentIndex);
                     attachmentReference.layout(1);
                     if (colorAttachmentIndex != -1) {
                        attachmentReference.aspectMask(VulkanConst.formatAspectMask((GpuFormat)layout.attachmentFormats().get(colorAttachmentIndex)));
                        subpassUsedAttachments[subpassIndex] |= 1L << colorAttachmentIndex;
                        firstSubpassUsingAttachment[colorAttachmentIndex] = Math.min(firstSubpassUsingAttachment[colorAttachmentIndex], subpassIndex);
                        lastSubpassUsingAttachment[colorAttachmentIndex] = Math.max(lastSubpassUsingAttachment[colorAttachmentIndex], subpassIndex);
                     }
                  }
               } else {
                  colorAttachmentReferences = null;
               }

               VkAttachmentReference2 depthStencilAttachmentReference;
               if (subpass.depthStencilAttachment() != -1) {
                  subpassUsedAttachments[subpassIndex] |= 1L << subpass.depthStencilAttachment();
                  firstSubpassUsingAttachment[subpass.depthStencilAttachment()] = Math.min(firstSubpassUsingAttachment[subpass.depthStencilAttachment()], subpassIndex);
                  lastSubpassUsingAttachment[subpass.depthStencilAttachment()] = Math.max(lastSubpassUsingAttachment[subpass.depthStencilAttachment()], subpassIndex);
                  depthStencilAttachmentReference = VkAttachmentReference2.calloc(stack).sType$Default();
                  depthStencilAttachmentReference.attachment(subpass.depthStencilAttachment());
                  depthStencilAttachmentReference.layout(1);
                  depthStencilAttachmentReference.aspectMask(VulkanConst.formatAspectMask((GpuFormat)layout.attachmentFormats().get(subpass.depthStencilAttachment())));
               } else {
                  depthStencilAttachmentReference = null;
               }

               VkSubpassDescription2 vkSubpass = ((VkSubpassDescription2)subpasses.get(subpassIndex)).sType$Default();
               vkSubpass.pInputAttachments(inputAttachmentReferences);
               if (colorAttachmentReferences != null) {
                  vkSubpass.colorAttachmentCount(colorAttachmentReferences.limit());
                  vkSubpass.pColorAttachments(colorAttachmentReferences);
               }

               vkSubpass.pDepthStencilAttachment(depthStencilAttachmentReference);
            }

            for(int subpassIndex = 0; subpassIndex < layout.subpasses().size(); ++subpassIndex) {
               long preserveAttachmentMask = 0L;

               for(int attachmentIndex = 0; attachmentIndex < layout.attachmentFormats().size(); ++attachmentIndex) {
                  if (firstSubpassUsingAttachment[attachmentIndex] <= subpassIndex && lastSubpassUsingAttachment[attachmentIndex] >= subpassIndex && !VulkanUtils.hasBit(subpassUsedAttachments[subpassIndex], attachmentIndex)) {
                     preserveAttachmentMask |= 1L << attachmentIndex;
                  }
               }

               if (preserveAttachmentMask != 0L) {
                  VkSubpassDescription2 subpass = (VkSubpassDescription2)subpasses.get(subpassIndex);
                  IntBuffer preserveAttachments = stack.callocInt(Long.bitCount(preserveAttachmentMask));

                  for(int attachmentIndex = 0; attachmentIndex < layout.attachmentFormats().size(); ++attachmentIndex) {
                     if (VulkanUtils.hasBit(preserveAttachmentMask, attachmentIndex)) {
                        preserveAttachments.put(attachmentIndex);
                     }
                  }

                  preserveAttachments.flip();
                  subpass.pPreserveAttachments(preserveAttachments);
               }
            }

            long[] subpassDependencyMask = new long[layout.subpasses().size()];
            List<IntIntPair> dependencies = new ReferenceArrayList();

            for(int currentSubpassIndex = 1; currentSubpassIndex < layout.subpasses().size(); ++currentSubpassIndex) {
               long currentSubpassUsed = subpassUsedAttachments[currentSubpassIndex];

               for(int previousSubpassIndex = currentSubpassIndex - 1; previousSubpassIndex >= 0; --previousSubpassIndex) {
                  if (!VulkanUtils.hasBit(subpassDependencyMask[currentSubpassIndex], previousSubpassIndex)) {
                     long previousSubpassUsed = subpassUsedAttachments[previousSubpassIndex];
                     if (VulkanUtils.hasAnyBit(currentSubpassUsed, previousSubpassUsed)) {
                        subpassDependencyMask[currentSubpassIndex] |= 1L << previousSubpassIndex;
                        subpassDependencyMask[currentSubpassIndex] |= subpassDependencyMask[previousSubpassIndex];
                        dependencies.add(new IntIntImmutablePair(previousSubpassIndex, currentSubpassIndex));
                     }
                  }
               }
            }

            VkSubpassDependency2.Buffer subpassDependencies;
            if (!dependencies.isEmpty()) {
               subpassDependencies = VkSubpassDependency2.calloc(dependencies.size(), stack);

               for(int i = 0; i < dependencies.size(); ++i) {
                  VkSubpassDependency2 subpassDependency = ((VkSubpassDependency2)subpassDependencies.get(i)).sType$Default();
                  subpassDependency.srcSubpass(((IntIntPair)dependencies.get(i)).firstInt());
                  subpassDependency.dstSubpass(((IntIntPair)dependencies.get(i)).secondInt());
                  int stageMask = 1920;
                  int accessMask = 1936;
                  subpassDependency.srcStageMask(1920);
                  subpassDependency.dstStageMask(1920);
                  subpassDependency.srcAccessMask(1936);
                  subpassDependency.dstAccessMask(1936);
                  subpassDependency.dependencyFlags(1);
               }
            } else {
               subpassDependencies = null;
            }

            VkRenderPassCreateInfo2 createInfo = VkRenderPassCreateInfo2.calloc(stack).sType$Default();
            createInfo.pAttachments(attachmentDescriptions);
            createInfo.pSubpasses(subpasses);
            createInfo.pDependencies(subpassDependencies);
            LongBuffer returnPtr = stack.callocLong(1);
            VulkanUtils.crashIfFailure(this.device, VK12.vkCreateRenderPass2(this.device.vkDevice(), createInfo, (VkAllocationCallbacks)null, returnPtr), "Failed to create VkRenderPass");
            long oldRenderPass = permutations.put(permutation, returnPtr.get(0));
            if (oldRenderPass != 0L) {
               throw new IllegalStateException();
            }

            long renderPass = returnPtr.get(0);
            StringBuilder nameBuilder = new StringBuilder();
            nameBuilder.append("RenderPass(Cached) ");
            nameBuilder.append(layout.name());
            nameBuilder.append('[');

            for(int i = 0; i < layout.attachmentFormats().size(); ++i) {
               int loadOp = permutation.loadOp(i);
               int storeOp = permutation.storeOp(i);
               if (loadOp == 1) {
                  nameBuilder.append('C');
               } else if (loadOp == 0) {
                  nameBuilder.append('L');
               } else {
                  nameBuilder.append('D');
               }

               if (storeOp == 0) {
                  nameBuilder.append('S');
               } else {
                  nameBuilder.append('D');
               }

               nameBuilder.append(':');
            }

            nameBuilder.setCharAt(nameBuilder.length() - 1, ']');
            VulkanDebug var10000 = this.device.instance().debug();
            VkDevice var10001 = this.device.vkDevice();
            Objects.requireNonNull(nameBuilder);
            var10000.setObjectName(var10001, 18, renderPass, (Supplier)(nameBuilder::toString));
            var56 = renderPass;
         } catch (Throwable var26) {
            if (stack != null) {
               try {
                  stack.close();
               } catch (Throwable var25) {
                  var26.addSuppressed(var25);
               }
            }

            throw var26;
         }

         if (stack != null) {
            stack.close();
         }

         return var56;
      }
   }

   public long getRenderPass(final RenderPassLayout layout, final List<RenderPassDescriptor.Attachment> attachments) {
      long loadOpClear = 0L;
      long loadOpLoad = 0L;
      long storeOpStore = 0L;

      for(int i = 0; i < attachments.size(); ++i) {
         RenderPassDescriptor.Attachment attachment = (RenderPassDescriptor.Attachment)attachments.get(i);
         if (attachment.clearValue() != null) {
            loadOpClear |= 1L << i;
         } else if (attachment.load()) {
            loadOpLoad |= 1L << i;
         }

         if (attachment.store()) {
            storeOpStore |= 1L << i;
         }
      }

      return this.getRenderPass(layout, new PermutationKey(loadOpClear, loadOpLoad, storeOpStore));
   }

   public long getRenderPass(final RenderPassLayout layout) {
      return this.getRenderPass(layout, RenderPassCache.PermutationKey.DEFAULT);
   }

   private static record PermutationKey(long loadOpClear, long loadOpLoad, long storeOpStore) {
      private static final PermutationKey DEFAULT = new PermutationKey();

      private PermutationKey() {
         this(0L, 0L, 0L);
      }

      private PermutationKey {
         super();
      }

      private int loadOp(final int attachmentIndex) {
         if (VulkanUtils.hasBit(this.loadOpClear, attachmentIndex)) {
            assert !VulkanUtils.hasBit(this.loadOpLoad, attachmentIndex);

            return 1;
         } else {
            return VulkanUtils.hasBit(this.loadOpLoad, attachmentIndex) ? 0 : 2;
         }
      }

      private int storeOp(final int attachmentIndex) {
         return VulkanUtils.hasBit(this.storeOpStore, attachmentIndex) ? 0 : 1;
      }
   }
}
