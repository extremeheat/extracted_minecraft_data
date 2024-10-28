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
   private final EntityModelSet entityModelSet;
   public Level level;
   public Camera camera;
   public HitResult cameraHitResult;
   private final Supplier<BlockRenderDispatcher> blockRenderDispatcher;
   private final Supplier<ItemRenderer> itemRenderer;
   private final Supplier<EntityRenderDispatcher> entityRenderer;

   public BlockEntityRenderDispatcher(Font var1, EntityModelSet var2, Supplier<BlockRenderDispatcher> var3, Supplier<ItemRenderer> var4, Supplier<EntityRenderDispatcher> var5) {
      super();
      this.itemRenderer = var4;
      this.entityRenderer = var5;
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
               tryRender(var1, () -> {
                  setupAndRender(var5, var1, var2, var3, var4);
               });
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

   public <E extends BlockEntity> boolean renderItem(E var1, PoseStack var2, MultiBufferSource var3, int var4, int var5) {
      BlockEntityRenderer var6 = this.getRenderer(var1);
      if (var6 == null) {
         return true;
      } else {
         tryRender(var1, () -> {
            var6.render(var1, 0.0F, var2, var3, var4, var5);
         });
         return false;
      }
   }

   private static void tryRender(BlockEntity var0, Runnable var1) {
      try {
         var1.run();
      } catch (Throwable var5) {
         CrashReport var3 = CrashReport.forThrowable(var5, "Rendering Block Entity");
         CrashReportCategory var4 = var3.addCategory("Block Entity Details");
         var0.fillCrashReportCategory(var4);
         throw new ReportedException(var3);
      }
   }

   public void setLevel(@Nullable Level var1) {
      this.level = var1;
      if (var1 == null) {
         this.camera = null;
      }

   }

   public void onResourceManagerReload(ResourceManager var1) {
      BlockEntityRendererProvider.Context var2 = new BlockEntityRendererProvider.Context(this, (BlockRenderDispatcher)this.blockRenderDispatcher.get(), (ItemRenderer)this.itemRenderer.get(), (EntityRenderDispatcher)this.entityRenderer.get(), this.entityModelSet, this.font);
      this.renderers = BlockEntityRenderers.createEntityRenderers(var2);
   }
}
