package net.minecraft.commands.execution;

import net.minecraft.commands.CommandResultCallback;

public record Frame(int depth, CommandResultCallback returnValueConsumer, FrameControl frameControl) {
   public Frame(int depth, CommandResultCallback returnValueConsumer, FrameControl frameControl) {
      super();
      this.depth = depth;
      this.returnValueConsumer = returnValueConsumer;
      this.frameControl = frameControl;
   }

   public void returnSuccess(int var1) {
      this.returnValueConsumer.onSuccess(var1);
   }

   public void returnFailure() {
      this.returnValueConsumer.onFailure();
   }

   public void discard() {
      this.frameControl.discard();
   }

   public int depth() {
      return this.depth;
   }

   public CommandResultCallback returnValueConsumer() {
      return this.returnValueConsumer;
   }

   public FrameControl frameControl() {
      return this.frameControl;
   }

   @FunctionalInterface
   public interface FrameControl {
      void discard();
   }
}
