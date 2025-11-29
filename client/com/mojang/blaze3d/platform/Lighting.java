package com.mojang.blaze3d.platform;

import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.ByteBuffer;
import net.minecraft.util.Mth;
import net.minecraft.world.level.dimension.DimensionType;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

public class Lighting implements AutoCloseable {
   private static final Vector3f DIFFUSE_LIGHT_0 = (new Vector3f(0.2F, 1.0F, -0.7F)).normalize();
   private static final Vector3f DIFFUSE_LIGHT_1 = (new Vector3f(-0.2F, 1.0F, 0.7F)).normalize();
   private static final Vector3f NETHER_DIFFUSE_LIGHT_0 = (new Vector3f(0.2F, 1.0F, -0.7F)).normalize();
   private static final Vector3f NETHER_DIFFUSE_LIGHT_1 = (new Vector3f(-0.2F, -1.0F, 0.7F)).normalize();
   private static final Vector3f INVENTORY_DIFFUSE_LIGHT_0 = (new Vector3f(0.2F, -1.0F, 1.0F)).normalize();
   private static final Vector3f INVENTORY_DIFFUSE_LIGHT_1 = (new Vector3f(-0.2F, -1.0F, 0.0F)).normalize();
   public static final int UBO_SIZE = (new Std140SizeCalculator()).putVec3().putVec3().get();
   private final GpuBuffer buffer;
   private final long paddedSize;

   public Lighting() {
      super();
      GpuDevice var1 = RenderSystem.getDevice();
      this.paddedSize = (long)Mth.roundToward(UBO_SIZE, var1.getUniformOffsetAlignment());
      this.buffer = var1.createBuffer(() -> "Lighting UBO", 136, this.paddedSize * (long)Lighting.Entry.values().length);
      Matrix4f var2 = (new Matrix4f()).rotationY(-0.3926991F).rotateX(2.3561945F);
      this.updateBuffer(Lighting.Entry.ITEMS_FLAT, var2.transformDirection(DIFFUSE_LIGHT_0, new Vector3f()), var2.transformDirection(DIFFUSE_LIGHT_1, new Vector3f()));
      Matrix4f var3 = (new Matrix4f()).scaling(1.0F, -1.0F, 1.0F).rotateYXZ(1.0821041F, 3.2375858F, 0.0F).rotateYXZ(-0.3926991F, 2.3561945F, 0.0F);
      this.updateBuffer(Lighting.Entry.ITEMS_3D, var3.transformDirection(DIFFUSE_LIGHT_0, new Vector3f()), var3.transformDirection(DIFFUSE_LIGHT_1, new Vector3f()));
      this.updateBuffer(Lighting.Entry.ENTITY_IN_UI, INVENTORY_DIFFUSE_LIGHT_0, INVENTORY_DIFFUSE_LIGHT_1);
      Matrix4f var4 = new Matrix4f();
      this.updateBuffer(Lighting.Entry.PLAYER_SKIN, var4.transformDirection(INVENTORY_DIFFUSE_LIGHT_0, new Vector3f()), var4.transformDirection(INVENTORY_DIFFUSE_LIGHT_1, new Vector3f()));
   }

   public void updateLevel(DimensionType.CardinalLightType var1) {
      switch (var1) {
         case DEFAULT -> this.updateBuffer(Lighting.Entry.LEVEL, DIFFUSE_LIGHT_0, DIFFUSE_LIGHT_1);
         case NETHER -> this.updateBuffer(Lighting.Entry.LEVEL, NETHER_DIFFUSE_LIGHT_0, NETHER_DIFFUSE_LIGHT_1);
      }

   }

   private void updateBuffer(Entry var1, Vector3f var2, Vector3f var3) {
      MemoryStack var4 = MemoryStack.stackPush();

      try {
         ByteBuffer var5 = Std140Builder.onStack(var4, UBO_SIZE).putVec3(var2).putVec3(var3).get();
         RenderSystem.getDevice().createCommandEncoder().writeToBuffer(this.buffer.slice((long)var1.ordinal() * this.paddedSize, this.paddedSize), var5);
      } catch (Throwable var8) {
         if (var4 != null) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }
         }

         throw var8;
      }

      if (var4 != null) {
         var4.close();
      }

   }

   public void setupFor(Entry var1) {
      RenderSystem.setShaderLights(this.buffer.slice((long)var1.ordinal() * this.paddedSize, (long)UBO_SIZE));
   }

   public void close() {
      this.buffer.close();
   }

   public static enum Entry {
      LEVEL,
      ITEMS_FLAT,
      ITEMS_3D,
      ENTITY_IN_UI,
      PLAYER_SKIN;

      private Entry() {
      }

      // $FF: synthetic method
      private static Entry[] $values() {
         return new Entry[]{LEVEL, ITEMS_FLAT, ITEMS_3D, ENTITY_IN_UI, PLAYER_SKIN};
      }
   }
}
