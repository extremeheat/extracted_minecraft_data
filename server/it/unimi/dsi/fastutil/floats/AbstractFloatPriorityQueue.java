package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.AbstractPriorityQueue;
import java.io.Serializable;

/** @deprecated */
@Deprecated
public abstract class AbstractFloatPriorityQueue extends AbstractPriorityQueue<Float> implements Serializable, FloatPriorityQueue {
   private static final long serialVersionUID = 1L;

   public AbstractFloatPriorityQueue() {
      super();
   }
}
