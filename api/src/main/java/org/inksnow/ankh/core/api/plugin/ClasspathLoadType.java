package org.inksnow.ankh.core.api.plugin;

public enum ClasspathLoadType {
  PARENT_ONLY(true, false, false),
  SELF_ONLY(false, true, false),
  PARENT_FIRST(true, true, false),
  SELF_FIRST(false, true, true);

  public final boolean p1;
  public final boolean s2;
  public final boolean p3;

  ClasspathLoadType(boolean p1, boolean s2, boolean p3) {
    this.p1 = p1;
    this.s2 = s2;
    this.p3 = p3;
  }
}