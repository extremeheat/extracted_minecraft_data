package net.minecraft.util;

import java.security.SignatureException;

@FunctionalInterface
public interface SignatureUpdater {
   void update(Output output) throws SignatureException;

   @FunctionalInterface
   public interface Output {
      void update(byte[] payload) throws SignatureException;
   }
}
