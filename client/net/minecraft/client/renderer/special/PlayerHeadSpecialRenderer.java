package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.function.Consumer;
import net.minecraft.client.model.object.skull.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class PlayerHeadSpecialRenderer implements SpecialModelRenderer<PlayerSkinRenderCache.RenderInfo> {
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final SkullModelBase modelBase;

   private PlayerHeadSpecialRenderer(final PlayerSkinRenderCache playerSkinRenderCache, final SkullModelBase modelBase) {
      super();
      this.playerSkinRenderCache = playerSkinRenderCache;
      this.modelBase = modelBase;
   }

   public void submit(final PlayerSkinRenderCache.RenderInfo argument, final ItemDisplayContext type, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      RenderType renderType = argument != null ? argument.renderType() : PlayerSkinRenderCache.DEFAULT_PLAYER_SKIN_RENDER_TYPE;
      SkullBlockRenderer.submitSkull((Direction)null, 180.0F, 0.0F, poseStack, submitNodeCollector, lightCoords, this.modelBase, renderType, outlineColor, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      PoseStack poseStack = new PoseStack();
      poseStack.translate(0.5F, 0.0F, 0.5F);
      poseStack.scale(-1.0F, -1.0F, 1.0F);
      this.modelBase.root().getExtentsForGui(poseStack, output);
   }

   public PlayerSkinRenderCache.RenderInfo extractArgument(final ItemStack stack) {
      ResolvableProfile profile = (ResolvableProfile)stack.get(DataComponents.PROFILE);
      return profile == null ? null : this.playerSkinRenderCache.getOrDefault(profile);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public @Nullable SpecialModelRenderer<?> bake(final SpecialModelRenderer.BakingContext context) {
         SkullModelBase model = SkullBlockRenderer.createModel(context.entityModelSet(), SkullBlock.Types.PLAYER);
         return model == null ? null : new PlayerHeadSpecialRenderer(context.playerSkinRenderCache(), model);
      }
   }
}
