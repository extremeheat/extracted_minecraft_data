package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
   private Map<PlayerSkin.Model, EntityRenderer<? extends Player, ?>> playerRenderers = Map.of();
   public final TextureManager textureManager;
   @Nullable
   public Camera camera;
   private Quaternionf cameraOrientation;
   public Entity crosshairPickEntity;
   private final ItemModelResolver itemModelResolver;
   private final MapRenderer mapRenderer;
   private final BlockRenderDispatcher blockRenderDispatcher;
   private final ItemInHandRenderer itemInHandRenderer;
   private final AtlasManager atlasManager;
   private final Font font;
   public final Options options;
   private final Supplier<EntityModelSet> entityModels;
   private final EquipmentAssetManager equipmentAssets;

   public <E extends Entity> int getPackedLightCoords(E var1, float var2) {
      return this.getRenderer(var1).getPackedLightCoords(var1, var2);
   }

   public EntityRenderDispatcher(Minecraft var1, TextureManager var2, ItemModelResolver var3, ItemRenderer var4, MapRenderer var5, BlockRenderDispatcher var6, AtlasManager var7, Font var8, Options var9, Supplier<EntityModelSet> var10, EquipmentAssetManager var11) {
      super();
      this.textureManager = var2;
      this.itemModelResolver = var3;
      this.mapRenderer = var5;
      this.atlasManager = var7;
      this.itemInHandRenderer = new ItemInHandRenderer(var1, this, var4, var3);
      this.blockRenderDispatcher = var6;
      this.font = var8;
      this.options = var9;
      this.entityModels = var10;
      this.equipmentAssets = var11;
   }

   public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T var1) {
      if (var1 instanceof AbstractClientPlayer var2) {
         PlayerSkin.Model var3 = var2.getSkin().model();
         EntityRenderer var4 = (EntityRenderer)this.playerRenderers.get(var3);
         return var4 != null ? var4 : (EntityRenderer)this.playerRenderers.get(PlayerSkin.Model.WIDE);
      } else {
         return (EntityRenderer)this.renderers.get(var1.getType());
      }
   }

   public <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S var1) {
      if (var1 instanceof PlayerRenderState var2) {
         PlayerSkin.Model var3 = var2.skin.model();
         EntityRenderer var4 = (EntityRenderer)this.playerRenderers.get(var3);
         return var4 != null ? var4 : (EntityRenderer)this.playerRenderers.get(PlayerSkin.Model.WIDE);
      } else {
         return (EntityRenderer)this.renderers.get(var1.entityType);
      }
   }

   public void prepare(Camera var1, Entity var2) {
      this.camera = var1;
      this.cameraOrientation = var1.rotation();
      this.crosshairPickEntity = var2;
   }

   public void overrideCameraOrientation(Quaternionf var1) {
      this.cameraOrientation = var1;
   }

   public <E extends Entity> boolean shouldRender(E var1, Frustum var2, double var3, double var5, double var7) {
      EntityRenderer var9 = this.getRenderer(var1);
      return var9.shouldRender(var1, var2, var3, var5, var7);
   }

   public <E extends Entity> EntityRenderState extractEntity(E var1, float var2) {
      EntityRenderer var3 = this.getRenderer(var1);

      try {
         return var3.createRenderState(var1, var2);
      } catch (Throwable var8) {
         CrashReport var5 = CrashReport.forThrowable(var8, "Extracting render state for an entity in world");
         CrashReportCategory var6 = var5.addCategory("Entity being extracted");
         var1.fillCrashReportCategory(var6);
         CrashReportCategory var7 = this.fillRendererDetails(var3, var5);
         var7.setDetail("Delta", var2);
         throw new ReportedException(var5);
      }
   }

   public <S extends EntityRenderState> void submit(S var1, double var2, double var4, double var6, PoseStack var8, SubmitNodeCollector var9) {
      EntityRenderer var10 = this.getRenderer(var1);

      try {
         Vec3 var11 = var10.getRenderOffset(var1);
         double var19 = var2 + var11.x();
         double var14 = var4 + var11.y();
         double var16 = var6 + var11.z();
         var8.pushPose();
         var8.translate(var19, var14, var16);
         var10.submit(var1, var8, var9);
         if (var1.displayFireAnimation) {
            var9.submitFlame(var8, var1, Mth.rotationAroundAxis(Mth.Y_AXIS, this.cameraOrientation, new Quaternionf()));
         }

         if (var1 instanceof PlayerRenderState) {
            var8.translate(-var11.x(), -var11.y(), -var11.z());
         }

         if ((Boolean)this.options.entityShadows().get() && !var1.isInvisible && !var1.shadowPieces.isEmpty()) {
            var9.submitShadow(var8, var1.shadowRadius, var1.shadowPieces);
         }

         if (!(var1 instanceof PlayerRenderState)) {
            var8.translate(-var11.x(), -var11.y(), -var11.z());
         }

         if (var1.hitboxesRenderState != null) {
            var9.submitHitbox(var8, var1, var1.hitboxesRenderState);
         }

         var8.popPose();
      } catch (Throwable var18) {
         CrashReport var12 = CrashReport.forThrowable(var18, "Rendering entity in world");
         CrashReportCategory var13 = var12.addCategory("EntityRenderState being rendered");
         var1.fillCrashReportCategory(var13);
         this.fillRendererDetails(var10, var12);
         throw new ReportedException(var12);
      }
   }

   private <S extends EntityRenderState> CrashReportCategory fillRendererDetails(EntityRenderer<?, S> var1, CrashReport var2) {
      CrashReportCategory var3 = var2.addCategory("Renderer details");
      var3.setDetail("Assigned renderer", var1);
      return var3;
   }

   public void resetCamera() {
      this.camera = null;
   }

   public double distanceToSqr(Entity var1) {
      return this.camera.getPosition().distanceToSqr(var1.position());
   }

   public double distanceToSqr(double var1, double var3, double var5) {
      return this.camera.getPosition().distanceToSqr(var1, var3, var5);
   }

   public Quaternionf cameraOrientation() {
      return this.cameraOrientation;
   }

   public ItemInHandRenderer getItemInHandRenderer() {
      return this.itemInHandRenderer;
   }

   public void onResourceManagerReload(ResourceManager var1) {
      EntityRendererProvider.Context var2 = new EntityRendererProvider.Context(this, this.itemModelResolver, this.mapRenderer, this.blockRenderDispatcher, var1, (EntityModelSet)this.entityModels.get(), this.equipmentAssets, this.atlasManager, this.font);
      this.renderers = EntityRenderers.createEntityRenderers(var2);
      this.playerRenderers = EntityRenderers.createPlayerRenderers(var2);
   }
}
