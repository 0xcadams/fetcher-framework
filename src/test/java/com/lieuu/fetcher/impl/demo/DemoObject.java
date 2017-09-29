package com.lieuu.fetcher.impl.demo;

public class DemoObject {

  private final int userId;
  private final int id;
  private final String title;
  private final String body;

  /**
   * @param userId
   * @param id
   * @param title
   * @param body
   */
  public DemoObject(final int userId, final int id, final String title, final String body) {
    this.userId = userId;
    this.id = id;
    this.title = title;
    this.body = body;
  }

  /**
   * Gets the value of userId.
   *
   * @return userId of type int
   */
  public int getUserId() {
    return this.userId;
  }

  /**
   * Gets the value of id.
   *
   * @return id of type int
   */
  public int getId() {
    return this.id;
  }

  /**
   * Gets the value of title.
   *
   * @return title of type String
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Gets the value of body.
   *
   * @return body of type String
   */
  public String getBody() {
    return this.body;
  }

}
