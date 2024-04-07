package net.minecraft.network.codec;

@FunctionalInterface
public interface StreamEncoder<O, T> {
   void encode(O var1, T var2);
}
