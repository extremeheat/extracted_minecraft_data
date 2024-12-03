package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.PotDecorations;

public class DecoratedPotSpecialRenderer implements SpecialModelRenderer<PotDecorations> {
   private final DecoratedPotRenderer decoratedPotRenderer;

   public DecoratedPotSpecialRenderer(DecoratedPotRenderer var1) {
      super();
      this.decoratedPotRenderer = var1;
   }

   @Nullable
   public PotDecorations extractArgument(ItemStack var1) {
      return (PotDecorations)var1.get(DataComponents.POT_DECORATIONS);
   }

   public void render(@Nullable PotDecorations var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      this.decoratedPotRenderer.renderInHand(var3, var4, var5, var6, (PotDecorations)Objects.requireNonNullElse(var1, PotDecorations.EMPTY));
   }

   // $FF: synthetic method
   @Nullable
   public Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }

   public static record Unbaked() implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = MapCodec.unit(new Unbaked());

      public Unbaked() {
         super();
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         return new DecoratedPotSpecialRenderer(new DecoratedPotRenderer(var1));
      }
   }
}
