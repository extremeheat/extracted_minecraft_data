package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.object.book.BookModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.EnchantTableRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import org.joml.Vector3fc;

public class BookSpecialRenderer implements NoDataSpecialModelRenderer {
   private final SpriteGetter sprites;
   private final BookModel model;
   private final BookModel.State state;

   public BookSpecialRenderer(final SpriteGetter sprites, final BookModel model, final BookModel.State state) {
      super();
      this.sprites = sprites;
      this.model = model;
      this.state = state;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      submitNodeCollector.submitModel(this.model, this.state, poseStack, lightCoords, overlayCoords, -1, EnchantTableRenderer.BOOK_TEXTURE, this.sprites, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.model.setupAnim(this.state);
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked(float openAngle, float page1, float page2) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(Codec.FLOAT.fieldOf("open_angle").forGetter(Unbaked::openAngle), Codec.FLOAT.fieldOf("page1").forGetter(Unbaked::page1), Codec.FLOAT.fieldOf("page2").forGetter(Unbaked::page2)).apply(i, Unbaked::new));

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public BookSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         return new BookSpecialRenderer(context.sprites(), new BookModel(context.entityModelSet().bakeLayer(ModelLayers.BOOK)), new BookModel.State(this.openAngle * 0.017453292F, this.page1, this.page2));
      }
   }
}
