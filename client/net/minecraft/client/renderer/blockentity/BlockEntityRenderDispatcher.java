package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRenderDispatcher implements ResourceManagerReloadListener {
   private Map<BlockEntityType<?>, BlockEntityRenderer<?, ?>> renderers = ImmutableMap.of();
   private final Font font;
   private final Supplier<EntityModelSet> entityModelSet;
   private Vec3 cameraPos;
   private final BlockRenderDispatcher blockRenderDispatcher;
   private final ItemModelResolver itemModelResolver;
   private final ItemRenderer itemRenderer;
   private final EntityRenderDispatcher entityRenderer;
   private final MaterialSet materials;
   private final PlayerSkinRenderCache playerSkinRenderCache;

   public BlockEntityRenderDispatcher(Font var1, Supplier<EntityModelSet> var2, BlockRenderDispatcher var3, ItemModelResolver var4, ItemRenderer var5, EntityRenderDispatcher var6, MaterialSet var7, PlayerSkinRenderCache var8) {
      super();
      this.itemRenderer = var5;
      this.itemModelResolver = var4;
      this.entityRenderer = var6;
      this.font = var1;
      this.entityModelSet = var2;
      this.blockRenderDispatcher = var3;
      this.materials = var7;
      this.playerSkinRenderCache = var8;
   }

   @Nullable
   public <E extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRenderer<E, S> getRenderer(E var1) {
      return (BlockEntityRenderer)this.renderers.get(var1.getType());
   }

   @Nullable
   public <E extends BlockEntity, S extends BlockEntityRenderState> BlockEntityRenderer<E, S> getRenderer(S var1) {
      return (BlockEntityRenderer)this.renderers.get(var1.blockEntityType);
   }

   public void prepare(Camera var1) {
      this.cameraPos = var1.position();
   }

   @Nullable
   public <E extends BlockEntity, S extends BlockEntityRenderState> S tryExtractRenderState(E var1, float var2, @Nullable ModelFeatureRenderer.CrumblingOverlay var3) {
      BlockEntityRenderer var4 = this.getRenderer(var1);
      if (var4 == null) {
         return null;
      } else if (var1.hasLevel() && var1.getType().isValid(var1.getBlockState())) {
         if (!var4.shouldRender(var1, this.cameraPos)) {
            return null;
         } else {
            Vec3 var5 = this.cameraPos;
            BlockEntityRenderState var6 = var4.createRenderState();
            var4.extractRenderState(var1, var6, var2, var5, var3);
            return (S)var6;
         }
      } else {
         return null;
      }
   }

   public <S extends BlockEntityRenderState> void submit(S var1, PoseStack var2, SubmitNodeCollector var3, CameraRenderState var4) {
      BlockEntityRenderer var5 = this.getRenderer(var1);
      if (var5 != null) {
         try {
            var5.submit(var1, var2, var3, var4);
         } catch (Throwable var9) {
            CrashReport var7 = CrashReport.forThrowable(var9, "Rendering Block Entity");
            CrashReportCategory var8 = var7.addCategory("Block Entity Details");
            var1.fillCrashReportCategory(var8);
            throw new ReportedException(var7);
         }
      }
   }

   public void onResourceManagerReload(ResourceManager var1) {
      BlockEntityRendererProvider.Context var2 = new BlockEntityRendererProvider.Context(this, this.blockRenderDispatcher, this.itemModelResolver, this.itemRenderer, this.entityRenderer, (EntityModelSet)this.entityModelSet.get(), this.font, this.materials, this.playerSkinRenderCache);
      this.renderers = BlockEntityRenderers.createEntityRenderers(var2);
   }
}
