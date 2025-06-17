package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Vector3f;

public class PlayerHeadSpecialRenderer implements SpecialModelRenderer<PlayerHeadRenderInfo> {
   private final Map<ResolvableProfile, PlayerHeadRenderInfo> updatedResolvableProfiles = new HashMap();
   private final SkinManager skinManager;
   private final SkullModelBase modelBase;
   private final PlayerHeadRenderInfo defaultPlayerHeadRenderInfo;

   PlayerHeadSpecialRenderer(SkinManager var1, SkullModelBase var2, PlayerHeadRenderInfo var3) {
      super();
      this.skinManager = var1;
      this.modelBase = var2;
      this.defaultPlayerHeadRenderInfo = var3;
   }

   public void render(@Nullable PlayerHeadRenderInfo var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      PlayerHeadRenderInfo var8 = (PlayerHeadRenderInfo)Objects.requireNonNullElse(var1, this.defaultPlayerHeadRenderInfo);
      RenderType var9 = var8.renderType();
      SkullBlockRenderer.renderSkull((Direction)null, 180.0F, 0.0F, var3, var4, var5, this.modelBase, var9);
   }

   public void getExtents(Set<Vector3f> var1) {
      PoseStack var2 = new PoseStack();
      var2.translate(0.5F, 0.0F, 0.5F);
      var2.scale(-1.0F, -1.0F, 1.0F);
      this.modelBase.root().getExtentsForGui(var2, var1);
   }

   @Nullable
   public PlayerHeadRenderInfo extractArgument(ItemStack var1) {
      ResolvableProfile var2 = (ResolvableProfile)var1.get(DataComponents.PROFILE);
      if (var2 == null) {
         return null;
      } else {
         PlayerHeadRenderInfo var3 = (PlayerHeadRenderInfo)this.updatedResolvableProfiles.get(var2);
         if (var3 != null) {
            return var3;
         } else {
            ResolvableProfile var4 = var2.pollResolve();
            return var4 != null ? this.createAndCacheIfTextureIsUnpacked(var4) : null;
         }
      }
   }

   @Nullable
   private PlayerHeadRenderInfo createAndCacheIfTextureIsUnpacked(ResolvableProfile var1) {
      PlayerSkin var2 = this.skinManager.getInsecureSkin(var1.gameProfile(), (PlayerSkin)null);
      if (var2 != null) {
         PlayerHeadRenderInfo var3 = PlayerHeadSpecialRenderer.PlayerHeadRenderInfo.create(var2);
         this.updatedResolvableProfiles.put(var1, var3);
         return var3;
      } else {
         return null;
      }
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
      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         SkullModelBase var2 = SkullBlockRenderer.createModel(var1, SkullBlock.Types.PLAYER);
         return var2 == null ? null : new PlayerHeadSpecialRenderer(Minecraft.getInstance().getSkinManager(), var2, PlayerHeadSpecialRenderer.PlayerHeadRenderInfo.create(DefaultPlayerSkin.getDefaultSkin()));
      }
   }

   public static record PlayerHeadRenderInfo(RenderType renderType) {
      public PlayerHeadRenderInfo(RenderType var1) {
         super();
         this.renderType = var1;
      }

      static PlayerHeadRenderInfo create(PlayerSkin var0) {
         return new PlayerHeadRenderInfo(SkullBlockRenderer.getPlayerSkinRenderType(var0.texture()));
      }
   }
}
