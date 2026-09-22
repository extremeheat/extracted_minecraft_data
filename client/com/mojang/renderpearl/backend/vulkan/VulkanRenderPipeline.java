package com.mojang.renderpearl.backend.vulkan;

import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.DepthStencilState;
import com.mojang.renderpearl.api.pipeline.SpvModule;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkAllocationCallbacks;
import org.lwjgl.vulkan.VkDescriptorSetLayoutBinding;
import org.lwjgl.vulkan.VkDescriptorSetLayoutCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineLayoutCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputDivisorStateCreateInfoEXT;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkPushConstantRange;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDivisorDescriptionEXT;

public final class VulkanRenderPipeline implements BackendRenderPipeline, Destroyable {
   private final VulkanDevice device;
   private final long withDepthPipeline;
   private final long withoutDepthPipeline;
   private final long pipelineLayout;
   private final long descriptorSetLayout;
   private final LongList shaderModules;
   private final List<CompiledRenderPipeline.CreateInfo.Uniform> uniforms;
   private final int maxUniformBinding;
   private boolean closed;

   public VulkanRenderPipeline(final VulkanDevice device, final long withDepthPipeline, final long withoutDepthPipeline, final long pipelineLayout, final long descriptorSetLayout, final LongList shaderModules, final List<CompiledRenderPipeline.CreateInfo.Uniform> uniforms) {
      super();
      this.device = device;
      this.withDepthPipeline = withDepthPipeline;
      this.withoutDepthPipeline = withoutDepthPipeline;
      this.pipelineLayout = pipelineLayout;
      this.descriptorSetLayout = descriptorSetLayout;
      this.shaderModules = shaderModules;
      this.uniforms = uniforms;
      int maxBinding = 0;

      for(CompiledRenderPipeline.CreateInfo.Uniform uniform : uniforms) {
         maxBinding = Math.max(maxBinding, uniform.binding());
      }

      this.maxUniformBinding = maxBinding;
   }

