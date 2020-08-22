package net.minecraft.client.renderer.blockentity;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.ShulkerModel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.HitResult;

public class BlockEntityRenderDispatcher {
   private final Map renderers = Maps.newHashMap();
   public static final BlockEntityRenderDispatcher instance = new BlockEntityRenderDispatcher();
   private final BufferBuilder singleRenderBuffer = new BufferBuilder(256);
   private Font font;
   public TextureManager textureManager;
   public Level level;
   public Camera camera;
   public HitResult cameraHitResult;

   private BlockEntityRenderDispatcher() {
      this.register(BlockEntityType.SIGN, new SignRenderer(this));
      this.register(BlockEntityType.MOB_SPAWNER, new SpawnerRenderer(this));
      this.register(BlockEntityType.PISTON, new PistonHeadRenderer(this));
      this.register(BlockEntityType.CHEST, new ChestRenderer(this));
      this.register(BlockEntityType.ENDER_CHEST, new ChestRenderer(this));
      this.register(BlockEntityType.TRAPPED_CHEST, new ChestRenderer(this));
      this.register(BlockEntityType.ENCHANTING_TABLE, new EnchantTableRenderer(this));
      this.register(BlockEntityType.LECTERN, new LecternRenderer(this));
      this.register(BlockEntityType.END_PORTAL, new TheEndPortalRenderer(this));
      this.register(BlockEntityType.END_GATEWAY, new TheEndGatewayRenderer(this));
      this.register(BlockEntityType.BEACON, new BeaconRenderer(this));
      this.register(BlockEntityType.SKULL, new SkullBlockRenderer(this));
      this.register(BlockEntityType.BANNER, new BannerRenderer(this));
      this.register(BlockEntityType.STRUCTURE_BLOCK, new StructureBlockRenderer(this));
      this.register(BlockEntityType.SHULKER_BOX, new ShulkerBoxRenderer(new ShulkerModel(), this));
      this.register(BlockEntityType.BED, new BedRenderer(this));
      this.register(BlockEntityType.CONDUIT, new ConduitRenderer(this));
      this.register(BlockEntityType.BELL, new BellRenderer(this));
      this.register(BlockEntityType.CAMPFIRE, new CampfireRenderer(this));
   }

   private void register(BlockEntityType var1, BlockEntityRenderer var2) {
      this.renderers.put(var1, var2);
   }

   @Nullable
   public BlockEntityRenderer getRenderer(BlockEntity var1) {
      return (BlockEntityRenderer)this.renderers.get(var1.getType());
   }

   public void prepare(Level var1, TextureManager var2, Font var3, Camera var4, HitResult var5) {
      if (this.level != var1) {
         this.setLevel(var1);
      }

      this.textureManager = var2;
      this.camera = var4;
      this.font = var3;
      this.cameraHitResult = var5;
   }

   public void render(BlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4) {
      if (var1.distanceToSqr(this.camera.getPosition().x, this.camera.getPosition().y, this.camera.getPosition().z) < var1.getViewDistance()) {
         BlockEntityRenderer var5 = this.getRenderer(var1);
         if (var5 != null) {
            if (var1.hasLevel() && var1.getType().isValid(var1.getBlockState().getBlock())) {
               tryRender(var1, () -> {
                  setupAndRender(var5, var1, var2, var3, var4);
               });
            }
         }
      }
   }

   private static void setupAndRender(BlockEntityRenderer var0, BlockEntity var1, float var2, PoseStack var3, MultiBufferSource var4) {
      Level var6 = var1.getLevel();
      int var5;
      if (var6 != null) {
         var5 = LevelRenderer.getLightColor(var6, var1.getBlockPos());
      } else {
         var5 = 15728880;
      }

      var0.render(var1, var2, var3, var4, var5, OverlayTexture.NO_OVERLAY);
   }

   @Deprecated
   public void renderItem(BlockEntity var1, PoseStack var2) {
      MultiBufferSource.BufferSource var3 = MultiBufferSource.immediate(this.singleRenderBuffer);
      this.renderItem(var1, var2, var3, 15728880, OverlayTexture.NO_OVERLAY);
      var3.endBatch();
   }

   public boolean renderItem(BlockEntity var1, PoseStack var2, MultiBufferSource var3, int var4, int var5) {
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

   public Font getFont() {
      return this.font;
   }
}
