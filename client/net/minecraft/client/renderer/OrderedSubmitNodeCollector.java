package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.MovingBlockRenderState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public interface OrderedSubmitNodeCollector {
   void submitHitbox(PoseStack var1, EntityRenderState var2, HitboxesRenderState var3);

   void submitShadow(PoseStack var1, float var2, List<EntityRenderState.ShadowPiece> var3);

   void submitNameTag(PoseStack var1, @Nullable Vec3 var2, int var3, Component var4, boolean var5, int var6, double var7, CameraRenderState var9);

   void submitText(PoseStack var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9, int var10);

   void submitFlame(PoseStack var1, EntityRenderState var2, Quaternionf var3);

   void submitLeash(PoseStack var1, EntityRenderState.LeashState var2);

   <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7, @Nullable TextureAtlasSprite var8, int var9, @Nullable ModelFeatureRenderer.CrumblingOverlay var10);

   default <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7, @Nullable ModelFeatureRenderer.CrumblingOverlay var8) {
      this.submitModel(var1, var2, var3, var4, var5, var6, -1, (TextureAtlasSprite)null, var7, var8);
   }

   default void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6) {
      this.submitModelPart(var1, var2, var3, var4, var5, var6, false, false, -1, (ModelFeatureRenderer.CrumblingOverlay)null, 0);
   }

   default void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6, int var7, @Nullable ModelFeatureRenderer.CrumblingOverlay var8) {
      this.submitModelPart(var1, var2, var3, var4, var5, var6, false, false, var7, var8, 0);
   }

   default void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6, boolean var7, boolean var8) {
      this.submitModelPart(var1, var2, var3, var4, var5, var6, var7, var8, -1, (ModelFeatureRenderer.CrumblingOverlay)null, 0);
   }

   void submitModelPart(ModelPart var1, PoseStack var2, RenderType var3, int var4, int var5, @Nullable TextureAtlasSprite var6, boolean var7, boolean var8, int var9, @Nullable ModelFeatureRenderer.CrumblingOverlay var10, int var11);

   void submitBlock(PoseStack var1, BlockState var2, int var3, int var4, int var5);

   void submitMovingBlock(PoseStack var1, MovingBlockRenderState var2);

   void submitBlockModel(PoseStack var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8, int var9);

   void submitItem(PoseStack var1, ItemDisplayContext var2, int var3, int var4, int var5, int[] var6, List<BakedQuad> var7, RenderType var8, ItemStackRenderState.FoilType var9);

   void submitCustomGeometry(PoseStack var1, RenderType var2, SubmitNodeCollector.CustomGeometryRenderer var3);

   void submitParticleGroup(SubmitNodeCollector.ParticleGroupRenderer var1);
}