   public boolean isClosed() {
      return this.closed;
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.device.createCommandEncoder().queueForDestroy(this);
      }
   }

   public static VulkanRenderPipeline compile(final VulkanDevice device, final CompiledRenderPipeline.CreateInfo pipelineCreateInfo) {
      MemoryStack stack = MemoryStack.stackPush();

      long descriptorSetLayout;
      try {
         int descriptorCount = pipelineCreateInfo.uniforms().size();
         VkDescriptorSetLayoutBinding.Buffer bindings = VkDescriptorSetLayoutBinding.calloc(descriptorCount, stack);

         for(int i = 0; i < descriptorCount; ++i) {
            VkDescriptorSetLayoutBinding binding = VkDescriptorSetLayoutBinding.calloc(stack);
            CompiledRenderPipeline.CreateInfo.Uniform uniform = (CompiledRenderPipeline.CreateInfo.Uniform)pipelineCreateInfo.uniforms().get(i);
            byte var10001;
            switch (uniform.type()) {
               case UNIFORM_BUFFER -> var10001 = 6;
               case COMBINED_IMAGE_SAMPLER -> var10001 = 1;
               case TEXEL_BUFFER -> var10001 = 4;
               default -> throw new MatchException((String)null, (Throwable)null);
            }

            binding.descriptorType(var10001);
            binding.descriptorCount(1);
            binding.binding(uniform.binding());
            binding.stageFlags(17);
            bindings.put(binding);
         }

         bindings.flip();
         VkDescriptorSetLayoutCreateInfo setCreateInfo = VkDescriptorSetLayoutCreateInfo.calloc(stack).sType$Default();
         setCreateInfo.pBindings(bindings);
         LongBuffer pointer = stack.callocLong(1);
         VulkanUtils.crashIfFailure(device, VK12.vkCreateDescriptorSetLayout(device.vkDevice(), setCreateInfo, (VkAllocationCallbacks)null, pointer), "Can't create descriptor set layout for " + pipelineCreateInfo.name());
         descriptorSetLayout = pointer.get(0);
      } catch (Throwable var39) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var35) {
               var39.addSuppressed(var35);
            }
         }

         throw var39;
      }

      if (stack != null) {
         stack.close();
      }

      MemoryStack stack = MemoryStack.stackPush();

      try {
         VkPipelineLayoutCreateInfo createInfo = VkPipelineLayoutCreateInfo.calloc(stack).sType$Default();
         if (pipelineCreateInfo.pushConstantsSize() != 0) {
            VkPushConstantRange.Buffer range = VkPushConstantRange.calloc(1, stack);
            range.stageFlags(2147483647);
            range.offset(0);
            range.size(pipelineCreateInfo.pushConstantsSize());
            createInfo.pPushConstantRanges(range);
         }

         createInfo.pSetLayouts(stack.longs(descriptorSetLayout));
         LongBuffer pointer = stack.callocLong(1);
         VulkanUtils.crashIfFailure(device, VK12.vkCreatePipelineLayout(device.vkDevice(), createInfo, (VkAllocationCallbacks)null, pointer), "Can't create pipeline for " + pipelineCreateInfo.name());
         pipelineLayout = pointer.get(0);
         device.instance().debug().setObjectName(device.vkDevice(), 17, pipelineLayout, (Supplier)(() -> "Pipeline layout for " + pipelineCreateInfo.name()));
      } catch (Throwable var38) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var34) {
               var38.addSuppressed(var34);
            }
         }

         throw var38;
      }

      if (stack != null) {
         stack.close();
      }

      LongList compiledShaderModules = new LongArrayList();
      MemoryStack stack = MemoryStack.stackPush();

      VulkanRenderPipeline var31;
      try {
         VkPipelineShaderStageCreateInfo.Buffer shaderStages = VkPipelineShaderStageCreateInfo.calloc(pipelineCreateInfo.shaders().size(), stack);

         for(SpvModule shader : pipelineCreateInfo.shaders()) {
            MemoryStack var13 = stack.push();

            long module;
            try {
               VkShaderModuleCreateInfo info = VkShaderModuleCreateInfo.calloc(stack).sType$Default().pCode(shader.spv());
               LongBuffer pointer = stack.callocLong(1);
               int var68 = VK12.vkCreateShaderModule(device.vkDevice(), info, (VkAllocationCallbacks)null, pointer);
               String var10002 = shader.name();
               VulkanUtils.crashIfFailure(device, var68, "Can't compile " + var10002 + " (" + String.valueOf(shader.type()) + ") for pipeline " + pipelineCreateInfo.name());
               VulkanDebug var10000 = device.instance().debug();
               VkDevice var69 = device.vkDevice();
               long var10003 = pointer.get(0);
               Objects.requireNonNull(pipelineCreateInfo);
               var10000.setObjectName(var69, 15, var10003, (Supplier)(pipelineCreateInfo::name));
               module = pointer.get(0);
               compiledShaderModules.add(module);
            } catch (Throwable var36) {
               if (var13 != null) {
                  try {
                     var13.close();
                  } catch (Throwable var33) {
                     var36.addSuppressed(var33);
                  }
               }

               throw var36;
            }

            if (var13 != null) {
               var13.close();
            }

            ByteBuffer entryPoint = stack.UTF8(shader.entryPoint());
            VkPipelineShaderStageCreateInfo stage = VkPipelineShaderStageCreateInfo.calloc(stack).sType$Default().stage(VulkanConst.toVk(shader.type())).module(module).pName(entryPoint);
            shaderStages.put(stage);
         }

         shaderStages.flip();
         List<CompiledRenderPipeline.CreateInfo.VertexBuffer> vertexBindings = pipelineCreateInfo.vertexBuffers();
         VkVertexInputBindingDescription.Buffer vertexBindingDescriptions = VkVertexInputBindingDescription.calloc(vertexBindings.size(), stack);
         VkVertexInputBindingDivisorDescriptionEXT.Buffer vertexBindingDivisorDescriptions = VkVertexInputBindingDivisorDescriptionEXT.calloc(vertexBindings.size(), stack);

         for(CompiledRenderPipeline.CreateInfo.VertexBuffer vertexBinding : vertexBindings) {
            VkVertexInputBindingDescription bindingDescription = VkVertexInputBindingDescription.calloc(stack).binding(vertexBinding.bufferSlot()).stride(vertexBinding.stride()).inputRate(vertexBinding.stepRate() > 0 ? 1 : 0);
            vertexBindingDescriptions.put(bindingDescription);
            if (vertexBinding.stepRate() > 0) {
               VkVertexInputBindingDivisorDescriptionEXT divisorBinding = VkVertexInputBindingDivisorDescriptionEXT.calloc(stack).binding(vertexBinding.bufferSlot()).divisor(vertexBinding.stepRate());
               vertexBindingDivisorDescriptions.put(divisorBinding);
            }
         }

         vertexBindingDescriptions.flip();
         vertexBindingDivisorDescriptions.flip();
         VkVertexInputAttributeDescription.Buffer vertexAttributeDescriptions = VkVertexInputAttributeDescription.calloc(pipelineCreateInfo.attribBindings().size(), stack);

         for(CompiledRenderPipeline.CreateInfo.AttribBinding attribBinding : pipelineCreateInfo.attribBindings()) {
            VkVertexInputAttributeDescription attributeDescription = VkVertexInputAttributeDescription.calloc(stack).location(attribBinding.location()).binding(attribBinding.bufferSlot()).offset(attribBinding.offset()).format(VulkanConst.toVk(attribBinding.format()));
            vertexAttributeDescriptions.put(attributeDescription);
         }

         vertexAttributeDescriptions.flip();
         VkPipelineVertexInputDivisorStateCreateInfoEXT vertexInputDivisorState = VkPipelineVertexInputDivisorStateCreateInfoEXT.calloc(stack).sType$Default().pVertexBindingDivisors(vertexBindingDivisorDescriptions);
         VkPipelineVertexInputStateCreateInfo vertexInputState = VkPipelineVertexInputStateCreateInfo.calloc(stack).sType$Default().pVertexAttributeDescriptions(vertexAttributeDescriptions).pVertexBindingDescriptions(vertexBindingDescriptions);
         if (vertexInputDivisorState.vertexBindingDivisorCount() > 0) {
            vertexInputState.pNext(vertexInputDivisorState);
         }

         VkPipelineInputAssemblyStateCreateInfo inputAssemblyState = VkPipelineInputAssemblyStateCreateInfo.calloc(stack).sType$Default().topology(VulkanConst.toVk(pipelineCreateInfo.primitiveTopology()));
         VkPipelineRasterizationStateCreateInfo rasterizationState = VkPipelineRasterizationStateCreateInfo.calloc(stack).sType$Default().polygonMode(VulkanConst.toVk(pipelineCreateInfo.polygonMode())).cullMode(pipelineCreateInfo.cull() ? 2 : 0).frontFace(1).lineWidth(1.0F);
         VkPipelineDepthStencilStateCreateInfo vkDepthStencilState = VkPipelineDepthStencilStateCreateInfo.calloc(stack).sType$Default();
         DepthStencilState depthStencilState = pipelineCreateInfo.depthStencilState();
         if (depthStencilState != null) {
            rasterizationState.depthBiasEnable(depthStencilState.depthBiasConstant() != 0.0F || depthStencilState.depthBiasScaleFactor() != 0.0F);
            rasterizationState.depthBiasConstantFactor(depthStencilState.depthBiasConstant());
            rasterizationState.depthBiasSlopeFactor(depthStencilState.depthBiasScaleFactor());
            vkDepthStencilState.depthTestEnable(true);
            vkDepthStencilState.depthWriteEnable(depthStencilState.writeDepth());
            vkDepthStencilState.depthCompareOp(VulkanConst.toVk(depthStencilState.depthTest()));
         }

         List<ColorTargetState> colorTargetStates = pipelineCreateInfo.colorTargetStates();
         VkPipelineColorBlendAttachmentState.Buffer blendAttachments = VkPipelineColorBlendAttachmentState.calloc(colorTargetStates.size(), stack);

         for(ColorTargetState colorTargetState : colorTargetStates) {
            blendAttachments.colorWriteMask(colorTargetState != null ? VulkanConst.toVk(colorTargetState) : 0);
            if (colorTargetState != null && colorTargetState.blendFunction().isPresent()) {
               applyBlendInformation(blendAttachments, (BlendFunction)colorTargetState.blendFunction().get());
            }

            blendAttachments.position(blendAttachments.position() + 1);
         }

         blendAttachments.position(0);
         VkPipelineColorBlendStateCreateInfo colorBlendState = VkPipelineColorBlendStateCreateInfo.calloc(stack).sType$Default().pAttachments(blendAttachments);
         VkPipelineViewportStateCreateInfo viewportState = VkPipelineViewportStateCreateInfo.calloc(stack).sType$Default().scissorCount(1).viewportCount(1);
         VkPipelineMultisampleStateCreateInfo multisampleState = VkPipelineMultisampleStateCreateInfo.calloc(stack).sType$Default().rasterizationSamples(1).sampleShadingEnable(false);
         VkPipelineDynamicStateCreateInfo dynamicStateInfo = VkPipelineDynamicStateCreateInfo.calloc(stack).sType$Default().pDynamicStates(stack.ints(1, 0));
         VkGraphicsPipelineCreateInfo.Buffer createInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack).sType$Default().flags(0).pStages(shaderStages).pVertexInputState(vertexInputState).pInputAssemblyState(inputAssemblyState).pRasterizationState(rasterizationState).pDepthStencilState(vkDepthStencilState).pColorBlendState(colorBlendState).pViewportState(viewportState).pMultisampleState(multisampleState).pDynamicState(dynamicStateInfo).layout(pipelineLayout).renderPass(device.renderpassCache().getRenderPass(VulkanConst.toVk(pipelineCreateInfo.depthStencilFormat()), colorTargetStates)).subpass(0);
         LongBuffer pointer = stack.callocLong(1);
         VulkanUtils.crashIfFailure(device, VK12.vkCreateGraphicsPipelines(device.vkDevice(), 0L, createInfo, (VkAllocationCallbacks)null, pointer), "Can't compile pipeline " + pipelineCreateInfo.name());
         long withDepthPipeline = pointer.get(0);
         device.instance().debug().setObjectName(device.vkDevice(), 19, withDepthPipeline, (Supplier)(() -> "Pipeline " + pipelineCreateInfo.name()));
         long withoutDepthPipeline;
         if (depthStencilState == null) {
            createInfo.renderPass(device.renderpassCache().getRenderPass(0, colorTargetStates));
            VulkanUtils.crashIfFailure(device, VK12.vkCreateGraphicsPipelines(device.vkDevice(), 0L, createInfo, (VkAllocationCallbacks)null, pointer), "Can't compile pipeline " + pipelineCreateInfo.name());
            withoutDepthPipeline = pointer.get(0);
            device.instance().debug().setObjectName(device.vkDevice(), 19, withoutDepthPipeline, (Supplier)(() -> "Pipeline " + pipelineCreateInfo.name()));
         } else {
            withoutDepthPipeline = 0L;
         }

         var31 = new VulkanRenderPipeline(device, withDepthPipeline, withoutDepthPipeline, pipelineLayout, descriptorSetLayout, compiledShaderModules, pipelineCreateInfo.uniforms());
      } catch (Throwable var37) {
         if (stack != null) {
            try {
               stack.close();
            } catch (Throwable var32) {
               var37.addSuppressed(var32);
            }
         }

         throw var37;
      }

      if (stack != null) {
         stack.close();
      }

      return var31;
   }

   public void destroy() {
      if (this.withDepthPipeline != 0L) {
         VK12.vkDestroyPipeline(this.device.vkDevice(), this.withoutDepthPipeline, (VkAllocationCallbacks)null);
         VK12.vkDestroyPipeline(this.device.vkDevice(), this.withDepthPipeline, (VkAllocationCallbacks)null);
         VK12.vkDestroyPipelineLayout(this.device.vkDevice(), this.pipelineLayout, (VkAllocationCallbacks)null);
         VK12.vkDestroyDescriptorSetLayout(this.device.vkDevice(), this.descriptorSetLayout, (VkAllocationCallbacks)null);

         for(int i = 0; i < this.shaderModules.size(); ++i) {
            VK12.vkDestroyShaderModule(this.device.vkDevice(), this.shaderModules.getLong(i), (VkAllocationCallbacks)null);
         }

      }
   }

   private static void applyBlendInformation(final VkPipelineColorBlendAttachmentState.Buffer blendAttachments, final BlendFunction blendFunction) {
      blendAttachments.blendEnable(true).colorBlendOp(VulkanConst.toVk(blendFunction.color().op())).alphaBlendOp(VulkanConst.toVk(blendFunction.alpha().op())).dstAlphaBlendFactor(VulkanConst.toVk(blendFunction.alpha().destFactor())).dstColorBlendFactor(VulkanConst.toVk(blendFunction.color().destFactor())).srcAlphaBlendFactor(VulkanConst.toVk(blendFunction.alpha().sourceFactor())).srcColorBlendFactor(VulkanConst.toVk(blendFunction.color().sourceFactor()));
   }

   public VulkanDevice device() {
      return this.device;
   }

   public long withDepthPipeline() {
      return this.withDepthPipeline;
   }

   public long withoutDepthPipeline() {
      return this.withoutDepthPipeline;
   }

   public long pipelineLayout() {
      return this.pipelineLayout;
   }

   public long descriptorSetLayout() {
      return this.descriptorSetLayout;
   }

   public List<CompiledRenderPipeline.CreateInfo.Uniform> uniforms() {
      return this.uniforms;
   }

   public int maxUniformBinding() {
      return this.maxUniformBinding;
   }
}
