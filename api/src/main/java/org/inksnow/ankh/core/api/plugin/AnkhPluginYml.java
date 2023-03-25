package org.inksnow.ankh.core.api.plugin;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class AnkhPluginYml {
  private final String name;
  private final boolean ankhSelfAsApi;
  private final Map<String, ClasspathLoadType> ankhClasspath;
  private final String ankhIssueUrl;

  public AnkhPluginYml(Map<String, Object> data) {
    this.name = (String) data.getOrDefault("name", "unnamed-ankh-plugin");
    this.ankhSelfAsApi = (Boolean) data.getOrDefault("ankh-self-as-api", false);
    Map<String, String> ankhClasspath = ((Map<String, String>) data.getOrDefault("ankh-classpath", Collections.emptyMap()));
    this.ankhClasspath = new LinkedHashMap<>(ankhClasspath.size());
    ankhClasspath.forEach((k, v) -> this.ankhClasspath.put(k, ClasspathLoadType.valueOf(v)));
    this.ankhIssueUrl = (String) data.getOrDefault("ankh-issue-url", "unknown");
  }

  public String getName() {
    return name;
  }

  public boolean isAnkhSelfAsApi() {
    return ankhSelfAsApi;
  }

  public Map<String, ClasspathLoadType> getAnkhClasspath() {
    return Collections.unmodifiableMap(ankhClasspath);
  }

  public String getAnkhIssueUrl() {
    return ankhIssueUrl;
  }
}