package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.PlayerSkinRenderCache;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public interface SpecialModelRenderer<T> {
   void submit(@Nullable T var1, ItemDisplayContext var2, PoseStack var3, SubmitNodeCollector var4, int var5, int var6, boolean var7);

   void getExtents(Set<Vector3f> var1);

   @Nullable
   T extractArgument(ItemStack var1);

   public interface BakingContext {
      EntityModelSet entityModelSet();

      MaterialSet materials();

      PlayerSkinRenderCache playerSkinRenderCache();

      public static record Simple(EntityModelSet entityModelSet, MaterialSet materials, PlayerSkinRenderCache playerSkinRenderCache) implements BakingContext {
         public Simple(EntityModelSet var1, MaterialSet var2, PlayerSkinRenderCache var3) {
            super();
            this.entityModelSet = var1;
            this.materials = var2;
            this.playerSkinRenderCache = var3;
         }
      }
   }

   public interface Unbaked {
      @Nullable
      SpecialModelRenderer<?> bake(BakingContext var1);

      MapCodec<? extends Unbaked> type();
   }
}
