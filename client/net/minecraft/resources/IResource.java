package net.minecraft.resources;

import java.io.Closeable;
import java.io.InputStream;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResource extends Closeable {
   ResourceLocation func_199029_a();

   InputStream func_199027_b();

   boolean func_199030_c();

   @Nullable
   <T> T func_199028_a(IMetadataSectionSerializer<T> var1);

   String func_199026_d();
}
