package edu.rice.comp413fall2020grey.Common;

import java.io.Serializable;
import java.util.Date;

/**
 * Abstract class that defines abstract message behaviour.
 */
public abstract class Message implements Serializable {

  /**
   * Timestamp for message.
   */
  private final Date timestamp;

  /**
   * Unique ID for message sender.
   */
  private final ServerUUID originSuperpeer;

  /**
   * Type of message.
   */
  private final String name;

  /**
   * Abstract class for messages.
   * @param timestamp The time this message was sent.
   * @param originSuperpeer Unique ID for message sender.
   * @param name Message type.
   */
  protected Message(final Date timestamp, final ServerUUID originSuperpeer, final String name) {
    this.timestamp = timestamp;
    this.originSuperpeer = originSuperpeer;
    this.name = name;
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

  /**
   * @return Type of this message.
   */
  public String getMessageType() { return this.name; }

}