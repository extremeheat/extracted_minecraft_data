package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.joml.Vector3fc;

public class StandingSignSpecialRenderer implements NoDataSpecialModelRenderer {
   private final MaterialSet materials;
   private final Model.Simple model;
   private final Material material;

   public StandingSignSpecialRenderer(final MaterialSet materials, final Model.Simple model, final Material material) {
      super();
      this.materials = materials;
      this.model = model;
      this.material = material;
   }

   public void submit(final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      SignRenderer.submitSpecial(this.materials, poseStack, submitNodeCollector, lightCoords, overlayCoords, this.model, this.material);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      SignRenderer.applyInHandTransforms(poseStack);
      this.model.root().getExtentsForGui(poseStack, output);
   }

   public static record Unbaked(WoodType woodType, Optional<Identifier> texture) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(WoodType.CODEC.fieldOf("wood_type").forGetter(Unbaked::woodType), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::texture)).apply(i, Unbaked::new));

      public Unbaked(final WoodType woodType) {
         this(woodType, Optional.empty());
      }

      public Unbaked {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(final SpecialModelRenderer.BakingContext context) {
         Model.Simple model = SignRenderer.createSignModel(context.entityModelSet(), this.woodType, true);
         Optional var10000 = this.texture;
         MaterialMapper var10001 = Sheets.SIGN_MAPPER;
         Objects.requireNonNull(var10001);
         Material material = (Material)var10000.map(var10001::apply).orElseGet(() -> Sheets.getSignMaterial(this.woodType));
         return new StandingSignSpecialRenderer(context.materials(), model, material);
      }
   }
}
