package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.FallingBlockRenderState;
import net.minecraft.client.renderer.entity.state.HitboxesRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class SubmitNodeStorage implements SubmitNodeCollector {
   private final List<ShadowSubmit> shadowSubmits = new ArrayList();
   private final List<FlameSubmit> flameSubmits = new ArrayList();
   private final List<NameTagSubmit> nameTagSubmitsSeethrough = new ArrayList();
   private final List<NameTagSubmit> nameTagSubmitsNormal = new ArrayList();
   private final List<TextSubmit> textSubmits = new ArrayList();
   private final List<HitboxSubmit> hitboxSubmits = new ArrayList();
   private final List<LeashSubmit> leashSubmits = new ArrayList();
   private final List<BlockSubmit> blockSubmits = new ArrayList();
   private final List<FallingBlockSubmit> fallingBlockSubmits = new ArrayList();
   private final List<BlockModelSubmit> blockModelSubmits = new ArrayList();
   private final List<ItemSubmit> itemSubmits = new ArrayList();
   private final Int2ObjectAVLTreeMap<Map<RenderType, List<ModelSubmit<?>>>> modelSubmits = new Int2ObjectAVLTreeMap();
   private final Set<ModelSubmitBucket> usedModelSubmitBuckets = new ObjectOpenHashSet();
   private final Map<RenderType, List<CustomGeometrySubmit>> customGeometrySubmits = new HashMap();
   private final Set<RenderType> customGeometrySubmitsUsage = new ObjectOpenHashSet();

   public SubmitNodeStorage() {
      super();
   }

   public void submitHitbox(PoseStack var1, EntityRenderState var2, HitboxesRenderState var3) {
      this.hitboxSubmits.add(new HitboxSubmit(new Matrix4f(var1.last().pose()), var2, var3));
   }

   public void submitShadow(PoseStack var1, float var2, List<EntityRenderState.ShadowPiece> var3) {
      PoseStack.Pose var4 = var1.last();
      this.shadowSubmits.add(new ShadowSubmit(new Matrix4f(var4.pose()), var2, var3));
   }

   public void submitNameTag(PoseStack var1, @Nullable Vec3 var2, Component var3, boolean var4, int var5, double var6) {
      if (var2 != null) {
         int var8 = "deadmau5".equals(var3.getString()) ? -10 : 0;
         Minecraft var9 = Minecraft.getInstance();
         var1.pushPose();
         var1.translate(var2.x, var2.y + 0.5, var2.z);
         var1.mulPose((Quaternionfc)var9.getEntityRenderDispatcher().cameraOrientation());
         var1.scale(0.025F, -0.025F, 0.025F);
         Matrix4f var10 = new Matrix4f(var1.last().pose());
         float var11 = (float)(-var9.font.width((FormattedText)var3)) / 2.0F;
         int var12 = (int)(var9.options.getBackgroundOpacity(0.25F) * 255.0F) << 24;
         if (var4) {
            this.nameTagSubmitsNormal.add(new NameTagSubmit(var10, var11, (float)var8, var3, LightTexture.lightCoordsWithEmission(var5, 2), -1, 0, var6));
            this.nameTagSubmitsSeethrough.add(new NameTagSubmit(var10, var11, (float)var8, var3, var5, -2130706433, var12, var6));
         } else {
            this.nameTagSubmitsNormal.add(new NameTagSubmit(var10, var11, (float)var8, var3, var5, -2130706433, var12, var6));
         }

         var1.popPose();
      }
   }

   public void submitText(PoseStack var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9) {
      this.textSubmits.add(new TextSubmit(new Matrix4f(var1.last().pose()), var2, var3, var4, var5, var6, var7, var8, var9));
   }

   public void submitFlame(PoseStack var1, EntityRenderState var2, Quaternionf var3) {
      this.flameSubmits.add(new FlameSubmit(var1.last().copy(), var2, var3));
   }

   public void submitLeash(PoseStack var1, EntityRenderState.LeashState var2) {
      this.leashSubmits.add(new LeashSubmit(new Matrix4f(var1.last().pose()), var2));
   }

   public <S> void submitModel(Model<? super S> var1, S var2, PoseStack var3, RenderType var4, int var5, int var6, int var7, @Nullable TextureAtlasSprite var8, int var9, int var10) {
      ((List)((Map)this.modelSubmits.computeIfAbsent(var10, (var0) -> new HashMap())).computeIfAbsent(var4, (var0) -> new ArrayList())).add(new ModelSubmit(var3.last().copy(), var1, var2, var5, var6, var7, var8, var9));
   }

   public void submitBlock(PoseStack var1, BlockState var2, int var3, int var4) {
      this.blockSubmits.add(new BlockSubmit(var1.last().copy(), var2, var3, var4));
   }

   public void submitFallingBlock(PoseStack var1, FallingBlockRenderState var2) {
      this.fallingBlockSubmits.add(new FallingBlockSubmit(new Matrix4f(var1.last().pose()), var2));
   }

   public void submitBlockModel(PoseStack var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8) {
      this.blockModelSubmits.add(new BlockModelSubmit(var1.last().copy(), var2, var3, var4, var5, var6, var7, var8));
   }

   public void submitItem(PoseStack var1, ItemStackRenderState var2, int var3, int var4) {
      this.itemSubmits.add(new ItemSubmit(var1.last().copy(), var2, var3, var4));
   }

   public void submitCustomGeometry(PoseStack var1, RenderType var2, SubmitNodeCollector.CustomGeometryRenderer var3) {
      List var4 = (List)this.customGeometrySubmits.computeIfAbsent(var2, (var0) -> new ArrayList());
      var4.add(new CustomGeometrySubmit(var1.last().copy(), var3));
   }

   public List<ShadowSubmit> getShadowSubmits() {
      return this.shadowSubmits;
   }

   public List<FlameSubmit> getFlameSubmits() {
      return this.flameSubmits;
   }

   public List<NameTagSubmit> getNameTagSubmitsSeethrough() {
      return this.nameTagSubmitsSeethrough;
   }

   public List<NameTagSubmit> getNameTagSubmitsNormal() {
      return this.nameTagSubmitsNormal;
   }

   public List<TextSubmit> getTextSubmits() {
      return this.textSubmits;
   }

   public List<HitboxSubmit> getHitboxSubmits() {
      return this.hitboxSubmits;
   }

   public List<LeashSubmit> getLeashSubmits() {
      return this.leashSubmits;
   }

   public List<BlockSubmit> getBlockSubmits() {
      return this.blockSubmits;
   }

   public List<FallingBlockSubmit> getFallingBlockSubmits() {
      return this.fallingBlockSubmits;
   }

   public List<BlockModelSubmit> getBlockModelSubmits() {
      return this.blockModelSubmits;
   }

   public List<ItemSubmit> getItemSubmits() {
      return this.itemSubmits;
   }

   public Int2ObjectAVLTreeMap<Map<RenderType, List<ModelSubmit<?>>>> getModelSubmits() {
      return this.modelSubmits;
   }

   public Map<RenderType, List<CustomGeometrySubmit>> getCustomGeometrySubmits() {
      return this.customGeometrySubmits;
   }

   public void clear() {
      this.shadowSubmits.clear();
      this.flameSubmits.clear();
      this.nameTagSubmitsNormal.clear();
      this.nameTagSubmitsSeethrough.clear();
      this.textSubmits.clear();
      this.hitboxSubmits.clear();
      this.leashSubmits.clear();
      this.blockSubmits.clear();
      this.fallingBlockSubmits.clear();
      this.blockModelSubmits.clear();
      this.itemSubmits.clear();
      ObjectBidirectionalIterator var1 = this.modelSubmits.int2ObjectEntrySet().iterator();

      while(var1.hasNext()) {
         Int2ObjectMap.Entry var2 = (Int2ObjectMap.Entry)var1.next();
         int var3 = var2.getIntKey();

         for(Map.Entry var5 : ((Map)var2.getValue()).entrySet()) {
            List var6 = (List)var5.getValue();
            if (!var6.isEmpty()) {
               this.usedModelSubmitBuckets.add(new ModelSubmitBucket((RenderType)var5.getKey(), var3));
               var6.clear();
            }
         }
      }

      for(Map.Entry var8 : this.customGeometrySubmits.entrySet()) {
         if (!((List)var8.getValue()).isEmpty()) {
            this.customGeometrySubmitsUsage.add((RenderType)var8.getKey());
            ((List)var8.getValue()).clear();
         }
      }

   }

   public void endFrame() {
      this.modelSubmits.int2ObjectEntrySet().removeIf((var1) -> {
         int var2 = var1.getIntKey();
         Map var3 = (Map)var1.getValue();
         var3.keySet().removeIf((var2x) -> !this.usedModelSubmitBuckets.contains(new ModelSubmitBucket(var2x, var2)));
         return var3.isEmpty();
      });
      this.usedModelSubmitBuckets.clear();
      this.customGeometrySubmits.keySet().removeIf((var1) -> !this.customGeometrySubmitsUsage.contains(var1));
      this.customGeometrySubmitsUsage.clear();
   }

   public static record ShadowSubmit(Matrix4f pose, float radius, List<EntityRenderState.ShadowPiece> pieces) {
      public ShadowSubmit(Matrix4f var1, float var2, List<EntityRenderState.ShadowPiece> var3) {
         super();
         this.pose = var1;
         this.radius = var2;
         this.pieces = var3;
      }
   }

   public static record FlameSubmit(PoseStack.Pose pose, EntityRenderState entityRenderState, Quaternionf rotation) {
      public FlameSubmit(PoseStack.Pose var1, EntityRenderState var2, Quaternionf var3) {
         super();
         this.pose = var1;
         this.entityRenderState = var2;
         this.rotation = var3;
      }
   }

   public static record NameTagSubmit(Matrix4f pose, float x, float y, Component text, int lightCoords, int color, int backgroundColor, double distanceToCameraSq) {
      public NameTagSubmit(Matrix4f var1, float var2, float var3, Component var4, int var5, int var6, int var7, double var8) {
         super();
         this.pose = var1;
         this.x = var2;
         this.y = var3;
         this.text = var4;
         this.lightCoords = var5;
         this.color = var6;
         this.backgroundColor = var7;
         this.distanceToCameraSq = var8;
      }
   }

   public static record TextSubmit(Matrix4f pose, float x, float y, FormattedCharSequence string, boolean dropShadow, Font.DisplayMode displayMode, int lightCoords, int color, int backgroundColor) {
      public TextSubmit(Matrix4f var1, float var2, float var3, FormattedCharSequence var4, boolean var5, Font.DisplayMode var6, int var7, int var8, int var9) {
         super();
         this.pose = var1;
         this.x = var2;
         this.y = var3;
         this.string = var4;
         this.dropShadow = var5;
         this.displayMode = var6;
         this.lightCoords = var7;
         this.color = var8;
         this.backgroundColor = var9;
      }
   }

   public static record HitboxSubmit(Matrix4f pose, EntityRenderState entityRenderState, HitboxesRenderState hitboxesRenderState) {
      public HitboxSubmit(Matrix4f var1, EntityRenderState var2, HitboxesRenderState var3) {
         super();
         this.pose = var1;
         this.entityRenderState = var2;
         this.hitboxesRenderState = var3;
      }
   }

   public static record LeashSubmit(Matrix4f pose, EntityRenderState.LeashState leashState) {
      public LeashSubmit(Matrix4f var1, EntityRenderState.LeashState var2) {
         super();
         this.pose = var1;
         this.leashState = var2;
      }
   }

   public static record ModelSubmit<S>(PoseStack.Pose pose, Model<? super S> model, S state, int lightCoords, int overlayCoords, int tintedColor, @Nullable TextureAtlasSprite sprite, int outlineColor) {
      public ModelSubmit(PoseStack.Pose var1, Model<? super S> var2, S var3, int var4, int var5, int var6, @Nullable TextureAtlasSprite var7, int var8) {
         super();
         this.pose = var1;
         this.model = var2;
         this.state = var3;
         this.lightCoords = var4;
         this.overlayCoords = var5;
         this.tintedColor = var6;
         this.sprite = var7;
         this.outlineColor = var8;
      }
   }

   public static record BlockSubmit(PoseStack.Pose pose, BlockState state, int lightCoords, int overlayCoords) {
      public BlockSubmit(PoseStack.Pose var1, BlockState var2, int var3, int var4) {
         super();
         this.pose = var1;
         this.state = var2;
         this.lightCoords = var3;
         this.overlayCoords = var4;
      }
   }

   public static record FallingBlockSubmit(Matrix4f pose, FallingBlockRenderState fallingBlockRenderState) {
      public FallingBlockSubmit(Matrix4f var1, FallingBlockRenderState var2) {
         super();
         this.pose = var1;
         this.fallingBlockRenderState = var2;
      }
   }

   public static record BlockModelSubmit(PoseStack.Pose pose, RenderType renderType, BlockStateModel model, float r, float g, float b, int lightCoords, int overlayCoords) {
      public BlockModelSubmit(PoseStack.Pose var1, RenderType var2, BlockStateModel var3, float var4, float var5, float var6, int var7, int var8) {
         super();
         this.pose = var1;
         this.renderType = var2;
         this.model = var3;
         this.r = var4;
         this.g = var5;
         this.b = var6;
         this.lightCoords = var7;
         this.overlayCoords = var8;
      }
   }

   public static record ItemSubmit(PoseStack.Pose pose, ItemStackRenderState state, int lightCoords, int overlayCoords) {
      public ItemSubmit(PoseStack.Pose var1, ItemStackRenderState var2, int var3, int var4) {
         super();
         this.pose = var1;
         this.state = var2;
         this.lightCoords = var3;
         this.overlayCoords = var4;
      }
   }

   public static record CustomGeometrySubmit(PoseStack.Pose pose, SubmitNodeCollector.CustomGeometryRenderer customGeometryRenderer) {
      public CustomGeometrySubmit(PoseStack.Pose var1, SubmitNodeCollector.CustomGeometryRenderer var2) {
         super();
         this.pose = var1;
         this.customGeometryRenderer = var2;
      }
   }

   static record ModelSubmitBucket(RenderType renderType, int order) {
      ModelSubmitBucket(RenderType var1, int var2) {
         super();
         this.renderType = var1;
         this.order = var2;
      }
   }
}
