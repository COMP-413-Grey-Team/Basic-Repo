package edu.rice.comp413fall2020grey.Common;

import java.util.Date;
import java.util.UUID;

/**
 * Abstract class that defines abstract message behaviour.
 */
public abstract class Message {

  private final Date timestamp;
  private final UUID originSuperpeer;

  protected Message(final Date timestamp, final UUID originSuperpeer) {
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
  public UUID getOriginSuperpeer() {
    return originSuperpeer;
  }

}