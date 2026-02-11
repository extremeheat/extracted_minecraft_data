package net.minecraft.client.renderer.state;

import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class CameraRenderState {
   public BlockPos blockPos;
   public Vec3 pos;
   public float xRot;
   public float yRot;
   public boolean initialized;
   public boolean isPanoramicMode;
   public Quaternionf orientation;
   public Frustum cullFrustum;
   public FogType fogType;
   public FogData fogData;
   public float hudFov;
   public float depthFar;
   public Matrix4f projectionMatrix;
   public Matrix4f viewRotationMatrix;
   public CameraEntityRenderState entityRenderState;

   public CameraRenderState() {
      super();
      this.blockPos = BlockPos.ZERO;
      this.pos = new Vec3(0.0, 0.0, 0.0);
      this.orientation = new Quaternionf();
      this.cullFrustum = new Frustum(new Matrix4f(), new Matrix4f());
      this.fogType = FogType.NONE;
      this.fogData = new FogData();
      this.projectionMatrix = new Matrix4f();
      this.viewRotationMatrix = new Matrix4f();
      this.entityRenderState = new CameraEntityRenderState();
   }
}
