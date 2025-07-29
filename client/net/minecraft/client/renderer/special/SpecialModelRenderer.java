package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;

public interface SpecialModelRenderer<T> {
   void render(@Nullable T var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7);

   void getExtents(Set<Vector3f> var1);

   @Nullable
   T extractArgument(ItemStack var1);

   public interface BakingContext {
      EntityModelSet entityModelSet();

      MaterialSet materials();

      public static record Simple(EntityModelSet entityModelSet, MaterialSet materials) implements BakingContext {
         public Simple(EntityModelSet var1, MaterialSet var2) {
            super();
            this.entityModelSet = var1;
            this.materials = var2;
         }
      }
   }

   public interface Unbaked {
      @Nullable
      SpecialModelRenderer<?> bake(BakingContext var1);

      MapCodec<? extends Unbaked> type();
   }
}
