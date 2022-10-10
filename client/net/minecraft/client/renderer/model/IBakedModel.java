package net.minecraft.client.renderer.model;

import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public interface IBakedModel {
   List<BakedQuad> func_200117_a(@Nullable IBlockState var1, @Nullable EnumFacing var2, Random var3);

   boolean func_177555_b();

   boolean func_177556_c();

   boolean func_188618_c();

   TextureAtlasSprite func_177554_e();

   ItemCameraTransforms func_177552_f();

   ItemOverrideList func_188617_f();
}
