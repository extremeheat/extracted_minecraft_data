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
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.HitResult;

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

   public BlockEntityRenderDispatcher(Font var1, Supplier<EntityModelSet> var2, BlockRenderDispatcher var3, ItemModelResolver var4, ItemRenderer var5, EntityRenderDispatcher var6) {
      super();
      this.itemRenderer = var5;
      this.itemModelResolver = var4;
      this.entityRenderer = var6;
      this.font = var1;
      this.entityModelSet = var2;
      this.blockRenderDispatcher = var3;
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

   public <E extends BlockEntity> void render(E var1, float var2, PoseStack var3, MultiBufferSource var4) {
      BlockEntityRenderer var5 = this.getRenderer(var1);
      if (var5 != null) {
         if (var1.hasLevel() && var1.getType().isValid(var1.getBlockState())) {
            if (var5.shouldRender(var1, this.camera.getPosition())) {
               try {
                  setupAndRender(var5, var1, var2, var3, var4);
               } catch (Throwable var9) {
                  CrashReport var7 = CrashReport.forThrowable(var9, "Rendering Block Entity");
                  CrashReportCategory var8 = var7.addCategory("Block Entity Details");
                  var1.fillCrashReportCategory(var8);
                  throw new ReportedException(var7);
               }
            }
         }
      }
   }

   private static <T extends BlockEntity> void setupAndRender(BlockEntityRenderer<T> var0, T var1, float var2, PoseStack var3, MultiBufferSource var4) {
      Level var6 = var1.getLevel();
      int var5;
      if (var6 != null) {
         var5 = LevelRenderer.getLightColor(var6, var1.getBlockPos());
      } else {
         var5 = 15728880;
      }

      var0.render(var1, var2, var3, var4, var5, OverlayTexture.NO_OVERLAY);
   }

   public void setLevel(@Nullable Level var1) {
      this.level = var1;
      if (var1 == null) {
         this.camera = null;
      }

   }

   public void onResourceManagerReload(ResourceManager var1) {
      BlockEntityRendererProvider.Context var2 = new BlockEntityRendererProvider.Context(this, this.blockRenderDispatcher, this.itemModelResolver, this.itemRenderer, this.entityRenderer, (EntityModelSet)this.entityModelSet.get(), this.font);
      this.renderers = BlockEntityRenderers.createEntityRenderers(var2);
   }
}
