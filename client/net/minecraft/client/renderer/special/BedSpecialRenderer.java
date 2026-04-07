package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.properties.BedPart;
import org.joml.Vector3fc;

public class BedSpecialRenderer implements NoDataSpecialModelRenderer {
   private final BedRenderer bedRenderer;
   private final SpriteId sprite;
   private final BedPart part;

   public BedSpecialRenderer(final BedRenderer bedRenderer, final SpriteId sprite, final BedPart part) {
      super();
      this.bedRenderer = bedRenderer;
      this.sprite = sprite;
      this.part = part;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      this.bedRenderer.submitPiece(this.part, this.sprite, poseStack, submitNodeCollector, lightCoords, overlayCoords, (ModelFeatureRenderer.CrumblingOverlay)null, outlineColor);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      this.bedRenderer.getExtents(this.part, output);
   }

   public static record Unbaked(Identifier texture, BedPart part) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), BedPart.CODEC.fieldOf("part").forGetter(Unbaked::part)).apply(i, Unbaked::new));

      public Unbaked(final DyeColor dyeColor, final BedPart part) {
         this(Sheets.colorToResourceSprite(dyeColor), part);
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public BedSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         return new BedSpecialRenderer(new BedRenderer(context), Sheets.BED_MAPPER.apply(this.texture), this.part);
      }
   }
}
