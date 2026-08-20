package com.mojang.renderpearl.frontend.shaders;

import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.GpuFormat;
import com.mojang.renderpearl.api.device.DeviceInfo;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.PolygonMode;
import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderSource;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.pipeline.UniformType;
import com.mojang.renderpearl.api.vertex.VertexFormat;
import com.mojang.renderpearl.api.vertex.VertexFormatElement;
import com.mojang.renderpearl.backend.api.BackendRenderPipeline;
import com.mojang.renderpearl.backend.api.GpuDeviceBackend;
import com.mojang.renderpearl.backend.api.SpvModule;
import com.mojang.renderpearl.frontend.FrontendRenderPipeline;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PipelineBuilder implements UncheckedAutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GpuDeviceBackend backendDevice;
   private final GlslCompiler compiler;

   public PipelineBuilder(final GpuDeviceBackend backendDevice) {
      super();
      this.backendDevice = backendDevice;
      DeviceInfo deviceInfo = backendDevice.getDeviceInfo();
      this.compiler = new GlslCompiler(deviceInfo.isZZeroToOne(), deviceInfo.features().shaderDrawParameters());
   }

   public void close() {
      this.compiler.close();
   }

   public CompiledRenderPipeline compilePipeline(final RenderPipeline pipeline, final ShaderSource shaderSource) {
      if (pipeline.getPolygonMode() == PolygonMode.WIREFRAME && !this.backendDevice.getDeviceInfo().features().wireframeFillMode()) {
         LOGGER.error("Pipeline {} uses {} fill mode, not supported by device", pipeline.getLocation(), PolygonMode.WIREFRAME);
         return null;
      } else {
         ReferenceArrayList<BackendRenderPipeline.CreateInfo.Shader> shaderCreateInfos = new ReferenceArrayList();

         try {
            EnumMap<ShaderType, SpvModule> shaderSources = new EnumMap(ShaderType.class);

            for(Map.Entry<ShaderType, Identifier> shader : pipeline.getShaders().entrySet()) {
               String source = loadShaderSource((Identifier)shader.getValue(), (ShaderType)shader.getKey(), shaderSource);
               if (source == null) {
                  LOGGER.error("Couldn't find source for {} shader ({})", shader.getKey(), shader.getValue());
                  Object var36 = null;
                  return (CompiledRenderPipeline)var36;
               }

               SpvModule spvModule = this.compiler.compileToSpv(((Identifier)shader.getValue()).toString(), source, (ShaderType)shader.getKey(), pipeline.getShaderDefines(), shaderSource);
               shaderSources.put((ShaderType)shader.getKey(), spvModule);
               shaderCreateInfos.add(new BackendRenderPipeline.CreateInfo.Shader(((Identifier)shader.getValue()).toString(), "main", spvModule));
            }

            List<BackendRenderPipeline.CreateInfo.VertexBuffer> vertexBuffers = new ReferenceArrayList();
            List<BackendRenderPipeline.CreateInfo.AttribBinding> attribBindings = new ReferenceArrayList();
            if (shaderSources.containsKey(ShaderType.VERTEX)) {
               Int2ObjectMap<GpuFormat> attribFormats = new Int2ObjectArrayMap();
               SpvModule vertexShader = (SpvModule)shaderSources.get(ShaderType.VERTEX);
               List<SpvModule.Reflection.InterfaceVariable> vertexShaderInputs = vertexShader.reflect().inputs();
               String vertexStageName = ((Identifier)pipeline.getShaders().get(ShaderType.VERTEX)).toString();
               Map<String, SpvModule.Reflection.InterfaceVariable> vertexShaderInputsByName = (Map)vertexShaderInputs.stream().collect(Collectors.toUnmodifiableMap(SpvModule.Reflection.InterfaceVariable::name, Function.identity()));
               List<VertexFormat> vertexBindings = pipeline.getVertexFormatBindings();
               String previousElementName = null;
               int previousElementLocation = 0;

               for(int i = 0; i < vertexBindings.size(); ++i) {
                  VertexFormat vertexBinding = (VertexFormat)vertexBindings.get(i);
                  if (vertexBinding != null) {
                     vertexBuffers.add(new BackendRenderPipeline.CreateInfo.VertexBuffer(i, vertexBinding.getVertexSize(), vertexBinding.getStepRate()));

                     for(VertexFormatElement element : vertexBinding.getElements()) {
                        SpvModule.Reflection.InterfaceVariable input = (SpvModule.Reflection.InterfaceVariable)vertexShaderInputsByName.get(element.name());
                        if (input != null) {
                           int attribLocation;
                           if (element.name().equals(previousElementName)) {
                              attribLocation = previousElementLocation + 1;
                           } else {
                              attribLocation = input.location();
                           }

                           attribBindings.add(new BackendRenderPipeline.CreateInfo.AttribBinding(i, attribLocation, element.offset(), element.format()));
                           attribFormats.put(attribLocation, element.format());
                           previousElementName = element.name();
                           previousElementLocation = attribLocation;
                        }
                     }
                  }
               }

               for(SpvModule.Reflection.InterfaceVariable vertexShaderInput : vertexShaderInputs) {
                  if (vertexShaderInput.decoration(31) != 0) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "vertex shader (%s) attrib (%s) has component decoration, this is not permitted", vertexStageName, vertexShaderInput.name()));
                  }

                  GpuFormat format = (GpuFormat)attribFormats.get(vertexShaderInput.location());
                  byte var10000;
                  switch (format.componentType()) {
                     case UNORM_8:
                     case SNORM_8:
                     case UNORM_16:
                     case SNORM_16:
                     case FLOAT_16:
                     case FLOAT_32:
                        var10000 = 13;
                        break;
                     case UINT_8:
                     case UINT_16:
                     case UINT_32:
                        var10000 = 8;
                        break;
                     case SINT_8:
                     case SINT_16:
                     case SINT_32:
                        var10000 = 7;
                        break;
                     default:
                        throw new ShaderCompileException("Unexpected value: " + String.valueOf(format.componentType()));
                  }

                  int expectedBaseType = var10000;
                  if (expectedBaseType != vertexShaderInput.type().baseType()) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "Unexpected base type for input attribute %s in vertex shader %s, expected %s got %s", vertexShaderInput.name(), vertexStageName, SpvUtil.baseTypeString(expectedBaseType), SpvUtil.baseTypeString(vertexShaderInput.type().baseType())));
                  }

                  if (vertexShaderInput.type().vectorSize() > format.componentCount()) {
                     throw new ShaderCompileException(String.format(Locale.ROOT, "Not enough components for input attribute %s in vertex shader %s, expected at least %s got %s", vertexShaderInput.name(), vertexStageName, vertexShaderInput.type().vectorSize(), format.componentCount()));
                  }
               }
            }

            String vertexStageName;
            Int2ObjectMap<InterfaceVariableInfo> outputSlotMap;
            if (shaderSources.containsKey(ShaderType.VERTEX)) {
               vertexStageName = ((Identifier)pipeline.getShaders().get(ShaderType.VERTEX)).toString();
               outputSlotMap = generateSlotMap(((SpvModule)shaderSources.get(ShaderType.VERTEX)).reflect().outputs(), vertexStageName, ShaderType.VERTEX);
            } else {
               vertexStageName = "";
               outputSlotMap = Int2ObjectMaps.emptyMap();
            }

            String fragmentStageName = ((Identifier)pipeline.getShaders().get(ShaderType.FRAGMENT)).toString();
            Int2ObjectMap<InterfaceVariableInfo> inputSlotMap = generateSlotMap(((SpvModule)shaderSources.get(ShaderType.FRAGMENT)).reflect().inputs(), vertexStageName, ShaderType.FRAGMENT);
            ObjectIterator pipelineUniforms = inputSlotMap.int2ObjectEntrySet().iterator();

            while(pipelineUniforms.hasNext()) {
               Int2ObjectMap.Entry<InterfaceVariableInfo> entry = (Int2ObjectMap.Entry)pipelineUniforms.next();
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

            List<BindGroupLayout.UniformDescription> pipelineUniforms = BindGroupLayout.flattenUniforms(pipeline.getBindGroupLayouts());
            Map<String, BindGroupLayout.UniformDescription> pipelineUniformsByName = new Object2ObjectOpenHashMap();

            for(BindGroupLayout.UniformDescription pipelineUniform : pipelineUniforms) {
               pipelineUniformsByName.put(pipelineUniform.name(), pipelineUniform);
            }

            Object2IntOpenHashMap<String> uniformBindings = new Object2IntOpenHashMap();
            IntList uniformResourceTypes = new IntArrayList();
            IntList uniformDimensions = new IntArrayList();
            ReferenceList<BindGroupLayout.UniformDescription> uniforms = new ReferenceArrayList();
            Iterator var57 = shaderCreateInfos.iterator();

            while(var57.hasNext()) {
               BackendRenderPipeline.CreateInfo.Shader shader = (BackendRenderPipeline.CreateInfo.Shader)var57.next();
               SpvModule.Reflection reflectionInfo = shader.module().reflect();

               for(SpvModule.Reflection.Descriptor descriptor : reflectionInfo.descriptors()) {
                  String uniformName = descriptor.name();
                  SpvModule.Reflection.Type type = descriptor.type();
                  BindGroupLayout.UniformDescription pipelineUniform = (BindGroupLayout.UniformDescription)pipelineUniformsByName.get(uniformName);
                  if (pipelineUniform == null) {
                     throw new ShaderCompileException("Unable to find shader defined uniform (" + uniformName + ")");
                  }

                  int newBinding = uniformBindings.computeIfAbsent(descriptor.name(), (var1) -> uniformBindings.size());
                  if (newBinding >= uniformResourceTypes.size()) {
                     uniformResourceTypes.add(descriptor.resourceType());
                     uniformDimensions.add(type.dimensions());
                     uniforms.add(new BindGroupLayout.UniformDescription(pipelineUniform.name(), pipelineUniform.type(), pipelineUniform.gpuFormat()));
                  } else {
                     if (descriptor.resourceType() != uniformResourceTypes.getInt(newBinding)) {
                        throw new ShaderCompileException("Uniform type for " + descriptor.name() + " does not match across all stages");
                     }

                     if (type.dimensions() != uniformDimensions.getInt(newBinding)) {
                        throw new ShaderCompileException("Uniform dimensions for " + descriptor.name() + " does not match across all stages");
                     }
                  }

                  descriptor.binding(newBinding);
                  descriptor.descriptorSetIndex(0);
               }
            }

            var57 = uniformBindings.keySet().iterator();

            while(var57.hasNext()) {
               String uniformName = (String)var57.next();
               BindGroupLayout.UniformDescription pipelineUniform = (BindGroupLayout.UniformDescription)pipelineUniformsByName.get(uniformName);

               assert pipelineUniform != null;

               int uniformBinding = uniformBindings.getInt(uniformName);
               int definedResourceType = uniformResourceTypes.getInt(uniformBinding);
               int expectedResourceType = SpvUtil.resourceType(pipelineUniform.type());
               int definedDimensions = uniformDimensions.getInt(uniformBinding);
               if (expectedResourceType != definedResourceType) {
                  throw new ShaderCompileException("Uniform type in shader does not match expected type from RenderPipeline for (" + uniformName + ")");
               }

               if (definedDimensions != 2147483647) {
                  UniformType var78;
                  switch (definedDimensions) {
                     case 0 -> throw new ShaderCompileException("1D textures not supported (" + uniformName + ")");
                     case 1 -> var78 = UniformType.COMBINED_IMAGE_SAMPLER;
                     case 2 -> throw new ShaderCompileException("3D textures not supported (" + uniformName + ")");
                     case 3 -> var78 = UniformType.COMBINED_IMAGE_SAMPLER;
                     case 4 -> var78 = UniformType.COMBINED_IMAGE_SAMPLER;
                     case 5 -> var78 = UniformType.TEXEL_BUFFER;
                     default -> throw new ShaderCompileException("Unexpected SpvDim (" + definedDimensions + ") for uniform (" + uniformName + ")");
                  }

                  UniformType expectedUniformType = var78;
                  if (pipelineUniform.type() != expectedUniformType) {
                     throw new ShaderCompileException("Unexpected dimensions for uniform (" + uniformName + "), does not match RenderPipeline definition");
                  }
               }
            }

            var57 = shaderCreateInfos.iterator();

            while(var57.hasNext()) {
               BackendRenderPipeline.CreateInfo.Shader shader = (BackendRenderPipeline.CreateInfo.Shader)var57.next();
               List<SpvModule.Reflection.PushConstant> pushConstants = shader.module().reflect().pushConstants();
               if (!pushConstants.isEmpty()) {
                  if (pushConstants.size() > 1) {
                     throw new ShaderCompileException("Shader may define at most one push_constant block");
                  }

                  SpvModule.Reflection.PushConstant pushConstant = (SpvModule.Reflection.PushConstant)pushConstants.getFirst();
                  if (pushConstant.size() > pipeline.pushConstantSize()) {
                     throw new ShaderCompileException("Shader push constant size exceeds pipeline declared size");
                  }
               }
            }

            BackendRenderPipeline.CreateInfo pipelineCreateInfo = new BackendRenderPipeline.CreateInfo(pipeline.getLocation().toString(), List.copyOf(shaderCreateInfos), List.copyOf(vertexBuffers), List.copyOf(attribBindings), List.copyOf(uniforms), pipeline.pushConstantSize(), pipeline.getDepthStencilState(), pipeline.getPolygonMode(), pipeline.isCull(), pipeline.getColorTargetStates(), pipeline.getPrimitiveTopology());
            BackendRenderPipeline backendPipeline = this.backendDevice.compilePipeline(pipelineCreateInfo);
            if (backendPipeline == null) {
               Object var70 = null;
               return (CompiledRenderPipeline)var70;
            } else {
               FrontendRenderPipeline var69 = new FrontendRenderPipeline(pipeline.getLocation().toString(), backendPipeline, ReferenceLists.unmodifiable(new ReferenceArrayList(pipeline.getVertexFormatBindings())), Object2IntMaps.unmodifiable(uniformBindings), BindGroupLayout.flattenUniforms(pipeline.getBindGroupLayouts()), ReferenceLists.unmodifiable(new ReferenceArrayList(pipeline.getColorTargetStates())), pipeline.wantsDepthTexture(), pipeline.pushConstantSize());
               return var69;
            }
         } catch (ShaderCompileException e) {
            LOGGER.error("Couldn't compile pipeline ({}): ", pipeline.getLocation(), e);
            Object vertexBuffers = null;
            return (CompiledRenderPipeline)vertexBuffers;
         } finally {
            shaderCreateInfos.forEach((info) -> info.module().close());
         }
      }
   }

   private static @Nullable String loadShaderSource(final Identifier id, final ShaderType type, final ShaderSource shaderSource) {
      String source = shaderSource.get(id, type);
      if (source == null) {
         LOGGER.error("Couldn't find source for {} shader ({})", type, id);
         return null;
      } else {
         return source;
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
