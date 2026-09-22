package com.mojang.blaze3d.pipeline;

import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import com.mojang.jtracy.TracyClient;
import com.mojang.jtracy.Zone;
import com.mojang.logging.LogUtils;
import com.mojang.renderpearl.api.device.GpuDevice;
import com.mojang.renderpearl.api.pipeline.BindGroupLayout;
import com.mojang.renderpearl.api.pipeline.CompiledRenderPipeline;
import com.mojang.renderpearl.api.pipeline.ShaderType;
import com.mojang.renderpearl.api.pipeline.SpvModule;
import com.mojang.renderpearl.util.ShaderCompileException;
import com.mojang.renderpearl.util.UncheckedAutoCloseable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import java.nio.ByteBuffer;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

public class PipelineBuilder implements UncheckedAutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final GpuDevice device;
   private final GlslCompiler compiler;

   public PipelineBuilder(final GpuDevice device) {
      super();
      this.device = device;
      this.compiler = new GlslCompiler();
   }

   public void close() {
      this.compiler.close();
   }

   private CompiledRenderPipeline.CreateInfo generateBackendCreateInfo(final RenderPipeline pipeline, final ShaderSource shaderSource) {
      EnumMap<ShaderType, SpvModule> shaderSources = new EnumMap(ShaderType.class);

      try {
         for(Map.Entry<ShaderType, Identifier> shader : pipeline.getShaders().entrySet()) {
            String source = loadShaderSource((Identifier)shader.getValue(), (ShaderType)shader.getKey(), shaderSource);
            if (source == null) {
               LOGGER.error("Couldn't find source for {} shader ({})", shader.getKey(), shader.getValue());
               return null;
            }

            String shaderName = ((Identifier)shader.getValue()).toString();
            ByteBuffer spirv = this.compiler.compileToSpv(shaderName, source, (ShaderType)shader.getKey(), pipeline.getShaderDefines(), shaderSource, this.device.getDeviceInfo());
            SpvModule spvModule = this.device.createSpvModule(shaderName, spirv, (ShaderType)shader.getKey(), "main");
            shaderSources.put((ShaderType)shader.getKey(), spvModule);
         }

         List<CompiledRenderPipeline.CreateInfo.VertexBuffer> vertexBuffers = new ReferenceArrayList();
         List<CompiledRenderPipeline.CreateInfo.AttribBinding> attribBindings = new ReferenceArrayList();
         if (shaderSources.containsKey(ShaderType.VERTEX)) {
            SpvModule vertexShader = (SpvModule)shaderSources.get(ShaderType.VERTEX);
            List<SpvModule.Reflection.InterfaceVariable> vertexShaderInputs = vertexShader.reflect().inputs();
            Map<String, SpvModule.Reflection.InterfaceVariable> vertexShaderInputsByName = (Map)vertexShaderInputs.stream().collect(Collectors.toUnmodifiableMap(SpvModule.Reflection.InterfaceVariable::name, Function.identity()));
            List<VertexFormat> vertexBindings = pipeline.getVertexFormatBindings();
            String previousElementName = null;
            int previousElementLocation = 0;

            for(int i = 0; i < vertexBindings.size(); ++i) {
               VertexFormat vertexBinding = (VertexFormat)vertexBindings.get(i);
               if (vertexBinding != null) {
                  vertexBuffers.add(new CompiledRenderPipeline.CreateInfo.VertexBuffer(i, vertexBinding.getVertexSize(), vertexBinding.getStepRate()));

                  for(VertexFormatElement element : vertexBinding.getElements()) {
                     SpvModule.Reflection.InterfaceVariable input = (SpvModule.Reflection.InterfaceVariable)vertexShaderInputsByName.get(element.name());
                     if (input != null) {
                        int attribLocation;
                        if (element.name().equals(previousElementName)) {
                           attribLocation = previousElementLocation + 1;
                        } else {
                           attribLocation = input.location();
                        }

                        attribBindings.add(new CompiledRenderPipeline.CreateInfo.AttribBinding(i, attribLocation, element.offset(), element.format()));
                        previousElementName = element.name();
                        previousElementLocation = attribLocation;
                     }
                  }
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
         ReferenceList<CompiledRenderPipeline.CreateInfo.Uniform> uniforms = new ReferenceArrayList();

         for(SpvModule shader : shaderSources.values()) {
            SpvModule.Reflection reflectionInfo = shader.reflect();

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
                  uniforms.add(new CompiledRenderPipeline.CreateInfo.Uniform(uniformName, newBinding, pipelineUniform.type(), pipelineUniform.gpuFormat()));
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

         return new CompiledRenderPipeline.CreateInfo(pipeline.getLocation().toString(), List.copyOf(shaderSources.values()), List.copyOf(vertexBuffers), List.copyOf(attribBindings), List.copyOf(uniforms), pipeline.pushConstantSize(), pipeline.getDepthStencilState(), pipeline.getDepthStencilFormat(), pipeline.getPolygonMode(), pipeline.isCull(), pipeline.getColorTargetStates(), pipeline.getPrimitiveTopology());
      } catch (ShaderCompileException e) {
         LOGGER.error("Couldn't compile pipeline ({}): ", pipeline.getLocation(), e);
         shaderSources.values().forEach(UncheckedAutoCloseable::close);
         return null;
      }
   }

   public CompletableFuture<CompiledRenderPipeline.Pending> compilePipeline(final RenderPipeline pipeline, final ShaderSource shaderSource, final Executor executor) {
      return CompletableFuture.supplyAsync(() -> this.compilePipeline(pipeline, shaderSource), executor);
   }

   public CompiledRenderPipeline.Pending compilePipeline(final RenderPipeline pipeline, final ShaderSource shaderSource) {
      Zone tracyZone = TracyClient.beginZone("Frontend Compile and reflection", false);

      CompiledRenderPipeline.CreateInfo createInfo;
      try {
         tracyZone.addText(pipeline.getLocation().toString());
         createInfo = this.generateBackendCreateInfo(pipeline, shaderSource);
      } catch (Throwable var8) {
         if (tracyZone != null) {
            try {
               tracyZone.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (tracyZone != null) {
         tracyZone.close();
      }

      if (createInfo == null) {
         return CompiledRenderPipeline.Pending.NULL;
      } else {
         CompiledRenderPipeline.Pending pendingPipeline = this.device.compilePipeline(createInfo);
         createInfo.shaders().forEach(UncheckedAutoCloseable::close);
         return pendingPipeline;
      }
   }

   private static @Nullable String loadShaderSource(final Identifier id, final ShaderType type, final ShaderSource shaderSource) {
      String source = shaderSource.getShader(id, type);
      if (source == null) {
         LOGGER.error("Couldn't find source for {} shader ({})", type, id);
         return null;
      } else {
         return source;
      }
   }
}
