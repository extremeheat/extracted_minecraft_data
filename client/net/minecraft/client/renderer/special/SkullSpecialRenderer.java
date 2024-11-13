package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.level.block.SkullBlock;

public class SkullSpecialRenderer implements SpecialModelRenderer<ResolvableProfile> {
   private final SkullBlock.Type skullType;
   private final SkullModelBase model;
   @Nullable
   private final ResourceLocation textureOverride;

   public SkullSpecialRenderer(SkullBlock.Type var1, SkullModelBase var2, @Nullable ResourceLocation var3) {
      super();
      this.skullType = var1;
      this.model = var2;
      this.textureOverride = var3;
   }

   @Nullable
   public ResolvableProfile extractArgument(ItemStack var1) {
      return (ResolvableProfile)var1.get(DataComponents.PROFILE);
   }

   public void render(@Nullable ResolvableProfile var1, ItemDisplayContext var2, PoseStack var3, MultiBufferSource var4, int var5, int var6, boolean var7) {
      RenderType var8 = SkullBlockRenderer.getRenderType(this.skullType, var1, this.textureOverride);
      SkullBlockRenderer.renderSkull((Direction)null, 180.0F, 0.0F, var3, var4, var5, this.model, var8);
   }

   // $FF: synthetic method
   @Nullable
   public Object extractArgument(final ItemStack var1) {
      return this.extractArgument(var1);
   }

   public static record Unbaked(SkullBlock.Type kind, Optional<ResourceLocation> textureOverride) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SkullBlock.Type.CODEC.fieldOf("kind").forGetter(Unbaked::kind), ResourceLocation.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride)).apply(var0, Unbaked::new));

      public Unbaked(SkullBlock.Type var1) {
         this(var1, Optional.empty());
      }

      public Unbaked(SkullBlock.Type var1, Optional<ResourceLocation> var2) {
         super();
         this.kind = var1;
         this.textureOverride = var2;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      @Nullable
      public SpecialModelRenderer<?> bake(EntityModelSet var1) {
         SkullModelBase var2 = SkullBlockRenderer.createModel(var1, this.kind);
         ResourceLocation var3 = (ResourceLocation)this.textureOverride.map((var0) -> var0.withPath((UnaryOperator)((var0x) -> "textures/entity/" + var0x + ".png"))).orElse((Object)null);
         return var2 != null ? new SkullSpecialRenderer(this.kind, var2, var3) : null;
      }
   }
}
