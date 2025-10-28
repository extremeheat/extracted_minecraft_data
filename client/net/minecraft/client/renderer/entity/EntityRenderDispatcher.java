package net.minecraft.client.renderer.entity;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.entity.ClientAvatarEntity;
import net.minecraft.client.entity.ClientMannequin;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MapRenderer;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.AtlasManager;
import net.minecraft.client.resources.model.EquipmentAssetManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.jspecify.annotations.Nullable;

public class EntityRenderDispatcher implements ResourceManagerReloadListener {
   private Map<EntityType<?>, EntityRenderer<?, ?>> renderers = ImmutableMap.of();
   private Map<PlayerModelType, AvatarRenderer<AbstractClientPlayer>> playerRenderers = Map.of();
   private Map<PlayerModelType, AvatarRenderer<ClientMannequin>> mannequinRenderers = Map.of();
   public final TextureManager textureManager;
   public @Nullable Camera camera;
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
   private final PlayerSkinRenderCache playerSkinRenderCache;

   public <E extends Entity> int getPackedLightCoords(E var1, float var2) {
      return this.getRenderer(var1).getPackedLightCoords(var1, var2);
   }

   public EntityRenderDispatcher(Minecraft var1, TextureManager var2, ItemModelResolver var3, ItemRenderer var4, MapRenderer var5, BlockRenderDispatcher var6, AtlasManager var7, Font var8, Options var9, Supplier<EntityModelSet> var10, EquipmentAssetManager var11, PlayerSkinRenderCache var12) {
      super();
      this.textureManager = var2;
      this.itemModelResolver = var3;
      this.mapRenderer = var5;
      this.atlasManager = var7;
      this.playerSkinRenderCache = var12;
      this.itemInHandRenderer = new ItemInHandRenderer(var1, this, var4, var3);
      this.blockRenderDispatcher = var6;
      this.font = var8;
      this.options = var9;
      this.entityModels = var10;
      this.equipmentAssets = var11;
   }

   public <T extends Entity> EntityRenderer<? super T, ?> getRenderer(T var1) {
      Objects.requireNonNull(var1);
      byte var3 = 0;
      Object var10000;
      //$FF: var3->value
      //0->net/minecraft/client/player/AbstractClientPlayer
      //1->net/minecraft/client/entity/ClientMannequin
      switch (var1.typeSwitch<invokedynamic>(var1, var3)) {
         case 0:
            AbstractClientPlayer var4 = (AbstractClientPlayer)var1;
            var10000 = this.getAvatarRenderer(this.playerRenderers, var4);
            break;
         case 1:
            ClientMannequin var5 = (ClientMannequin)var1;
            var10000 = this.getAvatarRenderer(this.mannequinRenderers, var5);
            break;
         default:
            var10000 = (EntityRenderer)this.renderers.get(var1.getType());
      }

      return (EntityRenderer<? super T, ?>)var10000;
   }

   public AvatarRenderer<AbstractClientPlayer> getPlayerRenderer(AbstractClientPlayer var1) {
      return this.<AbstractClientPlayer>getAvatarRenderer(this.playerRenderers, var1);
   }

   private <T extends Avatar & ClientAvatarEntity> AvatarRenderer<T> getAvatarRenderer(Map<PlayerModelType, AvatarRenderer<T>> var1, T var2) {
      PlayerModelType var3 = ((ClientAvatarEntity)var2).getSkin().model();
      AvatarRenderer var4 = (AvatarRenderer)var1.get(var3);
      return var4 != null ? var4 : (AvatarRenderer)var1.get(PlayerModelType.WIDE);
   }

   public <S extends EntityRenderState> EntityRenderer<?, ? super S> getRenderer(S var1) {
      if (var1 instanceof AvatarRenderState var2) {
         PlayerModelType var3 = var2.skin.model();
         EntityRenderer var4 = (EntityRenderer)this.playerRenderers.get(var3);
         return var4 != null ? var4 : (EntityRenderer)this.playerRenderers.get(PlayerModelType.WIDE);
      } else {
         return (EntityRenderer)this.renderers.get(var1.entityType);
      }
   }

   public void prepare(Camera var1, Entity var2) {
      this.camera = var1;
      this.crosshairPickEntity = var2;
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

   public <S extends EntityRenderState> void submit(S var1, CameraRenderState var2, double var3, double var5, double var7, PoseStack var9, SubmitNodeCollector var10) {
      EntityRenderer var11 = this.getRenderer(var1);

      try {
         Vec3 var12 = var11.getRenderOffset(var1);
         double var20 = var3 + var12.x();
         double var15 = var5 + var12.y();
         double var17 = var7 + var12.z();
         var9.pushPose();
         var9.translate(var20, var15, var17);
         var11.submit(var1, var9, var10, var2);
         if (var1.displayFireAnimation) {
            var10.submitFlame(var9, var1, Mth.rotationAroundAxis(Mth.Y_AXIS, var2.orientation, new Quaternionf()));
         }

         if (var1 instanceof AvatarRenderState) {
            var9.translate(-var12.x(), -var12.y(), -var12.z());
         }

         if (!var1.shadowPieces.isEmpty()) {
            var10.submitShadow(var9, var1.shadowRadius, var1.shadowPieces);
         }

         if (!(var1 instanceof AvatarRenderState)) {
            var9.translate(-var12.x(), -var12.y(), -var12.z());
         }

         var9.popPose();
      } catch (Throwable var19) {
         CrashReport var13 = CrashReport.forThrowable(var19, "Rendering entity in world");
         CrashReportCategory var14 = var13.addCategory("EntityRenderState being rendered");
         var1.fillCrashReportCategory(var14);
         this.fillRendererDetails(var11, var13);
         throw new ReportedException(var13);
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
      return this.camera.position().distanceToSqr(var1.position());
   }

   public ItemInHandRenderer getItemInHandRenderer() {
      return this.itemInHandRenderer;
   }

   public void onResourceManagerReload(ResourceManager var1) {
      EntityRendererProvider.Context var2 = new EntityRendererProvider.Context(this, this.itemModelResolver, this.mapRenderer, this.blockRenderDispatcher, var1, (EntityModelSet)this.entityModels.get(), this.equipmentAssets, this.atlasManager, this.font, this.playerSkinRenderCache);
      this.renderers = EntityRenderers.createEntityRenderers(var2);
      this.playerRenderers = EntityRenderers.createAvatarRenderers(var2);
      this.mannequinRenderers = EntityRenderers.createAvatarRenderers(var2);
   }
}
