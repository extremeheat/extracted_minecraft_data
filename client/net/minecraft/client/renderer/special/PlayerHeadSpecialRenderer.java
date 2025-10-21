package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Vector3f;

public class PlayerHeadSpecialRenderer implements SpecialModelRenderer<PlayerSkinRenderCache.RenderInfo> {
   private final PlayerSkinRenderCache playerSkinRenderCache;
   private final SkullModelBase modelBase;

   PlayerHeadSpecialRenderer(PlayerSkinRenderCache var1, SkullModelBase var2) {
      super();
      this.playerSkinRenderCache = var1;
      this.modelBase = var2;
   }

   public void submit(@Nullable PlayerSkinRenderCache.RenderInfo var1, ItemDisplayContext var2, PoseStack var3, SubmitNodeCollector var4, int var5, int var6, boolean var7, int var8) {
      RenderType var9 = var1 != null ? var1.renderType() : PlayerSkinRenderCache.DEFAULT_PLAYER_SKIN_RENDER_TYPE;
      SkullBlockRenderer.submitSkull((Direction)null, 180.0F, 0.0F, var3, var4, var5, this.modelBase, var9, var8, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.translate(0.5F, 0.0F, 0.5F);
      var2.scale(-1.0F, -1.0F, 1.0F);
      this.modelBase.root().getExtentsForGui(var2, var1);
   }

   @Nullable
   public PlayerSkinRenderCache.RenderInfo extractArgument(ItemStack var1) {
      ResolvableProfile var2 = (ResolvableProfile)var1.get(DataComponents.PROFILE);
      return var2 == null ? null : this.playerSkinRenderCache.getOrDefault(var2);
   }

   // $FF: synthetic method
   @Nullable
   public Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(Unbaked::new);

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      @Nullable
      public SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         SkullModelBase var2 = SkullBlockRenderer.createModel(var1.entityModelSet(), SkullBlock.Types.PLAYER);
         return var2 == null ? null : new PlayerHeadSpecialRenderer(var1.playerSkinRenderCache(), var2);
      }
   }
}
