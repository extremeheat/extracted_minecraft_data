package net.minecraft.client.renderer.entity.state;

import net.minecraft.client.renderer.block.BlockModelRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class LivingBlockRenderState extends EntityRenderState {
   public final ItemStackRenderState item = new ItemStackRenderState();
   public final BlockModelRenderState blockModel = new BlockModelRenderState();
   public BlockState blockState;
   public final Quaternionf rotation;
   public boolean hasRotation;
   public boolean hasRedOverlay;
   public float deathTime;
   public Vector3fc pogoScale;
   public Vec3 rotationPivot;

   public LivingBlockRenderState() {
      super();
      this.blockState = Blocks.AIR.defaultBlockState();
      this.rotation = new Quaternionf();
      this.pogoScale = new Vector3f(1.0F, 1.0F, 1.0F);
      this.rotationPivot = Vec3.ZERO;
   }
}
