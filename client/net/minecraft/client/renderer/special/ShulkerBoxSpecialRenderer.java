package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.ShulkerBoxRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import org.joml.Vector3fc;

public class ShulkerBoxSpecialRenderer implements NoDataSpecialModelRenderer {
   private final ShulkerBoxRenderer shulkerBoxRenderer;
   private final float openness;
   private final SpriteId sprite;

   public ShulkerBoxSpecialRenderer(final ShulkerBoxRenderer shulkerBoxRenderer, final float openness, final SpriteId sprite) {
      super();
      this.shulkerBoxRenderer = shulkerBoxRenderer;
      this.openness = openness;
      this.sprite = sprite;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      this.shulkerBoxRenderer.submit(poseStack, submitNodeCollector, lightCoords, overlayCoords, this.openness, (ModelFeatureRenderer.CrumblingOverlay)null, this.sprite, outlineColor);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      this.shulkerBoxRenderer.getExtents(this.openness, output);
   }

   public static record Unbaked(Identifier texture, float openness) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)).apply(i, Unbaked::new));

      public Unbaked() {
         this(Identifier.withDefaultNamespace("shulker"), 0.0F);
      }

      public Unbaked(final DyeColor color) {
         this(Sheets.colorToShulkerSprite(color), 0.0F);
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public ShulkerBoxSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         return new ShulkerBoxSpecialRenderer(new ShulkerBoxRenderer(context), this.openness, Sheets.SHULKER_MAPPER.apply(this.texture));
      }
   }
}
