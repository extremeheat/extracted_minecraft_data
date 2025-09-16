package net.minecraft.client.renderer.state;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;

public record BlockOutlineRenderState(BlockPos pos, boolean isTranslucent, boolean highContrast, VoxelShape shape, @Nullable VoxelShape collisionShape, @Nullable VoxelShape occlusionShape, @Nullable VoxelShape interactionShape) {
   public BlockOutlineRenderState(BlockPos var1, boolean var2, boolean var3, VoxelShape var4) {
      this(var1, var2, var3, var4, (VoxelShape)null, (VoxelShape)null, (VoxelShape)null);
   }

   public BlockOutlineRenderState(BlockPos var1, boolean var2, boolean var3, VoxelShape var4, @Nullable VoxelShape var5, @Nullable VoxelShape var6, @Nullable VoxelShape var7) {
      super();
      this.pos = var1;
      this.isTranslucent = var2;
      this.highContrast = var3;
      this.shape = var4;
      this.collisionShape = var5;
      this.occlusionShape = var6;
      this.interactionShape = var7;
   }
}
