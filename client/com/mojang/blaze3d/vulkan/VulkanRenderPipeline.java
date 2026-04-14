package com.mojang.blaze3d.vulkan;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.function.Supplier;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRenderingCreateInfoKHR;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

public record VulkanRenderPipeline(RenderPipeline info, VulkanDevice device, long withDepthPipeline, long withoutDepthPipeline, long pipelineLayout, VulkanBindGroupLayout layout, long vertexModule, long fragmentModule) implements CompiledRenderPipeline, Destroyable {
   public static final long INVALID_PIPELINE = 0L;

   public VulkanRenderPipeline {
      super();
   }

   public boolean isValid() {
      return this.withDepthPipeline != 0L;
   }

   public static VulkanRenderPipeline compile(final VulkanDevice device, final VulkanBindGroupLayout layout, final RenderPipeline pipeline, final long vertexModule, final long fragmentModule) {
      MemoryStack stack = MemoryStack.stackPush();

      long pipelineLayout;
      try {
         VkPipelineLayoutCreateInfo createInfo = VkPipelineLayoutCreateInfo.calloc(stack).sType$Default().pSetLayouts(stack.longs(layout.handle()));
         LongBuffer pointer = stack.callocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkCreatePipelineLayout(device.vkDevice(), createInfo, (VkAllocationCallbacks)null, pointer), "Can't create pipeline for " + String.valueOf(pipeline.getLocation()));
         pipelineLayout = pointer.get(0);
         device.instance().debug().setObjectName(device.vkDevice(), 17, pipelineLayout, (Supplier)(() -> "Pipeline layout for " + String.valueOf(pipeline.getLocation())));
      } catch (Throwable var37) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var36) {
               var37.addSuppressed(var36);
            }
         }

         throw var37;
      }

      if (stack != null) {
         stack.close();
      }

      stack = MemoryStack.stackPush();

      VulkanRenderPipeline var34;
      try {
         VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(2, stack);
         ByteBuffer nameMain = stack.UTF8("main");
         VkPipelineShaderStageCreateInfo vertexStage = VkPipelineShaderStageCreateInfo.calloc(stack).sType$Default().stage(1).module(vertexModule).pName(nameMain);
         VkPipelineShaderStageCreateInfo fragmentStage = VkPipelineShaderStageCreateInfo.calloc(stack).sType$Default().stage(16).module(fragmentModule).pName(nameMain);
         ((VkPipelineShaderStageCreateInfo.Buffer)((VkPipelineShaderStageCreateInfo.Buffer)shaderStages.put(vertexStage)).put(fragmentStage)).flip();
         VertexFormat vertexFormat = pipeline.getVertexFormat();
         VkVertexInputAttributeDescription.Buffer vertexAttributeDescriptions = VkVertexInputAttributeDescription.calloc(vertexFormat.getElements().size(), stack);
         VkVertexInputBindingDescription.Buffer vertexBindingDescriptions = VkVertexInputBindingDescription.calloc(vertexFormat.getElements().isEmpty() ? 0 : 1, stack);
         if (!vertexFormat.getElements().isEmpty()) {
            VkVertexInputBindingDescription bindingDescription = VkVertexInputBindingDescription.calloc(stack).binding(0).stride(vertexFormat.getVertexSize()).inputRate(0);
            vertexBindingDescriptions.put(bindingDescription);
         }

         List<VertexFormatElement> elements = vertexFormat.getElements();

         for(int i = 0; i < elements.size(); ++i) {
            VertexFormatElement element = (VertexFormatElement)elements.get(i);
            VkVertexInputAttributeDescription attributeDescription = VkVertexInputAttributeDescription.calloc(stack).location(i).binding(0).offset(vertexFormat.getOffset(element)).format(VulkanConst.toVk(element.format()));
            vertexAttributeDescriptions.put(attributeDescription);
         }

         vertexAttributeDescriptions.flip();
         vertexBindingDescriptions.flip();
         VkPipelineVertexInputStateCreateInfo vertexInputState = VkPipelineVertexInputStateCreateInfo.calloc(stack).sType$Default().pVertexAttributeDescriptions(vertexAttributeDescriptions).pVertexBindingDescriptions(vertexBindingDescriptions);
         VkPipelineInputAssemblyStateCreateInfo inputAssemblyState = VkPipelineInputAssemblyStateCreateInfo.calloc(stack).sType$Default().topology(VulkanConst.toVk(pipeline.getVertexFormatMode()));
         VkPipelineRasterizationStateCreateInfo rasterizationState = VkPipelineRasterizationStateCreateInfo.calloc(stack).sType$Default().polygonMode(VulkanConst.toVk(pipeline.getPolygonMode())).cullMode(pipeline.isCull() ? 2 : 0).frontFace(1).lineWidth(1.0F);
         VkPipelineDepthStencilStateCreateInfo depthStencilState = VkPipelineDepthStencilStateCreateInfo.calloc(stack).sType$Default();
         if (pipeline.getDepthStencilState() != null) {
            rasterizationState.depthBiasEnable(pipeline.getDepthStencilState().depthBiasConstant() != 0.0F && pipeline.getDepthStencilState().depthBiasScaleFactor() != 0.0F);
            rasterizationState.depthBiasConstantFactor(pipeline.getDepthStencilState().depthBiasConstant());
            rasterizationState.depthBiasSlopeFactor(pipeline.getDepthStencilState().depthBiasScaleFactor());
            depthStencilState.depthTestEnable(true);
            depthStencilState.depthWriteEnable(pipeline.getDepthStencilState().writeDepth());
            depthStencilState.depthCompareOp(VulkanConst.toVk(pipeline.getDepthStencilState().depthTest()));
         }

         VkPipelineColorBlendAttachmentState.Buffer blendAttachments = VkPipelineColorBlendAttachmentState.calloc(1, stack).colorWriteMask(VulkanConst.toVk(pipeline.getColorTargetState()));
         if (pipeline.getColorTargetState().blendFunction().isPresent()) {
            applyBlendInformation(blendAttachments, (BlendFunction)pipeline.getColorTargetState().blendFunction().get());
         }

         VkPipelineColorBlendStateCreateInfo colorBlendState = VkPipelineColorBlendStateCreateInfo.calloc(stack).sType$Default().pAttachments(blendAttachments);
         VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc(stack).sType$Default().scissorCount(1).viewportCount(1);
         VkPipelineMultisampleStateCreateInfo multisampleState = VkPipelineMultisampleStateCreateInfo.calloc(stack).sType$Default().rasterizationSamples(1).sampleShadingEnable(false);
         VkPipelineDynamicStateCreateInfo dynamicStateInfo = VkPipelineDynamicStateCreateInfo.calloc(stack).sType$Default().pDynamicStates(stack.ints(1, 0));
         VkPipelineRenderingCreateInfoKHR renderingInfo = VkPipelineRenderingCreateInfoKHR.calloc(stack).sType$Default();
         renderingInfo.pColorAttachmentFormats(stack.ints(37));
         renderingInfo.depthAttachmentFormat(126);
         VkGraphicsPipelineCreateInfo.Buffer createInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack).sType$Default().flags(0).pStages(shaderStages).pVertexInputState(vertexInputState).pInputAssemblyState(inputAssemblyState).pRasterizationState(rasterizationState).pDepthStencilState(depthStencilState).pColorBlendState(colorBlendState).pViewportState(viewportState).pMultisampleState(multisampleState).pDynamicState(dynamicStateInfo).layout(pipelineLayout).pNext(renderingInfo);
         LongBuffer pointer = stack.callocLong(1);
         VulkanUtils.crashIfFailure(VK12.vkCreateGraphicsPipelines(device.vkDevice(), 0L, createInfo, (VkAllocationCallbacks)null, pointer), "Can't compile pipeline " + String.valueOf(pipeline.getLocation()));
         long withDepthPipeline = pointer.get(0);
         device.instance().debug().setObjectName(device.vkDevice(), 19, withDepthPipeline, (Supplier)(() -> "Pipeline " + String.valueOf(pipeline.getLocation())));
         long withoutDepthPipeline;
         if (pipeline.getDepthStencilState() == null) {
            renderingInfo.depthAttachmentFormat(0);
            VulkanUtils.crashIfFailure(VK12.vkCreateGraphicsPipelines(device.vkDevice(), 0L, createInfo, (VkAllocationCallbacks)null, pointer), "Can't compile pipeline " + String.valueOf(pipeline.getLocation()));
            withoutDepthPipeline = pointer.get(0);
            device.instance().debug().setObjectName(device.vkDevice(), 19, withoutDepthPipeline, (Supplier)(() -> "Pipeline " + String.valueOf(pipeline.getLocation())));
         } else {
            withoutDepthPipeline = 0L;
         }

         var34 = new VulkanRenderPipeline(pipeline, device, withDepthPipeline, withoutDepthPipeline, pipelineLayout, layout, vertexModule, fragmentModule);
      } catch (Throwable var38) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var35) {
               var38.addSuppressed(var35);
            }
         }

         throw var38;
      }

      if (stack != null) {
         stack.close();
      }

      return var34;
   }

   public void destroy() {
      if (this.withDepthPipeline != 0L) {
         VK12.vkDestroyPipeline(this.device.vkDevice(), this.withoutDepthPipeline, (VkAllocationCallbacks)null);
         VK12.vkDestroyPipeline(this.device.vkDevice(), this.withDepthPipeline, (VkAllocationCallbacks)null);
         VK12.vkDestroyPipelineLayout(this.device.vkDevice(), this.pipelineLayout, (VkAllocationCallbacks)null);
         VK12.vkDestroyDescriptorSetLayout(this.device.vkDevice(), this.layout.handle(), (VkAllocationCallbacks)null);
         VK12.vkDestroyShaderModule(this.device.vkDevice(), this.vertexModule, (VkAllocationCallbacks)null);
         VK12.vkDestroyShaderModule(this.device.vkDevice(), this.fragmentModule, (VkAllocationCallbacks)null);
      }
   }

   private static void applyBlendInformation(final VkPipelineColorBlendAttachmentState.Buffer blendAttachments, final BlendFunction blendFunction) {
      blendAttachments.blendEnable(true).colorBlendOp(VulkanConst.toVk(blendFunction.color().op())).alphaBlendOp(VulkanConst.toVk(blendFunction.alpha().op())).dstAlphaBlendFactor(VulkanConst.toVk(blendFunction.alpha().destFactor())).dstColorBlendFactor(VulkanConst.toVk(blendFunction.color().destFactor())).srcAlphaBlendFactor(VulkanConst.toVk(blendFunction.alpha().sourceFactor())).srcColorBlendFactor(VulkanConst.toVk(blendFunction.color().sourceFactor()));
   }
}
