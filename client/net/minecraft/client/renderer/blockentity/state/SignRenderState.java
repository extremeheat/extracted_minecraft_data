package net.minecraft.client.renderer.blockentity.state;

import com.mojang.math.Transformation;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.properties.WoodType;
import org.jspecify.annotations.Nullable;

public class SignRenderState extends BlockEntityRenderState {
   public WoodType woodType;
   public @Nullable SignText frontText;
   public @Nullable SignText backText;
   public int textLineHeight;
   public int maxTextLineWidth;
   public boolean isTextFilteringEnabled;
   public boolean drawOutline;
   public SignTransformations transformations;

   public SignRenderState() {
      super();
      this.woodType = WoodType.OAK;
      this.transformations = SignRenderState.SignTransformations.IDENTITY;
   }

   public static record SignTransformations(Transformation body, Transformation frontText, Transformation backText) {
      public static final SignTransformations IDENTITY;

      public SignTransformations {
         super();
      }

      static {
         IDENTITY = new SignTransformations(Transformation.IDENTITY, Transformation.IDENTITY, Transformation.IDENTITY);
      }
   }
}
