package com.mojang.renderpearl.frontend.shaders;

import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.DeviceInfo;
import com.mojang.renderpearl.api.pipeline.BlendFunction;
import com.mojang.renderpearl.api.pipeline.ColorTargetState;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.PolygonMode;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.pipeline.SpvModule;
import com.mojang.renderpearl.api.pipeline.UniformType;
import com.mojang.renderpearl.util.ShaderCompileException;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PipelineValidator {
   public PipelineValidator() {
      super();
   }

   public static void validatePipelineCreateInfo(final DeviceInfo deviceInfo, final CompiledRenderPipeline.CreateInfo createInfo) throws ShaderCompileException {
      if (createInfo.polygonMode() == PolygonMode.WIREFRAME && !deviceInfo.features().wireframeFillMode()) {
         throw new ShaderCompileException("Device does not support wireframe fill mode");
      } else if (createInfo.vertexBuffers().size() > CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS) {
         throw new ShaderCompileException("Binding more than " + CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS + " vertex buffers is not supported");
      } else if (createInfo.attribBindings().size() > CompiledRenderPipeline.CreateInfo.MAX_VERTEX_ATTRIBS) {
         throw new ShaderCompileException("Binding more than " + CompiledRenderPipeline.CreateInfo.MAX_VERTEX_ATTRIBS + " vertex attribs is not supported");
      } else {
         int[] usedBufferSlots = new int[CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS];

         for(CompiledRenderPipeline.CreateInfo.VertexBuffer vertexBuffer : createInfo.vertexBuffers()) {
            if (usedBufferSlots[vertexBuffer.bufferSlot()] != 0) {
               throw new ShaderCompileException("Multiple vertex buffers using slot " + vertexBuffer.bufferSlot());
            }

            if (vertexBuffer.stride() % CompiledRenderPipeline.CreateInfo.MIN_VERTEX_STRIDE_ALIGNMENT != 0) {
               int var10002 = vertexBuffer.bufferSlot();
               throw new ShaderCompileException("Vertex buffer at slot " + var10002 + " stride not aligned to minimum of " + CompiledRenderPipeline.CreateInfo.MIN_VERTEX_STRIDE_ALIGNMENT);
            }

            usedBufferSlots[vertexBuffer.bufferSlot()] = vertexBuffer.stride();
         }

         boolean[] usedAttribLocations = new boolean[CompiledRenderPipeline.CreateInfo.MAX_VERTEX_ATTRIBS];

         for(CompiledRenderPipeline.CreateInfo.AttribBinding vertexAttrib : createInfo.attribBindings()) {
            if (vertexAttrib.bufferSlot() >= 0 && vertexAttrib.bufferSlot() <= CompiledRenderPipeline.CreateInfo.MAX_VERTEX_BUFFERS) {
               if (usedBufferSlots[vertexAttrib.bufferSlot()] == 0) {
                  int var50 = vertexAttrib.location();
                  throw new ShaderCompileException("Vertex attrib at location " + var50 + " attempting to use buffer slot " + vertexAttrib.bufferSlot() + " that does not have a buffer");
               }

               if (vertexAttrib.location() >= 0 && vertexAttrib.location() <= CompiledRenderPipeline.CreateInfo.MAX_VERTEX_ATTRIBS) {
                  if (usedAttribLocations[vertexAttrib.location()]) {
                     throw new ShaderCompileException("Multiple vertex attribs using location " + vertexAttrib.location());
                  }

                  usedAttribLocations[vertexAttrib.location()] = true;
                  if (vertexAttrib.offset() % vertexAttrib.format().byteAlignment() != 0) {
                     throw new ShaderCompileException("Vertex attrib " + vertexAttrib.location() + " uses an offset not aligned to its format");
                  }

                  if (usedBufferSlots[vertexAttrib.bufferSlot()] % vertexAttrib.format().byteAlignment() != 0) {
                     throw new ShaderCompileException("Vertex attrib " + vertexAttrib.location() + " will become unaligned due to buffer stride");
                  }

                  int attribEndOffset = vertexAttrib.offset() + vertexAttrib.format().blockSize();
                  if (attribEndOffset > usedBufferSlots[vertexAttrib.bufferSlot()]) {
                     throw new ShaderCompileException("Vertex attrib with location " + vertexAttrib.location() + " extends beyond buffer stride");
                  }
                  continue;
               }

               throw new ShaderCompileException("Vertex attrib location " + vertexAttrib.location() + " out of bounds");
            }

            throw new ShaderCompileException("Vertex attrib buffer slot " + vertexAttrib.location() + " out of bounds");
         }

         Optional<BlendFunction> lastBlend = Optional.empty();

         for(ColorTargetState activeColorTargetState : createInfo.colorTargetStates()) {
            if (activeColorTargetState != null) {
               Optional<BlendFunction> currentBlend = activeColorTargetState.blendFunction();
               if (currentBlend.isPresent()) {
                  if (lastBlend.isEmpty()) {
                     lastBlend = currentBlend;
                  } else if (!currentBlend.equals(lastBlend)) {
                     throw new ShaderCompileException("Blend functions must currently be the same for all color targets");
                  }
               }
            }
         }

         if (createInfo.pushConstantsSize() > 128) {
            throw new ShaderCompileException("Maximum push constant size is 128 bytes");
         } else {
            Map<ShaderType, SpvModule> shadersByType = new EnumMap(ShaderType.class);

            for(SpvModule shader : createInfo.shaders()) {
               shadersByType.put(shader.type(), shader);
               List<SpvModule.Reflection.PushConstant> pushConstants = shader.reflect().pushConstants();
               if (!pushConstants.isEmpty()) {
                  if (pushConstants.size() > 1) {
                     throw new ShaderCompileException("Shader may define at most one push_constant block");
                  }

                  SpvModule.Reflection.PushConstant pushConstant = (SpvModule.Reflection.PushConstant)pushConstants.getFirst();
                  if (pushConstant.size() > createInfo.pushConstantsSize()) {
                     throw new ShaderCompileException("Shader push constant size exceeds pipeline declared size");
                  }
               }
            }

            Int2ReferenceMap<CompiledRenderPipeline.CreateInfo.Uniform> pipelineUniformsByBinding = new Int2ReferenceOpenHashMap();

            for(CompiledRenderPipeline.CreateInfo.Uniform pipelineUniform : createInfo.uniforms()) {
               pipelineUniformsByBinding.put(pipelineUniform.binding(), pipelineUniform);
            }

            for(SpvModule shader : createInfo.shaders()) {
               SpvModule.Reflection reflectionInfo = shader.reflect();

               for(SpvModule.Reflection.Descriptor descriptor : reflectionInfo.descriptors()) {
                  if (descriptor.descriptorSetIndex() != 0) {
                     throw new ShaderCompileException("Descriptor sets other than 0 not supported");
                  }

                  CompiledRenderPipeline.CreateInfo.Uniform pipelineUniform = (CompiledRenderPipeline.CreateInfo.Uniform)pipelineUniformsByBinding.get(descriptor.binding());
                  if (pipelineUniform == null) {
                     int var51 = descriptor.binding();
                     throw new ShaderCompileException("Unable to find shader defined uniform at binding " + var51 + " with shader name (" + descriptor.name() + ")");
                  }

                  int definedResourceType = descriptor.resourceType();
                  int expectedResourceType = SpvUtil.resourceType(pipelineUniform.type());
                  int definedDimensions = descriptor.type().dimensions();
                  if (expectedResourceType != definedResourceType) {
                     throw new ShaderCompileException("Uniform type in shader does not match expected type from CreateInfo for (" + pipelineUniform.name() + ")");
                  }

                  if (definedDimensions != 2147483647) {
                     UniformType var10000;
                     switch (definedDimensions) {
                        case 0 -> throw new ShaderCompileException("1D textures not supported (" + pipelineUniform.name() + ")");
                        case 1 -> var10000 = UniformType.COMBINED_IMAGE_SAMPLER;
                        case 2 -> throw new ShaderCompileException("3D textures not supported (" + pipelineUniform.name() + ")");
                        case 3 -> var10000 = UniformType.COMBINED_IMAGE_SAMPLER;
                        case 4 -> var10000 = UniformType.COMBINED_IMAGE_SAMPLER;
                        case 5 -> var10000 = UniformType.TEXEL_BUFFER;
                        default -> throw new ShaderCompileException("Unexpected SpvDim (" + definedDimensions + ") for uniform (" + pipelineUniform.name() + ")");
                     }

                     UniformType expectedUniformType = var10000;
                     if (pipelineUniform.type() != expectedUniformType) {
                        throw new ShaderCompileException("Unexpected dimensions for uniform (" + pipelineUniform.name() + "), does not match RenderPipeline definition");
                     }
                  }
               }
            }

            if (shadersByType.containsKey(ShaderType.VERTEX)) {
               SpvModule vertexShader = (SpvModule)shadersByType.get(ShaderType.VERTEX);
               List<SpvModule.Reflection.InterfaceVariable> vertexShaderInputs = vertexShader.reflect().inputs();
               Int2ObjectMap<GpuFormat> attribFormats = new Int2ObjectArrayMap();

               for(CompiledRenderPipeline.CreateInfo.AttribBinding attribBinding : createInfo.attribBindings()) {
                  attribFormats.put(attribBinding.location(), attribBinding.format());
               }

               for(SpvModule.Reflection.InterfaceVariable vertexShaderInput : vertexShaderInputs) {
                  if (vertexShaderInput.decoration(31) != 0) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "vertex shader (%s) attrib (%s) has component decoration, this is not permitted", vertexShader.name(), vertexShaderInput.name()));
                  }

                  GpuFormat format = (GpuFormat)attribFormats.get(vertexShaderInput.location());
                  if (format == null) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "vertex shader (%s) attrib (%s) does not have a matching vertex buffer element", vertexShader.name(), vertexShaderInput.name()));
                  }

                  byte var49;
                  switch (format.componentType()) {
                     case UNORM_8:
                     case SNORM_8:
                     case UNORM_16:
                     case SNORM_16:
                     case FLOAT_16:
                     case FLOAT_32:
                        var49 = 13;
                        break;
                     case UINT_8:
                     case UINT_16:
                     case UINT_32:
                        var49 = 8;
                        break;
                     case SINT_8:
                     case SINT_16:
                     case SINT_32:
                        var49 = 7;
                        break;
                     default:
                        throw new ShaderCompileException("Unexpected value: " + String.valueOf(format.componentType()));
                  }

                  int expectedBaseType = var49;
                  if (expectedBaseType != vertexShaderInput.type().baseType()) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "Unexpected base type for input attribute %s in vertex shader %s, expected %s got %s", vertexShaderInput.name(), vertexShader.name(), SpvUtil.baseTypeString(expectedBaseType), SpvUtil.baseTypeString(vertexShaderInput.type().baseType())));
                  }

                  if (vertexShaderInput.type().vectorSize() > format.componentCount()) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "Not enough components for input attribute %s in vertex shader %s, expected at least %s got %s", vertexShaderInput.name(), vertexShader.name(), vertexShaderInput.type().vectorSize(), format.componentCount()));
                  }
               }
            }

            if (shadersByType.containsKey(ShaderType.VERTEX)) {
               String vertexStageName = ((SpvModule)shadersByType.get(ShaderType.VERTEX)).name();
               Int2ObjectMap<InterfaceVariableInfo> outputSlotMap = generateSlotMap(((SpvModule)shadersByType.get(ShaderType.VERTEX)).reflect().outputs(), vertexStageName, ShaderType.VERTEX);
               String fragmentStageName = ((SpvModule)shadersByType.get(ShaderType.FRAGMENT)).name();
               Int2ObjectMap<InterfaceVariableInfo> inputSlotMap = generateSlotMap(((SpvModule)shadersByType.get(ShaderType.FRAGMENT)).reflect().inputs(), vertexStageName, ShaderType.FRAGMENT);
               ObjectIterator var42 = inputSlotMap.int2ObjectEntrySet().iterator();

               while(var42.hasNext()) {
                  Int2ObjectMap.Entry<InterfaceVariableInfo> entry = (Int2ObjectMap.Entry)var42.next();
                  int location = entry.getIntKey();
                  InterfaceVariableInfo input = (InterfaceVariableInfo)entry.getValue();

                  assert input != null;

                  InterfaceVariableInfo output = (InterfaceVariableInfo)outputSlotMap.get(location);
                  if (output == null) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "Vertex shader (%s) missing output at location %d consumed by Fragment shader (%s)", vertexStageName, location, fragmentStageName));
                  }

                  if (!output.equals(input)) {
                     if (output.baseType() != input.baseType()) {
                        throw new ShaderCompileException(String.format(Locale.ROOT, "Vertex shader (%s) and Fragment shader (%s) have base type mismatch at location %d of %s and %s.", vertexStageName, fragmentStageName, location, SpvUtil.baseTypeString(output.baseType()), SpvUtil.baseTypeString(input.baseType())));
                     }

                     if (output.vectorLength() != input.vectorLength()) {
                        throw new ShaderCompileException(String.format(Locale.ROOT, "Vertex shader (%s) and Fragment shader (%s) have vector length mismatch at location %d of %d and %d.", vertexStageName, fragmentStageName, location, output.vectorLength(), input.vectorLength()));
                     }

                     if (output.flatInterpolated() != input.flatInterpolated()) {
                        throw new ShaderCompileException(String.format(Locale.ROOT, "Vertex shader (%s) and Fragment shader (%s) have mismatched interpolation at location %d, flat in %s", vertexStageName, fragmentStageName, location, output.flatInterpolated() ? "vertex" : "fragment"));
                     }
                  }
               }

            } else {
               throw new ShaderCompileException("Unable to determine Vertex stage");
            }
         }
      }
   }

   private static Int2ObjectMap<InterfaceVariableInfo> generateSlotMap(final List<SpvModule.Reflection.InterfaceVariable> interfaceVariables, final String stageName, final ShaderType shaderType) throws ShaderCompileException {
      Int2ObjectMap<InterfaceVariableInfo> slotMap = new Int2ObjectArrayMap();

      for(SpvModule.Reflection.InterfaceVariable interfaceVariable : interfaceVariables) {
         SpvModule.Reflection.Type interfaceVariableType = interfaceVariable.type();
         int baseLocation = interfaceVariable.location();
         if (interfaceVariable.decoration(31) != 0) {
            throw new ShaderCompileException(String.format(Locale.ROOT, "%s shader (%s) interface (%s) has component decoration, this is not permitted", shaderType.getName(), stageName, interfaceVariable.name()));
         }

         switch (interfaceVariableType.baseType()) {
            case 9:
            case 10:
            case 14:
            case 15:
               throw new ShaderCompileException(String.format(Locale.ROOT, "Unsupported interface variable type %s in %s shader (%s) location %d", SpvUtil.baseTypeString(interfaceVariableType.baseType()), shaderType.getName(), stageName, baseLocation));
            case 11:
            case 12:
            case 13:
         }

         InterfaceVariableInfo info = new InterfaceVariableInfo(interfaceVariableType.baseType(), interfaceVariableType.vectorSize(), interfaceVariable.hasDecoration(14));
         int totalArraySize = 1;
         int arrayDimensions = interfaceVariableType.arrayDimensions();
         if (arrayDimensions > 0) {
            for(int i = 0; i < arrayDimensions; ++i) {
               totalArraySize *= interfaceVariableType.arrayLength(i);
            }
         }

         for(int i = 0; i < totalArraySize; ++i) {
            slotMap.put(baseLocation + i, info);
         }
      }

      return slotMap;
   }

   private static record InterfaceVariableInfo(int baseType, int vectorLength, boolean flatInterpolated) {
      private InterfaceVariableInfo {
         super();
      }
   }
}
