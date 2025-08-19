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
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BlockEntityRenderDispatcher implements ResourceManagerReloadListener {
   private Map<BlockEntityType<?>, BlockEntityRenderer<?>> renderers = ImmutableMap.of();
   private final Font font;
   private final Supplier<EntityModelSet> entityModelSet;
   public Level level;
   public Camera camera;
   public HitResult cameraHitResult;
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
   public <E extends BlockEntity> BlockEntityRenderer<E> getRenderer(E var1) {
      return (BlockEntityRenderer)this.renderers.get(var1.getType());
   }

   public void prepare(Level var1, Camera var2, HitResult var3) {
      if (this.level != var1) {
         this.setLevel(var1);
      }

      this.camera = var2;
      this.cameraHitResult = var3;
   }

   public <E extends BlockEntity> void submit(E var1, float var2, PoseStack var3, @Nullable ModelFeatureRenderer.CrumblingOverlay var4, SubmitNodeCollector var5) {
      BlockEntityRenderer var6 = this.getRenderer(var1);
      if (var6 != null) {
         if (var1.hasLevel() && var1.getType().isValid(var1.getBlockState())) {
            if (var6.shouldRender(var1, this.camera.getPosition())) {
               try {
                  Vec3 var7 = this.camera.getPosition();
                  Level var11 = var1.getLevel();
                  int var12 = var11 != null ? LevelRenderer.getLightColor(var11, var1.getBlockPos()) : 15728880;
                  var6.submit(var1, var2, var3, var12, OverlayTexture.NO_OVERLAY, var7, var4, var5);
               } catch (Throwable var10) {
                  CrashReport var8 = CrashReport.forThrowable(var10, "Rendering Block Entity");
                  CrashReportCategory var9 = var8.addCategory("Block Entity Details");
                  var1.fillCrashReportCategory(var9);
                  throw new ReportedException(var8);
               }
            }
         }
      }
   }

   public void setLevel(@Nullable Level var1) {
      this.level = var1;
      if (var1 == null) {
         this.camera = null;
      }

   }

   public void onResourceManagerReload(ResourceManager var1) {
      BlockEntityRendererProvider.Context var2 = new BlockEntityRendererProvider.Context(this, this.blockRenderDispatcher, this.itemModelResolver, this.itemRenderer, this.entityRenderer, (EntityModelSet)this.entityModelSet.get(), this.font, this.materials, this.playerSkinRenderCache);
      this.renderers = BlockEntityRenderers.createEntityRenderers(var2);
   }
}
