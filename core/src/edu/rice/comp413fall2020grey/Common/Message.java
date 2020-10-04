package edu.rice.comp413fall2020grey.Common;

import java.io.Serializable;
import java.util.Date;

/**
 * Abstract class that defines abstract message behaviour.
 */
public abstract class Message implements Serializable {

  private final Date timestamp;
  private final ServerUUID originSuperpeer;

  protected Message(final Date timestamp, final ServerUUID originSuperpeer) {
    this.timestamp = timestamp;
    this.originSuperpeer = originSuperpeer;
  }

  /**
   * @return Date timestamp when message was created and sent
   */
  public Date getTimestamp() {
    return timestamp;
  }

  /**
   * @return UUID of superpeer sender of this message
   */
  public ServerUUID getOriginSuperpeer() {
    return originSuperpeer;
  }

}