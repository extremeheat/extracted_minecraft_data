package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public interface SubmitNodeCollector {
   void submitHitbox(PoseStack var1, EntityRenderState var2, HitboxesRenderState var3);

   void submitShadow(PoseStack var1, float var2, List<EntityRenderState.ShadowPiece> var3);

   void submitNameTag(PoseStack var1, @Nullable Vec3 var2, Component var3, boolean var4, int var5, double var6);

   void submitText(PoseStack var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9);

   void submitFlame(PoseStack var1, EntityRenderState var2, Quaternionf var3);

   void submitLeash(PoseStack var1, EntityRenderState.LeashState var2);

   <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7, @Nullable TextureAtlasSprite var8, int var9, int var10);

   default <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7) {
      this.submitModel(var1, var2, var3, var4, var5, var6, -1, (TextureAtlasSprite)null, var7, 0);
   }

   void submitBlock(PoseStack var1, BlockState var2, int var3, int var4);

   void submitFallingBlock(PoseStack var1, FallingBlockRenderState var2);

   void submitBlockModel(PoseStack var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8);

   void submitItem(PoseStack var1, ItemStackRenderState var2, int var3, int var4);

   void submitCustomGeometry(PoseStack var1, RenderType var2, CustomGeometryRenderer var3);

   public interface CustomGeometryRenderer {
      void render(PoseStack.Pose var1, VertexConsumer var2);
   }
}
