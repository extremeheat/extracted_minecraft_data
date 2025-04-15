package net.minecraft.client.renderer;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import java.nio.ByteBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;

public class DynamicUniforms implements AutoCloseable {
   public static final int TRANSFORM_UBO_SIZE = 164;
   private static final int INITIAL_CAPACITY = 2;
   private final DynamicUniformStorage<Transform> transforms = new DynamicUniformStorage<Transform>("Dynamic Transforms UBO", 164, 2);

   public DynamicUniforms() {
      super();
   }

   public void reset() {
      this.transforms.endFrame();
   }

   public void close() {
      this.transforms.close();
   }

   public GpuBufferSlice writeTransform(Matrix4fc var1, Vector4fc var2, Vector3fc var3, Matrix4fc var4, float var5) {
      return this.transforms.writeUniform(new Transform(new Matrix4f(var1), new Vector4f(var2), new Vector3f(var3), new Matrix4f(var4), var5));
   }

   public GpuBufferSlice[] writeTransforms(Transform... var1) {
      return this.transforms.writeUniforms(var1);
   }

   public static record Transform(Matrix4fc modelView, Vector4fc colorModulator, Vector3fc modelOffset, Matrix4fc textureMatrix, float lineWidth) implements DynamicUniformStorage.DynamicUniform {
      public Transform(Matrix4fc var1, Vector4fc var2, Vector3fc var3, Matrix4fc var4, float var5) {
         super();
         this.modelView = var1;
         this.colorModulator = var2;
         this.modelOffset = var3;
         this.textureMatrix = var4;
         this.lineWidth = var5;
      }

      public void write(ByteBuffer var1) {
         Std140Builder.intoBuffer(var1).putMat4f(this.modelView).putVec4(this.colorModulator).putVec3(this.modelOffset).putMat4f(this.textureMatrix).putFloat(this.lineWidth);
      }
   }
}
