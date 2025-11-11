package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.SkullBlockRenderer;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.SkullBlock;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class SkullSpecialRenderer implements NoDataSpecialModelRenderer {
   private final SkullModelBase model;
   private final float animation;
   private final RenderType renderType;

   public SkullSpecialRenderer(SkullModelBase var1, float var2, RenderType var3) {
      super();
      this.model = var1;
      this.animation = var2;
      this.renderType = var3;
   }

   public void submit(ItemDisplayContext var1, PoseStack var2, SubmitNodeCollector var3, int var4, int var5, boolean var6, int var7) {
      SkullBlockRenderer.submitSkull((Direction)null, 180.0F, this.animation, var2, var3, var4, this.model, this.renderType, var7, (ModelFeatureRenderer.CrumblingOverlay)null);
   }

   public void getExtents(Consumer<Vector3fc> var1) {
      PoseStack var2 = new PoseStack();
      var2.translate(0.5F, 0.0F, 0.5F);
      var2.scale(-1.0F, -1.0F, 1.0F);
      SkullModelBase.State var3 = new SkullModelBase.State();
      var3.animationPos = this.animation;
      var3.yRot = 180.0F;
      this.model.setupAnim(var3);
      this.model.root().getExtentsForGui(var2, var1);
   }

   public static record Unbaked(SkullBlock.Type kind, Optional<Identifier> textureOverride, float animation) implements SpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((var0) -> var0.group(SkullBlock.Type.CODEC.fieldOf("kind").forGetter(Unbaked::kind), Identifier.CODEC.optionalFieldOf("texture").forGetter(Unbaked::textureOverride), Codec.FLOAT.optionalFieldOf("animation", 0.0F).forGetter(Unbaked::animation)).apply(var0, Unbaked::new));

      public Unbaked(SkullBlock.Type var1) {
         this(var1, Optional.empty(), 0.0F);
      }

      public Unbaked(SkullBlock.Type var1, Optional<Identifier> var2, float var3) {
         super();
         this.kind = var1;
         this.textureOverride = var2;
         this.animation = var3;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }

      public @Nullable SpecialModelRenderer<?> bake(SpecialModelRenderer.BakingContext var1) {
         SkullModelBase var2 = SkullBlockRenderer.createModel(var1.entityModelSet(), this.kind);
         Identifier var3 = (Identifier)this.textureOverride.map((var0) -> var0.withPath((UnaryOperator)((var0x) -> "textures/entity/" + var0x + ".png"))).orElse((Object)null);
         if (var2 == null) {
            return null;
         } else {
            RenderType var4 = SkullBlockRenderer.getSkullRenderType(this.kind, var3);
            return new SkullSpecialRenderer(var2, this.animation, var4);
         }
      }
   }
}
