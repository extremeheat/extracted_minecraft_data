package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import java.util.List;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.CraftingGridRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.CraftingGrid;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class CraftingGridRenderer extends EntityRenderer<CraftingGrid, CraftingGridRenderState> {
   private static final Identifier TEXTURE_LOCATION_2X2 = Identifier.withDefaultNamespace("textures/entity/crafting_grid/2x2.png");
   private static final Identifier TEXTURE_LOCATION_3X3 = Identifier.withDefaultNamespace("textures/entity/crafting_grid/3x3.png");
   private static final RenderType RENDER_TYPE_2X2;
   private static final RenderType RENDER_TYPE_3X3;
   private static final int TICKS_PER_GHOST_ITEM = 40;
   private final ItemModelResolver itemModelResolver;

   protected CraftingGridRenderer(final EntityRendererProvider.Context context) {
      super(context);
      this.itemModelResolver = context.getItemModelResolver();
   }

   public CraftingGridRenderState createRenderState() {
      CraftingGridRenderState state = new CraftingGridRenderState();

      for(int i = 0; i < 9; ++i) {
         state.ghosts[i] = new ItemStackRenderState();
      }

      return state;
   }

   public void extractRenderState(final CraftingGrid entity, final CraftingGridRenderState state, final float partialTicks) {
      super.extractRenderState(entity, state, partialTicks);
      state.size = entity.getSize();
      state.rotation = entity.getYRot(partialTicks);
      ContextMap context = SlotDisplayContext.fromLevel(entity.level());

      for(int i = 0; i < 9; ++i) {
         SlotDisplay ghost = entity.getGhostItem(i);
         List<ItemStack> possibleStacks = ghost.resolveForStacks(context);
         ItemStack ghostItem = possibleStacks.isEmpty() ? ItemStack.EMPTY : (ItemStack)possibleStacks.get(entity.tickCount / 40 % possibleStacks.size());
         this.itemModelResolver.updateForNonLiving(state.ghosts[i], ghostItem, ItemDisplayContext.FIXED, entity);
      }

   }

   public void submit(final CraftingGridRenderState state, final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final CameraRenderState camera) {
      poseStack.pushPose();
      poseStack.rotateAround(Axis.YP.rotationDegrees(-state.rotation), 0.0F, 0.0F, 0.0F);
      poseStack.pushPose();
      poseStack.scale((float)(state.size + 2), 1.0F, (float)(state.size + 2));
      submitNodeCollector.submitCustomGeometry(poseStack, state.size == 2 ? RENDER_TYPE_2X2 : RENDER_TYPE_3X3, (pose, buffer) -> {
         vertex(buffer, pose, 0.0F, 0, 1, 1);
         vertex(buffer, pose, 1.0F, 0, 0, 1);
         vertex(buffer, pose, 1.0F, 1, 0, 0);
         vertex(buffer, pose, 0.0F, 1, 1, 0);
      });
      poseStack.popPose();
      float center = (float)state.size / 2.0F;

      for(int y = 0; y < state.size; ++y) {
         for(int x = 0; x < state.size; ++x) {
            poseStack.pushPose();
            poseStack.translate(center - (float)x - 0.5F, 0.0F, center - (float)y - 0.5F);
            poseStack.scale(0.5F, 0.5F, 0.5F);
            poseStack.rotateAround(Axis.XP.rotationDegrees(90.0F), 0.0F, 0.0F, 0.0F);
            state.ghosts[y * 3 + x].submit(poseStack, submitNodeCollector, 15728880, OverlayTexture.NO_OVERLAY, state.outlineColor);
            poseStack.popPose();
         }
      }

      poseStack.popPose();
      super.submit(state, poseStack, submitNodeCollector, camera);
   }

   private static void vertex(final VertexConsumer builder, final PoseStack.Pose pose, final float x, final int z, final int u, final int v) {
      builder.addVertex(pose, x - 0.5F, 0.0F, (float)z - 0.5F).setColor(255, 255, 255, 192).setUv((float)u, (float)v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(pose, 0.0F, 1.0F, 0.0F);
   }

   static {
      RENDER_TYPE_2X2 = RenderTypes.entityTranslucent(TEXTURE_LOCATION_2X2);
      RENDER_TYPE_3X3 = RenderTypes.entityTranslucent(TEXTURE_LOCATION_3X3);
   }
}
