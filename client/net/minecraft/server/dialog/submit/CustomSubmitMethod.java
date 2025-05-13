package net.minecraft.server.dialog.submit;

import com.mojang.serialization.MapCodec;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public interface CustomSubmitMethod extends SubmitMethod {
   MapCodec<? extends CustomSubmitMethod> mapCodec();

   ResourceLocation id();

   String payload(Map<String, String> var1);
}
