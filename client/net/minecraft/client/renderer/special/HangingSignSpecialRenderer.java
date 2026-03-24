package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SpriteMapper;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.resources.model.sprite.SpriteGetter;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.HangingSignBlock;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3fc;

public class HangingSignSpecialRenderer implements NoDataSpecialModelRenderer {
   private final SpriteGetter sprites;
   private final Model.Simple model;
   private final SpriteId sprite;

   public HangingSignSpecialRenderer(final SpriteGetter sprites, final Model.Simple model, final SpriteId sprite) {
      super();
      this.sprites = sprites;
      this.model = model;
      this.sprite = sprite;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      HangingSignRenderer.submitSpecial(this.sprites, poseStack, submitNodeCollector, lightCoords, overlayCoords, this.model, this.sprite);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked(WoodType woodType, HangingSignBlock.Attachment attachment, Optional<Identifier> texture) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), HangingSignBlock.Attachment.CODEC.optionalFieldOf("attachment", HangingSignBlock.Attachment.CEILING_MIDDLE).forGetter(Unbaked::attachment), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply(i, Unbaked::new));

      public Unbaked(final WoodType woodType, final HangingSignBlock.Attachment attachment) {
         this(woodType, attachment, Optional.empty());
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public HangingSignSpecialRenderer bake(final SpecialModelRenderer.BakingContext context) {
         Model.Simple model = HangingSignRenderer.createSignModel(context.entityModelSet(), this.woodType, this.attachment);
         Optional var10000 = this.texture;
         SpriteMapper var10001 = Sheets.HANGING_SIGN_MAPPER;
         Objects.requireNonNull(var10001);
         SpriteId sprite = (SpriteId)var10000.map(var10001::apply).orElseGet(() -> Sheets.getHangingSignSprite(this.woodType));
         return new HangingSignSpecialRenderer(context.sprites(), model, sprite);
      }
   }
}
