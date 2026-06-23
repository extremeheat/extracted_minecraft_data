package net.minecraft.client.renderer.special;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Consumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.AbstractEndPortalRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.util.StringRepresentable;
import org.joml.Vector3fc;

public class EndCubeSpecialRenderer implements NoDataSpecialModelRenderer {
   private final RenderType renderType;

   public EndCubeSpecialRenderer(final RenderType renderType) {
      super();
      this.renderType = renderType;
   }

   public void submit(final PoseStack poseStack, final SubmitNodeCollector submitNodeCollector, final int lightCoords, final int overlayCoords, final boolean hasFoil, final int outlineColor) {
      AbstractEndPortalRenderer.submitSpecial(this.renderType, poseStack, submitNodeCollector, outlineColor);
   }

   public void getExtents(final Consumer<Vector3fc> output) {
      AbstractEndPortalRenderer.getExtents(output);
   }

   public static record Unbaked(Type effect) implements NoDataSpecialModelRenderer.Unbaked {
      public static final MapCodec<Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(EndCubeSpecialRenderer.Type.CODEC.fieldOf("effect").forGetter(Unbaked::effect)).apply(i, Unbaked::new));

      public Unbaked {
         super();
      }

      public SpecialModelRenderer<Void> bake(final SpecialModelRenderer.BakingContext context) {
         EndCubeSpecialRenderer var10000 = new EndCubeSpecialRenderer;
         RenderType var10002;
         switch (this.effect.ordinal()) {
            case 0 -> var10002 = RenderTypes.endPortal();
            case 1 -> var10002 = RenderTypes.endGateway();
            default -> throw new MatchException((String)null, (Throwable)null);
         }

         var10000.<init>(var10002);
         return var10000;
      }

      public MapCodec<Unbaked> type() {
         return MAP_CODEC;
      }
   }

   public static enum Type implements StringRepresentable {
      PORTAL("portal"),
      GATEWAY("gateway");

      public static final Codec<Type> CODEC = StringRepresentable.<Type>fromEnum(Type::values);
      private final String name;

      private Type(final String name) {
         this.name = name;
      }

      public String getSerializedName() {
         return this.name;
      }

      // $FF: synthetic method
      private static Type[] $values() {
         return new Type[]{PORTAL, GATEWAY};
      }
   }
}
