package net.minecraft.data;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import java.io.IOException;

public interface IDataProvider {
   HashFunction field_208307_a = Hashing.sha1();

   void func_200398_a(DirectoryCache var1) throws IOException;

   String func_200397_b();
}
