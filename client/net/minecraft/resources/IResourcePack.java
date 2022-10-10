package net.minecraft.resources;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.resources.data.IMetadataSectionSerializer;
import net.minecraft.util.ResourceLocation;

public interface IResourcePack extends Closeable {
   InputStream func_195763_b(String var1) throws IOException;

   InputStream func_195761_a(ResourcePackType var1, ResourceLocation var2) throws IOException;

   Collection<ResourceLocation> func_195758_a(ResourcePackType var1, String var2, int var3, Predicate<String> var4);

   boolean func_195764_b(ResourcePackType var1, ResourceLocation var2);

   Set<String> func_195759_a(ResourcePackType var1);

   @Nullable
   <T> T func_195760_a(IMetadataSectionSerializer<T> var1) throws IOException;

   String func_195762_a();
}
