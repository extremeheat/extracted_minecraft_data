package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.chest.ChestModel;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.SpriteGetter;
import net.minecraft.client.resources.model.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.joml.Vector3fc;

public class ChestSpecialRenderer implements NoDataSpecialModelRenderer {
   public static final Identifier GIFT_CHEST_TEXTURE = Identifier.withDefaultNamespace("christmas");
   public static final Identifier NORMAL_CHEST_TEXTURE = Identifier.withDefaultNamespace("normal");
   public static final Identifier TRAPPED_CHEST_TEXTURE = Identifier.withDefaultNamespace("trapped");
   public static final Identifier ENDER_CHEST_TEXTURE = Identifier.withDefaultNamespace("ender");
   public static final Identifier COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper");
   public static final Identifier EXPOSED_COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper_exposed");
   public static final Identifier WEATHERED_COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper_weathered");
   public static final Identifier OXIDIZED_COPPER_CHEST_TEXTURE = Identifier.withDefaultNamespace("copper_oxidized");
   private final SpriteGetter sprites;
   private final ChestModel model;
   private final SpriteId sprite;
   private final float openness;

   public ChestSpecialRenderer(final SpriteGetter sprites, final ChestModel model, final SpriteId sprite, final float openness) {
      super();
      this.sprites = sprites;
      this.model = model;
      this.sprite = sprite;
      this.openness = openness;
   }

   public void submit(final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      submitNodeCollector.submitModel(this.model, this.openness, poseStack, this.sprite.renderType(RenderTypes::entitySolid), lightCoords, overlayCoords, -1, this.sprites.get(this.sprite), outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.model.setupAnim(this.openness);
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked(Identifier texture, float openness) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Identifier.CODEC.fieldOf("texture").forGetter(Unbaked::texture), Codec.FLOAT.optionalFieldOf("openness", 0.0F).forGetter(Unbaked::openness)).apply(i, Unbaked::new));

      public Unbaked(final Identifier texture) {
         this(texture, 0.0F);
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(final SpecialModelRenderer.BakingContext context) {
         ChestModel model = new ChestModel(context.entityModelSet().bakeLayer(ModelLayers.CHEST));
         SpriteId fullTexture = Sheets.CHEST_MAPPER.apply(this.texture);
         return new ChestSpecialRenderer(context.sprites(), model, fullTexture, this.openness);
      }
   }
}
