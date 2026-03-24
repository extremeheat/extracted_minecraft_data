package net.minecraft.commands.execution;

import net.minecraft.commands.CommandResultCallback;

public record Frame(int depth, CommandResultCallback returnValueConsumer, FrameControl frameControl) {
   public Frame {
      super();
   }

   public void returnSuccess(final int value) {
      this.returnValueConsumer.onSuccess(value);
   }

   public void returnFailure() {
      this.returnValueConsumer.onFailure();
   }

   public void discard() {
      this.frameControl.discard();
   }

   @FunctionalInterface
   public interface FrameControl {
      void discard();
   }
}
