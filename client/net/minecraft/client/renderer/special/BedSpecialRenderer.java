package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BedRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3fc;

public class BedSpecialRenderer implements NoDataSpecialModelRenderer {
   private final BedRenderer bedRenderer;
   private final SpriteId sprite;

   public BedSpecialRenderer(final BedRenderer bedRenderer, final SpriteId sprite) {
      super();
      this.bedRenderer = bedRenderer;
      this.sprite = sprite;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      this.bedRenderer.submitSpecial(poseStack, submitNodeCollector, lightCoords, overlayCoords, this.sprite, outlineColor);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      this.bedRenderer.getExtents(output);
   }

   public static record Unbaked(Identifier texture) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture)).apply(i, Unbaked::new));

      public Unbaked(final DyeColor dyeColor) {
         this(Sheets.colorToResourceSprite(dyeColor));
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public BedSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         return new BedSpecialRenderer(new BedRenderer(context), Sheets.BED_MAPPER.apply(this.texture));
      }
   }
}
