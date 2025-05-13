package net.minecraft.server.dialog.submit;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.registries.BuiltInRegistries;

public interface SubmitMethod {
   Codec<SubmitMethod> CODEC = BuiltInRegistries.SUBMIT_METHOD_TYPE.byNameCodec().dispatch(SubmitMethod::mapCodec, (var0) -> var0);

   MapCodec<? extends SubmitMethod> mapCodec();
}
