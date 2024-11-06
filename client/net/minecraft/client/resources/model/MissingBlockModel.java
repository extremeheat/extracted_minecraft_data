package net.minecraft.client.resources.model;

import java.util.EnumMap;
import java.util.List;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class MissingBlockModel {
   private static final String NAME = "missing";
   private static final String TEXTURE_SLOT = "missingno";
   public static final ResourceLocation LOCATION = ResourceLocation.withDefaultNamespace("builtin/missing");
   public static final ModelResourceLocation VARIANT;

   public MissingBlockModel() {
      super();
   }

   public static UnbakedModel missingModel() {
      BlockFaceUV var0 = new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0);
      EnumMap var1 = new EnumMap(Direction.class);
      Direction[] var2 = Direction.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Direction var5 = var2[var4];
         var1.put(var5, new BlockElementFace(var5, -1, "missingno", var0));
      }

      BlockElement var6 = new BlockElement(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(16.0F, 16.0F, 16.0F), var1);
      return new BlockModel((ResourceLocation)null, List.of(var6), (new TextureSlots.Data.Builder()).addReference("particle", "missingno").addTexture("missingno", new Material(TextureAtlas.LOCATION_BLOCKS, MissingTextureAtlasSprite.getLocation())).build(), (Boolean)null, (UnbakedModel.GuiLight)null, ItemTransforms.NO_TRANSFORMS);
   }

   static {
      VARIANT = new ModelResourceLocation(LOCATION, "missing");
   }
}
