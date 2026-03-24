package net.minecraft.client.renderer.blockentity.state;

import com.mojang.math.Transformation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.level.block.entity.BannerPatternLayers;

public class BannerRenderState extends BlockEntityRenderState {
   public DyeColor baseColor;
   public BannerPatternLayers patterns;
   public float phase;
   public Transformation transformation;
   public BannerBlock.AttachmentType attachmentType;

   public BannerRenderState() {
      super();
      this.patterns = BannerPatternLayers.EMPTY;
      this.transformation = Transformation.IDENTITY;
      this.attachmentType = BannerBlock.AttachmentType.GROUND;
   }
}
