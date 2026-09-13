package net.minecraft.client.resources;

import java.io.InputStream;
import net.minecraft.client.resources.data.IMetadataSection;

public interface IResource {
   InputStream func_110527_b();

   boolean func_110528_c();

   IMetadataSection func_110526_a(String var1);
}
