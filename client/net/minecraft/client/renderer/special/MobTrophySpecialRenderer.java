package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.MobTrophyRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobTrophyInfo;

public class MobTrophySpecialRenderer implements SpecialModelRenderer<MobTrophyInfo> {
   public MobTrophySpecialRenderer() {
      super();
   }

   public void render(@Nullable MobTrophyInfo var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      if (var1 != null) {
         Minecraft var8 = Minecraft.getInstance();
         EntityRenderDispatcher var9 = var8.getEntityRenderDispatcher();
         MobTrophyRenderer.render(var3, var4, var5, var1, var9, Direction.NORTH, var8.level);
      }

   }

   @Nullable
   public MobTrophyInfo extractArgument(ItemStack var1) {
      return (MobTrophyInfo)var1.get(DataComponents.MOB_TROPHY_TYPE);
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

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new MobTrophySpecialRenderer();
      }
   }
}
